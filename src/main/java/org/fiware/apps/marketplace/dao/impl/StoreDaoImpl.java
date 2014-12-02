package org.fiware.apps.marketplace.dao.impl;

import java.util.List;

import org.fiware.apps.marketplace.dao.StoreDao;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.utils.MarketplaceHibernateDao;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Repository;

@Repository("storeDao")
public class StoreDaoImpl extends MarketplaceHibernateDao implements StoreDao {

	@Override
	public void save(Store store) {
		getHibernateTemplate().saveOrUpdate(store);				
	}
	
	@Override
	public void update(Store store) {
		getHibernateTemplate().update(store);		
	}

	@Override
	public void delete(Store store) {
		getHibernateTemplate().delete(store);		
	}

	@Override
	public Store findByName(String name) throws StoreNotFoundException {	
		List<?> list = getHibernateTemplate().find("from Store where name=?", name);	
		
		if (list.size() == 0){
			throw new StoreNotFoundException("Store " + name + " not found");
		} else {
			return (Store) list.get(0);
		}
			
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Store> getStoresPage(int offset, int max) {
		return (List<Store>) getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(Store.class), offset, max);
	}
	
	@Override
	public List <Store> getAllStores() {
		return getHibernateTemplate().loadAll(Store.class);
	}

}
