package org.fiware.apps.marketplace.bo.impl;

import java.util.List;

import org.fiware.apps.marketplace.bo.ServiceBo;
import org.fiware.apps.marketplace.dao.ServiceDao;
import org.fiware.apps.marketplace.exceptions.ServiceNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.rdf.RdfIndexer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service("serviceBo")
public class ServiceBoImpl implements ServiceBo{
	
	@Autowired
	ServiceDao serviceDao;
	
	public void setServiceDao (ServiceDao serviceDao){
		this.serviceDao = serviceDao;
	}
	
	@Override
	@Transactional(readOnly=false)
	public void save(Service service) {		
		serviceDao.save(service);	
		RdfIndexer.indexService(service);	
	}

	@Override
	@Transactional(readOnly=false)
	public void update(Service service) {
		serviceDao.update(service);
		RdfIndexer.deleteService(service);
		RdfIndexer.indexService(service);
	}

	@Override
	@Transactional(readOnly=false)
	public void delete(Service service) {
		serviceDao.delete(service);
		RdfIndexer.deleteService(service);
	}

	@Override
	public Service findById(Integer id) throws ServiceNotFoundException{
		return serviceDao.findById(id);
	}
	
	@Override
	public Service findByName(String name) throws ServiceNotFoundException {
		return serviceDao.findByName(name);
	}

	@Override
	public Service findByNameAndStore(String name, String store) throws ServiceNotFoundException, StoreNotFoundException {
		return serviceDao.findByNameAndStore(name, store);
	}
	
	@Override
	public List<Service> getAllServices() {
		return serviceDao.getAllServices();
	}
	
	@Override
	public List<Service> getServicesPage(int offset, int max) {
		return serviceDao.getServicesPage(offset, max);
	}

}
