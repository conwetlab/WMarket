package org.fiware.apps.marketplace.dao.impl;

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
