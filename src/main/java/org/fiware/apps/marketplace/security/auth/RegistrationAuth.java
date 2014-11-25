package org.fiware.apps.marketplace.security.auth;

import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Localuser;

public abstract class RegistrationAuth<T> {
	
	/**
	 * Method to return the Localuser who is owner of the entity
	 * @param entity The entity whose owner the developer wants to know
	 * @return The owner of the entity
	 */
	protected abstract Localuser getEntityOwner(T entity);
	
	/**
	 * Method to know if the logged user is the owner of the entity
	 * @param entity The entity to check. 
	 * @return True if the logged user is the owner of the entity. False otherwise
	 */
	protected boolean isLoggedUserTheOwner(T entity) {
		boolean canAccess = false;

		try {
			Localuser loggedUser = AuthUtils.getAuthUtils().getLoggedUser();
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
			isLoggedIn = AuthUtils.getAuthUtils().getLoggedUser() != null;
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
