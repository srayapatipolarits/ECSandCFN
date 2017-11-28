package com.sp.web.repository.spectrum;

import com.sp.web.model.spectrum.HiringFilterInsights;
import com.sp.web.model.spectrum.HiringFilterProfileBalance;
import com.sp.web.model.spectrum.LearnerStatus;
import com.sp.web.model.spectrum.ProfileBalance;

/**
 * SpectrumRepository contains the method for handling the db operation for spectrum.
 * 
 * @author pradeepruhil
 *
 */
public interface SpectrumRepository {
  
  public ProfileBalance getProfileBalance(String companyId);
  
  public void saveProfileBalances(ProfileBalance profileBalance);
  
  /**
   * <code>getHiringFilterInsiths</code> method will return the hiring insits of the user.
   * 
   * @param companyId
   *          of the user.
   * @return the hiring filter insights
   */
  public HiringFilterInsights getHiringFilterInsights(String companyId);
  
  /**
   * <code>saveHiringFilterInsights</code> method will save or update the hiring filter insiths.
   * 
   * @param hiringFilterInsights
   *          hiring fitler insitsh.
   */
  public void saveHiringFilterInsights(HiringFilterInsights hiringFilterInsights);
  
  /**
   * getHiringFilterProfileBalance will retunr the hiring profile balance of the comapny.
   * 
   * @param companyId
   *          of the user.
   * @return hirng filter proifle balance.
   */
  public HiringFilterProfileBalance getHiringFilterProfileBalance(String companyId);
  
  /**
   * <code>saveHiringFilterProfileBalances</code> method will save or update the hiring filter
   * profile balance
   * 
   * @param profileBalance
   *          of the user.
   */
  public void saveHiringFilterProfileBalances(HiringFilterProfileBalance profileBalance);
  
  /**
   * <code>getLearnerStatusBalance</code> method will save the learner status of the user in the db.
   * 
   * @param companyId
   *          of the user.
   * @return date.
   */
  public LearnerStatus getLearnerStatusBalance(String companyId);
  
  public void saveLeanerStatusBalance(LearnerStatus learnerStatus);
}
