package org.fiware.apps.marketplace.bo;

import java.util.List;

import org.fiware.apps.marketplace.exceptions.ServiceNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.model.Service;

public interface ServiceBo {
	void save(Service service);
	void update(Service service);
	void delete(Service service);
	Service findByName(String name) throws ServiceNotFoundException;
	Service findByNameAndStore(String name, String store) throws ServiceNotFoundException, StoreNotFoundException;
	Service findById(Integer id) throws ServiceNotFoundException;
	List<Service> getAllServices();
	List<Service> getServicesPage(int offset, int max);
}
