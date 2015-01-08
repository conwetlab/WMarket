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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.fiware.apps.marketplace.bo.OfferingBo;
import org.fiware.apps.marketplace.model.Offering;
import org.springframework.stereotype.Service;

@Service("offeringBo")
public class OfferingBoImpl implements OfferingBo {

	// TODO Store data in a database or similar

	private HashMap<String, Offering> offeringMap;

	public OfferingBoImpl() {
		offeringMap = new HashMap<String, Offering>();
	}

	@Override
	public void save(Offering offering) {
		if (!offeringMap.containsKey(offering.getOfferingUri()))
			offeringMap.put(offering.getOfferingUri(), offering);
	}

	@Override
	public void delete(Offering offering) {
		if (offeringMap.containsKey(offering.getOfferingUri()))
			offeringMap.remove(offering.getOfferingUri());
	}

	@Override
	public Offering getByUri(String uri) {
		if (offeringMap.containsKey(uri))
			return offeringMap.get(uri);
		else
			return null;
	}

	@Override
	public List<Offering> getAllOfferings() {
		return new ArrayList<Offering>(offeringMap.values());
	}
}
