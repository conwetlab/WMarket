package org.fiware.apps.marketplace.dao;

import java.util.List;

import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.model.Store;

public interface StoreDao {
	void save(Store store);
	void update(Store store);
	void delete(Store store);
	Store findByName(String url) throws StoreNotFoundException;
	List<Store> getStoresPage(int offset, int max);
	List <Store> getAllStores();

}
