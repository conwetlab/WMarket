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
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.User;

public interface UserBo {

	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// CRUD ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new user in the data base
	 * @param user The new user to be created
	 * @throws NotAuthorizedException If the current user is not authorized to create a new user
	 * @throws ValidationException If the given user is invalid
	 */
	public void save(User user) throws NotAuthorizedException, ValidationException;
	
	/**
	 * Updates an existing user
	 * @param userName The name of the user to be updated
	 * @param updatedUser The updated user
	 * @throws NotAuthorizedException If the current user is not authorized to update the given user
	 * @throws ValidationException If the updated user is invalid
	 * @throws UserNotFoundException If it does not exist a user with the given user name
	 */
	public void update(String userName, User updatedUser) throws NotAuthorizedException,
			ValidationException, UserNotFoundException;
	
	/**
	 * Deletes an existing user
	 * @param userName The name of the user to be deleted
	 * @throws NotAuthorizedException If the current user is not authorized to delete the given user
	 * @throws UserNotFoundException If it does not exist a user with the given user name
	 */
	public void delete(String userName) throws NotAuthorizedException,
			UserNotFoundException;
	
	/**
	 * Swaps the provider status of a user:
	 * <ul>
	 * <li>If the user was a provider, they will become a consumer</li>
	 * <li>If the user was a consumer, they will become a provider</li>
	 * </ul>
	 * @param userName The name of the user whose provider status must be updated
	 * @throws NotAuthorizedException If the current user is not authorized to change the provider status of the
	 * given user
	 * @throws UserNotFoundException If it does not exist a user with the given user name
	 */
	public void changeProviderStatus(String userName) throws NotAuthorizedException, 
			UserNotFoundException;
	
	/**
	 * Returns a user based on its user name
	 * @param userName The user name of the user to be returned
	 * @return The user with the given user name
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the user
	 * @throws UserNotFoundException If it does not exist a user with the given user name
	 */
	public User findByName(String userName) throws NotAuthorizedException, 
			UserNotFoundException;
	
	/**
	 * Returns a user based on its email
	 * @param email The email of the user to be returned
	 * @return The user with the given email
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the user
	 * @throws UserNotFoundException If it does not exist a user with the given email
	 */
	public User findByEmail(String email) throws NotAuthorizedException,
			UserNotFoundException;

	
	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// LIST ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	
	/**
     * Returns all the users stored in the database
     * @return All the users stored in the database
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the list of existing users
	 */
	public List<User> getAllUsers() throws NotAuthorizedException;
	
	/**
     * Returns a sublist of all the users stored in the database
     * @param offset The first user to be retrieved
     * @param max The max number of users to be returned
     * @return A sublist of all the users stored in the database
	 * @throws NotAuthorizedException If the current user is not authorized to retrieve the list of existing users
	 */
	public List<User> getUsersPage(int offset, int max) throws NotAuthorizedException;

	
	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// EXTRA //////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the user that have performed the HTTP request
	 * @return The user that have performed the request
	 * @throws UserNotFoundException If the current user is not registered in the database (it should never happen)
	 */
	public User getCurrentUser() throws UserNotFoundException;
	
	
	////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// VERIFICATIONS ///////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Checks if the current user's password matches with the given one
	 * @param password The password to be checked
	 * @return true if the given password matches with the current user's password. false otherwise
	 * @throws UserNotFoundException If the current user is not registered in the database (it should never happen)
	 */
	public boolean checkCurrentUserPassword(String password) throws UserNotFoundException;

}
