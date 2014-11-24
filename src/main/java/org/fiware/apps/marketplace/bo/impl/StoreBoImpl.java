package org.fiware.apps.marketplace.bo.impl;

import java.util.List;

import org.fiware.apps.marketplace.bo.StoreBo;
import org.fiware.apps.marketplace.dao.StoreDao;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.model.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("storeBo")
public class StoreBoImpl implements StoreBo{

	@Autowired
	StoreDao storeDao;
	
	public void setStoreDao (StoreDao storeDao){
		this.storeDao = storeDao;
	}
	
	@Override
	@Transactional(readOnly=false)
	public void save(Store store) {
		storeDao.save(store);
		
	}

	@Override
	@Transactional(readOnly=false)
	public void update(Store store) {
		storeDao.update(store);
		
	}

	@Override
	@Transactional(readOnly=false)
	public void delete(Store store) {
		storeDao.delete(store);
		
	}

	@Override
	public Store findByName(String name) throws StoreNotFoundException {
		return storeDao.findByName(name);
	}
	
	@Override
	public List<Store> findStores() {
		return storeDao.findStores();
	}
}
