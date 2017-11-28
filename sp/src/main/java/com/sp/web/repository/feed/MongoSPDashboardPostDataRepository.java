package com.sp.web.repository.feed;

import com.sp.web.model.feed.SPDashboardPostData;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

/**
 * @author Dax Abraham
 * 
 *         The mongo implementation for the SPActivityFeedsRepository interface.
 */
@Repository
public class MongoSPDashboardPostDataRepository extends
    GenericMongoRepositoryImpl<SPDashboardPostData> implements SPDashboardPostDataRepository {
  
}
