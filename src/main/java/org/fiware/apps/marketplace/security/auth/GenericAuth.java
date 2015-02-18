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

import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.User;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class GenericAuth<T> {
	
	@Autowired
	private AuthUtils authUtils;
	
	/**
	 * Method to return the Localuser who is owner of the entity
	 * @param entity The entity whose owner the developer wants to know
	 * @return The owner of the entity
	 */
	protected abstract User getEntityOwner(T entity);
	
	/**
	 * Method to know if the logged user is the owner of the entity
	 * @param entity The entity to check. 
	 * @return True if the logged user is the owner of the entity. False otherwise
	 */
	protected boolean isLoggedUserTheOwner(T entity) {
		boolean canAccess = false;

		try {
			User loggedUser = authUtils.getLoggedUser();
			// logged User can be null if the user is not logged in...
			if (loggedUser != null && loggedUser.equals(this.getEntityOwner(entity))) {
				canAccess = true;
			}
		} catch (UserNotFoundException ex) {
			// Nothing to do... False will be returned
		}

		return canAccess;
		
	}
	
	/**
	 * Method to know if the user is logged in
	 * @return True if the user is logged in. Otherwise, it will return False
	 */
	protected boolean isLoggedIn() {
		boolean isLoggedIn = false;

		try {
			isLoggedIn = authUtils.getLoggedUser() != null;
		} catch (UserNotFoundException ex) {
			//Nothing to do... False will be returned
		}

		return isLoggedIn;
	}
	
	/**
	 * @return By default it returns True if the user is logged in
	 */
	public boolean canCreate() {
		return this.isLoggedIn();
	}

	/**
	 * @param entity The entity that is going to be updated
	 * @return By default it returns True if the logged user is the owner of the entity
	 */
	public boolean canUpdate(T entity) {
		return this.isLoggedUserTheOwner(entity);
	}

	/**
	 * @param entity The entity that is going to be deleted
	 * @return By default it returns True if the logged user is the owner of the entity
	 */
	public boolean canDelete(T entity) {
		return this.isLoggedUserTheOwner(entity);
	}

	/**
	 * @param entity The entity that is going to be got
	 * @return By default it returns True
	 */
	public boolean canGet(T entity) {
		return true;
	}

	/**
	 * @return By default, it returns True
	 */
	public boolean canList() {
		return true;
	}


}
