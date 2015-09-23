package org.fiware.apps.marketplace.bo;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
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
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.OfferingNotFoundException;
import org.fiware.apps.marketplace.exceptions.ReviewNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.Review;

public interface OfferingBo {
	
	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// CRUD ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an offering 
	 * @param offering The offering to be created
	 * @throws NotAuthorizedException If the current user is not authorized to create the offering
	 */
	public void save(Offering offering) throws NotAuthorizedException;
	
	/**
	 * Updates an existing offering
	 * @param offering The updated offering 
	 * @throws NotAuthorizedException If the current user is not authorized to update the offering
	 */
	public void update(Offering offering) throws NotAuthorizedException;
	
	/**
	 * Deletes an existing offering
	 * @param offering The offering to be deleted
	 * @throws NotAuthorizedException If the current user is not authorized to delete the offering
	 */
	public void delete(Offering offering) throws NotAuthorizedException;
	
	/**
	 * Returns an offering based on its URI
	 * @param uri The URI of the offering to be retrieved
	 * @return The offering with the given URI
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the offering
	 */
	public Offering findByUri(String uri) throws NotAuthorizedException;
	
	/**
	 * Returns an offering based on its name and the description that contains it (a description is defined by
	 * its name and the store that contains it)
	 * @param storeName The name of the store that contains the description
	 * @param descriptionName The name of the description that contains the offering
	 * @param offeringName The name of the offering to be retrieved
	 * @return The offering with the given name contained in the provided description
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the offering
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws DescriptionNotFoundException If it does not exist a description with the given name in the provided
	 * store
	 * @throws OfferingNotFoundException If it does not exit an offering with the given name in the provided 
	 * description
	 */
	public Offering findOfferingByNameStoreAndDescription(String storeName, 
			String descriptionName, String offeringName) throws NotAuthorizedException,
			StoreNotFoundException, DescriptionNotFoundException, OfferingNotFoundException;
	
	
	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// LIST ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	
	// public List<Offering> getAllOfferings() throws NotAuthorizedException;
	
	/**
	 * Returns a sublist of all the offerings stored in the database
	 * @param offset The first offering to be retrieved
	 * @param max The max number of offerings to be returned
	 * @param orderBy The field that will be used to order the returned offerings
	 * @param desc true to sort results in reverse order
	 * @return A sublist of all the offerings stored in the database
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the list of offerings
	 */
	public List<Offering> getOfferingsPage(int offset, int max, String orderBy, boolean desc) 
			throws NotAuthorizedException;
	
	// public List<Offering> getAllStoreOfferings(String storeName) 
	// 		throws StoreNotFoundException, NotAuthorizedException;
	
	/**
	 * Returns a sublist of all the offerings contained in a given store
	 * @param storeName The name of the store that contains the offerings to be retrieved
	 * @param offset The first offering to be retrieved
	 * @param max The max number of offerings to be returned
	 * @param orderBy The field that will be used to order the returned offerings
	 * @param desc true to sort results in reverse order
	 * @return A sublist of all the offerings contained in the given store
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the list of offerings
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 */
	public List<Offering> getStoreOfferingsPage(String storeName, int offset, int max, String orderBy, boolean desc) 
			throws NotAuthorizedException, StoreNotFoundException;
	
	// public List<Offering> getAllDescriptionOfferings(String storeName, String descriptionName) 
	// 		throws NotAuthorizedException, StoreNotFoundException, DescriptionNotFoundException;
	
	/**
	 * Returns a sublist of all the offerings contained in a given description
	 * @param storeName The name of the store that contains the description
	 * @param descriptionName The name of the description that contains the offerings to be retrieved
	 * @param offset The first offering to be retrieved
	 * @param max The max number of offerings to be returned
	 * @param orderBy The field that will be used to order the returned offerings
	 * @param desc true to sort results in reverse order
	 * @return A sublist of all the offerings contained in the given description 
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the list of offerings
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws DescriptionNotFoundException If it does not exist a description with the given name in the provided
	 * store
	 */
	public List<Offering> getDescriptionOfferingsPage(String storeName, String descriptionName, 
			int offset, int max, String orderBy, boolean desc) throws NotAuthorizedException, StoreNotFoundException, 
			DescriptionNotFoundException;	
	

