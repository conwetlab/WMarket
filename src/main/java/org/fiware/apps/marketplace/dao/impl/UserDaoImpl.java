package org.fiware.apps.marketplace.dao.impl;

import java.util.List;

import org.fiware.apps.marketplace.dao.UserDao;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.utils.MarketplaceHibernateDao;
import org.hibernate.criterion.DetachedCriteria;
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
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsersPage(int offset, int max) {
		return (List<User>) getHibernateTemplate().findByCriteria(
				DetachedCriteria.forClass(User.class), offset, max);
	}

	@Override
	public List<User> getAllUsers() {
		return getHibernateTemplate().loadAll(User.class);
	}

}
