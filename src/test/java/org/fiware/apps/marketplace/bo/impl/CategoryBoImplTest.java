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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.fiware.apps.marketplace.dao.CategoryDao;
import org.fiware.apps.marketplace.exceptions.CategoryNotFoundException;
import org.fiware.apps.marketplace.model.Category;
import org.fiware.apps.marketplace.model.Offering;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class CategoryBoImplTest {
	
	@Mock private CategoryDao categoryDaoMock;
	@InjectMocks private CategoryBoImpl categoryBo;
	
	@Before 
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	private void testIsNameAvailable(boolean isAvailable) {
		String categoryName = "category";
		when(categoryDaoMock.isNameAvailable(categoryName)).thenReturn(isAvailable);
		
		// Call the function and check that DAO is properly called
		assertThat(categoryBo.isNameAvailable(categoryName)).isEqualTo(isAvailable);
		verify(categoryDaoMock).isNameAvailable(categoryName);
	}
	
	@Test
	public void testIsNameAvailableTrue() {
		testIsNameAvailable(true);
	}
	
	@Test
	public void testIsNameAvailableFalse() {
		testIsNameAvailable(false);
	}
	
	@Test
	public void testFindByNameFound() throws Exception{
		String categoryName = "category";
		Category category = mock(Category.class);
		when(categoryDaoMock.findByName(categoryName)).thenReturn(category);
		
		// Call the function and check that DAO is properly called
		assertThat(categoryBo.findByName(categoryName)).isEqualTo(category);
		verify(categoryDaoMock).findByName(categoryName);
	}
	
	@Test(expected=CategoryNotFoundException.class)
	public void testFindByNameNotFound() throws Exception {
		String categoryName = "category";
		doThrow(new CategoryNotFoundException("")).when(categoryDaoMock).findByName(categoryName);
		
		// Call the function (should throw exception)
		categoryBo.findByName(categoryName);
	}
	
	@Test
	public void testGetCategoryOfferingsSortedByFound() throws Exception {
		String categoryName = "category";
		int offset = 8;
		int max = 19;
		String orderBy = "averageScore";
		boolean desc = true;
		
		// Mocking
		@SuppressWarnings("unchecked")
		List<Offering> offerings = mock(List.class);
		when(categoryDaoMock.getCategoryOfferingsSortedBy(categoryName, offset, max, orderBy, desc))
				.thenReturn(offerings);
		
		// Call the function
		assertThat(categoryBo.getCategoryOfferingsSortedBy(categoryName, offset, max, orderBy, desc))
				.isEqualTo(offerings);
	}
	
	@Test(expected=CategoryNotFoundException.class)
	public void testGetCategoryOfferingsSortedByNotFound() throws Exception {
		String categoryName = "category";
		int offset = 1;
		int max = 7;
		String orderBy = "averageScore";
		boolean desc = true;
		
		// Mocking
		doThrow(new CategoryNotFoundException("")).when(categoryDaoMock).getCategoryOfferingsSortedBy(
				categoryName, offset, max, orderBy, desc);
		
		// Call the function
		categoryBo.getCategoryOfferingsSortedBy(categoryName, offset, max, orderBy, desc);
	}
	
	@Test
	public void testGetCategoriesPage() {
		
		int offset = 8;
		int max = 20;
		
		// Mocking
		@SuppressWarnings("unchecked")
		List<Category> categories = mock(List.class);
		when(categoryDaoMock.getCategoriesPage(offset, max)).thenReturn(categories);
		
		// Call the function
		assertThat(categoryBo.getCategoriesPage(offset, max)).isEqualTo(categories);
		verify(categoryDaoMock).getCategoriesPage(offset, max);
		
	}
	
	@Test
	public void testGetAllCategories() {
		
		// Mocking
		@SuppressWarnings("unchecked")
		List<Category> categories = mock(List.class);
		when(categoryDaoMock.getAllCategories()).thenReturn(categories);
		
		// Call the function
		assertThat(categoryBo.getAllCategories()).isEqualTo(categories);
		verify(categoryDaoMock).getAllCategories();
	}
	
}
