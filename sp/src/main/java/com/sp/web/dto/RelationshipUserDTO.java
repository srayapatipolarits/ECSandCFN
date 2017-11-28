package com.sp.web.dto;

import com.sp.web.model.User;

/**
 * @author Dax Abraham
 * 
 *         This is the DTO Class for the user relationship manager.
 */
public class RelationshipUserDTO extends BaseUserDTO {

  /**
   * Constructor.
   */
  public RelationshipUserDTO(User user) {
    super(user);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return getEmail().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof RelationshipUserDTO)) {
      return false;
    }
    return getEmail().equals(((RelationshipUserDTO) obj).getEmail());
  }
}
