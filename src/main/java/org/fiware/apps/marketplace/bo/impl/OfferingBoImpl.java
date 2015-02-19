package org.fiware.apps.marketplace.bo.impl;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
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

import org.fiware.apps.marketplace.bo.OfferingBo;
import org.fiware.apps.marketplace.dao.OfferingDao;
import org.fiware.apps.marketplace.exceptions.DescriptionNotFoundException;
import org.fiware.apps.marketplace.exceptions.OfferingNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.model.Offering;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("offeringBo")
public class OfferingBoImpl implements OfferingBo {
	
	@Autowired private OfferingDao offeringDao;

	@Override
	@Transactional(readOnly = false)
	public void save(Offering offering) {
		offeringDao.save(offering);
	}

	@Override
	@Transactional(readOnly = false)
	public void update(Offering offering) {
		offeringDao.update(offering);
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(Offering offering) {
		offeringDao.delete(offering);
	}

	@Override
	@Transactional
	public Offering findByUri(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public Offering findDescriptionByNameStoreAndDescription(String storeName, 
			String descriptionName, String offeringName)
			throws OfferingNotFoundException, StoreNotFoundException, DescriptionNotFoundException {
		return offeringDao.findDescriptionByNameStoreAndDescription(storeName, descriptionName, offeringName);
	}

	@Override
	@Transactional
	public List<Offering> getAllOfferings() {
		return offeringDao.getAllOfferings();
	}

	@Override
	@Transactional
	public List<Offering> getOfferingsPage(int offset, int max) {
		return offeringDao.getOfferingsPage(offset, max);
	}

	@Override
	@Transactional
	public List<Offering> getAllStoreOfferings(String storeName) throws StoreNotFoundException {
		return offeringDao.getAllStoreOfferings(storeName);
	}

	@Override
	@Transactional
	public List<Offering> getStoreOfferingsPage(String storeName, int offset,
			int max) throws StoreNotFoundException {
		return offeringDao.getStoreOfferingsPage(storeName, offset, max);
	}

	@Override
	@Transactional
	public List<Offering> getAllDescriptionOfferings(String storeName, String descriptionName) 
			throws StoreNotFoundException, DescriptionNotFoundException {
		return offeringDao.getAllDescriptionOfferings(storeName, descriptionName);
	}

	@Override
	@Transactional
	public List<Offering> getDescriptionOfferingsPage(String storeName, 
			String descriptionName, int offset, int max) throws StoreNotFoundException, DescriptionNotFoundException {
		return offeringDao.getDescriptionOfferingsPage(storeName, descriptionName, offset, max);
	}
	
}
