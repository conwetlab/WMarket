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
import java.util.Date;
import java.util.List;

import org.fiware.apps.marketplace.bo.DescriptionBo;
import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.dao.DescriptionDao;
import org.fiware.apps.marketplace.dao.OfferingDao;
import org.fiware.apps.marketplace.exceptions.DescriptionNotFoundException;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.helpers.OfferingResolver;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.Description;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.validators.DescriptionValidator;
import org.fiware.apps.marketplace.rdf.RdfIndexer;
import org.fiware.apps.marketplace.security.auth.DescriptionAuth;
import org.fiware.apps.marketplace.utils.NameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service("descriptionBo")
public class DescriptionBoImpl implements DescriptionBo {
	
	@Autowired private DescriptionAuth descriptionAuth;
	@Autowired private DescriptionValidator descriptionValidator;
	@Autowired private DescriptionDao descriptionDao;
	@Autowired private RdfIndexer rdfIndexer;
	@Autowired private OfferingResolver offeringResolver;
	@Autowired private UserBo userBo;
	@Autowired private StoreBo storeBo;
	@Autowired private OfferingDao offeringDao;
		
	@Override
	@Transactional(readOnly=false)
	public void save(String storeName, Description description) 
			throws MalformedURLException, NotAuthorizedException, 
			ValidationException, StoreNotFoundException {
		
		try {
			
			User user = userBo.getCurrentUser();
			Store store = storeBo.findByName(storeName);
			
			// Check rights (exception is risen if user is not allowed)
			descriptionAuth.canCreate(description);
			
			// Set basic fields
			description.setRegistrationDate(new Date());
			description.setStore(store);
			description.setCreator(user);
			description.setLasteditor(user);
			
			// Validate the description (exception is risen if the user is not valid)
			descriptionValidator.validateDescription(description, true);
			
			// Set the name
			description.setName(NameGenerator.getURLName(description.getDisplayName()));
			
			// Get all the offerings described in the USDL
			List<Offering> offerings = offeringResolver.resolveOfferingsFromServiceDescription(description);
			description.addOfferings(offerings);
			
			// Save the description
			descriptionDao.save(description);
			
			// Index
			rdfIndexer.indexService(description);
		} catch (UserNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	@Transactional(readOnly=false)
	public void update(String storeName, String descriptionName, Description updatedDescription) 
			throws MalformedURLException, NotAuthorizedException, 
			ValidationException, StoreNotFoundException, DescriptionNotFoundException {
		
		try {
			Description descriptionToBeUpdated = this.findByNameAndStore(storeName, descriptionName);
			
			// Check rights (exception is risen if user is not allowed)
			descriptionAuth.canUpdate(descriptionToBeUpdated);
			
			// Validate the description (exception is risen if the user is not valid)
			descriptionValidator.validateDescription(updatedDescription, false);
			
			// Update fields
			if (updatedDescription.getDisplayName() != null) {
				descriptionToBeUpdated.setDisplayName(updatedDescription.getDisplayName());
			}
	
			if (updatedDescription.getUrl() != null) {
				descriptionToBeUpdated.setUrl(updatedDescription.getUrl());
			}
	
			if (updatedDescription.getDescription() != null) {
				descriptionToBeUpdated.setDescription(updatedDescription.getDescription());
			}
	
			descriptionToBeUpdated.setLasteditor(userBo.getCurrentUser());
	
			// Remove previous offerings
			descriptionToBeUpdated.getOfferings().removeAll(descriptionToBeUpdated.getOfferings());
			
			// Get all the offerings described in the USDL
			List<Offering> offerings = offeringResolver.resolveOfferingsFromServiceDescription(descriptionToBeUpdated);
			descriptionToBeUpdated.setOfferings(offerings);	// Add the new offerings
	
			// Save the description
			descriptionDao.update(descriptionToBeUpdated);
			
			// Reindex
			rdfIndexer.deleteService(descriptionToBeUpdated);
			rdfIndexer.indexService(descriptionToBeUpdated);
			
		} catch (UserNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	@Transactional(readOnly=false)
	public void delete(String storeName, String descriptionName) 
			throws NotAuthorizedException, StoreNotFoundException, DescriptionNotFoundException {
		
		Store store = storeBo.findByName(storeName);
		Description description = this.findByNameAndStore(storeName, descriptionName);
		
		// Check rights (exception is risen if user is not allowed)
		descriptionAuth.canDelete(description);
		
		// Delete the description from the data base
		store.getDescriptions().remove(description);
		descriptionDao.delete(description);
		
		// Delete indexes
		rdfIndexer.deleteService(description);
	}

	@Override
	@Transactional
	public Description findById(Integer id) 
			throws DescriptionNotFoundException, NotAuthorizedException{
		
		Description description = descriptionDao.findById(id);
		
		// Check rights (exception is risen if user is not allowed)
		descriptionAuth.canGet(description);
		
		return description;
	}

	@Override
	@Transactional
	public Description findByNameAndStore(String storeName, String descriptionName) 
			throws StoreNotFoundException, DescriptionNotFoundException, 
			NotAuthorizedException {
		
		Description description = descriptionDao.findByNameAndStore(storeName, descriptionName);
		
		// Check rights (exception is risen if user is not allowed)
		descriptionAuth.canGet(description);
		
		return description;
	}
	
	@Override
	@Transactional
	public List<Description> getAllDescriptions() 
			throws NotAuthorizedException {
		
		// Check rights (exception is risen if user is not allowed)
		descriptionAuth.canList();

		return descriptionDao.getAllDescriptions();
	}
	
	@Override
	@Transactional
	public List<Description> getDescriptionsPage(int offset, int max)
			throws NotAuthorizedException {
		
		// Check rights (exception is risen if user is not allowed)
		descriptionAuth.canList();
		
		return descriptionDao.getDescriptionsPage(offset, max);
	}

	@Override
	@Transactional
	public List<Description> getStoreDescriptions(String storeName) 
			throws StoreNotFoundException, NotAuthorizedException {
		
		// Will throw exception in case the Store does not exist
		Store store = storeBo.findByName(storeName);
		
		// Check rights (exception is risen if user is not allowed)
		descriptionAuth.canList(store);
		
		List<Description> descriptions = descriptionDao.getStoreDescriptions(storeName);
		
		return descriptions;
	}

	@Override
	@Transactional
	public List<Description> getStoreDescriptionsPage(String storeName,
			int offset, int max) throws StoreNotFoundException, 
			NotAuthorizedException {
		
		// Will throw exception in case the Store does not exist
		Store store = storeBo.findByName(storeName);
		
		// Check rights (exception is risen if user is not allowed)
		descriptionAuth.canList(store);

		return descriptionDao.getStoreDescriptionsPage(storeName, offset, max);
	}

}
