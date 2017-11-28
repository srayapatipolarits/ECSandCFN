package com.sp.web.repository.bm.ta;

import com.sp.web.model.bm.ta.UserToneAnalysis;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

/**
 * 
 * @author Dax Abraham
 *
 *         The mongo implementation for the user tone analysis repository interface.
 */
@Repository
public class MongoUserToneAnalysisRepository extends GenericMongoRepositoryImpl<UserToneAnalysis>
    implements UserToneAnalysisRepository {
  
}
