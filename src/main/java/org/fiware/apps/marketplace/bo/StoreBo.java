package org.fiware.apps.marketplace.bo;

import java.util.List;

import org.fiware.apps.marketplace.model.Store;


public interface StoreBo {
	void save(Store store);
	void update(Store store);
	void delete(Store store);
	Store findByName(String name);
	List <Store> findStores();
	
}
