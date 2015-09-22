package org.fiware.apps.marketplace.dao;

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

import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.User;

public interface UserDao {
	
	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// CRUD ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new user in the database
	 * @param user The user to be created
	 */
	public void save(User user);
	
	/**
	 * Updates an existing user
	 * @param user The user to be updated
	 */
	public void update(User user);
	
	/**
	 * Deletes an existing user
	 * @param user The user to be deleted
	 */
	public void delete(User user);
	
	/**
	 * Returns a user based on its user name
	 * @param userName The user name of the user to be returned
	 * @return The user with the given user name
	 * @throws UserNotFoundException If it does not exist a user with the given user name
	 */
	public User findByName(String userName) throws UserNotFoundException;
	
	/**
	 * Returns a user based on its email
	 * @param email The email of the user to be returned
	 * @return The user with the given email
	 * @throws UserNotFoundException If it does not exist a user with the given email
	 */
	public User findByEmail(String email) throws UserNotFoundException;


	////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// VERIFICATIONS ///////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Checks if a user name is in use
	 * @param userName The user name to be checked
	 * @return true if the given user name is not in use. false otherwise
	 */
	public boolean isUserNameAvailable(String userName);
	
	/**
	 * Checks if an email is in use
	 * @param email The email to be checked
	 * @return true if the given email is not in use. false otherwise
	 */
    public boolean isEmailAvailable(String email);

    
	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// LIST ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	
    /**
     * Returns all the users stored in the database
     * @return All the users stored in the database
     */
    public List<User> getAllUsers();
    
    /**
     * Returns a sublist of all the users stored in the database
     * @param offset The first user to be retrieved
     * @param max The max number of users to be returned
     * @return A sublist of all the users stored in the database
     */
    public List<User> getUsersPage(int offset, int max);
	
	
}
