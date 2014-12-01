package org.fiware.apps.marketplace.dao;

import java.util.List;

import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.User;

public interface UserDao {
	void save(User localuser);
	void update(User localuser);
	void delete(User localuser);
	User findByName(String username) throws UserNotFoundException;
	List<User> getUsersPage(int offset, int max);
	List <User> getAllUsers();
}
