package org.fiware.apps.marketplace.bo;

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

import org.fiware.apps.marketplace.bo.impl.StoreBoImpl;
import org.fiware.apps.marketplace.dao.StoreDao;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.validators.StoreValidator;
import org.fiware.apps.marketplace.security.auth.StoreAuth;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StoreBoImplTest {
	
	@Mock private StoreAuth storeAuthMock;
	@Mock private StoreValidator storeValidatorMock;
	@Mock private StoreDao storeDaoMock;
	@Mock private UserBo userBoMock;
	@InjectMocks private StoreBoImpl storeBo;
	
	private static final String NAME = "store";
	
	@Before 
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.storeBo = spy(this.storeBo);
	}
	
	// TODO: Add more tests!
	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// UPDATE ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private void testUpdateStoreField(Store updatedStore) {
		try {
			
			User user = mock(User.class);
			
			Store store = new Store();
			store.setName(NAME);
			store.setUrl("http://store.lab.fiware.org");
			store.setDescription("Basic Description");
			store.setLasteditor(null);
			
			// Mock
			doReturn(user).when(userBoMock).getCurrentUser();
			doReturn(store).when(storeBo).findByName(NAME);
			when(storeAuthMock.canUpdate(store)).thenReturn(true);
			
			String previousStoreName = store.getName();

			// Call the method
			storeBo.update(NAME, updatedStore);

			// New values
			String newStoreName = updatedStore.getName() != null ? updatedStore.getName() : store.getName();
			assertThat(store.getName()).isEqualTo(newStoreName);

			String newStoreUrl = updatedStore.getUrl() != null ? updatedStore.getUrl() : store.getUrl();
			assertThat(store.getUrl()).isEqualTo(newStoreUrl);

			String newStoreDescription = updatedStore.getDescription() != null ? 
					updatedStore.getDescription() : store.getDescription();
			assertThat(store.getDescription()).isEqualTo(newStoreDescription);
			
			// Assert that the name is not changed
			assertThat(store.getName()).isEqualTo(previousStoreName);
			
			// Assert that last modifier has changed
			assertThat(store.getLasteditor()).isEqualTo(user);
		} catch (Exception ex) {
			// It's not supposed to happen
			fail("Exception " + ex + " is not supposed to happen");
		}
	}

	@Test
	public void testUpdateStoreDisplayName() {
		Store newStore = new Store();
		newStore.setDisplayName("new_name");
		testUpdateStoreField(newStore);
	}

	@Test
	public void testUpdateStoreUrl() {
		Store newStore = new Store();
		newStore.setUrl("http://fiware.org");
		testUpdateStoreField(newStore);
	}

	@Test
	public void testUpdateStoreDescription() {
		Store newStore = new Store();
		newStore.setDescription("New Description");
		testUpdateStoreField(newStore);
	}

}
