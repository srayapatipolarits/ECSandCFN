package com.sp.web.repository.hiring.dashboard;

import com.sp.web.model.hiring.dashboard.HiringDashboardSettings;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

/**
 * Admin genric repository for dashboard settings.
 * 
 * @author pradeepruhil
 *
 */
@Repository
public class AdminHiringDashboardMongoRepository extends
    GenericMongoRepositoryImpl<HiringDashboardSettings> implements HiringDashboardRepository {
  
}
