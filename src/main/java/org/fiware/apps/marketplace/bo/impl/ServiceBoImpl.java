package org.fiware.apps.marketplace.bo.impl;

import org.fiware.apps.marketplace.bo.ServiceBo;
import org.fiware.apps.marketplace.dao.ServiceDao;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.rdf.RdfHelper;
import org.fiware.apps.marketplace.rdf.RdfIndexer;
import org.fiware.apps.marketplace.utils.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.hp.hpl.jena.rdf.model.Model;


@org.springframework.stereotype.Service("serviceBo")
public class ServiceBoImpl implements ServiceBo{
	
	@Autowired
	ServiceDao serviceDao;
	
	public void setServiceDao (ServiceDao serviceDao){
		this.serviceDao = serviceDao;
	}
	
	@Override
	public void save(Service service) {		
		serviceDao.save(service);	
		RdfIndexer.indexService(service);	
	}

	@Override
	public void update(Service service) {
		serviceDao.update(service);
		RdfIndexer.deleteService(service);
		RdfIndexer.indexService(service);
	}

	@Override
	public void delete(Service service) {
		serviceDao.delete(service);
		RdfIndexer.deleteService(service);
	}

	@Override
	public Service findById(Integer id) {
		return serviceDao.findById(id);
	}
	
	@Override
	public Service findByName(String name) {
		return serviceDao.findByName(name);
	}

	@Override
	public Service findByNameAndStore(String name, String store) {
		return serviceDao.findByNameAndStore(name, store);
	}

}
