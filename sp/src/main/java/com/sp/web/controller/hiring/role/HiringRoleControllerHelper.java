package com.sp.web.controller.hiring.role;

import com.sp.web.controller.generic.GenericControllerHelper;
import com.sp.web.dto.hiring.role.HiringRoleDTO;
import com.sp.web.dto.hiring.role.HiringRoleListingDTO;
import com.sp.web.form.hiring.role.HiringRoleForm;
import com.sp.web.model.User;
import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.hiring.role.HiringRoleFactory;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.inject.Inject;

/**
 * 
 * @author Dax Abraham
 *
 *         The controller helper for the Hiring Role controller.
 */
@Component
public class HiringRoleControllerHelper
    extends
    GenericControllerHelper<HiringRole, HiringRoleListingDTO, HiringRoleDTO, HiringRoleForm, HiringRoleFactory> {
  
  private static final String MODULE_NAME = "hiringRole";
  
  /**
   * Constructor.
   * 
   * @param factory
   *          - hiring role factory
   */
  @Inject
  public HiringRoleControllerHelper(HiringRoleFactory factory) {
    super(MODULE_NAME, factory);
  }

  /**
   * Helper method to assign the portrait to the role.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    the response to the assign request
   */
  public SPResponse assignPortrait(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    HiringRoleForm form = (HiringRoleForm) params[0];
    form.validateAssign();
    return resp.add(MODULE_NAME, factory.assignPortrait(user, form));
  }
  
  /**
   * Helper method to remove the portrait from the role.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    the response to the remove request
   */
  public SPResponse removePortrait(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    HiringRoleForm form = (HiringRoleForm) params[0];
    form.validateGet();
    factory.removePortrait(user, form);
    return resp.isSuccess();
  }  
  
  /**
   * Helper method to get the roles and portrait match for the given user.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    the response to the get request
   */
  public SPResponse getUserRolesAndMatch(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String userId = (String) params[0];
    Assert.hasText(userId, "User id required.");
    
    return resp.add(MODULE_NAME + "AndMatch", factory.getUserRoleMatch(userId));
  }
  
  /**
   * Helper method to get the roles without any assigned portraits.
   * 
   * @param user
   *          - user
   * @return
   *    the response to the get request
   */
  public SPResponse getRolesWithoutPortrait(User user) {
    final SPResponse resp = new SPResponse();
    
    return resp.add(MODULE_NAME + "List", factory.getRolesWithoutPortrait(user));
  }  
  
  /**
   * Helper method to add the users to a given role.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    the response to the add request
   */
  public SPResponse addToRole(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    HiringRoleForm form = (HiringRoleForm) params[0];
    form.validateAddToRole();
    factory.addToRole(user, form);
    return resp.isSuccess();
  }   
}
