package org.fiware.apps.marketplace.bo.impl;

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

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;

import org.fiware.apps.marketplace.bo.RatingBo;
import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.bo.impl.StoreBoImpl;
import org.fiware.apps.marketplace.controllers.MediaContentController;
import org.fiware.apps.marketplace.dao.StoreDao;
import org.fiware.apps.marketplace.exceptions.NotAuthorizedException;
import org.fiware.apps.marketplace.exceptions.RatingNotFoundException;
import org.fiware.apps.marketplace.exceptions.StoreNotFoundException;
import org.fiware.apps.marketplace.exceptions.ValidationException;
import org.fiware.apps.marketplace.model.Rating;
import org.fiware.apps.marketplace.model.Store;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.model.validators.StoreValidator;
import org.fiware.apps.marketplace.security.auth.StoreAuth;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

public class StoreBoImplTest {
	
	@Rule public TemporaryFolder mediaFolder = new TemporaryFolder();
	
	@Mock private StoreAuth storeAuthMock;
	@Mock private StoreValidator storeValidatorMock;
	@Mock private StoreDao storeDaoMock;
	@Mock private UserBo userBoMock;
	@Mock private RatingBo ratingBoMock;
	@InjectMocks private StoreBoImpl storeBo;
	
	private static final String NAME = "wstore";
	private static final String DISPLAY_NAME = "WStore";
	private static final String NOT_AUTHORIZED_BASE = "You are not authorized to %s";
	private static final String MEDIA_URL = MediaContentController.class.getAnnotation(Path.class).value();
	
	private static final String IMAGE_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAACXBIWXMAAAsTAA"
			+ "ALEwEAmpwYAAADpElEQVRIDa1WS0ubURAdk/gAN75QCApGjIItSIOuRMSiIrhzIUj7J1pL7UZ/QEH6DxIhdK8uJFio+Fi4ykLsp"
			+ "oJ1YVqtulB8mzg9Z5r79YsIyaIDk/v4zj0z987MvSlTVXFSBkE/iLks5zB8gWYE+hLaAW2FUvah36FfoV+AT6MlPoQmh/E/Uvbz"
			+ "Y0dO7DNoHEpgKZoA7nmeJ4h+mcf7BPkrADKhUEiDwWBRcuKoWPMT+jrPF3BG0Irf8zcYF5AGAgEtLy83EkfGlnM4Eg/r60/5d0I"
			+ "DofwEPfcWjI+Pa19fnzf2f/P3R0dHdWJi4jHO7SRkZwXrPPMVeBt+eHiQhoYGyWQyUlFRIQsLC5JIJKSrq0taWloE3svp6ans7u"
			+ "7K5OSkDA8Py8XFhTQ3N8vZ2ZmAQ8DxC3wjcHzHBdgCyuOgd5FIRLEI30uTk5MThVO21nGAZx6r0YgwFQs+1tTU6PHxsbHf3d1pL"
			+ "pfT+/v7As1mszYm6OrqSjs7O43jUWLEaGDaGchng87MzBg5SYoJDVNSqZQZ8AWb4w80kKIB94Ee7O3t2aJSDOC8DctdRqNRkmZ9"
			+ "u1hhvrJCWYVsLMB1dXXWZ8CKCdfBggU3HA4THnRc6EfJEOEss4dyfX0tt7e31ufCYkKMI7y5uTE4duOWtXouMiUrKysFAZPDw0M"
			+ "DlGLAkSMWcnl5acaqq6udAaGBHxw1NjZKW1ub1NfXy9ramgEQA6E3TxniHHftvEbcrB7a29ultrbW1uNnnx0LMnag2AHPxHR9fR"
			+ "0cpcvQ0JCt4xXiDzINeGlKcnigTU1NBp6dnVVUrCIuXs67rOEcKlqXlpa0t7fX8L4iszH4LE2t0NxHGlhcXNTu7m4H0v7+fsVRF"
			+ "GwnmUxqVVWVh/F57c3BQMydr10V3B53MTAwoNvb2zo4OKg4T43H40Z+fn6uR0dH1uf1wIon3q1j36d/rwqgMWcPTIYfXTXPzc0p"
			+ "j4OklNXVVTO6sbGhW1tbistQY7GYEbrd+8j5NtgDFECa8br+hon3UGHmUKampqSnp0fGxsZkeXnZsompiKMQGLWb09WOLSj8mQb"
			+ "nDrmZpjl0+A5/Rv8tccxtVnE6nRZ4bPnN6gbGy3Ne6Tgawr1Cs4HIO+CS5MQ4R+95Dz3kjXxC+xtTH6FhEtBrxMMKsKOjQw4ODm"
			+ "w3m5ubghvXOIFlyzeAnpOcjtslZV7lAf6nkw9QAmpnXEI7D8yTj769aPhoAsvOyH/72/IH8JNvDJtE0dkAAAAASUVORK5CYII=";
	
