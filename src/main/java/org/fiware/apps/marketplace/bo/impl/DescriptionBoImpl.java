package org.fiware.apps.marketplace.bo.impl;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
 * Copyright (C) 2014-2015 CoNWeT Lab, Universidad Politécnica de Madrid
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holders nor the names of its contributors
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fiware.apps.marketplace.bo.DescriptionBo;
import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.dao.CategoryDao;
import org.fiware.apps.marketplace.dao.DescriptionDao;
import org.fiware.apps.marketplace.dao.ServiceDao;
import org.fiware.apps.marketplace.dao.StoreDao;
import org.fiware.apps.marketplace.exceptions.DescriptionNotFoundException;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.helpers.OfferingResolver;
import org.fiware.apps.marketplace.model.Category;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.Description;
import org.fiware.apps.marketplace.model.PricePlan;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.validators.DescriptionValidator;
import org.fiware.apps.marketplace.rdf.RdfIndexer;
import org.fiware.apps.marketplace.security.auth.DescriptionAuth;
import org.fiware.apps.marketplace.utils.NameGenerator;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.hp.hpl.jena.shared.JenaException;

@org.springframework.stereotype.Service("descriptionBo")
public class DescriptionBoImpl implements DescriptionBo {
	
	private static Logger logger = LoggerFactory.getLogger(DescriptionBo.class);
	
	@Autowired private DescriptionAuth descriptionAuth;
	@Autowired private DescriptionValidator descriptionValidator;
	@Autowired private DescriptionDao descriptionDao;
	@Autowired private RdfIndexer rdfIndexer;
	@Autowired private OfferingResolver offeringResolver;
	@Autowired private UserBo userBo;
	@Autowired private StoreDao storeDao;
	@Autowired private CategoryDao categoryDao;
	@Autowired private ServiceDao serviceDao;
	@Autowired private SessionFactory sessionFactory;
	
	private static final String JENA_ERROR = "Your RDF could not be parsed.";
		
