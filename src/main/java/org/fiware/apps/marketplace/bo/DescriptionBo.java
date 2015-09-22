package org.fiware.apps.marketplace.bo;

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

import java.util.List;

import org.fiware.apps.marketplace.exceptions.DescriptionNotFoundException;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Description;

public interface DescriptionBo {
	
	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// CRUD ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new description in an existing store
	 * @param storeName The name of the store that will contain the description
	 * @param description The description itself
	 * @throws NotAuthorizedException If the current user is not authorized to create the given description
	 * @throws ValidationException If the given description is not valid
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 */
	public void save(String storeName, Description description) throws NotAuthorizedException, 
			ValidationException, StoreNotFoundException;
	
	/**
	 * Updates a description contained in a given store
	 * @param storeName The name of the store that contains the description 
	 * @param descriptionName The name of the description to be updated
	 * @param updatedDescription The updated description
	 * @throws NotAuthorizedException If the current user is not authorized to update the given description 
	 * @throws ValidationException If the updated description is not valid
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws DescriptionNotFoundException If it does not exist a description with the given name in the store
	 */
	public void update(String storeName, String descriptionName, Description updatedDescription) 
			throws NotAuthorizedException, ValidationException,
			StoreNotFoundException, DescriptionNotFoundException;
	
	/**
	 * Deletes a description contained in a given store
	 * @param storeName The name of the store that contains the description
	 * @param descriptionName The name of the description to be deleted
	 * @throws NotAuthorizedException If the current user is not authorized to delete the given description 
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws DescriptionNotFoundException If it does not exist a description with the given name in the store
	 */
	public void delete(String storeName, String descriptionName) throws NotAuthorizedException, 
			StoreNotFoundException, DescriptionNotFoundException;
	
	/**
	 * Returns a description contained in a given store
	 * @param storeName The name of the store that contains the description
	 * @param descriptionName The name of the description to be retrieved
	 * @return The description contained in the given store
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the given description 
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws DescriptionNotFoundException If it does not exist a description with the given name in the store
	 */
	public Description findByNameAndStore(String storeName, String descriptionName) 
			throws NotAuthorizedException, StoreNotFoundException, DescriptionNotFoundException;
	
	/**
	 * Returns a description based on its ID
	 * @param id The ID of the description to be retrieved
	 * @return The description with the given ID
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the requested description
	 * @throws DescriptionNotFoundException If it does not exist a description with the given ID
	 */
	public Description findById(Integer id) throws NotAuthorizedException, DescriptionNotFoundException;
	

	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// LIST ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all the descriptions created by the current user
	 * @return All the descriptions created by the current user
	 */
	public List<Description> getCurrentUserDescriptions();
	
	/**
	 * Returns all the descriptions
	 * @return All the descriptions
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the list of descriptions
	 */
	public List<Description> getAllDescriptions() throws NotAuthorizedException;
	
	/**
	 * Returns a sublist of all the stored descriptions
	 * @param offset The first description to be retrieved
	 * @param max The max number of descriptions to be returned
	 * @return A sublist of all the stored descriptions
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the list of descriptions
	 */
	public List<Description> getDescriptionsPage(int offset, int max) 
			throws NotAuthorizedException;
	
	/**
	 * Return all the descriptions contained in a given store
	 * @param storeName The name of the store whose descriptions want to be retrieved
	 * @return All the descriptions contained in the given store
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws NotAuthorizedException IF the current user is not authorized to retrieve the list of descriptions
	 */
	public List<Description> getStoreDescriptions(String storeName) 
			throws StoreNotFoundException, NotAuthorizedException;
	
	/**
	 * Returns a sublist of all the descriptions contained in a given store
	 * @param storeName The name of the store whose descriptions want to be retrieved
	 * @param offset The first description to be retrieved
	 * @param max The max number of descriptions to be returned
	 * @return A sublist of all the descriptions contained in the given store
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws NotAuthorizedException IF the current user is not authorized to retrieve the list of descriptions
	 */
	public List<Description> getStoreDescriptionsPage(String storeName, 
			int offset, int max) throws StoreNotFoundException, NotAuthorizedException;

    /**
     * Returns the list of descriptions created by a given user in a given store
     * @param userName The user that created the descriptions
     * @param storeName The name of the store whose descriptions want to be retrieved
     * @return The list of descriptions created by the given user in the given store
     * @throws UserNotFoundException If it does not exist a user with the given name
     * @throws StoreNotFoundException If it does not exist a store with the given name
     */
    public List<Description> getUserDescriptionsInStore(String userName, String storeName)
            throws UserNotFoundException, StoreNotFoundException;
    
    
	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// EXTRA //////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieves all the descriptions stored in the database and update all their offerings
     * This method <strong>MUST NOT</strong> be exposed as an API. It is just for internal usage.
     */
    public void updateAllDescriptions();

}
