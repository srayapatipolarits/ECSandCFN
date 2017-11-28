package com.sp.web.controller.generic;

import com.sp.web.form.generic.GenericForm;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;

/**
 * @author Dax Abraham
 *
 *         The generic helper class.<br/>
 * <br/>
 * 
 *         T - The entity under management<br/>
 *         L - The Listing DTO for the entity<br/>
 *         D - The details DTO for the entity<br/>
 *         F - The form for the entity for create, update, etc.<br/>
 *         R - The factory class to support the different crud operations.<br/>
 * 
 */
public class GenericControllerHelper<T, L, D, F extends GenericForm<T>, R extends GenericFactory<L, D, F>> {
  
  protected String moduleName = "moduleName";
  protected R factory;
  
  /**
   * Constructor.
   * 
   * @param factory
   *          - factory
   */
  public GenericControllerHelper(String moduleName, R factory) {
    this.factory = factory;
    this.moduleName = moduleName;
  }
  
  /**
   * Helper method to get all.
   * 
   * @param user
   *          - user
   * @return the list of instances
   */
  public SPResponse getAll(User user) {
    return new SPResponse().add(moduleName + "Listing", factory.getAll(user));
  }
  
  /**
   * Get the details for the given instance.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the get request
   */
  @SuppressWarnings("unchecked")
  public SPResponse get(User user, Object[] params) {
    
    // get the id
    F form = (F) params[0];
    form.validateGet();
    
    // sending the response
    return new SPResponse().add(moduleName, factory.get(user, form));
  }
  
  /**
   * Helper method to create a new instance.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the create request
   */
  @SuppressWarnings("unchecked")
  public SPResponse create(User user, Object[] params) {
    F form = (F) params[0];
    form.validate();
    return new SPResponse().add(moduleName, factory.create(user, form));
  }
  
  /**
   * Helper method to create a new instance.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the create request
   */
  @SuppressWarnings("unchecked")
  public SPResponse update(User user, Object[] params) {
    F form = (F) params[0];
    form.validateUpdate();
    return new SPResponse().add(moduleName, factory.update(user, form));
  }
  
  /**
   * Get the details for the given instance.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the get request
   */
  @SuppressWarnings("unchecked")
  public SPResponse delete(User user, Object[] params) {
    
    F form = (F) params[0];
    form.validateGet();
    
    // delete the instance
    factory.delete(user, form);
    
    // sending the response
    return new SPResponse().isSuccess();
  }
  
}
