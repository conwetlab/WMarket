package org.fiware.apps.marketplace.bo.impl;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
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

import java.util.Date;
import java.util.List;

import org.fiware.apps.marketplace.bo.ReviewBo;
import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.dao.UserDao;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.ReviewNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Review;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.validators.UserValidator;
import org.fiware.apps.marketplace.security.auth.UserAuth;
import org.fiware.apps.marketplace.utils.NameGenerator;
import org.pac4j.springframework.security.authentication.ClientAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userBo")
public class UserBoImpl implements UserBo {

	@Autowired private ReviewBo reviewBo;
	@Autowired private UserDao userDao;
	@Autowired private UserAuth userAuth;
	@Autowired private UserValidator userValidator;
	// Encoder must be the same in all the platform: use the bean
	@Autowired private PasswordEncoder encoder;

	private static final Logger logger = LoggerFactory.getLogger(UserBoImpl.class);

	@Override
	@Transactional(readOnly=false)
	public void save(User user) throws NotAuthorizedException, ValidationException{
		
		// Check rights and raise exception if user is not allowed to perform this action
		if (!userAuth.canCreate(user)) {
			throw new NotAuthorizedException("create user");
		}
		
		// Set user name based on the display name. It's possible to have to users with
		// the same display name, but it's necessary to set a different user name for
		// each one.
		String basicUserName = NameGenerator.getURLName(user.getDisplayName());
		String finalUserName = basicUserName;
		boolean available = userDao.isUserNameAvailable(basicUserName);
		int counter = 1;
		
		while(!available) {
			finalUserName = basicUserName + "-" + counter++;
			available = userDao.isUserNameAvailable(finalUserName);
		}
		
		user.setUserName(finalUserName);
		
		// Exception is risen if the user is not valid
		userValidator.validateNewUser(user);
		
		// Encode the password
		user.setPassword(encoder.encode(user.getPassword()));
		
		// Set the registration date
		user.setCreatedAt(new Date());
		
		// Save the new user
		userDao.save(user);
	}

	@Override
	@Transactional(readOnly=false)
	public void update(String userName, User updatedUser) 
			throws NotAuthorizedException, ValidationException, UserNotFoundException {
		
		User userToBeUpdated = userDao.findByName(userName);
		
		// Check rights and raise exception if user is not allowed to perform this action
		if (!userAuth.canUpdate(userToBeUpdated)) {
			throw new NotAuthorizedException("update user " + userName);
		}
		
		// Exception is risen if the user is not valid
		userValidator.validateUpdatedUser(userToBeUpdated, updatedUser);		

		// At this moment, user name cannot be changed to avoid error with sessions
		// For this reason this field is ignored
		// userToBeUpdated.setUserName(user.getUserName());
		// if (updatedUser.getUserName() != null && !updatedUser.getUserName().equals(userToBeUpdated.getUserName())) {
		// 	throw new ValidationException("userName", "userName cannot be changed");
		// }
		
		if (updatedUser.getCompany() != null) {
			userToBeUpdated.setCompany(updatedUser.getCompany());
		}
		
		if (updatedUser.getPassword() != null) {
			// Encode the password
			userToBeUpdated.setPassword(encoder.encode(updatedUser.getPassword()));
		}
		
		if (updatedUser.getEmail() != null) {
			userToBeUpdated.setEmail(updatedUser.getEmail());
		}
		
		if (updatedUser.getDisplayName() != null) {
			userToBeUpdated.setDisplayName(updatedUser.getDisplayName());
		}

		userDao.update(userToBeUpdated);
	}

	@Override
	@Transactional(readOnly=false)
	public void delete(String userName) throws NotAuthorizedException, UserNotFoundException {
		
		User user = userDao.findByName(userName);
		
		// Check rights and raise exception if user is not allowed to perform this action
		if (!userAuth.canDelete(user)) {
			throw new NotAuthorizedException("delete user " + userName);
		}
		
		// Delete reviews manually so average score is recalculated
		// This also avoid unexpected Hibernate exceptions
		List<Review> userReviews = user.getReviews();
		for (Review review: userReviews) {
			try {
				reviewBo.deleteReview(review.getReviewableEntity(), review.getId());
			} catch (ReviewNotFoundException ex) {
				// Not expected
			}
		}
		
		userDao.delete(user);
	}

	@Override
	@Transactional
	public User findByName(String userName) throws NotAuthorizedException, 
			UserNotFoundException {
		
		User user = userDao.findByName(userName);
		
		// Check rights and raise exception if user is not allowed to perform this action
		if (!userAuth.canGet(user)) {
			throw new NotAuthorizedException("find user");
		}
		
		return user;
	}
	
	@Override
	@Transactional
	public User findByEmail(String email) throws NotAuthorizedException, 
			UserNotFoundException {
		
		User user = userDao.findByEmail(email);
		
		// Check rights and raise exception if user is not allowed to perform this action
		if (!userAuth.canGet(user)) {
			throw new NotAuthorizedException("find user");
		}
		
		return user;
	}

	@Override
	@Transactional
	public List<User> getUsersPage(int offset, int max) throws NotAuthorizedException {
		// Check rights and raise exception if user is not allowed to perform this action
		if (!userAuth.canList()) {
			throw new NotAuthorizedException("list users");
		}
		
		return userDao.getUsersPage(offset, max);
	}

	@Override
	@Transactional
	public List<User> getAllUsers() throws NotAuthorizedException {
		// Check rights and raise exception if user is not allowed to perform this action
		if (!userAuth.canList()) {
			throw new NotAuthorizedException("list users");
		}
		
		return userDao.getAllUsers();
	}

	@Override
	@Transactional
	public User getCurrentUser() throws UserNotFoundException {
		String userName;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// When OAuth2 is being used, we should cast the authentication to read the correct user name
		if (authentication instanceof ClientAuthenticationToken) {
			userName = ((ClientAuthenticationToken) authentication).getUserProfile().getId();
		} else {
			userName = authentication.getName();
		}

		logger.info("User: {}", userName);
		return userDao.findByName(userName);
	}

	@Override
	public boolean checkCurrentUserPassword(String password) throws UserNotFoundException{
		User user = getCurrentUser();
		return encoder.matches(password, user.getPassword());
	}

	@Override
	@Transactional
	public void changeProviderStatus(String userName) throws NotAuthorizedException,
			UserNotFoundException {
		
		User userToBeUpdated = userDao.findByName(userName);
		
		// Check rights and raise exception if user is not allowed to perform this action
		if (!userAuth.canUpdate(userToBeUpdated)) {
			throw new NotAuthorizedException("update user");
		}
		
		// Method is transactional so the instance is automatically updated
		userToBeUpdated.setProvider(!userToBeUpdated.isProvider());
	}
}
