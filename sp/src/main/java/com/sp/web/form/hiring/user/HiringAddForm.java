package com.sp.web.form.hiring.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.service.hiring.role.HiringRoleFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The form class to input hiring users (employees or candidates).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HiringAddForm {
  
  private List<String> emails;
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate dueBy;
  private Set<String> tags;
  private UserType type;
  private Set<String> roleIds;
  private List<List<String>> referenceTypes;
  private boolean referenceCheck;
  private List<String> tagList;
  
  public List<String> getEmails() {
    return emails;
  }
  
  public void setEmails(List<String> emails) {
    this.emails = emails;
  }
  
  public LocalDate getDueBy() {
    return dueBy;
  }
  
  public void setDueBy(LocalDate dueBy) {
    this.dueBy = dueBy;
  }
  
  public Set<String> getTags() {
    return tags;
  }
  
  public void setTags(Set<String> tags) {
    this.tags = tags;
  }

  public UserType getType() {
    return type;
  }

  public void setType(UserType type) {
    this.type = type;
  }

  public Set<String> getRoleIds() {
    return roleIds;
  }

  public void setRoleIds(Set<String> roleIds) {
    this.roleIds = roleIds;
  }

  public boolean isReferenceCheck() {
    return referenceCheck;
  }

  public void setReferenceCheck(boolean referenceCheck) {
    this.referenceCheck = referenceCheck;
  }
  
  public List<List<String>> getReferenceTypes() {
    return referenceTypes;
  }

  public void setReferenceTypes(List<List<String>> referenceTypes) {
    this.referenceTypes = referenceTypes;
  }
  
  /**
   * Validate the add request.
   * 
   * @param roleFactory
   *          - role factory
   */
  public void validate(User user, HiringRoleFactory roleFactory) {
    Assert.notEmpty(getEmails(), "Email required.");
    Assert.notNull(type, "Type required.");
    if (type == UserType.HiringCandidate) {
      Assert.notEmpty(roleIds, "Role required.");
      roleIds.forEach(r -> roleFactory.validateRole(user, r));
    }
    if (!CollectionUtils.isEmpty(tags)) {
      tagList = tags.stream().map(WordUtils::capitalizeFully).collect(Collectors.toList());
    }
  }

  /**
   * Creating a new user with the data in the form.
   * 
   * @param user
   *          - user
   * @param email
   *          - email
   * @return
   *    the new hiring user
   */
  public HiringUser create(User user, String email) {
    HiringUser hiringUser = new HiringUser();
    hiringUser.setEmail(email);
    hiringUser.setCreatedOn(LocalDate.now());
    hiringUser.setTagList(tagList);
    hiringUser.setType(type);
    hiringUser.setAssessmentDueDate(dueBy);
    hiringUser.setCompanyId(user.getCompanyId());
    Set<String> hiringCoordinatorIds = new HashSet<String>();
    hiringCoordinatorIds.add(user.getId());
    hiringUser.setHiringCoordinatorIds(hiringCoordinatorIds);
    hiringUser.getProfileSettings().setLocale(user.getLocale());
    if (type == UserType.HiringCandidate) {
      hiringUser.addRole(RoleType.HiringCandidate);
      hiringUser.setHiringRoleIds(roleIds);
    } else {
      hiringUser.addRole(RoleType.HiringEmployee);
    }
    return hiringUser;
  }
}
