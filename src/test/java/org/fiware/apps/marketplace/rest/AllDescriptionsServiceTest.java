package org.fiware.apps.marketplace.rest;

/*
 * #%L
 * FiwareMarketplace
 * %%
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.fiware.apps.marketplace.bo.DescriptionBo;
import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.Description;
import org.fiware.apps.marketplace.model.Descriptions;
import org.fiware.apps.marketplace.security.auth.DescriptionRegistrationAuth;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AllDescriptionsServiceTest {
	
	@Mock private DescriptionBo descriptionBoMock;
	@Mock private DescriptionRegistrationAuth descriptionRegistrationAuthMock;
	
	@InjectMocks private AllDescriptionsService allOfferingsDescriptionsService;
	
	private static final String OFFSET_MAX_INVALID = "offset (%d) and/or max (%d) are not valid";
	
	@Before 
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testListAllDescriptionsNotAllowed() {
		// Mocks
		when(descriptionRegistrationAuthMock.canList()).thenReturn(false);

		// Call the method
		Response res = allOfferingsDescriptionsService.listOfferingsDescriptions(0, 100);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 401, ErrorType.UNAUTHORIZED, 
				"You are not authorized to list offerings");
	}
	
	
	private void testListAllDescriptionsInvalidParams(int offset, int max) {
		// Mocks
		when(descriptionRegistrationAuthMock.canList()).thenReturn(true);

		// Call the method
		Response res = allOfferingsDescriptionsService.listOfferingsDescriptions(offset, max);

		// Assertions
		GenericRestTestUtils.checkAPIError(res, 400, ErrorType.BAD_REQUEST, 
				String.format(OFFSET_MAX_INVALID, offset, max));
	}
	
	@Test
	public void testListAllDescriptionsInvalidOffset() {
		testListAllDescriptionsInvalidParams(-1, 100);
	}
	
	@Test
	public void testListAllDescriptionsInvalidMax() {
		testListAllDescriptionsInvalidParams(0, -1);
	}
	
	@Test
	public void testListAllDescriptionsInvalidOffsetMax() {
		testListAllDescriptionsInvalidParams(-1, -1);
	}
	
	@Test
	public void testListAllDescriptionsGetNoErrors() {
		List<Description> oferringsDescriptions = new ArrayList<Description>();
		for (int i = 0; i < 3; i++) {
			Description offeringDescription = new Description();
			offeringDescription.setId(i);
			oferringsDescriptions.add(offeringDescription);
		}
		
		// Mocks
		when(descriptionRegistrationAuthMock.canList()).thenReturn(true);
		when(descriptionBoMock.getDescriptionsPage(anyInt(), anyInt())).
				thenReturn(oferringsDescriptions);
		
		// Call the method
		int offset = 0;
		int max = 100;
		Response res = allOfferingsDescriptionsService.listOfferingsDescriptions(offset, max);
		
		// Verify
		verify(descriptionBoMock).getDescriptionsPage(offset, max);
		
		// Assertions
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat(((Descriptions) res.getEntity()).
				getDescriptions()).isEqualTo(oferringsDescriptions);
	}
	
	@Test
	public void testListAllDescriptionsException() {
		// Mocks
		String exceptionMsg = "exception";
		doThrow(new RuntimeException("", new Exception(exceptionMsg))).when(descriptionBoMock).
				getDescriptionsPage(anyInt(), anyInt());
		when(descriptionRegistrationAuthMock.canList()).thenReturn(true);

		// Call the method
		int offset = 0;
		int max = 100;
		Response res = allOfferingsDescriptionsService.listOfferingsDescriptions(offset, max);
		
		// Verify
		verify(descriptionBoMock).getDescriptionsPage(offset, max);
		
		// Check exception
		GenericRestTestUtils.checkAPIError(res, 500, ErrorType.INTERNAL_SERVER_ERROR, exceptionMsg);
	}
}
