package com.sp.web.form.hiring.group;

import com.sp.web.form.generic.GenericForm;
import com.sp.web.model.User;
import com.sp.web.model.hiring.group.HiringGroup;

import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         This is the form class for the people analytics groups.
 */
public class HiringGroupForm implements GenericForm<HiringGroup> {

  private String id;
  private String name;
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
  
  public List<String> getUserIds() {
    return userIds;
  }

  public void setUserIds(List<String> userIds) {
    this.userIds = userIds;
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

  @Override
  public HiringGroup create(User user) {
    HiringGroup group = new HiringGroup();
    group.setName(name);
    group.setUserIds(new HashSet<String>());
    group.setCompanyId(user.getCompanyId());
    return group;
  }

  @Override
  public void update(User user, HiringGroup instanceToUpdate) {
    instanceToUpdate.setName(name);
  }

  public void validateGroupUpdate() {
    validateGet();
    Assert.notEmpty(userIds, "User ids required.");
  }
  
}
