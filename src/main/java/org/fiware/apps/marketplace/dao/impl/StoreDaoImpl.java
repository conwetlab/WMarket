package org.fiware.apps.marketplace.dao.impl;

import java.util.List;

import org.fiware.apps.marketplace.dao.StoreDao;
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
	public Store findByName(String name) {	
		List list = getHibernateTemplate().find(
                "from Store where name=?",name
           );	
		
		System.out.println(list.size());
		
		
		if (list.size()!=0){
			return (Store)list.get(0);
		}		
		return null;
			
	}
	
	@Override
	public List <Store> findStores() {
		List list = getHibernateTemplate().loadAll(Store.class);
		if (list.size()==0){
			return null;
		}		
		
		return list;
	}

}
