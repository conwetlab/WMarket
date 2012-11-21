package org.fiware.apps.marketplace.dao;

import java.util.List;

import org.fiware.apps.marketplace.model.Localuser;

public interface LocaluserDao {
	void save(Localuser localuser);
	void update(Localuser localuser);
	void delete(Localuser localuser);
	Localuser findByName(String username);
	List <Localuser> findLocalusers();
}
