package org.fiware.apps.marketplace.dao.impl;

import java.util.List;

import org.fiware.apps.marketplace.dao.LocaluserDao;
import org.fiware.apps.marketplace.model.Localuser;
import org.fiware.apps.marketplace.util.MarketplaceHibernateDao;
import org.springframework.stereotype.Repository;

@Repository("localuserDao")
public class LocaluserDaoImpl  extends MarketplaceHibernateDao implements LocaluserDao {

	@Override
	public void save(Localuser store) {
		getHibernateTemplate().saveOrUpdate(store);	
	}

	@Override
	public void update(Localuser store) {
		getHibernateTemplate().update(store);	
		
	}

	@Override
	public void delete(Localuser store) {
		getHibernateTemplate().delete(store);
	}

	@Override
	public Localuser findByName(String username) {
		List list = getHibernateTemplate().find(
				"from Localuser where username=?",username
				);
		if (list.size()!=0){
			return (Localuser)list.get(0);
		}		
		return null;
	}

	@Override
	public List<Localuser> findLocalusers() {
		List list = getHibernateTemplate().loadAll(Localuser.class);
		if (list.size()==0){
			return null;
		}		
		
		return list;
	}

}
