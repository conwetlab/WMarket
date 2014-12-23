package org.fiware.apps.marketplace.dao;

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

import org.fiware.apps.marketplace.model.Rating;
import org.fiware.apps.marketplace.model.RatingCategory;
import org.fiware.apps.marketplace.model.RatingCategoryEntry;
import org.fiware.apps.marketplace.model.RatingObject;
import org.fiware.apps.marketplace.model.RatingObjectCategory;

public interface RatingDao {
	void saveRating(Rating obj);
	void updateRating(Rating obj);
	void deleteRating(Rating obj);
	Rating getRating(int objId);	
	List<Rating> getRatings(String objectCategoryId, String objectId);
	
	void saveRatingCategory(RatingCategory obj);
	void updateRatingCategory(RatingCategory obj);
	void deleteRatingCategory(RatingCategory obj);
	RatingCategory getRatingCategory(String objectCategoryId, String categoryId);	
	
	void saveRatingCategoryEntry(RatingCategoryEntry obj);
	void updateRatingCategoryEntry(RatingCategoryEntry obj);
	void deleteRatingCategoryEntry(RatingCategoryEntry obj);
	RatingCategoryEntry getRatingCategoryEntry(int ratingId, String categoryId);	
	
	void saveRatingObject(RatingObject obj);
	void updateRatingObject(RatingObject obj);
	void deleteRatingObject(RatingObject obj);
	RatingObject getRatingObject(String catId, String objId);	
	List<RatingObject> getRatingObjects(String objectCategoryId);
	
	void saveRatingObjectCategory(RatingObjectCategory obj);
	void updateRatingObjectCategory(RatingObjectCategory obj);
	void deleteRatingObjectCategory(RatingObjectCategory obj);
	RatingObjectCategory getRatingObjectCategory(String objId);
	List<RatingCategory> getRatingCategories(String objectCategoryId);
	List<RatingObjectCategory> getRatingObjectCategories();
		
	
	
}
