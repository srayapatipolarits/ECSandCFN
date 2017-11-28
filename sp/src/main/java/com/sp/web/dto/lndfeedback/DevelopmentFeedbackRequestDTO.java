package com.sp.web.dto.lndfeedback;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.User;

/**
 * <code>DevelopmentFeedbackRequestDTO</code> is the feedbackRequestDTO.
 * 
 * @author pradeepruhil
 *
 */
public class DevelopmentFeedbackRequestDTO {
  
  private UserMarkerDTO user;
  private String id;
  private String spFeature;
  
  /**
   * Constructor.
   * 
   * @param user
   *          user
   * @param id
   *          of development feedback.
   */
  public DevelopmentFeedbackRequestDTO(User user, String id) {
    this.setUser(new UserMarkerDTO(user));
    this.id = id;
  }
    
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public void setSpFeature(String spFeature) {
    this.spFeature = spFeature;
  }

  public String getSpFeature() {
    return spFeature;
  }

  public UserMarkerDTO getUser() {
    return user;
  }

  public void setUser(UserMarkerDTO user) {
    this.user = user;
  }
  
}
