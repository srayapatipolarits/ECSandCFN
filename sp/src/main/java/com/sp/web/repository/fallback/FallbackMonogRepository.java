package com.sp.web.repository.fallback;

import com.sp.web.model.fallback.FallbackBean;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

/**
 * MongoFallbackRepository holds the database api for the fallback bean.
 * 
 * @author pradeepruhil
 *
 */
@Repository
public class FallbackMonogRepository extends GenericMongoRepositoryImpl<FallbackBean> implements
    FallbackRepository {
}
