package org.fiware.apps.marketplace.dao.impl;

import java.util.List;

import org.fiware.apps.marketplace.dao.LocaluserDao;
import org.fiware.apps.marketplace.model.Localuser;
import org.fiware.apps.marketplace.utils.MarketplaceHibernateDao;
import org.springframework.stereotype.Repository;

@Repository("localuserDao")
public class LocaluserDaoImpl  extends MarketplaceHibernateDao implements LocaluserDao {

	@Override
	public void save(Localuser user) {
		getHibernateTemplate().saveOrUpdate(user);	
	}

	@Override
	public void update(Localuser user) {
		getHibernateTemplate().update(user);	
		
	}

	@Override
	public void delete(Localuser user) {
		getHibernateTemplate().delete(user);
	}

	@Override
	public Localuser findByName(String username) {
		List<?> list = getHibernateTemplate().find("from Localuser where username=?", username);
		
		if (list.size() != 0){
			return (Localuser) list.get(0);
		}
		//FIXME: Else -> Throw UserNotFoundException
		
		return null;
	}

	@Override
	public List<Localuser> findLocalusers() {
		List<Localuser> list = getHibernateTemplate().loadAll(Localuser.class);
		
		if (list.size() == 0){
			return null;
		}		
		
		return list;
	}

}
