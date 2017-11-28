package com.sp.web.repository.hiring.lens;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.Constants;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The mongo repository implementation for the hiring lens.
 */
@Repository
public class HiringLensRepositoryMongoImpl extends GenericMongoRepositoryImpl<FeedbackUser>
    implements HiringLensRepository {
  
  @Override
  public List<FeedbackUser> findByUserFor(String feedbackFor) {
    return mongoTemplate.find(query(where(Constants.ENTITY_FEEDBACK_FOR).is(feedbackFor)
        .andOperator(where(Constants.ENTITY_FEATURE_TYPE).is(FeatureType.PrismLensHiring))),
        FeedbackUser.class);
  }
  
}
