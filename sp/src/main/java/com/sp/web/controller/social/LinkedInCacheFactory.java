package com.sp.web.controller.social;

import com.sp.web.model.User;
import com.sp.web.model.linkedin.LinkedInOtherProfile;
import com.sp.web.model.linkedin.SPLinkedInProfile;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.LinkedInProfileFull;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * LinkedInCache factory will cache the linkedin resposne for the user.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class LinkedInCacheFactory {
  
  private static final Logger LOG = Logger.getLogger(LinkedInCacheFactory.class);
  /**
   * Linkedin to connect to the linked and get connections.
   */
  @Inject
  private ConnectionRepository connectionRepository;
  
  @Autowired
  private CacheManager cacheManager;
  
  /**
   * <code>getLinekdInProfile</code> method will cache the linkedINporfile and will store again the
   * user email.
   * 
   * @param user
   *          user meail
   * @return the SP linked in profile.
   */
  @Cacheable(value = "linkedInProfile", key = "#user.email")
  public SPLinkedInProfile getLinkedInProfile(User user) {
    Connection<LinkedIn> connection = connectionRepository.findPrimaryConnection(LinkedIn.class);
    if (connection == null) {
      return null;
    }
    LinkedInProfileFull userProfileFull = connection.getApi().profileOperations()
        .getUserProfileFull();
    
    String url = String
        .format(
            "https://api.linkedin.com/v1/people/id=%s:(courses,certifications,volunteer,projects,patents,testScores,languages,honors-awards,publications)",
            connection.getApi().profileOperations().getProfileId());
    LinkedInOtherProfile otherProfileDate = connection.getApi().restOperations()
        .getForObject(url, LinkedInOtherProfile.class);
    SPLinkedInProfile linkedInProfile = new SPLinkedInProfile();
    linkedInProfile.setOtherProfileData(otherProfileDate);
    linkedInProfile.setLinkedProfile(userProfileFull);
    LOG.info("Linekd in profile retunred is " + linkedInProfile);
    return linkedInProfile;
    
  }
  
  @CachePut(value = "linkedInProfile", key = "#user.email")
  public SPLinkedInProfile updateLinkedInProfile(User user) {
    return getLinkedInProfile(user);
  }
  
  /**
   * <code>isProfileCached</code> method will return whether profile is cahced or not.
   * 
   * @param email
   *          against which to cehck wehterh profile is cached or not
   * @return the true or false
   */
  public boolean isProfileCached(String email) {
    ValueWrapper valueWrapper = cacheManager.getCache("linkedInProfile").get(email);
    if (valueWrapper != null) {
      if (valueWrapper.get() == null) {
        return false;
      } else {
        return true;
      }
    } else {
      return false;
    }
  }
}
