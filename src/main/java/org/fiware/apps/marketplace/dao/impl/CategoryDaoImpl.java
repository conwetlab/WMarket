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

import java.util.List;

import org.fiware.apps.marketplace.dao.CategoryDao;
import org.fiware.apps.marketplace.exceptions.CategoryNotFoundException;
import org.fiware.apps.marketplace.model.Category;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.utils.MarketplaceHibernateDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("classificationDao")
public class CategoryDaoImpl extends MarketplaceHibernateDao implements CategoryDao {
	
	private final static String CATEGORIES_TABLE_NAME = Category.class.getName();
	private final static String OFFERINGS_TABLE_NAME = Offering.class.getName();
	
	@Override
	public boolean isNameAvailable(String name) {
		
		boolean exists = false;
		
		try {
			findByName(name);
		} catch (CategoryNotFoundException e) {
			exists = true;
		}
		
		return exists;
	}

	@Override
	public Category findByName(String categoryName) throws CategoryNotFoundException {
		
		List<?> list = getSession()
				.createQuery(String.format("from %s where name=:name", CATEGORIES_TABLE_NAME))
				.setParameter("name", categoryName)
				.list();
		
		if (list.isEmpty()) {
			throw new CategoryNotFoundException("Category " + categoryName + " not found");
		} else {
			return (Category) list.get(0);
		}
	}

	@Override
	public List<Offering> getCategoryOfferingsSortedBy(String categoryName, String orderBy, boolean desc) 
			throws CategoryNotFoundException {
		
		Category category = findByName(categoryName);
		String ascOrDesc = desc ? "DESC" : "ASC";
				
		List<?> list = getSession()
				.createQuery(String.format("from %s where :category in elements(categories) "
						+ "ORDER BY %s %s", OFFERINGS_TABLE_NAME, orderBy, ascOrDesc))
				.setParameter("category", category)
				.list();
		
		@SuppressWarnings("unchecked")
		List<Offering> lo = (List<Offering>) list;
		
		return lo;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<Category> getCategoriesPage(int offset, int max) {
		return getSession()
				.createCriteria(Category.class)
				.setFirstResult(offset)
				.setMaxResults(max)
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<Category> getAllCategories() {
		return getSession()
				.createCriteria(Category.class)
				.list();
	}

}
