package com.sp.web.form;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sp.web.dto.user.UserMarkerDTO;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

/**
 * @author Dax Abraham
 * 
 *         The basic form to take user information.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserMarkerForm {
  
  @NotBlank
  private String name;
  @NotBlank
  private String title;
  @NotBlank
  private String smallProfileImage;
  private String id;
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getSmallProfileImage() {
    return smallProfileImage;
  }
  
  public void setSmallProfileImage(String smallProfileImage) {
    this.smallProfileImage = smallProfileImage;
  }

  /**
   * Get a new instance of user marker dto from the data in this form.
   * 
   * @return
   *      - user marker dto
   */
  public UserMarkerDTO getUserMarkerDTO() {
    UserMarkerDTO user = UserMarkerDTO.newInstance();
    update(user);
    return user;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  /**
   * Validate the update.
   */
  public void validateUpdate() {
    Assert.hasText(id, "Id is required.");
  }

  /**
   * Update the given user with the form data.
   *  
   * @param user
   *          - user to update
   */
  public void update(UserMarkerDTO user) {
    BeanUtils.copyProperties(this, user, "id");
    user.setFirstName(name);
    if (!StringUtils.isBlank(id)) {
      user.setId(id);
    }
  }

  /**
   * Validate the create request.
   */
  public void validate() {
    Assert.hasText(name, "Name is required.");
    Assert.hasText(title, "Title is required.");
    Assert.hasText(smallProfileImage, "Image is required.");
  }
  
}
