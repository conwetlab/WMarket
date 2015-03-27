package org.fiware.apps.marketplace.bo.impl;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
 * Copyright (C) 2014 CoNWeT Lab, Universidad Politécnica de Madrid
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

import java.util.Date;
import java.util.List;

import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.dao.StoreDao;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.validators.StoreValidator;
import org.fiware.apps.marketplace.security.auth.StoreAuth;
import org.fiware.apps.marketplace.utils.NameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("storeBo")
public class StoreBoImpl implements StoreBo{

	@Autowired private StoreDao storeDao;
	@Autowired private StoreAuth storeAuth;
	@Autowired private StoreValidator storeValidator;
	@Autowired private UserBo userBo;

    @Override
    @Transactional(readOnly=false)
    public Store create(String displayName, String url, String description, User committer)
            throws NotAuthorizedException, ValidationException {
        Store newStore = new Store();

        newStore.setDisplayName(displayName);
        newStore.setUrl(url);
        newStore.setDescription(description);

        this.saveStore(newStore, committer);

        return newStore;
    }

    @Override
    @Transactional(readOnly=false)
    public Store create(Store store, User committer)
            throws NotAuthorizedException, ValidationException {
        Store newStore = new Store();

        newStore.setDisplayName(store.getDisplayName());
        newStore.setUrl(store.getUrl());
        newStore.setDescription(store.getDescription());

        this.saveStore(newStore, committer);

        return newStore;
    }

	@Override
	@Transactional(readOnly=false)
	public void update(String storeName, Store updatedStore) throws NotAuthorizedException, 
			ValidationException, StoreNotFoundException {
		
		try {
			// Get the currently logged-in user
			User user = userBo.getCurrentUser();
			
			Store storeToBeUpdate = this.findByName(storeName);
			
			// Check rights and raise exception if user is not allowed to perform this action
			if (!storeAuth.canUpdate(storeToBeUpdate)) {
				throw new NotAuthorizedException("update store");
			}
			
			// Exception is risen if the store is not valid
			// Store returned by the BBDD cannot be updated if the updated store is not valid.
			this.validateToUpdate(updatedStore, storeToBeUpdate);

			// Update fields
			if (updatedStore.getUrl() != null) {
				storeToBeUpdate.setUrl(updatedStore.getUrl());
			}

			if (updatedStore.getDescription() != null) {
				storeToBeUpdate.setDescription(updatedStore.getDescription());
			}

			if (updatedStore.getDisplayName() != null) {
				storeToBeUpdate.setDisplayName(updatedStore.getDisplayName());
			}
			
			storeToBeUpdate.setLasteditor(user);
			
			storeDao.update(storeToBeUpdate);
			
		} catch (UserNotFoundException ex) {
			// This exception is not supposed to happen
			throw new RuntimeException(ex);
		}
		
	}

	@Override
	@Transactional(readOnly=false)
	public void delete(String storeName) throws NotAuthorizedException, StoreNotFoundException {
		
		Store store = this.findByName(storeName);
		
		// Check rights and raise exception if user is not allowed to perform this action
		if (!storeAuth.canDelete(store)) {
			throw new NotAuthorizedException("delete store");
		}
		
		storeDao.delete(store);
		
	}

	@Override
	@Transactional
	public Store findByName(String name) throws StoreNotFoundException, 
			NotAuthorizedException {
		
		Store store = storeDao.findByName(name);
		
		// Check rights and raise exception if user is not allowed to perform this action
		if (!storeAuth.canGet(store)) {
			throw new NotAuthorizedException("find store");
		}
		
		return store;
	}
	
	@Override
	@Transactional
	public List<Store> getStoresPage(int offset, int max) 
			throws NotAuthorizedException {
		
		// Check rights and raise exception if user is not allowed to perform this action
		if (!storeAuth.canList()) {
			throw new NotAuthorizedException("list stores");
		}
		
		return storeDao.getStoresPage(offset, max);
	}
	
	@Override
	@Transactional
	public List<Store> getAllStores() throws NotAuthorizedException {
		
		// Check rights and raise exception if user is not allowed to perform this action
		if (!storeAuth.canList()) {
			throw new NotAuthorizedException("list stores");
		}
		
		return storeDao.getAllStores();
	}

    @Override
    public void validateToCreate(String displayName, String url, String description)
            throws ValidationException {
        storeValidator.validateDisplayName(displayName);
        this.validateUniqueDisplayName(displayName);
        storeValidator.validateURL(url);
        storeValidator.validateDescription(description);
    }

    @Override
    public void validateToCreate(Store store)
            throws ValidationException {
        this.validateToCreate(store.getDisplayName(), store.getUrl(), store.getDescription());
    }

    @Override
    public void validateToUpdate(Store store, Store oldStore)
            throws ValidationException {
        if (store.getDisplayName() != null) {
            storeValidator.validateDisplayName(store.getDisplayName());
            this.validateUniqueDisplayName(store.getDisplayName(), oldStore.getName());
        }

        if (store.getUrl() != null) {
            storeValidator.validateURL(store.getUrl());
        }

        storeValidator.validateDescription(store.getDescription());
    }

    private String generateName(String displayName) {
        return NameGenerator.getURLName(displayName);
    }

    private void saveStore(Store store, User committer)
            throws NotAuthorizedException {
        // Check if the currently logged-in user can create a store
        if (!storeAuth.canCreate(store)) {
            throw new NotAuthorizedException("create store");
        }

        store.setName(this.generateName(store.getDisplayName()));

        // Set the today's date as registration date
        store.setRegistrationDate(new Date());

        // Set the committer as creator and latest editor
        store.setCreator(committer);
        store.setLasteditor(committer);

        storeDao.save(store);
    }

    private void validateUniqueDisplayName(String displayName)
            throws ValidationException {
        if (storeDao.containsWithName(this.generateName(displayName))) {
            throw new ValidationException("displayName", "The name is already taken.");
        }
    }

    private void validateUniqueDisplayName(String displayName, String oldName)
            throws ValidationException {
        String name = this.generateName(displayName);

        if (storeDao.containsWithName(name) && !oldName.equals(name)) {
            throw new ValidationException("displayName", "The name is already taken.");
        }
    }

}
