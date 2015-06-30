package org.fiware.apps.marketplace.dao;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2015 CoNWeT Lab, Universidad Politécnica de Madrid
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

import org.fiware.apps.marketplace.exceptions.DescriptionNotFoundException;
import org.fiware.apps.marketplace.exceptions.OfferingNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Offering;

public interface OfferingDao {
	
	// Save, update & delete
	public void save(Offering offering);
	public void update(Offering offering);
	public void delete(Offering offering);
	
	// Find
	public Offering findByNameStoreAndDescription(String storeName, String descriptionName, 
			String offeringName) throws StoreNotFoundException, DescriptionNotFoundException, 
			OfferingNotFoundException;
	
	// Get offerings
	// public List<Offering> getAllOfferings();
	public List<Offering> getOfferingsPage(int offset, int max, String orderBy, boolean desc);
	// public List<Offering> getAllStoreOfferings(String storeName) 
	//	 	throws StoreNotFoundException;
	public List<Offering> getStoreOfferingsPage(String storeName, int offset, int max, String orderBy, boolean desc) 
			throws StoreNotFoundException;
	// public List<Offering> getAllDescriptionOfferings(String storeName, String descriptionName)
	// 		throws StoreNotFoundException, DescriptionNotFoundException;
	public List<Offering> getDescriptionOfferingsPage(String storeName, String descriptionName, 
			int offset, int max, String orderBy, boolean desc) throws StoreNotFoundException, 
			DescriptionNotFoundException;
	
	public List<Offering> getUserBookmarkedOfferings(String userName, int offset, int max, String orderBy, 
			boolean desc) throws UserNotFoundException;
	

}
