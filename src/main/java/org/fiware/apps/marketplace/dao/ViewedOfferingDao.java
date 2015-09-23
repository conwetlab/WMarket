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

import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.ViewedOffering;

public interface ViewedOfferingDao {
	
	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// CRUD ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a viewed offering in the database
	 * @param viewedOffering The viewed offering to be created
	 */
	public void save(ViewedOffering viewedOffering);
	
	/**
	 * Updates an existing viewed offering 
	 * @param viewedOffering The updated viewed offering
	 */
	public void update(ViewedOffering viewedOffering);
	
	/**
	 * Deletes an existing viewed offering
	 * @param viewedOffering The viewed offering to be deleted
	 */
	public void delete(ViewedOffering viewedOffering);
	
	
	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// LIST ///////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all the offerings viewed by a given user
	 * @param userName The user whose viewed offerings want to be retrieved
	 * @return All the offering viewed by the given user
	 * @throws UserNotFoundException If it does not exist a user with the given user name
	 */
	public List<ViewedOffering> getUserViewedOfferings(String userName) throws UserNotFoundException;
	
	/**
	 * Returns a sublist of all the offerings viewed by a given user
	 * @param userName The user whose viewed offerings want to be retrieved
	 * @param offset The first viewed offering to be retrieved
	 * @param max The max number of viewed offerings to be returned
	 * @return A sublist of all the offerings viewed by a given user
	 * @throws UserNotFoundException If it does not exist a user with the given user name
	 */
	public List<ViewedOffering> getUserViewedOfferingsPage(String userName, int offset, int max) 
			throws UserNotFoundException;
	
	/**
	 * Returns a sublist of all the offerings viewed by other users (ordered by view date)
	 * @param userName The user to be excluded
	 * @param max The max number of offerings to be returned
	 * @return A sublist of all the offerings viewed by other users
	 * @throws UserNotFoundException If it does not exist a user with the given user name
	 */
	public List<ViewedOffering> getOfferingsViewedByOtherUsers(String userName, int max) throws UserNotFoundException;

}
