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

import java.util.List;

import org.fiware.apps.marketplace.exceptions.CategoryNotFoundException;
import org.fiware.apps.marketplace.model.Category;
import org.fiware.apps.marketplace.model.Offering;


public interface CategoryBo {
	
	/**
	 * Checks if a given category name exists
	 * @param name The category name to be checked
	 * @return true if the category name does not exist. false otherwise
	 */
	public boolean isNameAvailable(String name);
	
	/**
	 * Returns a category based on its name
	 * @param name The name of the category to be retrieved
	 * @return The category with the given name
	 * @throws CategoryNotFoundException If it does not exist a category with the given name
	 */
	public Category findByName(String name) throws CategoryNotFoundException;
	
	/**
	 * Returns a sublist of the offerings contained in the category defined by the given category name
	 * @param categoryName The name of the category whose offerings want to be retrieved
	 * @param offset The first offering to be retrieved
	 * @param max The max number of offerings to be returned
	 * @param orderBy The field that will be used to order the returned offerings
	 * @param desc true to sort results in reverse order 
	 * @return The list of offerings contained in the given category
	 * @throws CategoryNotFoundException If a category with the given name does not exist
	 */
	public List<Offering> getCategoryOfferingsSortedBy(String categoryName, int offset, int max, 
			String orderBy, boolean desc) throws CategoryNotFoundException;
	
	/**
	 * Returns a sublist of all the stored categories
	 * @param offset The first category to be retrieved
	 * @param max The max number of categories to be returned
	 * @return A sublist of all the stored categories
	 */
	public List<Category> getCategoriesPage(int offset, int max);
	
	/**
	 * Returns all the stored categories
	 * @return All the stored categories
	 */
	public List<Category> getAllCategories();
	
}
