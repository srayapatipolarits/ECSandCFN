package com.sp.web.model;

import org.springframework.beans.BeanUtils;

/**
 * @author Dax Abraham
 * 
 *         The entity class representing the deleted users.
 */
public class DeletedUser extends User {

  /**
   * Serial Version UID.
   */
  private static final long serialVersionUID = -1110924115437055255L;

  /**
   * Constructor.
   * 
   * @param user
   *        - user
   */
  public DeletedUser(User user) {
    BeanUtils.copyProperties(user, this);
  }

  /**
   * Default Constructor. 
   */
  public DeletedUser() {
  }

  

}
