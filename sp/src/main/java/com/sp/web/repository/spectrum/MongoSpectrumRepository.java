package com.sp.web.repository.spectrum;

import com.sp.web.model.spectrum.HiringFilterInsights;
import com.sp.web.model.spectrum.HiringFilterProfileBalance;
import com.sp.web.model.spectrum.LearnerStatus;
import com.sp.web.model.spectrum.ProfileBalance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * SpectrumRepository contains the operations for fetching and retrieving the data from the sectrum.
 * 
 * @author pradeepruhil
 *
 */
@Repository
public class MongoSpectrumRepository implements SpectrumRepository {
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  /**
   * <code>getProfileBalance</code> method will return the profile balance of all the users in a
   * company
   * 
   * @param companyId
   *          of the user.
   * @return the proifle balance.
   */
  @Override
  public ProfileBalance getProfileBalance(String companyId) {
    return mongoTemplate.findOne(Query.query(Criteria.where("companyId").is(companyId)),
        ProfileBalance.class);
  }
  
  @Override
  public void saveProfileBalances(ProfileBalance profileBalance) {
    mongoTemplate.save(profileBalance);
  }
  
  /**
   * @see com.sp.web.repository.spectrum.SpectrumRepository#getHiringFilterInsights(java.lang.String)
   */
  @Override
  public HiringFilterInsights getHiringFilterInsights(String companyId) {
    return mongoTemplate.findOne(Query.query(Criteria.where("companyId").is(companyId)),
        HiringFilterInsights.class);
  }
  
  /**
   * @see com.sp.web.repository.spectrum.SpectrumRepository#saveHiringFilterInsights(com.sp.web.model
   *      .spectrum.HiringFilterInsights)
   */
  @Override
  public void saveHiringFilterInsights(HiringFilterInsights hiringFilterInsights) {
    mongoTemplate.save(hiringFilterInsights);
    
  }
  
  /**
   * 
   * @see com.sp.web.repository.spectrum.SpectrumRepository#getHiringFilterProfileBalance(java.lang.String
   *      )
   */
  @Override
  public HiringFilterProfileBalance getHiringFilterProfileBalance(String companyId) {
    return mongoTemplate.findOne(Query.query(Criteria.where("companyId").is(companyId)),
        HiringFilterProfileBalance.class);
  }
  
  /**
   * @see com.sp.web.repository.spectrum.SpectrumRepository#saveHiringFilterProfileBalances(com.sp.web.model.spectrum.HiringFilterProfileBalance)
   */
  @Override
  public void saveHiringFilterProfileBalances(HiringFilterProfileBalance profileBalance) {
    mongoTemplate.save(profileBalance);
    
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * com.sp.web.repository.spectrum.SpectrumRepository#getLearnerStatusBalance(java.lang.String)
   */
  @Override
  public LearnerStatus getLearnerStatusBalance(String companyId) {
    return mongoTemplate.findOne(Query.query(Criteria.where("companyId").is(companyId)),
        LearnerStatus.class);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * com.sp.web.repository.spectrum.SpectrumRepository#saveLeanerStatusBalance(com.sp.web.model.
   * spectrum.LearnerStatus)
   */
  @Override
  public void saveLeanerStatusBalance(LearnerStatus learnerStatus) {
    mongoTemplate.save(learnerStatus);
  }
}
