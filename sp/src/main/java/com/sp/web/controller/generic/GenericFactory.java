package com.sp.web.controller.generic;

import com.sp.web.model.User;

import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The generic factory interface class.<br/><br/>
 *         
 *         L - The Listing DTO for the entity<br/>
 *         D - The details DTO for the entity<br/>
 *         F - The form for the entity for create, update, etc.<br/>
 */
public interface GenericFactory<L, D, F> {
  
  /**
   * Get all the instances.
   * 
   * @param user
   *          - user
   * @return
   *      list of listing DTO for the instances
   */
  List<L> getAll(User user);
  
  /**
   * Get the details for the given id.
   * 
   * @param user
   *          - user
   * @param form
   *          - form
   * @return
   *     the DTO instance
   */
  D get(User user, F form);
  
  /**
   * Create a new instance with the data given in the form.
   * 
   * @param user
   *          - user
   * @param form
   *          - form
   * @return
   *    the DTO for the newly created instance
   */
  D create(User user, F form);
  
  /**
   * Update the data for the given instance.
   * 
   * @param user
   *          - user
   * @param form
   *          - form
   * @return
   *      the DTO for the newly created instance
   */
  D update(User user, F form);
  
  /**
   * Delete the instance given by id.
   * 
   * @param user
   *          - user
   * @param form
   *          - instance to delete
   */
  void delete(User user, F form);
}