	@Override
	@Transactional(readOnly=false, rollbackFor=Exception.class)
	public void save(String storeName, Description description) 
			throws NotAuthorizedException, 
			ValidationException, StoreNotFoundException {
		
		Store store = null;
		User user = null;
		
		try {
			
			user = userBo.getCurrentUser();
			store = storeDao.findByName(storeName);
			
			// Check rights and raise exception if user is not allowed to perform this action
			if (!descriptionAuth.canCreate(description)) {
				throw new NotAuthorizedException("create description");
			}
			
			// Set basic fields
			Date now = new Date();
			description.setCreatedAt(now);
			description.setUpdatedAt(now);
			description.setStore(store);
			description.setCreator(user);
			description.setLasteditor(user);
			
			// Set the name based on the display name
			description.setName(NameGenerator.getURLName(description.getDisplayName()));
			
			// Exception is risen if the description is not valid
			descriptionValidator.validateNewDescription(description);
			
			// Get all the offerings described in the USDL
			List<Offering> offerings = offeringResolver.resolveOfferingsFromServiceDescription(description);
			description.addOfferings(offerings);
			
			// Save the description
			store.addDescription(description);
			// Use StoreDAO to create. It's easier and safer. Useful to avoid weird Hibernate exceptions
			storeDao.update(store);
			
			// ID is required to index the service. Otherwise, the indexer will throw an exception
			// The ID is not automatically set when the description is saved by adding it to the Store
			// so we need to retrieve the object from the database
			try {
				Description createdDescription = descriptionDao.findByNameAndStore(storeName, description.getName());
				description.setId(createdDescription.getId());
			} catch (DescriptionNotFoundException ex) {
				// This should not happen. We have just created the description
			}
			
			// Index
			rdfIndexer.indexOrUpdateService(description);
		} catch (MalformedURLException | JenaException ex) {
			
			// These two exceptions are only thrown if the description cannot be parsed by the RdfIndexer
			// When the indexer cannot index the service, the description cannot be attached to the Store
			// Because of this, the we have set the rollbackFor parameter
			
			String errorMessage;
			if (ex instanceof JenaException) {
				errorMessage = JENA_ERROR;
			} else {
				errorMessage = ex.getMessage();
			}
			
			throw new ValidationException("url", errorMessage);
		} catch (UserNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private void removeUnusedServicesAndCategories(Set<Category> affectedCategories, Set<Service> affectedServices) {
		
		// So the categories and the services are updated
		// Avoid Hibernate Exceptions
		sessionFactory.getCurrentSession().flush();
		
		// Once that the description has been updated, remove unused offerings and services
		for (Category category: affectedCategories) {				
			if (category.getOfferings().size() == 0) {
				category.getServices().clear();
				categoryDao.delete(category);
			}
		}
		
		for (Service service: affectedServices) {
			if (service.getOfferings().size() == 0) {
				service.getCategories().clear();
				serviceDao.delete(service);
			}
		}
	}

	private void update(String storeName, String descriptionName, Description updatedDescription, boolean checkRights) 
			throws NotAuthorizedException, 
			ValidationException, StoreNotFoundException, DescriptionNotFoundException {
		
		try {
			Description descriptionToBeUpdated = descriptionDao.findByNameAndStore(storeName, descriptionName);
			
			// Check rights and raise exception if user is not allowed to perform this action
			if (checkRights && !descriptionAuth.canUpdate(descriptionToBeUpdated)) {
				throw new NotAuthorizedException("update description");
			}
			
			//Set store in description
			updatedDescription.setStore(descriptionToBeUpdated.getStore());
			
			// Exception is risen if the description is not valid
			descriptionValidator.validateUpdatedDescription(descriptionToBeUpdated, updatedDescription);
			
			// Update URL (and the included offerings)
			if (updatedDescription.getUrl() != null) {
				
				List<Offering> descriptionOfferings = descriptionToBeUpdated.getOfferings();
				Set<Category> currentCategories = new HashSet<>();
				Set<Service> currentServices = new HashSet<>();
				
				for (Offering offering: descriptionOfferings) {
					currentCategories.addAll(offering.getCategories());
					currentServices.addAll(offering.getServices());
				}
				
				// Change URL
				descriptionToBeUpdated.setUrl(updatedDescription.getUrl());
				
				// Get all the offerings described in the USDL
				List<Offering> newOfferings = offeringResolver
						.resolveOfferingsFromServiceDescription(descriptionToBeUpdated);
				
				// When a description is updated, some offerings can be deleted.
				// If categories and services are not removed from these offerings,
				// the system will try to remove them from the database but this will fail
				// since categories and services can be attached to other offerings.
				List<Offering> removedOfferings = new ArrayList<>(descriptionOfferings);
				removedOfferings.removeAll(newOfferings);
				
				for (Offering offering: removedOfferings) {
					offering.getCategories().clear();
					offering.getServices().clear();
				}
								
				// Remove offerings not contained in the new description
				descriptionOfferings.retainAll(newOfferings);
				
				for (Offering updatedOffering: newOfferings) {
					
					int index = descriptionOfferings.indexOf(updatedOffering);
					
					if (index < 0) {
						// A new offering that was not included before in the previous USDL
						descriptionOfferings.add(updatedOffering);
					} else {
						// An old offering that is still present in the current USDL
						// We have to replace some fields, but we cannot remove this offering and
						// create a new one: reviews and bookmarks will be lost
						Offering offering = descriptionOfferings.get(index);
						
						// We have to update the fields. equals only depends on the URI
						offering.setDescription(updatedOffering.getDescription());
						offering.setDisplayName(updatedOffering.getDisplayName());
						offering.setImageUrl(updatedOffering.getImageUrl());
						offering.setVersion(updatedOffering.getVersion());
						offering.setDisplayName(updatedOffering.getDisplayName());
						offering.setName(updatedOffering.getName());
						offering.setCategories(updatedOffering.getCategories());
						offering.setServices(updatedOffering.getServices());
						
						// Set price plans (offering field MUST be updated. Otherwise an exception will be risen)
						offering.getPricePlans().clear();
						for (PricePlan pricePlan: updatedOffering.getPricePlans()) {
							pricePlan.setOffering(offering);
							offering.getPricePlans().add(pricePlan);
						}
						
					}
				}

				removeUnusedServicesAndCategories(currentCategories, currentServices);
								
				// When the description URL changes, the index must be updated. 
				rdfIndexer.indexOrUpdateService(descriptionToBeUpdated);
			}
			
			// Update the rest of fields
			if (updatedDescription.getDisplayName() != null) {
				descriptionToBeUpdated.setDisplayName(updatedDescription.getDisplayName());
			}
			
			if (updatedDescription.getComment() != null) {
				descriptionToBeUpdated.setComment(updatedDescription.getComment());
			}
			
			// If the action is automatically performed by the system, last editor field
			// should not be updated
			if (checkRights) {
				descriptionToBeUpdated.setLasteditor(userBo.getCurrentUser());
			}
			
			descriptionToBeUpdated.setUpdatedAt(new Date());
			
			// Update the description
			descriptionDao.update(descriptionToBeUpdated);
			
		} catch (MalformedURLException ex) {
			throw new ValidationException("url", ex.getMessage());
		} catch (JenaException ex) {
			throw new ValidationException("url", JENA_ERROR);			
		} catch (UserNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	@Transactional(readOnly=false, rollbackFor=Exception.class)
	public void update(String storeName, String descriptionName, Description updatedDescription) 
			throws NotAuthorizedException, 
			ValidationException, StoreNotFoundException, DescriptionNotFoundException {
		
		this.update(storeName, descriptionName, updatedDescription, true);
	}

	@Override
	@Transactional(readOnly=false)
	public void delete(String storeName, String descriptionName) 
			throws NotAuthorizedException, StoreNotFoundException, DescriptionNotFoundException {
		
		Store store = storeDao.findByName(storeName);
		Description description = descriptionDao.findByNameAndStore(storeName, descriptionName);
		
		// Check rights and raise exception if user is not allowed to perform this action
		if (!descriptionAuth.canDelete(description)) {
			throw new NotAuthorizedException("delete description");
		}
		
		Set<Category> categories = new HashSet<>();
		Set<Service> services = new HashSet<>();
		
		// If categories and services are not removed from the attached offerings,
		// the system will try to remove them from the database but this will fail since
		// categories and services can be attached to other offerings.
		for (Offering offering: description.getOfferings()) {
			categories.addAll(offering.getCategories());
			services.addAll(offering.getServices());
			offering.getCategories().clear();
			offering.getServices().clear();
		}
		
		// Delete the description from the data base
		// We must relay on StoreDao to remove descriptions. 
		// It's easier and safer and weird exceptions are avoided
		store.removeDescription(description);
		storeDao.update(store);
		
		// Remove unused services and categories
		removeUnusedServicesAndCategories(categories, services);
		
		// Delete indexes
		rdfIndexer.deleteService(description);
	}

	@Override
	@Transactional
	public Description findById(Integer id) 
			throws DescriptionNotFoundException, NotAuthorizedException{
		
		Description description = descriptionDao.findById(id);
		
		// Check rights and raise exception if user is not allowed to perform this action
		if (!descriptionAuth.canGet(description)) {
			throw new NotAuthorizedException("find description");
		}
		
		return description;
	}

	@Override
	@Transactional
	public Description findByNameAndStore(String storeName, String descriptionName) 
			throws StoreNotFoundException, DescriptionNotFoundException, 
			NotAuthorizedException {
		
		Description description = descriptionDao.findByNameAndStore(storeName, descriptionName);
		
		// Check rights and raise exception if user is not allowed to perform this action
		if (!descriptionAuth.canGet(description)) {
			throw new NotAuthorizedException("find description");
		}
		
		return description;
	}
	
	@Override
	@Transactional
	public List<Description> getCurrentUserDescriptions() {
		try {
			return descriptionDao.getUserDescriptions(userBo.getCurrentUser().getUserName());
		} catch (UserNotFoundException e) {
			// This exception should never happen
			throw new RuntimeException(e);
		}
	}
	
	@Override
	@Transactional
	public List<Description> getAllDescriptions() 
			throws NotAuthorizedException {
		
		// Check rights and raise exception if user is not allowed to perform this action
		if (!descriptionAuth.canList()) {
			throw new NotAuthorizedException("list descriptions");
		}

		return descriptionDao.getAllDescriptions();
	}
	
	@Override
	@Transactional
	public List<Description> getDescriptionsPage(int offset, int max)
			throws NotAuthorizedException {
		
		// Check rights and raise exception if user is not allowed to perform this action
		if (!descriptionAuth.canList()) {
			throw new NotAuthorizedException("list descriptions");
		}
		
		return descriptionDao.getDescriptionsPage(offset, max);
	}

	@Override
	@Transactional
	public List<Description> getStoreDescriptions(String storeName) 
			throws StoreNotFoundException, NotAuthorizedException {
		
		// Will throw exception in case the Store does not exist
		Store store = storeDao.findByName(storeName);
		
		// Check rights and raise exception if user is not allowed to perform this action
		if (!descriptionAuth.canList(store)) {
			throw new NotAuthorizedException("list descriptions in store " + store.getName());
		}
		
		return store.getDescriptions();
	}

	@Override
	@Transactional
	public List<Description> getStoreDescriptionsPage(String storeName,
			int offset, int max) throws StoreNotFoundException, 
			NotAuthorizedException {
		
		// Will throw exception in case the Store does not exist
		Store store = storeDao.findByName(storeName);
		
		// Check rights and raise exception if user is not allowed to perform this action
		if (!descriptionAuth.canList(store)) {
			throw new NotAuthorizedException("list descriptions in store " + store.getName());
		}
		
		return descriptionDao.getStoreDescriptionsPage(storeName, offset, max);
	}

    @Override
    @Transactional
    public List<Description> getUserDescriptionsInStore(String userName, String storeName)
            throws UserNotFoundException, StoreNotFoundException {

        return descriptionDao.getUserDescriptionsInStore(userName, storeName);
    }

	@Override
	@Transactional
	public void updateAllDescriptions() {
		
		logger.info("Trying to update all the descriptions...");
		
		List<Description> descriptions = descriptionDao.getAllDescriptions();
		for (Description description: descriptions) {
			Description updatedDescription = new Description();
			updatedDescription.setUrl(description.getUrl());
			String descriptionName = description.getName();
			String storeName = description.getStore().getName();
			
			try {
				this.update(storeName, descriptionName, updatedDescription, false);
				logger.info(String.format("Description %s (store: %s) updated", descriptionName, storeName));
			} catch (ValidationException e) {
				logger.warn(String.format("Description %s (store: %s) could not be updated", 
						descriptionName, storeName), e);
			} catch (NotAuthorizedException e) {
				// Not expected. Rights are not checked				
			} catch (StoreNotFoundException | DescriptionNotFoundException e) {
				// Not expected. Store and description are supposed to exist
			}
		}
	}

}
