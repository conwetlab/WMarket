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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.fiware.apps.marketplace.dao.CategoryDao;
import org.fiware.apps.marketplace.exceptions.ClassificationNotFoundException;
import org.fiware.apps.marketplace.model.Category;
import org.fiware.apps.marketplace.model.Offering;
import org.fiware.apps.marketplace.utils.MarketplaceHibernateDao;
import org.springframework.stereotype.Repository;

@Repository("classificationDao")
public class CategoryDaoImpl extends MarketplaceHibernateDao implements CategoryDao {
	
	private final static String CATEGORY_TABLE_NAME = Category.class.getName();
	private final static HashMap<String, Comparator<Offering>> COMPARATORS = new HashMap<>();
	
	static {
		COMPARATORS.put("name", new NameComparator());
		COMPARATORS.put("rating", new RatingComparator());
		COMPARATORS.put("publicationDate", new PublicationDateComparator());
	}
	
	@Override
	public boolean isNameAvailable(String name) {
		
		boolean exists = false;
		
		try {
			findByName(name);
		} catch (ClassificationNotFoundException e) {
			exists = true;
		}
		
		return exists;
	}
	

	@Override
	public Category findByName(String categoryName) throws ClassificationNotFoundException {
		
		List<?> list = getSession()
				.createQuery(String.format("from %s where name=:name", CATEGORY_TABLE_NAME))
				.setParameter("name", categoryName)
				.list();
		
		if (list.isEmpty()) {
			throw new ClassificationNotFoundException("Category " + categoryName + " not found");
		} else {
			return (Category) list.get(0);
		}
	}


	@Override
	public List<Offering> getCategoryOfferingsSortedBy(String categoryName, String sortedBy) 
			throws ClassificationNotFoundException {
				
		// Get comparator
		Comparator<Offering> comparator = COMPARATORS.get(sortedBy);
				
		// If comparator is not found, exception is thrown
		if (comparator == null) {
			
			String validSorters = "";
			for (String validSorter: COMPARATORS.keySet()) {
				
				// Delimiter is not added with the first element
				if (!validSorters.equals("")) {
					validSorters += ", ";
				}
				
				validSorters += validSorter;
			}
						
			throw new IllegalArgumentException("Invalid param sortedBy. Valid values are: " + validSorters);
		}
		
		// Create the sorted set with with the comparator and sort offerings
		Category category = findByName(categoryName);
		List<Offering> offerings = new ArrayList<>(category.getOfferings());
		Collections.sort(offerings, comparator);
				
		return offerings;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// COMPARATORS /////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	private static class RatingComparator implements Comparator<Offering> {

		@Override
		public int compare(Offering o1, Offering o2) {
			// Sorted based on the average score
			if (o1.getAverageScore() < o2.getAverageScore()) {
				return 1; 	// Offerings with higher scores will be the first ones 
			} else if (o1.getAverageScore() == o2.getAverageScore()) {
				return 0;
			} else {
				return -1;	// Offering with lower scores will be the last ones
			}
		}
	}

	private static class NameComparator implements Comparator<Offering> {

		@Override
		public int compare(Offering o1, Offering o2) {
			// Sorted based on offering name
			return o1.getName().compareTo(o2.getName());
		}
	}
	
	private static class PublicationDateComparator implements Comparator<Offering> {

		@Override
		public int compare(Offering o1, Offering o2) {
			return o1.getDescribedIn().getRegistrationDate().
					compareTo(o2.getDescribedIn().getRegistrationDate());
		}	
	}
	
}
