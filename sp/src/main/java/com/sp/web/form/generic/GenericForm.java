package com.sp.web.form.generic;

import com.sp.web.model.User;

/**
 * @author Dax Abraham
 * 
 *         The interface for generic forms.
 */
public interface GenericForm<T> {
  
  /**
   * Validate the form data for create.
   */
  void validate();
  
  /**
   * Validate the form data for update.
   */
  void validateUpdate();

  /**
   * Validate the get request.
   */
  void validateGet();
  
  /**
   * Create a new instance of T from form data.
   * 
   * @param user
   *          - user
   * @return
   *    new instance
   */
  T create(User user);
  
  /**
   * Update the current instance of T from the data given.
   * 
   * @param user
   *          - user
   * @param instanceToUpdate
   *        - instance to update
   */
  void update(User user, T instanceToUpdate);
}
