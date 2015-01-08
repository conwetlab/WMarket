package org.fiware.apps.marketplace.bo.impl;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
 * Copyright (C) 2014 CoNWeT Lab, Universidad Politécnica de Madrid
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

import org.fiware.apps.marketplace.bo.RatingBo;
import org.fiware.apps.marketplace.dao.RatingDao;
import org.fiware.apps.marketplace.model.Rating;
import org.fiware.apps.marketplace.model.RatingCategory;
import org.fiware.apps.marketplace.model.RatingCategoryEntry;
import org.fiware.apps.marketplace.model.RatingObject;
import org.fiware.apps.marketplace.model.RatingObjectCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ratingBo")
public class RatingBoImpl implements RatingBo {
	
	@Autowired
	RatingDao ratingDao;



	@Override
	public void saveRating(Rating obj) {
		ratingDao.saveRating(obj);
		
	}

	@Override
	public void updateRating(Rating obj) {
		ratingDao.updateRating(obj);
		
	}

	@Override
	public void deleteRating(Rating obj) {
		ratingDao.deleteRating(obj);
		
	}

	@Override
	public Rating getRating(int objId) {
		return ratingDao.getRating(objId);
	}

	@Override
	public void saveRatingCategory(RatingCategory obj) {
		ratingDao.saveRatingCategory(obj);
		
	}

	@Override
	public void updateRatingCategory(RatingCategory obj) {
		ratingDao.saveRatingCategory(obj);
		
	}

	@Override
	public void deleteRatingCategory(RatingCategory obj) {
		ratingDao.deleteRatingCategory(obj);
		
	}

	@Override
	public RatingCategory getRatingCategory(String objectCategoryId, String categoryId) {
		return ratingDao.getRatingCategory(objectCategoryId, categoryId);
	}

	@Override
	public void saveRatingCategoryEntry(RatingCategoryEntry obj) {
		ratingDao.saveRatingCategoryEntry(obj);
		
	}

	@Override
	public void updateRatingCategoryEntry(RatingCategoryEntry obj) {
		ratingDao.updateRatingCategoryEntry(obj);
		
	}

	@Override
	public void deleteRatingCategoryEntry(RatingCategoryEntry obj) {
		ratingDao.deleteRatingCategoryEntry(obj);
		
	}

	@Override
	public RatingCategoryEntry getRatingCategoryEntry(int ratingId, String categoryId) {
		return ratingDao.getRatingCategoryEntry(ratingId, categoryId);
	}

	@Override
	public void saveRatingObject(RatingObject obj) {
		ratingDao.saveRatingObject(obj);
		
	}

	@Override
	public void updateRatingObject(RatingObject obj) {
		ratingDao.updateRatingObject(obj);
		
	}

	@Override
	public void deleteRatingObject(RatingObject obj) {
		ratingDao.deleteRatingObject(obj);
		
	}

	@Override
	public RatingObject getRatingObject(String catId, String objId) {
		return ratingDao.getRatingObject(catId, objId);
	}

	@Override
	public void saveRatingObjectCategory(RatingObjectCategory obj) {
		ratingDao.saveRatingObjectCategory(obj);
		
	}

	@Override
	public void updateRatingObjectCategory(RatingObjectCategory obj) {
		ratingDao.updateRatingObjectCategory(obj);
		
	}

	@Override
	public void deleteRatingObjectCategory(RatingObjectCategory obj) {
		ratingDao.deleteRatingObjectCategory(obj);
		
	}

	@Override
	public RatingObjectCategory getRatingObjectCategory(
			String objId) {
		return ratingDao.getRatingObjectCategory(objId);
	}

	@Override
	public List<Rating> getRatings(String objectCategoryId, String objectId) {
		return ratingDao.getRatings(objectCategoryId, objectId);

	}

	@Override
	public List<RatingObject> getRatingObjects(String objectCategoryId) {
		return ratingDao.getRatingObjects(objectCategoryId);
	}

	@Override
	public List<RatingCategory> getRatingCategories(String objectCategoryId) {	
		return ratingDao.getRatingCategories(objectCategoryId);
	}

	@Override
	public List<RatingObjectCategory> getRatingObjectCategories() {
		return ratingDao.getRatingObjectCategories();
	}

}
