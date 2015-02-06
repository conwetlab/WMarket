package org.fiware.apps.marketplace.bo.impl;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
 * Copyright (C) 2014-2015 CoNWeT Lab, Universidad Politécnica de Madrid
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holders nor the names of its contributors
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.util.List;

import org.fiware.apps.marketplace.bo.OfferingsDescriptionBo;
import org.fiware.apps.marketplace.dao.OfferingsDescriptionDao;
import org.fiware.apps.marketplace.exceptions.OfferingDescriptionNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.model.OfferingsDescription;
import org.fiware.apps.marketplace.rdf.RdfIndexer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service("offeringsDescriptionBo")
public class OfferingsDescriptionBoImpl implements OfferingsDescriptionBo{
	
	@Autowired private OfferingsDescriptionDao offeringsDescriptionDao;
	@Autowired private RdfIndexer rdfIndexer;
	
	public void setOfferingsDescriptionDao (OfferingsDescriptionDao offeringsDescriptionDao){
		this.offeringsDescriptionDao = offeringsDescriptionDao;
	}
	
	@Override
	@Transactional(readOnly=false)
	public void save(OfferingsDescription offeringsDescription) {		
		offeringsDescriptionDao.save(offeringsDescription);	
		rdfIndexer.indexService(offeringsDescription);	
	}

	@Override
	@Transactional(readOnly=false)
	public void update(OfferingsDescription offeringsDescription) {
		offeringsDescriptionDao.update(offeringsDescription);
		rdfIndexer.deleteService(offeringsDescription);
		rdfIndexer.indexService(offeringsDescription);
	}

	@Override
	@Transactional(readOnly=false)
	public void delete(OfferingsDescription offeringsDescription) {
		offeringsDescriptionDao.delete(offeringsDescription);
		rdfIndexer.deleteService(offeringsDescription);
	}

	@Override
	public OfferingsDescription findById(Integer id) throws OfferingDescriptionNotFoundException{
		return offeringsDescriptionDao.findById(id);
	}
	
	@Override
	public OfferingsDescription findByName(String name) throws OfferingDescriptionNotFoundException {
		return offeringsDescriptionDao.findByName(name);
	}

	@Override
	public OfferingsDescription findByNameAndStore(String name, String store) 
			throws OfferingDescriptionNotFoundException, StoreNotFoundException {
		return offeringsDescriptionDao.findByNameAndStore(name, store);
	}
	
	@Override
	public List<OfferingsDescription> getAllOfferingsDescriptions() {
		return offeringsDescriptionDao.getAllOfferingsDescriptions();
	}
	
	@Override
	public List<OfferingsDescription> getOfferingsDescriptionsPage(int offset, int max) {
		return offeringsDescriptionDao.getOfferingsDescriptionsPage(offset, max);
	}

}
