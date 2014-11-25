package org.fiware.apps.marketplace.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.fiware.apps.marketplace.dao.StoreDao;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.utils.MarketplaceHibernateDao;
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
	
	@Override
	public List <Store> findStores() {
		List<Store> list = getHibernateTemplate().loadAll(Store.class);
		
		if (list.size()==0){
			list = new ArrayList<Store>();
		}		
		
		return list;
	}

}
