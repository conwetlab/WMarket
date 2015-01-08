package org.fiware.apps.marketplace.dao.impl;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
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

import org.fiware.apps.marketplace.dao.RatingDao;
import org.fiware.apps.marketplace.model.Rating;
import org.fiware.apps.marketplace.model.RatingCategory;
import org.fiware.apps.marketplace.model.RatingCategoryEntry;
import org.fiware.apps.marketplace.model.RatingObject;
import org.fiware.apps.marketplace.model.RatingObjectCategory;
import org.fiware.apps.marketplace.utils.MarketplaceHibernateDao;
import org.springframework.stereotype.Repository;

@Repository("ratingDao")
public class RatingDaoImpl extends MarketplaceHibernateDao implements RatingDao{

	@Override
	public void saveRating(Rating rating) {
		getHibernateTemplate().saveOrUpdate(rating);		
	}

	@Override
	public void updateRating(Rating rating) {
		getHibernateTemplate().update(rating);		
	}

	@Override
	public void deleteRating(Rating rating) {
		getHibernateTemplate().delete(rating);			
	}

	@Override
	public Rating getRating(int ratingId) {
		List list = getHibernateTemplate().find(
				"from Rating where RATING_ID=?",ratingId
				);
		if (list.size()!=0){
			return (Rating)list.get(0);
		}		
		return null;
	}

	@Override
	public void saveRatingCategory(RatingCategory obj) {
		getHibernateTemplate().saveOrUpdate(obj);

	}

	@Override
	public void updateRatingCategory(RatingCategory obj) {
		getHibernateTemplate().update(obj);	

	}

	@Override
	public void deleteRatingCategory(RatingCategory obj) {
		getHibernateTemplate().delete(obj);

	}

	@Override
	public RatingCategory getRatingCategory(String objectCategoryId, String categoryId) {

		List list = getHibernateTemplate().find("select o "+
				"from RatingObjectCategory c, RatingCategory o  " +
				"where c.id = o.ratingObjectCategory " +
				"and c.name=? and o.name=?",objectCategoryId, categoryId
				);
		if (list.size()!=0){
			return (RatingCategory)list.get(0);
		}		
		return null;
	}

	@Override
	public void saveRatingCategoryEntry(RatingCategoryEntry obj) {
		getHibernateTemplate().saveOrUpdate(obj);

	}

	@Override
	public void updateRatingCategoryEntry(RatingCategoryEntry obj) {
		getHibernateTemplate().update(obj);	

	}

	@Override
	public void deleteRatingCategoryEntry(RatingCategoryEntry obj) {
		getHibernateTemplate().delete(obj);

	}

	@Override
	public RatingCategoryEntry getRatingCategoryEntry(int ratingId, String categoryId) {
		List list = getHibernateTemplate().find(
				"select r from RatingCategoryEntry r, RatingCategory c, Rating rat where " +
						"r.ratingCategory = c.id and " +
						"r.rating = rat.id and c.name = ? and rat.id = ?", categoryId, ratingId
				);
		if (list.size()!=0){
			return (RatingCategoryEntry)list.get(0);
		}		
		return null;
	}

	@Override
	public void saveRatingObject(RatingObject obj) {
		getHibernateTemplate().saveOrUpdate(obj);

	}

	@Override
	public void updateRatingObject(RatingObject obj) {
		getHibernateTemplate().update(obj);	

	}

	@Override
	public void deleteRatingObject(RatingObject obj) {
		getHibernateTemplate().delete(obj);

	}

	@Override
	public RatingObject getRatingObject(String catId, String objId) {
		List list = getHibernateTemplate().find("select o "+
				"from RatingObjectCategory c, RatingObject o  " +
				"where c.id = o.ratingObjectCategory " +
				"and c.name=? and o.objectId=?",catId, objId
				);
		if (list.size()!=0){
			System.out.println(list.get(0).getClass().getCanonicalName());
			return (RatingObject)list.get(0);
		}		
		return null;
	}

	@Override
	public void saveRatingObjectCategory(RatingObjectCategory obj) {
		getHibernateTemplate().saveOrUpdate(obj);

	}	


	@Override
	public void updateRatingObjectCategory(RatingObjectCategory obj) {
		getHibernateTemplate().update(obj);	

	}

	@Override
	public void deleteRatingObjectCategory(RatingObjectCategory obj) {
		getHibernateTemplate().delete(obj);

	}

	@Override
	public RatingObjectCategory getRatingObjectCategory(String objId) {
		List list = getHibernateTemplate().find(
				"from RatingObjectCategory where RATING_OBJECT_CATEGORY_NAME=?",objId
				);
		if (list.size()!=0){
			return (RatingObjectCategory)list.get(0);
		}		
		return null;
	}

	@Override
	public List<Rating> getRatings(String objectCategoryId, String objectId) {				

		List list = getHibernateTemplate().find("select r "+
				"from Rating r, RatingObjectCategory c, RatingObject o  " +
				"where r.ratingObject = o.id and " +
				"o.ratingObjectCategory = c.id and " +
				"c.name = ? and o.objectId = ?",objectCategoryId, objectId);
		if (list.size()!=0){
			return (List<Rating>)list;
		}		
		return null;


	}

	@Override
	public List<RatingObject> getRatingObjects(String objectCategoryId) {
		List list = getHibernateTemplate().find("select o "+
				"from RatingObjectCategory c, RatingObject o  " +
				"where " +
				"o.ratingObjectCategory = c.id and " +
				"c.name = ?",objectCategoryId);
		if (list.size()!=0){
			return (List<RatingObject>)list;
		}		
		return null;

	}

	@Override
	public List<RatingCategory> getRatingCategories(String objectCategoryId) {
		List list = getHibernateTemplate().find("select rc "+
				"from RatingObjectCategory c, RatingCategory rc  " +
				"where " +
				"rc.ratingObjectCategory = c.id and " +
				"c.name = ?",objectCategoryId);
		if (list.size()!=0){
			return (List<RatingCategory>)list;
		}		
		return null;
	}

	@Override
	public List<RatingObjectCategory> getRatingObjectCategories() {
		List list = getHibernateTemplate().find("select o "+
				"from RatingObjectCategory o");
		if (list.size()!=0){
			return (List<RatingObjectCategory>)list;
		}		
		return null;
	
	}

}