	private String getRelativeImagePath(Store store) {
		return "store/" + store.getName() + ".png";
	}
	
	@Before 
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
		// Set media folder for testing
		ReflectionTestUtils.setField(storeBo, "mediaFolder", mediaFolder.getRoot().getAbsolutePath());

		// Sometimes we need to mock methods
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
	public void testSaveInvalidStore() throws Exception {
		
		Store store = mock(Store.class);
		when(store.getDisplayName()).thenReturn(DISPLAY_NAME);
		doThrow(new ValidationException("a field", "invalid")).when(storeValidatorMock).validateNewStore(store);
		when(storeAuthMock.canCreate(store)).thenReturn(true);
		
		// Call the method and check that DAO is not called
		testSaveException(store);
	}
	
	private void testSave(boolean includeImage) {
		
		Store store = mock(Store.class);
		when(store.getName()).thenReturn(NAME);
		when(store.getDisplayName()).thenReturn(DISPLAY_NAME);
		when(storeAuthMock.canCreate(store)).thenReturn(true);
		
		if (includeImage) {
			when(store.getImageBase64()).thenReturn(IMAGE_BASE64);
		}
		
		try {
			storeBo.save(store);
			
			// Verify that the DAO has been called
			verify(storeDaoMock).save(store);
			
			// Verify that the name has been properly set.
			verify(store).setName(NAME);
			
			// Verify that the image has been set
			String imageName = getRelativeImagePath(store);
			
			int setImagePathTimes = includeImage ? 1 : 0;
			verify(store, times(setImagePathTimes)).setImagePath(MEDIA_URL + "/" + imageName);
			assertThat(Paths.get(mediaFolder.getRoot().getAbsolutePath(), imageName).toFile().exists()).isEqualTo(includeImage);
			
		} catch (Exception e) {
			fail("Exception not expected", e);
		}
	}
	
	@Test
	public void testSaveWithImage() {
		testSave(true);
	}
	
