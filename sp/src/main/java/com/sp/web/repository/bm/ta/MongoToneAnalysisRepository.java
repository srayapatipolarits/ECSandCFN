package com.sp.web.repository.bm.ta;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.Constants;
import com.sp.web.model.bm.ta.ToneAnalysisRecord;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The mongo implementation for the tone analysis repository interface.
 */
@Repository
public class MongoToneAnalysisRepository extends GenericMongoRepositoryImpl<ToneAnalysisRecord>
    implements ToneAnalysisRepository {

  @Override
  public List<ToneAnalysisRecord> findAllByUserId(String userId) {
    final Query query = query(where(Constants.ENTITY_USER_ID).is(userId));
    query.with(new Sort(Direction.DESC, Constants.ENTITY_CREATED_ON));
    return mongoTemplate.find(query, ToneAnalysisRecord.class);
  }
}
