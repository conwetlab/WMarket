package org.fiware.apps.marketplace.dao;

import java.util.List;

import org.fiware.apps.marketplace.exceptions.ServiceNotFoundException;
import org.fiware.apps.marketplace.model.Service;

public interface ServiceDao {
	void save(Service service);
	void update(Service service);
	void delete(Service service);
	Service findByName(String name) throws ServiceNotFoundException;
	Service findByNameAndStore(String name, String store) throws ServiceNotFoundException;
	Service findById(Integer id);
	List<Service> getAllServices();
	List<Service> getServicesPage(int offset, int max);
}
