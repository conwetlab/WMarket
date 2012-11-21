package org.fiware.apps.marketplace.dao.impl;

import java.util.List;

import org.fiware.apps.marketplace.dao.ServiceDao;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.util.MarketplaceHibernateDao;
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
	public Service findById(Integer id){
		Object res = getHibernateTemplate().get(Service.class, id);
		return (Service) res;
	}
	
	@Override
	public Service findByName(String name) {
		List list = getHibernateTemplate().find(
				"from Service where name=?",name
				);
		if (list.size()!=0){
			return (Service)list.get(0);
		}		
		return null;
	}

	@Override
	public Service findByNameAndStore(String name, String store) {
		Object[] params  = {name , store};
		List list = getHibernateTemplate().find("from Service where name=? and store.name =?", params);				
	
		if (list.size()!=0){
			return (Service)list.get(0);
		}		
		return null;
	}

}
