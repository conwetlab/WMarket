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

import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.ReviewNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Review;
import org.fiware.apps.marketplace.model.Store;


public interface StoreBo {

	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// CRUD ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new store in the database
	 * @param store The store to be created
	 * @throws NotAuthorizedException If the current user is not authorized to create stores
	 * @throws ValidationException If the given store is not valid
	 */
	public void save(Store store) throws NotAuthorizedException, ValidationException;

	/**
	 * Updates an existing store
	 * @param name The name of the store to be updated
	 * @param updatedStore The updated store
	 * @throws NotAuthorizedException If the current user is not authorized to update the given store
	 * @throws ValidationException If the given updated store is not valid
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 */
	public void update(String name, Store updatedStore) 
			throws NotAuthorizedException, ValidationException, StoreNotFoundException;

	/**
	 * Deletes an existing store
	 * @param storeName The name of the store to be deleted
	 * @throws NotAuthorizedException If the current user is not authorized to delete the given store
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 */
	public void delete(String storeName) throws NotAuthorizedException, StoreNotFoundException;

	/**
	 * Returns a store based on its name
	 * @param name The name of the store to be retrieved
	 * @return The store with the given name
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the store with the given name
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 */
	public Store findByName(String name) 
			throws NotAuthorizedException, StoreNotFoundException;


	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// LIST ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all the stores contained in the database
	 * @return All the stores contained in the database
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the list of existing stores
	 */
	public List <Store> getAllStores() throws NotAuthorizedException;

	/**
	 * Returns a sublist of all the stores contained in the database
	 * @param offset The first store to be retrieved
	 * @param max The max number of stores to be returned
	 * @param orderBy The field that will be used to order the returned stores
	 * @param desc true to sort results in reverse order
	 * @return A sublist of all the stores contained in the database
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the list of existing stores
	 */
	public List<Store> getStoresPage(int offset, int max, String orderBy, boolean desc)
			throws NotAuthorizedException;


	////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// REVIEWS /////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a store review
	 * @param storeName The name of the store to be reviewed
	 * @param review The review itself
	 * @throws NotAuthorizedException If the current user is not authorized to review the store
	 * @throws StoreNotFoundException If it does not exit a store with the given name
	 * @throws ValidationException If the given review is not valid
	 */
	public void createReview(String storeName, Review review) 
			throws NotAuthorizedException, StoreNotFoundException, ValidationException;

	/**
	 * Updates a store review
	 * @param storeName The name of the store whose review is going to be updated
	 * @param reviewId The ID of the review that is going to be updated
	 * @param review The updated review
	 * @throws NotAuthorizedException If the current user is not authorized to update the review
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws ReviewNotFoundException If it does not exist a review with the given ID for the given store
	 * @throws ValidationException If the updated review is not valid
	 */
	public void updateReview(String storeName, int reviewId, Review review) 
			throws NotAuthorizedException, StoreNotFoundException, ReviewNotFoundException, ValidationException;

	/**
	 * Returns a specific store review
	 * @param storeName The name of the store whose review is going to be retrieved
	 * @param reviewId The ID of the review that is going to be returned
	 * @return The store review with the given ID
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the review
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws ReviewNotFoundException If it does not exist a review with the given ID for the given store
	 */
	public Review getReview(String storeName, int reviewId) 
			throws NotAuthorizedException, StoreNotFoundException, ReviewNotFoundException;

	/**
	 * Deletes a store review
	 * @param storeName The name of the store whose review is going to be deleted
	 * @param reviewId The ID of the review that is going to be deleted
	 * @throws NotAuthorizedException If the current user is not authorized to delete the review
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws ReviewNotFoundException If it does not exist a review with the given ID for the given store
	 */
	public void deleteReview(String storeName, int reviewId) 
			throws NotAuthorizedException, StoreNotFoundException, ReviewNotFoundException;

	/**
	 * Returns all the reviews of a given store
	 * @param storeName The name of the store whose reviews want to be retrieved
	 * @return All the reviews of the given store
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the reviews of the given store
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 */
	public List<Review> getReviews(String storeName) throws NotAuthorizedException, StoreNotFoundException;

	/**
	 * Returns a sublist of all the reviews of a given store
	 * @param storeName The name of the store whose reviews want to be retrieved
	 * @param offset The first review to be retrieved
	 * @param max The max number of reviews to be returned
	 * @param orderBy The field that will be used to order the returned reviews
	 * @param desc true to sort results in reverse order
	 * @return A sublist of all the reviews of the given store
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the reviews of the given store
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 */
	public List<Review> getReviewsPage(String storeName, int offset, int max, String orderBy, boolean desc) 
			throws NotAuthorizedException, StoreNotFoundException;

}
