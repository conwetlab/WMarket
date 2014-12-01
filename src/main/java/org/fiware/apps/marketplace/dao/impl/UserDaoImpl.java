package org.fiware.apps.marketplace.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.fiware.apps.marketplace.dao.UserDao;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.utils.MarketplaceHibernateDao;
import org.springframework.stereotype.Repository;

@Repository("userDao")
public class UserDaoImpl  extends MarketplaceHibernateDao implements UserDao {

	@Override
	public void save(User user) {
		getHibernateTemplate().saveOrUpdate(user);	
	}

	@Override
	public void update(User user) {
		getHibernateTemplate().update(user);	
		
	}

	@Override
	public void delete(User user) {
		getHibernateTemplate().delete(user);
	}

	@Override
	public User findByName(String username) throws UserNotFoundException{
		List<?> list = getHibernateTemplate().find("from User where userName=?", username);
		
		if (list.size() == 0) {
			throw new UserNotFoundException("User " + username + " not found");
		} else {
			return (User) list.get(0);
		}
	}

	@Override
	public List<User> findLocalusers() {
		List<User> list = getHibernateTemplate().loadAll(User.class);
		
		if (list.size() == 0){
			list = new ArrayList<User>();
		}		
		
		return list;
	}

}
