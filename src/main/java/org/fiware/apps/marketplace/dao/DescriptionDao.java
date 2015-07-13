package org.fiware.apps.marketplace.dao;

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

import java.util.List;

import org.fiware.apps.marketplace.exceptions.DescriptionNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Description;

public interface DescriptionDao {
	
	// Save, update & delete
	// public void save(Description description);
	public void update(Description description);
	// public void delete(Description description);
	
	// Find
	public Description findByNameAndStore(String storeName, String descriptionName) 
			throws StoreNotFoundException, DescriptionNotFoundException;
	public Description findById(Integer id) throws DescriptionNotFoundException;
	
	// Verifications
	public boolean isNameAvailableInStore(String storeName, String descriptionName);
	public boolean isDisplayNameAvailableInStore(String storeName, String displayName);
	public boolean isURLAvailableInStore(String storeName, String url);
	
	// Get descriptions
	public List<Description> getUserDescriptions(String userName) 
			throws UserNotFoundException;
	public List<Description> getAllDescriptions();
	public List<Description> getDescriptionsPage(int offset, int max);
	public List<Description> getStoreDescriptions(String storeName) 
			throws StoreNotFoundException;
	public List<Description> getStoreDescriptionsPage(String storeName, 
			int offset, int max) throws StoreNotFoundException;

    /**
     * Get the current list of descriptions created by a user in one specific store.
     *
     * @param userName
     * @param storeName
     *
     * @throws UserNotFoundException If there is not a User with the userName given.
     * @throws StoreNotFoundException If there is not a Store with the storeName given.
     *
     * @return The list of descriptions.
     */
    public List<Description> getUserDescriptionsInStore(String userName, String storeName)
            throws UserNotFoundException, StoreNotFoundException;

}
