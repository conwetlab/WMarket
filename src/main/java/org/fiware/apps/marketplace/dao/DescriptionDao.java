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


	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// CRUD ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	// public void save(Description description);

	/**
	 * Updates an existing description
	 * @param description The updated description
	 */
	public void update(Description description);

	// public void delete(Description description);

	/**
	 * Returns a description based on its name and the store that contains it
	 * @param storeName The name of the store that contains the description to be retrieved
	 * @param descriptionName The name of the description to be retrieved
	 * @return A description contained in the given store and with the provided name
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws DescriptionNotFoundException If it does not exist a description with the given name
	 */
	public Description findByNameAndStore(String storeName, String descriptionName) 
			throws StoreNotFoundException, DescriptionNotFoundException;

	/**
	 * Returns a description given its ID
	 * @param id The ID of the description to be retrieved
	 * @return The description with the given ID
	 * @throws DescriptionNotFoundException If it does not exist a description with the given ID
	 */
	public Description findById(Integer id) throws DescriptionNotFoundException;


	////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// VERIFICATIONS ///////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Checks if there are another description in the given store with the same name
	 * @param storeName The name of the store
	 * @param descriptionName The name of the description to be checked
	 * @return true if there is another description in the given store with the same name. False otherwise
	 */
	public boolean isNameAvailableInStore(String storeName, String descriptionName);

	/**
	 * Checks if there are another description in the given store with the same display name
	 * @param storeName The name of the store
	 * @param displayName The display name of the description to be checked
	 * @return true if there is another description in the given store with the same display name. False otherwise
	 */
	public boolean isDisplayNameAvailableInStore(String storeName, String displayName);

	/**
	 * Checks if there are another description in the given store with the same URL
	 * @param storeName The name of the store
	 * @param url The URL of the description to be checked
	 * @return true if there is another description in the given store with the same URL. False otherwise
	 */
	public boolean isURLAvailableInStore(String storeName, String url);


	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// LIST ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the descriptions created by a given user
	 * @param userName The user name of the user whose descriptions want to be retrieved
	 * @return The list of descriptions created by the given user
	 * @throws UserNotFoundException If it does not exist a user with the provided user name
	 */
	public List<Description> getUserDescriptions(String userName) 
			throws UserNotFoundException;

	/**
	 * Returns all the stored descriptions 
	 * @return All the stored description
	 */
	public List<Description> getAllDescriptions();

	/**
	 * Returns a sublist of descriptions
	 * @param offset The first description to be retrieved
	 * @param max The max number of descriptions to be returned
	 * @return A sublist of descriptions based on the provided offset and max
	 */
	public List<Description> getDescriptionsPage(int offset, int max);

	/**
	 * Returns all the descriptions contained in a store
	 * @param storeName The name of the store that contains the descriptions to be retrieved
	 * @return All the descriptions contained in the store with the provided name
	 * @throws StoreNotFoundException If it does not exist a store with the provided name
	 */
	public List<Description> getStoreDescriptions(String storeName) 
			throws StoreNotFoundException;

	/**
	 * Returns a sublist of all the descriptions contained in the a store
	 * @param storeName The name of the store that contains the descriptions to be retrieved
	 * @param offset The first description to be retrieved
	 * @param max The max number of descriptions to be returned
	 * @return A sublist of all the descriptions contained in the store defined by the provided name
	 * @throws StoreNotFoundException If it does not exist a store with the provided name
	 */
	public List<Description> getStoreDescriptionsPage(String storeName, 
			int offset, int max) throws StoreNotFoundException;

	/**
	 * Returns the current list of descriptions created by a user in one specific store
	 * @param userName The user name of the user whose descriptions want to be retrieved
	 * @param storeName The name of the store that contains the descriptions to be retrieved
	 * @throws UserNotFoundException If there is not a User with the userName given
	 * @throws StoreNotFoundException If there is not a Store with the storeName given
	 * @return The list of descriptions.
	 */
	public List<Description> getUserDescriptionsInStore(String userName, String storeName)
			throws UserNotFoundException, StoreNotFoundException;

}
