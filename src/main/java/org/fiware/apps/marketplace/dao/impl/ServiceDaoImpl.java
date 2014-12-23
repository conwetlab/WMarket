package org.fiware.apps.marketplace.dao.impl;

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

import java.util.List;

import org.fiware.apps.marketplace.dao.ServiceDao;
import org.fiware.apps.marketplace.exceptions.ServiceNotFoundException;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.utils.MarketplaceHibernateDao;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Repository;

@Repository("serviceDao")
public class ServiceDaoImpl extends MarketplaceHibernateDao implements ServiceDao {

	@Override
	public void save(Service service) {
		getHibernateTemplate().saveOrUpdate(service);		
	}

	@Override
	public void update(Service service) {
		getHibernateTemplate().update(service);		
	}

	@Override
	public void delete(Service service) {
		getHibernateTemplate().delete(service);		
	}

	@Override
	public Service findById(Integer id) {
		Object res = getHibernateTemplate().get(Service.class, id);
		return (Service) res;
	}
	
	private Service findByQuery(String query, Object[] params) throws ServiceNotFoundException {
		List<?> list = getHibernateTemplate().find(query, params);
		
		if (list.size() == 0) {
			throw new ServiceNotFoundException("Service " + params[0] + " not found");
		} else {
			return (Service) list.get(0);
		}
	}
	
	@Override
	public Service findByName(String name) throws ServiceNotFoundException {
		Object[] params = {name};
		return this.findByQuery("from Service where name = ?", params);
	}

	@Override
	public Service findByNameAndStore(String name, String store) throws ServiceNotFoundException {
		Object[] params  = {name , store};
		return this.findByQuery("from Service where name = ? and store.name = ?", params);				
	}
	
	@Override
	public List<Service> getAllServices() {
		return getHibernateTemplate().loadAll(Service.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Service> getServicesPage(int offset, int max) {
		return (List<Service>) getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Service.class), offset, max);
	}

}
