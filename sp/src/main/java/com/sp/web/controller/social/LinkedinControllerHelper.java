package com.sp.web.controller.social;

import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.model.User;
import com.sp.web.model.linkedin.SPLinkedInProfile;
import com.sp.web.mvc.SPResponse;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * <code>LinkedinControllerhelper</code> class provides method to get connectio from linkedin.
 * 
 * @author pradeep
 *
 */
@Component
public class LinkedinControllerHelper {
  
  private static final Logger LOG = Logger.getLogger(LinkedinControllerHelper.class);
  
  @Autowired
  private LinkedInCacheFactory cacheFactory;
  
  /**
   * <code>getUserProfileFull</code> method will return the user full profile.
   * 
   * @param user
   *          for which linkedproifle is to be returned
   * @return the use linkedin profile.
   */
  public SPResponse getUserProfileFull(User user, Object[] param) {
    SPResponse response = new SPResponse();
    
    boolean isUpdateProfile = (boolean) param[0];
    SPLinkedInProfile linkedInProfile;
    if (isUpdateProfile) {
      linkedInProfile = cacheFactory.updateLinkedInProfile(user);
    } else {
      linkedInProfile = cacheFactory.getLinkedInProfile(user);
    }
    
    if (linkedInProfile == null) {
      response.isDoRedirect();
      response.add("redirectUrl", "/sp/connect/linkedin");
    } else {
      
      HashMap<RangeType, PersonalityBeanResponse> personality = user.getAnalysis().getPersonality();
      
      PersonalityBeanResponse personalityBeanResponse = personality.get(RangeType.Primary);
      String primaryPersonalityType = personalityBeanResponse.getPersonalityType().toString();
      String profileStatement = MessagesHelper.genderNormalizeFromKey("feedback."
          + primaryPersonalityType + ".title", user);
      linkedInProfile.setProfileStatement(profileStatement);
      response.add("linkedProfile", linkedInProfile);
    }
    return response;
    
  } 
  
  /**
   * getLinkedINView method will return the linked in view.
   * 
   * @param user
   *          of the user.
   * @return the sp response.
   */
  public SPResponse getLinkedInView(User user) {
    SPResponse spResponse = new SPResponse();
    spResponse.isDoRedirect();
    boolean isProfileCached = cacheFactory.isProfileCached(user.getEmail());
    LOG.debug("Is linked profile cahced " + isProfileCached + user.getEmail());
    if (isProfileCached) {
      spResponse.add("redirectUrl", "connect/linkedinConnected");
    } else {
      spResponse.add("redirectUrl", "connect/linkedinConnect");
    }
    return spResponse;
  }
}
