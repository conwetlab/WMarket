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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.core.Response;

import org.fiware.apps.marketplace.bo.CategoryBo;
import org.fiware.apps.marketplace.exceptions.CategoryNotFoundException;
import org.fiware.apps.marketplace.model.APIError;
import org.fiware.apps.marketplace.model.Categories;
import org.fiware.apps.marketplace.model.Category;
import org.fiware.apps.marketplace.model.ErrorType;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.model.Offerings;
import org.hibernate.QueryException;
import org.hibernate.exception.SQLGrammarException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CategoriesServiceTest {
	
	@Mock private CategoryBo categoryBoMock;
	@InjectMocks private CategoriesService service;
	
	@Before 
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// GET CATEGORIES ///////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testGetCategories() {
		
		int offset = 7;
		int max = 30;
		
		// Mocking
		@SuppressWarnings("unchecked")
		List<Category> categories = mock(List.class);
		when(categoryBoMock.getCategoriesPage(offset, max)).thenReturn(categories);
		
		// Call the function
		Response res = service.getCategories(offset, max);
		
		// Check response
		assertThat(res.getStatus()).isEqualTo(200);
		Categories returnedCategories = (Categories) res.getEntity();
		assertThat(returnedCategories.getCategories()).isEqualTo(categories);
		
	}
	
	@Test
	public void testGetCategoriesException() {
		
		int offset = 9;
		int max = 90;
		
		// Mocking
		String exceptionMsg = "error";
		doThrow(new RuntimeException(new Exception(exceptionMsg)))
				.when(categoryBoMock).getCategoriesPage(offset, max);
		
		// Call the function
		Response res = service.getCategories(offset, max);
		
		// Check response
		assertThat(res.getStatus()).isEqualTo(500);
		APIError error = (APIError) res.getEntity();
		assertThat(error.getErrorMessage()).isEqualTo(exceptionMsg);
		assertThat(error.getErrorType()).isEqualTo(ErrorType.INTERNAL_SERVER_ERROR);
		
		// Verify that categoryBoMock has been properly called
		verify(categoryBoMock).getCategoriesPage(offset, max);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////// GET RECOMMENDATIONS /////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testGetCategoryRecommendations() throws Exception {
		
		String categoryName = "dataset";
		String orderBy = "age";
		boolean desc = false;
		
		// Mock
		@SuppressWarnings("unchecked")
		List<Offering> offerings = mock(List.class);
		when(categoryBoMock.getCategoryOfferingsSortedBy(categoryName, orderBy, desc)).thenReturn(offerings);
		
		// Call the function
		Response res = service.getCategoryRecommendations(categoryName, orderBy, desc);
		
		// Check response
		assertThat(res.getStatus()).isEqualTo(200);
		assertThat(((Offerings) res.getEntity()).getOfferings()).isEqualTo(offerings);
		
		// Verify that categoryBoMock has been properly called
		verify(categoryBoMock).getCategoryOfferingsSortedBy(categoryName, orderBy, desc);
	}
	
	private void testGetCategoryRecommendationsException(String orderBy, Exception ex,
			int errorStatus, ErrorType errorType, String expectedErrorMsg) {
		
		String categoryName = "dataset";
		boolean desc = false;
		
		try {
			// Mock
			doThrow(ex).when(categoryBoMock).getCategoryOfferingsSortedBy(categoryName, orderBy, desc);
			
			// Call the function
			Response res = service.getCategoryRecommendations(categoryName, orderBy, desc);
			
			// Check response
			assertThat(res.getStatus()).isEqualTo(errorStatus);
			APIError error = (APIError) res.getEntity();
			assertThat(error.getErrorType()).isEqualTo(errorType);
			assertThat(error.getErrorMessage()).isEqualTo(expectedErrorMsg);
			
			// Verify that categoryBoMock has been properly called
			verify(categoryBoMock).getCategoryOfferingsSortedBy(categoryName, orderBy, desc);
			
		} catch (Exception e) {
			fail("Exception not expected", e);
		}
		
	}
	
	private void testGetCategoryRecommendationsOrderError(Exception ex) {
		String orderBy = "averageScore";
		testGetCategoryRecommendationsException(orderBy, ex, 400, ErrorType.BAD_REQUEST, 
				"Offerings cannot be ordered by " + orderBy + ".");
	}
	
	@Test
	public void testGetCategoryRecommendationsQueryException() {
		testGetCategoryRecommendationsOrderError(new QueryException(new Exception()));
	}
	
	@Test
	public void testGetCategoryRecommendationsSQLGrammarException() {
		testGetCategoryRecommendationsOrderError(new SQLGrammarException("", new SQLException()));
	}
	
	@Test
	public void testGetCategoryRecommendationsCategoryNotFound() {
		String categoryExceptionMsg = "category not found";
		testGetCategoryRecommendationsException("name", new CategoryNotFoundException(categoryExceptionMsg), 
				404, ErrorType.NOT_FOUND, categoryExceptionMsg);
	}
	
	@Test
	public void testGetCategoryRecommendationsNotHandledException() {
		String categoryExceptionMsg = "unhandled error";
		testGetCategoryRecommendationsException("name", new RuntimeException(new Exception(categoryExceptionMsg)), 
				500, ErrorType.INTERNAL_SERVER_ERROR, categoryExceptionMsg);
	}

}
