package com.sp.web.form.hiring.user;

import com.sp.web.form.BaseUserForm;
import com.sp.web.model.HiringUser;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The form class to store the hired candidate information.
 */
public class HireCandidateForm extends BaseUserForm {
  
  private String id;
  private String email;
  private List<String> tags;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    Optional.ofNullable(email).ifPresent(
        e -> this.email = StringUtils.trimWhitespace(e.toLowerCase()));
  }
  
  public List<String> getTags() {
    return tags;
  }
  
  public void setTags(List<String> tags) {
    this.tags = tags;
  }
  
  /**
   * Validate the form data.
   */
  public void validate() {
    Assert.hasText(id, "Id required.");
    Assert.hasText(firstName, "First name required.");
    Assert.hasText(lastName, "Last name required.");
    Assert.hasText(email, "Email required.");
  }
  
  /**
   * Update the user with the form data.
   * 
   * @param user
   *          -user
   */
  public void update(HiringUser user) {
    BeanUtils.copyProperties(this, user);
    user.setCreatedOn(LocalDate.now());
    user.setId(null);
    Optional.ofNullable(user.getHiringRoleIds()).ifPresent(Set::clear);
    user.setTagList(tags.stream().map(WordUtils::capitalizeFully).collect(Collectors.toList()));
  }
  
  /**
   * Check if the email address has changed for the hiring user.
   * 
   * @param hiringUser
   *          - hiring user
   * @return true if same else false
   */
  public boolean isSameEmail(HiringUser hiringUser) {
    return (hiringUser.getEmail().equals(email));
  }
  
}
