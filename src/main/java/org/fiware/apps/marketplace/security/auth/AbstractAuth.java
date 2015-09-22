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
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.User;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractAuth<T> {
	
	@Autowired private UserBo userBo;
	
	protected UserBo getUserBo() {
		return userBo;
	}
	
	/**
	 * Method to return the User who is owner of the entity
	 * @param entity The entity whose owner the developer wants to know
	 * @return The owner of the entity
	 */
	protected abstract User getEntityOwner(T entity);
	
	/**
	 * Method to know if the logged user is the owner of the entity
	 * @param entity The entity to check. 
	 * @return true if the logged user is the owner of the entity. False otherwise.
	 */
	protected boolean isOwner(T entity) {
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
		
		return canAccess;		
	}
	
	/**
	 * Method to know if the user is logged in
	 * @param entity The entity to check.
	 * @return true if the user is logged in. False otherwise.
	 */
	protected boolean isLoggedIn(T entity)  {
		User loggedUser = null;

		try {
			loggedUser = userBo.getCurrentUser();
		} catch (UserNotFoundException ex) {
			// Nothing to do
		}
		
		return loggedUser != null;
	}
	
	/**
	 * @param entity The entity that is going to be created
	 * @return true if the user is allowed to create a entity. False otherwise.
	 */
	public boolean canCreate(T entity) {
		return this.isLoggedIn(entity);
	}

	/**
	 * @param entity The entity that is going to be updated
	 * @return true if the user is allowed to update a entity. False otherwise.
	 */
	public boolean  canUpdate(T entity) {
		return this.isOwner(entity);
	}

	/**
	 * @param entity The entity that is going to be deleted
	 * @return true if the user is allowed to delete a entity. False otherwise.
	 */
	public boolean canDelete(T entity) {
		return this.isOwner(entity);
	}

	/**
	 * @param entity The entity that is going to be got
	 * @return true if the user is allowed to retrieve an entity. False otherwise.
	 */
	public boolean canGet(T entity) {
		return true;
	}

	/**
	 * @return By default, it returns True
	 * @return true if the user is allowed to list the entities. False otherwise.
	 */
	public boolean canList() {
		return true;
	}


}
