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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.fiware.apps.marketplace.bo.impl.StoreBoImpl;
import org.fiware.apps.marketplace.dao.StoreDao;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
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
	
	private static final String NAME = "wstore";
	private static final String DISPLAY_NAME = "WStore";
	private static final String NOT_AUTHORIZED_BASE = "You are not authorized to %s";
	
	@Before 
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.storeBo = spy(this.storeBo);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// SAVE ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testSaveException(Store store) throws Exception {
		
		try {
			// Call the method
			storeBo.save(store);
			fail("Exception expected");
		} catch (Exception e) {
			// Verify that the DAO has not been called
			verify(storeDaoMock, never()).save(store);	
			// Throw the exception
			throw e;
		}
	}
	
	@Test
	public void testSaveNotAuthorized() throws Exception {
		try {
			Store store = mock(Store.class);
			when(store.getDisplayName()).thenReturn(DISPLAY_NAME);
			when(storeAuthMock.canCreate(store)).thenReturn(false);
			
			// Call the method and check that DAO is not called
			testSaveException(store);
			
			// If the exception is not risen, the method is not properly working
			failBecauseExceptionWasNotThrown(NotAuthorizedException.class);
		} catch (NotAuthorizedException ex) {
			assertThat(ex.getMessage()).isEqualTo(String.format(NOT_AUTHORIZED_BASE, "create store"));
		}
	}
	
	@Test(expected=ValidationException.class)
	public void testSaveInvalidUser() throws Exception {
		
		Store store = mock(Store.class);
		when(store.getDisplayName()).thenReturn(DISPLAY_NAME);
		doThrow(new ValidationException("a field", "invalid")).when(storeValidatorMock).validateNewStore(store);
		when(storeAuthMock.canCreate(store)).thenReturn(true);
		
		// Call the method and check that DAO is not called
		testSaveException(store);
	}
	
	@Test
	public void testSave() {
		
		Store store = mock(Store.class);
		when(store.getDisplayName()).thenReturn(DISPLAY_NAME);
		when(storeAuthMock.canCreate(store)).thenReturn(true);
		
		try {
			storeBo.save(store);
			
			// Verify that the DAO has been called
			verify(storeDaoMock).save(store);
			
			// Verify that the name has been properly set.
			verify(store).setName(NAME);
			
		} catch (Exception e) {
			fail("Exception not expected", e);
		}
	}

	
	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// UPDATE ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testUpdateException(String storeName, Store updatedStore) throws Exception {		
		// Call the method
		try {
			storeBo.update(storeName, updatedStore);
			fail("Exception expected");
		} catch (Exception e) {
			verify(storeDaoMock, never()).update(any(Store.class));
			throw e;
		}
	}
	
	@Test(expected=StoreNotFoundException.class)
	public void testUpdateNotFound() throws Exception {
		
		Store updatedStore = mock(Store.class);
		
		// Configure mocks
		doThrow(new StoreNotFoundException("Store not found")).when(storeDaoMock).findByName(NAME);
		
		// Execute the function an check that DAO has not been called
		testUpdateException(NAME, updatedStore);
	}

	@Test(expected=ValidationException.class)
	public void testUpdateNotValid() throws Exception {
		
		Store storeToUpdate = mock(Store.class);
		Store updatedStore = mock(Store.class);
		
		// Configure mocks
		doReturn(storeToUpdate).when(storeDaoMock).findByName(NAME);
		doThrow(new ValidationException("a field", "not valid")).when(storeValidatorMock)
				.validateUpdatedStore(storeToUpdate, updatedStore);
		when(storeAuthMock.canUpdate(storeToUpdate)).thenReturn(true);
		
		// Execute the function an check that DAO has not been called
		testUpdateException(NAME, updatedStore);
	}
	
	@Test
	public void testUpdateNotAuthorized() throws Exception {
		try {
			Store storeToUpdate = mock(Store.class);
			Store updatedStore = mock(Store.class);
			
			// Configure mocks
			doReturn(storeToUpdate).when(storeDaoMock).findByName(NAME);
			when(storeAuthMock.canUpdate(storeToUpdate)).thenReturn(false);
			
			// Execute the function an check that DAO has not been called
			testUpdateException(NAME, updatedStore);
			
			// If the exception is not risen, the method is not properly working
			failBecauseExceptionWasNotThrown(NotAuthorizedException.class);

		} catch (NotAuthorizedException ex) {
			assertThat(ex.getMessage()).isEqualTo(String.format(NOT_AUTHORIZED_BASE, "update store"));
		}
	}
	
	private void testUpdateStoreField(Store updatedStore) {
		try {
			
			User user = mock(User.class);
			
			Store store = new Store();
			store.setName(NAME);
			store.setUrl("http://store.lab.fiware.org");
			store.setComment("Basic Comment");
			store.setLasteditor(null);
			
			// Mock
			doReturn(user).when(userBoMock).getCurrentUser();
			doReturn(store).when(storeDaoMock).findByName(NAME);
			when(storeAuthMock.canUpdate(store)).thenReturn(true);
			
			String previousStoreName = store.getName();

			// Call the method
			storeBo.update(NAME, updatedStore);

			// New values
			String newStoreName = updatedStore.getName() != null ? updatedStore.getName() : store.getName();
			assertThat(store.getName()).isEqualTo(newStoreName);

			String newStoreUrl = updatedStore.getUrl() != null ? updatedStore.getUrl() : store.getUrl();
			assertThat(store.getUrl()).isEqualTo(newStoreUrl);

			String newStoreDescription = updatedStore.getComment() != null ? 
					updatedStore.getComment() : store.getComment();
			assertThat(store.getComment()).isEqualTo(newStoreDescription);
			
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
	public void testUpdateStoreComment() {
		Store newStore = new Store();
		newStore.setComment("New Comment");
		testUpdateStoreField(newStore);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// DELETE ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testDeleteException(String userName) throws Exception {
		
		try {			
			// Call the method
			storeBo.delete(userName);
			fail("Exception expected");
		} catch (Exception e) {
			// Verify that the DAO has not been called
			verify(storeDaoMock, never()).delete(any(Store.class));
			
			// Throw the exception
			throw e;
		}

	}
	
	@Test(expected=StoreNotFoundException.class)
	public void testDeleteStoreNotFoundException() throws Exception {
		doThrow(new StoreNotFoundException("userNotFound")).when(storeDaoMock).findByName(NAME);
		testDeleteException(NAME);
	}
	
	@Test
	public void testDeleteNotAuthorizedException() throws Exception {
		try {
			Store store = mock(Store.class);
			
			doReturn(store).when(storeDaoMock).findByName(NAME);
			when(storeAuthMock.canDelete(store)).thenReturn(false);
			
			testDeleteException(NAME);
			
			// If the exception is not risen, the method is not properly working
			failBecauseExceptionWasNotThrown(NotAuthorizedException.class);
		} catch (NotAuthorizedException ex) {
			assertThat(ex.getMessage()).isEqualTo(String.format(NOT_AUTHORIZED_BASE, "delete store"));
		}

		
	}
	
	@Test
	public void testDelete() throws Exception {
		Store store = mock(Store.class);
		
		// Configure Mock
		doReturn(store).when(storeDaoMock).findByName(NAME);
		when(storeAuthMock.canDelete(store)).thenReturn(true);
		
		// Call the method
		storeBo.delete(NAME);
		
		// Verify that the method has been called
		verify(storeDaoMock).delete(store);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// FIND BY NAME /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=StoreNotFoundException.class)
	public void testFindByNameException() throws Exception {
		doThrow(new StoreNotFoundException("store not found")).when(storeDaoMock).findByName(NAME);
		
		storeBo.findByName(NAME);
	}
	
	@Test
	public void testFinByNameNotAuthorized() throws Exception{
		
		Store store = mock(Store.class);
		
		try {
			
			// Set up mocks
			when(storeDaoMock.findByName(NAME)).thenReturn(store);
			when(storeAuthMock.canGet(store)).thenReturn(false);
			
			// Call the function
			storeBo.findByName(NAME);
			
			// In an exception is no risen, the test should fail
			failBecauseExceptionWasNotThrown(NotAuthorizedException.class);
			
		} catch (NotAuthorizedException ex) {
			assertThat(ex.getMessage()).isEqualTo(String.format(NOT_AUTHORIZED_BASE, "find store"));
		}
		
		// Verifications
		verify(storeDaoMock).findByName(NAME);


	}

	@Test
	public void testFindByName() throws Exception {
		Store store = mock(Store.class);
		
		// Set up mocks
		when(storeDaoMock.findByName(NAME)).thenReturn(store);
		when(storeAuthMock.canGet(store)).thenReturn(true);
		
		// Call the function
		Store returnedStore = storeBo.findByName(NAME);
		
		// Verifications
		assertThat(returnedStore).isEqualTo(store);
		verify(storeDaoMock).findByName(NAME);

	}

	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////// GET ALL STORES ////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testGetAllStoresNotAuthorized() throws Exception {
		try {
			//Mocking
			when(storeAuthMock.canList()).thenReturn(false);
			
			// Call the function
			storeBo.getAllStores();
			
			// If the exception is not risen, the method is not properly working
			failBecauseExceptionWasNotThrown(NotAuthorizedException.class);
		} catch (NotAuthorizedException ex) {
			assertThat(ex.getMessage()).isEqualTo(String.format(NOT_AUTHORIZED_BASE, "list stores"));
		}
	}
	
	@Test
	public void testGetAllStores() throws Exception {
		
		@SuppressWarnings("unchecked")
		List<Store> stores = mock(List.class);
		
		when(storeDaoMock.getAllStores()).thenReturn(stores);
		when(storeAuthMock.canList()).thenReturn(true);
		
		// Call the function
		assertThat(storeBo.getAllStores()).isEqualTo(stores);
		
		// Verify that the DAO is called
		verify(storeDaoMock).getAllStores();
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////// GET STORES PAGE ///////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testGetStoresPageNotAuthorized() throws Exception {
		try {
			// Mocking
			when(storeAuthMock.canList()).thenReturn(false);
			
			// Call the function
			storeBo.getStoresPage(0, 7);
			
			// If the exception is not risen, the method is not properly working
			failBecauseExceptionWasNotThrown(NotAuthorizedException.class);
		} catch (NotAuthorizedException ex) {
			assertThat(ex.getMessage()).isEqualTo(String.format(NOT_AUTHORIZED_BASE, "list stores"));
		}
	}
	
	@Test
	public void testGetStoresPage() throws Exception {
		
		@SuppressWarnings("unchecked")
		List<Store> stores = mock(List.class);
		
		int offset = 8;
		int max = 22;
		
		when(storeDaoMock.getStoresPage(offset, max)).thenReturn(stores);
		when(storeAuthMock.canList()).thenReturn(true);
		
		// Call the function
		assertThat(storeBo.getStoresPage(offset, max)).isEqualTo(stores);
		
		// Verify that the DAO is called
		verify(storeDaoMock).getStoresPage(offset, max);
	}
}