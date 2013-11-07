package org.fiware.apps.marketplace.bo;

import java.util.List;

import org.fiware.apps.marketplace.model.Rating;
import org.fiware.apps.marketplace.model.RatingCategory;
import org.fiware.apps.marketplace.model.RatingCategoryEntry;
import org.fiware.apps.marketplace.model.RatingObject;
import org.fiware.apps.marketplace.model.RatingObjectCategory;

public interface RatingBo {
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
	List<RatingCategory> getRatingCategories(String objectCategoryId);
	
	void saveRatingObject(RatingObject obj);
	void updateRatingObject(RatingObject obj);
	void deleteRatingObject(RatingObject obj);
	RatingObject getRatingObject(String catId , String objId);	
	List<RatingObject> getRatingObjects(String objectCategoryId);
	
	void saveRatingObjectCategory(RatingObjectCategory obj);
	void updateRatingObjectCategory(RatingObjectCategory obj);
	void deleteRatingObjectCategory(RatingObjectCategory obj);
	RatingObjectCategory getRatingObjectCategory(String objId);		
	List<RatingObjectCategory> getRatingObjectCategories();	
	
}
