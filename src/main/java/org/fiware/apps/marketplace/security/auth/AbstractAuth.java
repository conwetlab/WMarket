package org.fiware.apps.marketplace.security.auth;

/*
 * #%L
 * FiwareMarketplace
 * %%
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

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.User;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractAuth<T> {
	
	@Autowired
	private UserBo userBo;
	
	private String getActionForException(T entity, String action) {
		return action + " " + entity.getClass().getSimpleName().toLowerCase() 
				+ " " + genEntityName(entity);
	}
	
	/**
	 * Returns the name of an entity. It's useful to return a more detailed
	 * error message
	 * @param entity The entity
	 * @return The name of the entity
	 */
	protected abstract String genEntityName(T entity);
	
	/**
	 * Method to return the Localuser who is owner of the entity
	 * @param entity The entity whose owner the developer wants to know
	 * @return The owner of the entity
	 */
	protected abstract User getEntityOwner(T entity);
	
	/**
	 * Method to know if the logged user is the owner of the entity
	 * @param entity The entity to check. 
	 * @param action The action that is being performed
	 * @throws NotAuthorizedException if the user is not authorized to perform the action
	 */
	protected void isOwner(T entity, String action) throws NotAuthorizedException {
		boolean canAccess = false;
		User loggedUser = null;

		try {
			loggedUser = userBo.getCurrentUser();
			// logged User can be null if the user is not logged in...
			if (this.getEntityOwner(entity).equals(loggedUser)) {
				canAccess = true;
			}
		} catch (UserNotFoundException ex) {
			// Nothing to do... False will be returned
		}
		
		if (!canAccess) {
			throw new NotAuthorizedException(loggedUser, getActionForException(entity, action));
		}		
	}
	
	/**
	 * Method to know if the user is logged in
	 * @param entity The entity to check.
	 * @throws NotAuthorizedException if the user is not logged in
	 */
	protected void isLoggedIn(T entity, String action) throws NotAuthorizedException {
		User loggedUser = null;

		try {
			loggedUser = userBo.getCurrentUser();
		} catch (UserNotFoundException ex) {
			// Nothing to do
		}
		
		if (loggedUser == null) {
			throw new NotAuthorizedException(loggedUser, getActionForException(entity, action));
		}
	}
	
	/**
	 * @return By default it returns True if the user is logged in
	 * @throws NotAuthorizedException if the user is not authorized to create the entity
	 */
	public void canCreate(T entity) throws NotAuthorizedException {
		this.isLoggedIn(entity, "create");
	}

	/**
	 * @param entity The entity that is going to be updated
	 * @throws NotAuthorizedException if the user is not authorized to update the entity
	 */
	public void canUpdate(T entity) throws NotAuthorizedException {
		this.isOwner(entity, "update");
	}

	/**
	 * @param entity The entity that is going to be deleted
	 * @throws NotAuthorizedException if the user is not authorized to delete the entity
	 */
	public void canDelete(T entity) throws NotAuthorizedException {
		this.isOwner(entity, "delete");
	}

	/**
	 * @param entity The entity that is going to be got
	 * @throws NotAuthorizedException if the user is not authorized to get the entity
	 */
	public void canGet(T entity) throws NotAuthorizedException {

	}

	/**
	 * @return By default, it returns True
	 * @throws NotAuthorizedException if the user is not authorized to list the entities
	 */
	public void canList() throws NotAuthorizedException {

	}


}
