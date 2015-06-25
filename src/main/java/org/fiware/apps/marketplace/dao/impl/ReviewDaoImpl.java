package org.fiware.apps.marketplace.dao.impl;

import java.util.List;

import org.fiware.apps.marketplace.dao.ReviewDao;
import org.fiware.apps.marketplace.model.Review;
import org.fiware.apps.marketplace.model.ReviewableEntity;
import org.fiware.apps.marketplace.utils.MarketplaceHibernateDao;
import org.springframework.stereotype.Repository;

@Repository("reviewDao")
public class ReviewDaoImpl extends MarketplaceHibernateDao implements ReviewDao {
	
	private static final String TABLE_NAME = Review.class.getName();

	@Override
	public List<Review> getReviewsPage(ReviewableEntity entity, int offset, int max, String orderBy, boolean desc) {
		
		String descString = desc ? "DESC" : "ASC";
		
		@SuppressWarnings("unchecked")
		List<Review> list = getSession()
				.createQuery(String.format("from %s where reviewableEntity=:entity ORDER BY %s %s", 
						TABLE_NAME, orderBy, descString))
				.setParameter("entity", entity)
				.setFirstResult(offset)
				.setMaxResults(max)
				.list();
		
		return list;
	}

}
