package com.sp.web.repository.marketing;

import com.sp.web.model.marketing.SPMarketing;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

@Repository
public class SPMarketingRepositoryMongoImpl extends GenericMongoRepositoryImpl<SPMarketing>
    implements SPMarketingRepository {

}
