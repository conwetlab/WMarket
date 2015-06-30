package org.fiware.apps.marketplace.controllers.rest.v2;

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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.fiware.apps.marketplace.bo.OfferingBo;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.Offerings;
import org.fiware.apps.marketplace.model.User;
import org.hibernate.QueryException;
import org.hibernate.exception.SQLGrammarException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class OfferingsInStoreServiceTest {
	
	@Mock private OfferingBo offeringBoMock;
	@InjectMocks private OfferingsInStoreService offeringsInStoreService;
	
	private static final String OFFSET_MAX_INVALID = "offset and/or max are not valid";
	private static final String STORE_NAME = "store";
	
	@Before 
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}
	
	private void testListOfferingsInStoreInvalidParams(int offset, int max) {
		// Call the method
		Response res = offeringsInStoreService.listOfferingsInStore(STORE_NAME, offset, max, "name", true);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 400, ErrorType.BAD_REQUEST, 
				String.format(OFFSET_MAX_INVALID, offset, max));
	}
	
	@Test
	public void testListOfferingsInStoreInvalidOffset() {
		testListOfferingsInStoreInvalidParams(-1, 100);
	}
	
	@Test
	public void testListOfferingsInStoreInvalidMax() {
		testListOfferingsInStoreInvalidParams(0, -1);
	}
	
	@Test
	public void testListOfferingsInStoreInvalidOffsetMax() {
		testListOfferingsInStoreInvalidParams(-1, -1);
	}
	
	@Test
	public void testListAllOfferingsGetNoErrors() throws Exception {
		List<Offering> oferrings = new ArrayList<Offering>();
		for (int i = 0; i < 3; i++) {
			Offering offering = new Offering();
			offering.setId(i);
			oferrings.add(offering);
		}
		
		// Mocks
		when(offeringBoMock.getStoreOfferingsPage(eq(STORE_NAME), anyInt(), anyInt(), anyString(), anyBoolean())).
				thenReturn(oferrings);
		
		// Call the method
		int offset = 0;
		int max = 100;
		String orderBy = "name";
		boolean desc = false;
		Response res = offeringsInStoreService.listOfferingsInStore(STORE_NAME, offset, max, orderBy, desc);
		
		// Verify
		verify(offeringBoMock).getStoreOfferingsPage(STORE_NAME, offset, max, orderBy, desc);
		
		// Assertions
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat(((Offerings) res.getEntity()).
				getOfferings()).isEqualTo(oferrings);
	}
	
	private void testListOfferingsInStoreException(String orderBy, Exception expectedException, int errorCode, 
			ErrorType errorType, String message) {
		
		try {
			// Mocks
			doThrow(expectedException).when(offeringBoMock)
					.getStoreOfferingsPage(eq(STORE_NAME), anyInt(), anyInt(), anyString(), anyBoolean());
	
			// Call the method
			int offset = 0;
			int max = 100;
			boolean desc = true;
			Response res = offeringsInStoreService.listOfferingsInStore(STORE_NAME, offset, max, orderBy, desc);
			
			// Verify
			verify(offeringBoMock).getStoreOfferingsPage(STORE_NAME, offset, max, orderBy, desc);
			
			// Check exception
			GenericRestTestUtils.checkAPIError(res, errorCode, errorType, message);
		} catch (Exception e) {
			fail("Exception not expected", e);
		}
	}
	
	@Test
	public void testListOfferingsInStoreRunTimeException() {
		String message = "exception";
		testListOfferingsInStoreException("name", new RuntimeException("", new Exception(message)), 
				500, ErrorType.INTERNAL_SERVER_ERROR, message);
	}
	
	@Test
	public void testListOfferingsInStoreStoreNotFoundException() {
		String message = "exception";
		testListOfferingsInStoreException("name", new StoreNotFoundException(message), 
				404, ErrorType.NOT_FOUND, message);
	}
	
	@Test
	public void testListOfferingsInStoreUnauthorizedException() {
		String userName = "user-example";
		User user = mock(User.class);
		when(user.getUserName()).thenReturn(userName);
		Exception e = new NotAuthorizedException("list offerings");
	
		testListOfferingsInStoreException("name", e, 403, ErrorType.FORBIDDEN, e.getMessage());
	}
	
	
	@Test
	public void testListAllOfferingsInStoreSQLGrammarException() throws NotAuthorizedException {
		String orderBy = "name";
		testListOfferingsInStoreException(orderBy, new SQLGrammarException("", new SQLException()), 400, 
				ErrorType.BAD_REQUEST, "Offerings cannot be ordered by " + orderBy + ".");
	}
	
	@Test
	public void testListAllOfferingsInStoreQueryException() throws NotAuthorizedException {
		String orderBy = "name";
		testListOfferingsInStoreException(orderBy, new QueryException(""), 400, 
				ErrorType.BAD_REQUEST, "Offerings cannot be ordered by " + orderBy + ".");
	}
	
}
