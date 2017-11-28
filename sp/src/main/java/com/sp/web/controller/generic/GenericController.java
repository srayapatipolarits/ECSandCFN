package com.sp.web.controller.generic;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.form.generic.GenericForm;
import com.sp.web.mvc.SPResponse;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Dax Abraham
 * 
 *         The controller interface for the generic controller.<br/>
 *         Need to ensure that each controller also is annotated with 
 *         
 *         @ Controller
 *         @ RequestMapping("/xxx")
 * <br/>
 * 
 *         T - The entity under management<br/>
 *         L - The Listing DTO for the entity<br/>
 *         D - The details DTO for the entity<br/>
 *         F - The form for the entity for create, update, etc.<br/>
 *         H - The controller helper <br/>
 */
public class GenericController<T, L, D, F extends GenericForm<T>, H extends GenericControllerHelper<?, ?, ?, ?, ?>> {
  
  protected H helper;
  
  /**
   * Constructor.
   * 
   * @param helper
   *          - helper
   */
  public GenericController(H helper) {
    this.helper = helper;
  }
  
  /**
   * Controller method to get all.
   * 
   * @param token
   *          - user
   * @return the response to get all
   */
  @RequestMapping(value = "/getAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAll(Authentication token) {
    return process(helper::getAll, token);
  }
  
  /**
   * Controller method to get.
   * 
   * @param form
   *          - form
   * @param token
   *          - user
   * @return the response to get
   */
  @RequestMapping(value = "/get", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse get(F form, Authentication token) {
    return process(helper::get, token, form);
  }
  
  /**
   * Controller method to create.
   * 
   * @param form
   *          - form
   * @param token
   *          - user
   * @return the response to create
   */
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse create(@RequestBody F form, Authentication token) {
    return process(helper::create, token, form);
  }
  
  /**
   * Controller method to update.
   * 
   * @param form
   *          - form
   * @param token
   *          - user
   * @return the response to update
   */
  @RequestMapping(value = "/update", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse update(@RequestBody F form, Authentication token) {
    return process(helper::update, token, form);
  }
  
  /**
   * Controller method to delete.
   * 
   * @param form
   *          - form
   * @param token
   *          - user
   * @return the response to delete
   */
  @RequestMapping(value = "/delete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse delete(F form, Authentication token) {
    return process(helper::delete, token, form);
  }
  
}
