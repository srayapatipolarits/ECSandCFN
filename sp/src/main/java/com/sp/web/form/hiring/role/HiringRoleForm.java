package com.sp.web.form.hiring.role;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.form.generic.GenericForm;
import com.sp.web.model.User;
import com.sp.web.model.hiring.role.HiringRole;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The form class for the hiring roles.
 */
public class HiringRoleForm implements GenericForm<HiringRole> {
  
  private String id;
  private String name;
  private String description;
  private String portraitId;
  private boolean addPrism;
  private List<String> userIds;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public String getPortraitId() {
    return portraitId;
  }
  
  public void setPortraitId(String portraitId) {
    this.portraitId = portraitId;
  }
  
  @Override
  public void validate() {
    Assert.hasText(name, "Name required.");
  }
  
  @Override
  public void validateUpdate() {
    validateGet();
    validate();
  }
  
  @Override
  public void validateGet() {
    Assert.hasText(id, "Id required.");
  }
  
  public void validateAssign() {
    validateGet();
    Assert.hasText(portraitId, "Portriat id required.");
  }
  
  public void validateAddToRole() {
    validateGet();
    Assert.notEmpty(userIds, "User ids required.");
  }
  
  @Override
  public HiringRole create(User user) {
    HiringRole role = new HiringRole();
    role.setName(name);
    role.setDescription(description);
    if (!StringUtils.isBlank(portraitId)) {
      role.setPortraitId(portraitId);
    }
    UserMarkerDTO createdBy = new UserMarkerDTO(user);
    role.setCreatedBy(createdBy);
    role.setUpdatedBy(createdBy);
    LocalDateTime createdOn = LocalDateTime.now();
    role.setCreatedOn(createdOn);
    role.setUpdatedOn(createdOn);
    role.setCompanyId(user.getCompanyId());
    return role;
  }
  
  @Override
  public void update(User user, HiringRole instanceToUpdate) {
    instanceToUpdate.setName(name);
    instanceToUpdate.setDescription(description);
    if (!StringUtils.isBlank(portraitId)) {
      instanceToUpdate.setPortraitId(portraitId);
    } else {
      instanceToUpdate.setPortraitId(null);
    }
    instanceToUpdate.setUpdatedBy(new UserMarkerDTO(user));
    instanceToUpdate.setUpdatedOn(LocalDateTime.now());
  }
  
  public boolean isAddPrism() {
    return addPrism;
  }
  
  public void setAddPrism(boolean addPrism) {
    this.addPrism = addPrism;
  }

  public List<String> getUserIds() {
    return userIds;
  }

  public void setUserIds(List<String> userIds) {
    this.userIds = userIds;
  }
  
}
