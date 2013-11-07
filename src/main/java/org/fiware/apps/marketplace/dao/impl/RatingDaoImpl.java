package org.fiware.apps.marketplace.dao.impl;

import java.util.List;

import org.fiware.apps.marketplace.dao.RatingDao;
import org.fiware.apps.marketplace.model.Rating;
import org.fiware.apps.marketplace.model.RatingCategory;
import org.fiware.apps.marketplace.model.RatingCategoryEntry;
import org.fiware.apps.marketplace.model.RatingObject;
import org.fiware.apps.marketplace.model.RatingObjectCategory;
import org.fiware.apps.marketplace.util.MarketplaceHibernateDao;
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