	////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// BOOKMARKING ///////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Swaps the bookmarking state of an offering for a given user:
	 * <ul>
	 * <li>If the offering has been bookmarked by the user, the offering will be unbookmarked</li>
	 * <li>If the offering has not been bookmarked by the user, the offering will be bookmarked</li>
	 * </ul>
	 * @param storeName The name of the store that contains the description
	 * @param descriptionName The name of the description that contains the offering
	 * @param offeringName The name of the offering to be (un)bookmarked
	 * @throws NotAuthorizedException If the current user is not authorized to (un)bookmark the offering
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws DescriptionNotFoundException If it does not exist a description with the given name in the provided
	 * store
	 * @throws OfferingNotFoundException If it does not exit an offering with the given name in the provided 
	 * description
	 */
	public void bookmark(String storeName, String descriptionName, String offeringName) 
			throws NotAuthorizedException, StoreNotFoundException, DescriptionNotFoundException,
			OfferingNotFoundException;
	
	/**
	 * Return all the offerings bookmarked by the current user
	 * @return All the offerings bookmarked by the current user
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the list of bookmaked offerings
	 */
	public List<Offering> getAllBookmarkedOfferings() throws NotAuthorizedException;
	
	/**
	 * Return a sublist of all the offerings bookmarked by the current user
	 * @param offset The first offering to be retrieved
	 * @param max The max number of offerings to be returned
	 * @param orderBy The field that will be used to order the returned offerings
	 * @param desc true to sort results in reverse order
	 * @return A sublist of all the offerings bookmarked by the current user
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the list of bookmaked offerings
	 */
	public List<Offering> getBookmarkedOfferingsPage(int offset, int max, String orderBy, boolean desc) 
			throws NotAuthorizedException;
	

	////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////// VIEWED OFFERIGNS /////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a sublist of all the offerings viewed by the current user
	 * @param offset The first offering to be retrieved
	 * @param max The max number of offerings to be returned
	 * @return A sublist of all the offerings viewed by the current user
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the list of offering viewed
	 * by themselves
	 */
	public List<Offering> getLastViewedOfferingsPage(int offset, int max) throws NotAuthorizedException;
	
	/**
	 * Returns a sublist of all the offerings viewed by other users (ordered by view date)
	 * @param max The max number of offerings to be returned
	 * @return A sublist of all the offerings viewed by other users
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the list of offerings viewed
	 * by other users
	 */
	public List<Offering> getOfferingsViewedByOtherUsers(int max) throws NotAuthorizedException;
	
	
	////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// REVIEWS /////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an offering review
	 * @param storeName The name of the store that contains the description
	 * @param descriptionName The name of the description that contains the offering
	 * @param offeringName The name of the offering to be reviewed
	 * @param review The review itself
	 * @throws NotAuthorizedException If the current user is not authorized to review the offering
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws DescriptionNotFoundException If it does not exist a description with the given name in the provided
	 * store
	 * @throws OfferingNotFoundException If it does not exit an offering with the given name in the provided 
	 * description
	 * @throws ValidationException If the given review is not valid
	 */
	public void createReview(String storeName, String descriptionName, String offeringName, Review review) 
			throws NotAuthorizedException, StoreNotFoundException,
			DescriptionNotFoundException, OfferingNotFoundException, ValidationException;
	
