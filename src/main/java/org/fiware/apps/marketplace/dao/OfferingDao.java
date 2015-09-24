package org.fiware.apps.marketplace.dao;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2015 CoNWeT Lab, Universidad Politécnica de Madrid
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
import org.fiware.apps.marketplace.exceptions.OfferingNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Offering;

public interface OfferingDao {
	
	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// CRUD ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new offering in the data base
	 * @param offering The offering to be created
	 */
	public void save(Offering offering);
	
	/**
	 * Updates an existing offering
	 * @param offering The updated offering
	 */
	public void update(Offering offering);
	
	/**
	 * Deletes an existing offering
	 * @param offering The offering to be deleted
	 */
	public void delete(Offering offering);
	
	/**
	 * Returns an offering based on its name and the description that contains it (a description is defined by
	 * its name and the store that contains it)
	 * @param storeName The name of the store that contains the description
	 * @param descriptionName The name of the description that contains the offering
	 * @param offeringName The name of the offering to be retrieved
	 * @return An offering with the given name contained in the provided description (a description is defined
	 * by its name and the store that contains it)
	 * @throws StoreNotFoundException It it does not exist a store with the provided name
	 * @throws DescriptionNotFoundException If it does not exist a description in the given store with the 
	 * provided name
	 * @throws OfferingNotFoundException If it does not exist an offering in the given description with the 
	 * provided name
	 */
	public Offering findByNameStoreAndDescription(String storeName, String descriptionName, 
			String offeringName) throws StoreNotFoundException, DescriptionNotFoundException, 
			OfferingNotFoundException;
	
	
	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// LIST ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	
	// public List<Offering> getAllOfferings();
	
	/**
	 * Returns a sublist of all the offerings stored in the database 
	 * @param offset The first offering to be retrieved
	 * @param max The max number of offerings to be returned
	 * @param orderBy The field that will be used to order the returned offerings
	 * @param desc true to sort results in reverse order
	 * @return A sublist of all the offerings stored in the database
	 */
	public List<Offering> getOfferingsPage(int offset, int max, String orderBy, boolean desc);
	
	// public List<Offering> getAllStoreOfferings(String storeName) 
	//	 	throws StoreNotFoundException;
	
	/**
	 * Returns a sublist of all the offerings contained in a given store
	 * @param storeName The name of the store that contains the offerings to be retrieved
	 * @param offset The first offering to be retrieved
	 * @param max The max number of offerings to be returned
	 * @param orderBy The field that will be used to order the returned offerings
	 * @param desc true to sort results in reverse order
	 * @return A sublist of all the offerings contained in the given store
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 */
	public List<Offering> getStoreOfferingsPage(String storeName, int offset, int max, String orderBy, boolean desc) 
			throws StoreNotFoundException;
	
	// public List<Offering> getAllDescriptionOfferings(String storeName, String descriptionName)
	// 		throws StoreNotFoundException, DescriptionNotFoundException;
	
	/**
	 * Returns a sublist of all the offerings contained in a given description
	 * @param storeName The name of the store that contains the description
	 * @param descriptionName The name of the description whose offerings want to be retrieved
	 * @param offset The first offering to be retrieved
	 * @param max The max number of offerings to be returned
	 * @param orderBy The field that will be used to order the returned offerings
	 * @param desc true to sort results in reverse order
	 * @return A sublist of all the offerings contained in the given description 
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws DescriptionNotFoundException If it does not exist a description in the given store with the 
	 * provided name
	 */
	public List<Offering> getDescriptionOfferingsPage(String storeName, String descriptionName, 
			int offset, int max, String orderBy, boolean desc) throws StoreNotFoundException, 
			DescriptionNotFoundException;
	
	/**
	 * Returns a sublist of all the offerings bookmarked by a given user
	 * @param userName The name of the user which bookmarked the offerings
	 * @param offset The first offering to be retrieved
	 * @param max The max number of offerings to be returned
	 * @param orderBy The field that will be used to order the returned offerings
	 * @param desc true to sort results in reverse order
	 * @return A sublist of all the offerings bookmarked by the given user
	 * @throws UserNotFoundException If it does not exist a user with the given user name
	 */
	public List<Offering> getBookmarkedOfferingsPage(String userName, int offset, int max, String orderBy, 
			boolean desc) throws UserNotFoundException;

}
