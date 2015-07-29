package org.fiware.apps.marketplace.dao.impl;

import java.util.ArrayList;

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

import org.fiware.apps.marketplace.dao.UserDao;
import org.fiware.apps.marketplace.dao.ViewedOfferingDao;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.ViewedOffering;
import org.fiware.apps.marketplace.utils.MarketplaceHibernateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("viewedOfferingDao")
public class ViewedOfferingDaoImpl extends MarketplaceHibernateDao implements ViewedOfferingDao {
	
	private static final String TABLE_NAME = ViewedOffering.class.getName();
	
	@Autowired private UserDao userDao;

	@Override
	public void save(ViewedOffering viewedOffering) {
		getSession().saveOrUpdate(viewedOffering);	
	}

	@Override
	public void update(ViewedOffering viewedOffering) {
		getSession().merge(viewedOffering);		
	}

	@Override
	public void delete(ViewedOffering viewedOffering) {
		getSession().delete(viewedOffering);		
	}

	@Override
	public List<ViewedOffering> getUserViewedOfferings(String userName)
			throws UserNotFoundException {
		
		return getUserViewedOfferingsPage(userName, 0, Integer.MAX_VALUE);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ViewedOffering> getUserViewedOfferingsPage(String userName,
			int offset, int max) throws UserNotFoundException {
		// Throw exception if user does not exist
		User user = userDao.findByName(userName);
		
		return getSession().createQuery(String.format("from %s where user = :user order by date desc", TABLE_NAME))
				.setParameter("user", user)
				.setFirstResult(offset)
				.setMaxResults(max)
				.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ViewedOffering> getOfferingsViewedByOtherUsers(String userName, int max) 
			throws UserNotFoundException {
		
		// Throw exception if user does not exist
		User user = userDao.findByName(userName);
		
		// This list can include repeated offerings
		List<ViewedOffering> viewedOfferings = getSession().createQuery(
				String.format("FROM %s where user != :user order by date desc", TABLE_NAME))
				.setParameter("user", user)
				.list();
		
		// Filter the list
		List<ViewedOffering> filteredViewedOfferings = new ArrayList<>();
		List<Offering> offeringsAuxList = new ArrayList<>();
		for (int i = 0; i < viewedOfferings.size() && filteredViewedOfferings.size() < max; i++) {
			
			ViewedOffering viewedOffering = viewedOfferings.get(i);
			
			if (!offeringsAuxList.contains(viewedOffering.getOffering())) {
				filteredViewedOfferings.add(viewedOffering);
				offeringsAuxList.add(viewedOffering.getOffering());
			}
		}
		
		return filteredViewedOfferings;	
	}

}
