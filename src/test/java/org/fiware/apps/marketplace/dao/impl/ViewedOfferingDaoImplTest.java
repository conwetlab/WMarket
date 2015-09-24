package org.fiware.apps.marketplace.dao.impl;

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
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.fiware.apps.marketplace.dao.UserDao;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.ViewedOffering;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ViewedOfferingDaoImplTest {
	
	@Mock private UserDao userDaoMock;
	@Mock private SessionFactory sessionFactory; 
	@InjectMocks private ViewedOfferingDaoImpl viewedOfferingDao = new ViewedOfferingDaoImpl();
	
	@Before 
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}
	
	private void testGetOfferingsViewedByOtherUsers(List<ViewedOffering> bbddOfferings, 
			List<ViewedOffering> expectedOfferings, int max) {
		
		try {
			
			Query query = mock(Query.class);
			doReturn(query).when(query).setParameter(anyString(), anyObject());
			doReturn(bbddOfferings).when(query).list();
			
			Session session = mock(Session.class);
			doReturn(query).when(session).createQuery(anyString());
			
			User user = mock(User.class);
			
			doReturn(session).when(sessionFactory).getCurrentSession();
			doReturn(user).when(userDaoMock).findByName(anyString());
	
			List<ViewedOffering> returnedOfferings = viewedOfferingDao.getOfferingsViewedByOtherUsers("", max);
			
			// Verifications
			String className = "org.fiware.apps.marketplace.model.ViewedOffering";
			verify(session).createQuery(String.format("FROM %s where user != :user order by date desc", className));
			verify(query).setParameter("user", user);
			
			// Check that the returned viewed offerings are OK!
			assertThat(returnedOfferings).isEqualTo(expectedOfferings);
		} catch (Exception ex) {
			fail("Exception not expected", ex);
		}
	}
	
	@Test
	public void testGetOfferingViewedByOtherUsersOneElement() {
		List<ViewedOffering> list = new ArrayList<>();
		
		ViewedOffering viewedOffering = mock(ViewedOffering.class);
		Offering offering = mock(Offering.class);
		doReturn(offering).when(viewedOffering).getOffering();
		list.add(viewedOffering);
		
		testGetOfferingsViewedByOtherUsers(list, list, 20);
	}
	
	@Test
	public void testGetOfferingViewedByOtherUsersTwoElementsDifferentOffering() {
		List<ViewedOffering> list = new ArrayList<>();
		
		ViewedOffering viewedOffering1 = mock(ViewedOffering.class);
		Offering offering1 = mock(Offering.class);
		doReturn(offering1).when(viewedOffering1).getOffering();
		
		ViewedOffering viewedOffering2 = mock(ViewedOffering.class);
		Offering offering2 = mock(Offering.class);
		doReturn(offering2).when(viewedOffering2).getOffering();
		
		list.add(viewedOffering1);
		list.add(viewedOffering2);
		
		testGetOfferingsViewedByOtherUsers(list, list, 20);
	}
	
	@Test
	public void testGetOfferingViewedByOtherUsersTwoElementsDifferentOfferingMax() {
		List<ViewedOffering> bbddList = new ArrayList<>();
		List<ViewedOffering> expectedList = new ArrayList<>();

		// Mock objects
		ViewedOffering viewedOffering1 = mock(ViewedOffering.class);
		Offering offering1 = mock(Offering.class);
		doReturn(offering1).when(viewedOffering1).getOffering();
		
		ViewedOffering viewedOffering2 = mock(ViewedOffering.class);
		Offering offering2 = mock(Offering.class);
		doReturn(offering2).when(viewedOffering2).getOffering();
		
		// Fill the lists
		bbddList.add(viewedOffering1);
		bbddList.add(viewedOffering2);
		expectedList.add(viewedOffering1);

		testGetOfferingsViewedByOtherUsers(bbddList, expectedList, 1);
	}
	
	@Test
	public void testGetOfferingViewedByOtherUsersTwoElementsSameOffering() {
		List<ViewedOffering> bbddList = new ArrayList<>();
		List<ViewedOffering> expectedList = new ArrayList<>();
		
		// Mock objects
		Offering offering= mock(Offering.class);
		
		ViewedOffering viewedOffering1 = mock(ViewedOffering.class);
		doReturn(offering).when(viewedOffering1).getOffering();
		
		ViewedOffering viewedOffering2 = mock(ViewedOffering.class);
		doReturn(offering).when(viewedOffering2).getOffering();
		
		// Fill the lists
		bbddList.add(viewedOffering1);
		bbddList.add(viewedOffering2);
		expectedList.add(viewedOffering1);
		
		// Call the method
		testGetOfferingsViewedByOtherUsers(bbddList, expectedList, 20);
	}

}
