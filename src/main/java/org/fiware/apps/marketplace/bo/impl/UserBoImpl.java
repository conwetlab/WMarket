package org.fiware.apps.marketplace.bo.impl;

import java.util.List;

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.dao.UserDao;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userBo")
public class UserBoImpl implements UserBo {

	@Autowired
	UserDao userDao;
	
	public void setStoreDao (UserDao localuser){
		this.userDao = localuser;
	}
	
	@Override
	@Transactional(readOnly=false)
	public void save(User localuser) {
		userDao.save(localuser);
	}

	@Override
	@Transactional(readOnly=false)
	public void update(User localuser) {
		userDao.update(localuser);
	}

	@Override
	@Transactional(readOnly=false)
	public void delete(User localuser) {
		userDao.delete(localuser);
	}

	@Override
	public User findByName(String username) throws UserNotFoundException {
		return userDao.findByName(username);
	}

	@Override
	public List<User> findLocalusers() {
		return userDao.findLocalusers();
	}

}
