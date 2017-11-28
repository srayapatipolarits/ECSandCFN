package com.sp.web.controller.hiring.spectrum;

import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.model.spectrum.ErtiAnalytics;
import com.sp.web.model.spectrum.ErtiInsights;
import com.sp.web.model.spectrum.ProfileBalance;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.spectrum.SpectrumFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * HiringSpectrumControllerHelper class is the helper class for the spectrum for People Analytics
 * plateform.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class HiringSpectrumControllerHelper {
  
  @Autowired
  private SpectrumFactory spectrumFactory;
  
  @Autowired
  private HiringUserFactory userFactory;
  
  /**
   * SPResponse getPersonalityBalance
   * 
   * @param user
   *          is the admin for people analytics plateform.
   * @return the personality balance.
   */
  public SPResponse getPersonalityBalance(User user) {
    
    List<HiringUser> all = userFactory.getAllValid(user.getCompanyId());
    ProfileBalance hiringProfileBalance = spectrumFactory.getHiringProfileBalance(
        user.getCompanyId(), all, true);
    SPResponse response = new SPResponse();
    response.add("profileBalance", hiringProfileBalance);
    return response;
    
  }
  
  /**
   * SPResponse getErtiInsights
   * 
   * @param user
   *          is the admin for people analytics plateform.
   * @return the personality balance.
   */
  public SPResponse getErtiInsights(User user) {
    
    List<HiringUser> all = userFactory.getAllValid(user.getCompanyId());
    ErtiInsights eritInsights = spectrumFactory.getHiringErtiInsights(user.getCompanyId(), all);
    SPResponse response = new SPResponse();
    response.add("eritInsights", eritInsights);
    return response;
    
  }
  
  /**
   * SPResponse getErtiAnalytics method return the erti analytics
   * 
   * @param user
   *          is the admin for people analytics plateform.
   * @return the personality balance.
   */
  public SPResponse getErtiAnalytics(User user) {
    
    List<HiringUser> all = userFactory.getAllValid(user.getCompanyId());
    ErtiAnalytics ertiAnalytics = spectrumFactory.getHiringErtiAnalytics(user.getCompanyId(), all);
    SPResponse response = new SPResponse();
    response.add("ertiAnalytics", ertiAnalytics);
    return response;
    
  }
}