	/**
	 * Updates an existing offering review
	 * @param storeName The name of the store that contains the description
	 * @param descriptionName The name of the description that contains the offering
	 * @param offeringName The name of the offering whose review is going to be updated
	 * @param reviewId The ID of the review that is going to be updated
	 * @param review The updated review
	 * @throws NotAuthorizedException If the current user is not authorized to update the review
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws DescriptionNotFoundException If it does not exist a description with the given name in the provided
	 * store
	 * @throws OfferingNotFoundException If it does not exit an offering with the given name in the provided 
	 * description
	 * @throws ReviewNotFoundException If it does not exist a review with the given ID for the given offering
	 * @throws ValidationException If the updated review is not valid
	 */
	public void updateReview(String storeName, String descriptionName, String offeringName, int reviewId, 
			Review review) throws NotAuthorizedException, OfferingNotFoundException, StoreNotFoundException,
			DescriptionNotFoundException, ReviewNotFoundException, ValidationException;	
	
	/**
	 * Returns an existing offering review
	 * @param storeName The name of the store that contains the description
	 * @param descriptionName The name of the description that contains the offering
	 * @param offeringName The name of the offering whose review is going to be retrieved
	 * @param reviewId The ID of the review that is going to be returned
	 * @return The offering review with the given ID
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the review
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws DescriptionNotFoundException If it does not exist a description with the given name in the provided
	 * store
	 * @throws OfferingNotFoundException If it does not exit an offering with the given name in the provided 
	 * description
	 * @throws ReviewNotFoundException If it does not exist a review with the given ID for the given offering
	 */
	public Review getReview(String storeName, String descriptionName, String offeringName, int reviewId) throws 
			NotAuthorizedException, StoreNotFoundException, DescriptionNotFoundException, OfferingNotFoundException,
			ReviewNotFoundException;
	
	/**
	 * Deletes an existing offering review
	 * @param storeName The name of the store that contains the description
	 * @param descriptionName The name of the description that contains the offering
	 * @param offeringName The name of the offering whose review is going to be deleted
	 * @param reviewId The ID of the review that is going to be returned
	 * @throws NotAuthorizedException If the current user is not authorized to delete the review
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws DescriptionNotFoundException If it does not exist a description with the given name in the provided
	 * store
	 * @throws OfferingNotFoundException If it does not exit an offering with the given name in the provided 
	 * description
	 * @throws ReviewNotFoundException If it does not exist a review with the given ID for the given offering
	 */
	public void deleteReview(String storeName, String descriptionName, String offeringName, int reviewId) 
			throws NotAuthorizedException, StoreNotFoundException, DescriptionNotFoundException, 
			OfferingNotFoundException, ReviewNotFoundException;
	
	/**
	 * Returns all the reviews of a given offering
	 * @param storeName The name of the store that contains the description
	 * @param descriptionName The name of the description that contains the offering
	 * @param offeringName The name of the offering whose reviews want to be retrieved
	 * @return All the reviews of the given offering
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the reviews of the given 
	 * offering
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws DescriptionNotFoundException If it does not exist a description with the given name in the provided
	 * store
	 * @throws OfferingNotFoundException If it does not exit an offering with the given name in the provided 
	 * description
	 */
	public List<Review> getReviews(String storeName, String descriptionName, String offeringName) throws 
			NotAuthorizedException, StoreNotFoundException, DescriptionNotFoundException, OfferingNotFoundException;
	

	/**
	 * Returns a sublist of all the reviews of a given offering
	 * @param storeName The name of the store that contains the description
	 * @param descriptionName The name of the description that contains the offering
	 * @param offeringName The name of the offering whose reviews want to be retrieved
	 * @param offset The first review to be retrieved
	 * @param max The max number of reviews to be returned
	 * @param orderBy The field that will be used to order the returned reviews
	 * @param desc true to sort results in reverse order
	 * @return A sublist of all the reviews of the given offering
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the reviews of the given 
	 * offering
	 * @throws StoreNotFoundException If it does not exist a store with the given name
	 * @throws DescriptionNotFoundException If it does not exist a description with the given name in the provided
	 * store
	 * @throws OfferingNotFoundException If it does not exit an offering with the given name in the provided 
	 * description
	 */
	public List<Review> getReviewsPage(String storeName, String descriptionName, String offeringName, 
			int offset, int max, String orderBy, boolean desc) throws NotAuthorizedException, 
			StoreNotFoundException, DescriptionNotFoundException, OfferingNotFoundException;
	
}