	@Test
	public void testSaveWithoutImage() {
		testSave(false);
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
			
			// When the image is updated
			String imageName = getRelativeImagePath(store);
			File imageFile = Paths.get(mediaFolder.getRoot().getAbsolutePath(), imageName).toFile();
			if (updatedStore.getImageBase64() != null) {
				
				assertThat(store.getImageBase64()).isEqualTo(IMAGE_BASE64);
				
				// Verify that the image has been set
				assertThat(store.getImagePath()).isEqualTo(MEDIA_URL + "/" + imageName);
				
				// Verify that the image has been created
				assertThat(imageFile.exists()).isTrue();		
			} else {
				assertThat(imageFile.exists()).isFalse();
			}
			
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
	
	@Test
	public void testUpdateStoreImage() {
		Store newStore = new Store();
		newStore.setImageBase64(IMAGE_BASE64);
		testUpdateStoreField(newStore);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// DELETE ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void testDeleteException(String storeName) throws Exception {
		
		try {			
			// Call the method
			storeBo.delete(storeName);
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
	
	private void testDelete(boolean fileExist) throws Exception {
		Store store = mock(Store.class);
		when(store.getName()).thenReturn(NAME);
		
		File imageFile = Paths.get(mediaFolder.getRoot().getAbsolutePath(), getRelativeImagePath(store)).toFile();
		
		// Create the image that should be deleted
		if (fileExist) {
			imageFile.getParentFile().mkdirs();		// Create required directories
			imageFile.createNewFile();
		}

		// Check image status
		assertThat(imageFile.exists()).isEqualTo(fileExist);
		
		// Configure Mock
		doReturn(store).when(storeDaoMock).findByName(NAME);
		when(storeAuthMock.canDelete(store)).thenReturn(true);
		
		// Call the method
		storeBo.delete(NAME);
		
		// Assert that image does not exist
		assertThat(imageFile.exists()).isFalse();
		
		// Verify that the method has been called
		verify(storeDaoMock).delete(store);
	}
	
	@Test
	public void testDeleteImageExist() throws Exception {
		testDelete(true);
	}
	
	@Test
	public void testDeleteImageNotExist() throws Exception {
		testDelete(false);
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

	private void testFindByName(boolean imageExist) throws Exception {
		
		Store store = mock(Store.class);
		when(store.getName()).thenReturn(NAME);
		
		String imageName = getRelativeImagePath(store);
		File imageFile = Paths.get(mediaFolder.getRoot().getAbsolutePath(), imageName).toFile();
		
		// Create the image that should be deleted
		if (imageExist) {
			imageFile.getParentFile().mkdirs();
			imageFile.createNewFile();
		}
		
		// Set up mocks
		when(storeDaoMock.findByName(NAME)).thenReturn(store);
		when(storeAuthMock.canGet(store)).thenReturn(true);
		
		// Call the function
		Store returnedStore = storeBo.findByName(NAME);
		
		// Verifications
		assertThat(returnedStore).isEqualTo(store);
		verify(storeDaoMock).findByName(NAME);
		
		// Check that image path has been set
		int setImagePathTimes = imageExist ? 1 : 0;
		verify(store, times(setImagePathTimes)).setImagePath(MEDIA_URL + "/" + imageName);

	}
	
	@Test
	public void testFindByNameImageExist() throws Exception {
		testFindByName(true);
	}
	
	@Test
	public void testFindByNameImageDoesNotExist() throws Exception {
		testFindByName(false);
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
		
		List<Store> stores = new ArrayList<Store>();
		
		// Add stores to the list
		Store store1 = mock(Store.class);
		when(store1.getName()).thenReturn(NAME);
		
		String storeName2 = NAME + "a";
		Store store2 = mock(Store.class);
		when(store2.getName()).thenReturn(storeName2);
		
		stores.add(store1);
		stores.add(store2);
		
		// Create image for store1
		String image1Name = getRelativeImagePath(store1);
		File imageFile = Paths.get(mediaFolder.getRoot().getAbsolutePath(), image1Name).toFile();
		imageFile.getParentFile().mkdirs();		// Create required directories
		imageFile.createNewFile();
		
		// Mocks
		when(storeDaoMock.getAllStores()).thenReturn(stores);
		when(storeAuthMock.canList()).thenReturn(true);
		
		// Call the function
		assertThat(storeBo.getAllStores()).isEqualTo(stores);
		
		// Verify that the DAO is called
		verify(storeDaoMock).getAllStores();
		
		// Verify that image path has been set for image1 and has not been set for image2
		verify(store1).setImagePath(MEDIA_URL + "/" + image1Name);
		verify(store2, never()).setImagePath(anyString());

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
		
		List<Store> stores = new ArrayList<Store>();
		
		// Add stores to the list
		Store store1 = mock(Store.class);
		when(store1.getName()).thenReturn(NAME);
		
		String storeName2 = NAME + "a";
		Store store2 = mock(Store.class);
		when(store2.getName()).thenReturn(storeName2);
		
		stores.add(store1);
		stores.add(store2);
		
		// Create image for store1
		String image1Name = getRelativeImagePath(store1);
		File imageFile = Paths.get(mediaFolder.getRoot().getAbsolutePath(), image1Name).toFile();
		imageFile.getParentFile().mkdirs();		// Create required directories
		imageFile.createNewFile();

		// Mocks
		int offset = 8;
		int max = 22;
		
		when(storeDaoMock.getStoresPage(offset, max)).thenReturn(stores);
		when(storeAuthMock.canList()).thenReturn(true);
		
		// Call the function
		assertThat(storeBo.getStoresPage(offset, max)).isEqualTo(stores);
		
		// Verify that the DAO is called
		verify(storeDaoMock).getStoresPage(offset, max);
		
		// Verify that image path has been set for image1 and has not been set for image2
		verify(store1).setImagePath(MEDIA_URL + "/" + image1Name);
		verify(store2, never()).setImagePath(anyString());

	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// CREATE RATING ////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=StoreNotFoundException.class)
	public void testCreateRatingStoreNotFoundException() throws Exception {
		
		String storeName = "store";

		// Configure mock
		Exception e = new StoreNotFoundException("");
		doThrow(e).when(storeDaoMock).findByName(storeName);

		// Call the function
		Rating rating = new Rating();
		storeBo.createRating(storeName, rating);
	}
	
	private void testCreateRatingException(Exception e) throws Exception {
		
		String storeName = "store";

		// Configure mock
		Store store = mock(Store.class);
		Rating rating = mock(Rating.class);
		doReturn(store).when(storeDaoMock).findByName(storeName);
		doThrow(e).when(ratingBoMock).createRating(store, rating);

		// Call the function
		storeBo.createRating(storeName, rating);		
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testCreateRatingNotAuthorizedException() throws Exception {
		testCreateRatingException(new NotAuthorizedException("create rating"));
	}
	
	@Test(expected=ValidationException.class)
	public void testCreateRatingValidationException() throws Exception {
		testCreateRatingException(new ValidationException("score", "create rating"));
	}
	
	@Test
	public void testCreateRating() throws Exception {
		
		String storeName = "store";
		Store store = mock(Store.class);

		// Configure mock
		doReturn(store).when(storeDaoMock).findByName(storeName);
		
		// Call the function
		Rating rating = new Rating();
		storeBo.createRating(storeName, rating);		
		
		// Verify that ratingBo has been called
		verify(ratingBoMock).createRating(store, rating);
		
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// UPDATE RATING ////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=StoreNotFoundException.class)
	public void testUpdateRatingNotFound() throws Exception {
		
		String storeName = "store";

		// Configure mock
		StoreNotFoundException e = new StoreNotFoundException("");
		doThrow(e).when(storeDaoMock).findByName(storeName);

		// Call the function
		Rating rating = new Rating();
		storeBo.updateRating(storeName, 9, rating);
	}
	
	private void testUpdateRatingException(Exception e) throws Exception {
		
		String storeName = "store";
		int ratingId = 9;

		// Configure mock
		Store store = mock(Store.class);
		Rating rating = mock(Rating.class);
		doReturn(store).when(storeDaoMock).findByName(storeName);
		doThrow(e).when(ratingBoMock).updateRating(store, ratingId, rating);

		// Call the function
		storeBo.updateRating(storeName, ratingId, rating);		
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testUpdateRatingNotAuthorizedException() throws Exception {
		testUpdateRatingException(new NotAuthorizedException("update rating"));
	}
	
	@Test(expected=ValidationException.class)
	public void testUpdateRatingValidationException() throws Exception {
		testUpdateRatingException(new ValidationException("score", "update rating"));
	}
	
	@Test
	public void testUpdateRating() throws Exception {
		
		String storeName = "store";
		Store store = mock(Store.class);

		// Configure mock
		doReturn(store).when(storeDaoMock).findByName(storeName);
		
		// Call the function
		int ratingId = 9;
		Rating rating = new Rating();
		storeBo.updateRating(storeName, ratingId, rating);
		
		// Verify that ratingBo has been called
		verify(ratingBoMock).updateRating(store, ratingId, rating);
		
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// GET RATINGS /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=StoreNotFoundException.class)
	public void testGetRatingsNotFound() throws Exception {
		
		String storeName = "store";

		// Configure mock
		StoreNotFoundException e = new StoreNotFoundException("");
		doThrow(e).when(storeDaoMock).findByName(storeName);

		// Call the function
		storeBo.getRatings(storeName);
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testGetRatingsNotAuthorized() throws Exception {
		
		String storeName = "store";
		Store store = mock(Store.class);
		
		// Configure mocks
		doReturn(store).when(storeDaoMock).findByName(storeName);
		doThrow(new NotAuthorizedException("")).when(ratingBoMock).getRatings(store);
		
		// Actual call
		storeBo.getRatings(storeName);
	}
	
	@Test
	public void testGetRatings() throws Exception {
		
		String storeName = "store";
		Store store = mock(Store.class);

		// Configure mock
		doReturn(store).when(storeDaoMock).findByName(storeName);
		
		@SuppressWarnings("unchecked")
		List<Rating> ratings = mock(List.class);
		doReturn(ratings).when(ratingBoMock).getRatings(store);
		
		// Actual call
		assertThat(storeBo.getRatings(storeName)).isEqualTo(ratings);
		
		// Verify that ratingBoMock has been properly called
		verify(ratingBoMock).getRatings(store);

	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// GET RATING //////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=StoreNotFoundException.class)
	public void testGetRatingNotFound() throws Exception {
		
		String storeName = "store";

		// Configure mock
		doThrow(new StoreNotFoundException("")).when(storeDaoMock).findByName(storeName);

		// Call the function
		storeBo.getRating(storeName, 9);
	}
	
	private void testGetRatingException(Exception ex) throws Exception {
		
		String storeName = "store";
		int ratingId = 9;
		Store store = mock(Store.class);

		// Configure mock
		doReturn(store).when(storeDaoMock).findByName(storeName);

		doThrow(ex).when(ratingBoMock).getRating(store, ratingId);
		
		// Actual call
		storeBo.getRating(storeName, ratingId);
	}
	
	@Test(expected=NotAuthorizedException.class)
	public void testGetRatingNotAuthorized() throws Exception {
		testGetRatingException(new NotAuthorizedException(""));
	}
	
	@Test(expected=RatingNotFoundException.class)
	public void testGetRatingRatingNotFound() throws Exception {
		testGetRatingException(new RatingNotFoundException(""));
	}
	
	@Test
	public void testGetRating() throws Exception {
				
		String storeName = "store";
		int ratingId = 9;
		Store store = mock(Store.class);

		// Configure mock
		doReturn(store).when(storeDaoMock).findByName(storeName);

		Rating rating = mock(Rating.class);
		doReturn(rating).when(ratingBoMock).getRating(store, ratingId);
		
		// Actual call
		storeBo.getRating(storeName, ratingId);
		
		// Verify that ratingBoMock has been properly called
		verify(ratingBoMock).getRating(store, ratingId);
	}
	
}