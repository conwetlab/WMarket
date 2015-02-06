package org.fiware.apps.marketplace.dao.impl;

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

import org.fiware.apps.marketplace.dao.OfferingsDescriptionDao;
import org.fiware.apps.marketplace.exceptions.OfferingDescriptionNotFoundException;
import org.fiware.apps.marketplace.model.OfferingsDescription;
import org.fiware.apps.marketplace.utils.MarketplaceHibernateDao;

import org.hibernate.criterion.DetachedCriteria;

import org.springframework.stereotype.Repository;

@Repository("offeringsDescriptionDao")
public class OfferingsDescriptionDaoImpl extends MarketplaceHibernateDao implements OfferingsDescriptionDao {
	
	private final static String TABLE_NAME = OfferingsDescription.class.getName();

	@Override
	public void save(OfferingsDescription offeringsDescription) {
		getHibernateTemplate().saveOrUpdate(offeringsDescription);		
	}

	@Override
	public void update(OfferingsDescription offeringsDescription) {
		getHibernateTemplate().update(offeringsDescription);		
	}

	@Override
	public void delete(OfferingsDescription offeringsDescription) {
		getHibernateTemplate().delete(offeringsDescription);		
	}

	@Override
	public OfferingsDescription findById(Integer id) {
		Object res = getHibernateTemplate().get(OfferingsDescription.class, id);
		return (OfferingsDescription) res;
	}
	
	private OfferingsDescription findByQuery(String query, Object[] params) throws OfferingDescriptionNotFoundException {
		List<?> list = getHibernateTemplate().find(query, params);
		
		if (list.size() == 0) {
			throw new OfferingDescriptionNotFoundException("Offerings Description " + params[0] + " not found");
		} else {
			return (OfferingsDescription) list.get(0);
		}
	}
	
	@Override
	public OfferingsDescription findByName(String name) throws OfferingDescriptionNotFoundException {
		Object[] params = {name};
		String query = String.format("from %s where name = ?", TABLE_NAME);
		return this.findByQuery(query, params);
	}

	@Override
	public OfferingsDescription findByNameAndStore(String name, String store) throws OfferingDescriptionNotFoundException {
		Object[] params  = {name , store};
		String query = String.format("from %s where name = ? and store.name = ?", TABLE_NAME);
		return this.findByQuery(query, params);				
	}
	
	@Override
	public List<OfferingsDescription> getAllOfferingsDescriptions() {
		return getHibernateTemplate().loadAll(OfferingsDescription.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OfferingsDescription> getOfferingsDescriptionsPage(int offset, int max) {
		return (List<OfferingsDescription>) getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(OfferingsDescription.class), offset, max);
	}

}
