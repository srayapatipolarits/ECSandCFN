package com.sp.web.controller.hiring.group;

import com.sp.web.controller.generic.GenericControllerHelper;
import com.sp.web.dto.hiring.group.HiringGroupDTO;
import com.sp.web.dto.hiring.group.HiringGroupListingDTO;
import com.sp.web.dto.hiring.user.HiringUserListingDTO;
import com.sp.web.form.hiring.group.HiringGroupForm;
import com.sp.web.model.User;
import com.sp.web.model.hiring.group.HiringGroup;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.hiring.group.HiringGroupFactory;

import org.springframework.stereotype.Component;

import java.util.List;

import javax.inject.Inject;

/**
 * 
 * @author Dax Abraham
 *
 *         The helper class for the people analytics group controller.
 */
@Component
public class HiringGroupControllerHelper
    extends
    GenericControllerHelper<HiringGroup, HiringGroupListingDTO, HiringGroupDTO, HiringGroupForm, HiringGroupFactory> {
  
  private static final String MODULE_NAME = "hiringGroup";
  
  @Inject
  public HiringGroupControllerHelper(HiringGroupFactory factory) {
    super(MODULE_NAME, factory);
  }
  
  /**
   * Add users to a given group.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the add users request
   */
  public SPResponse addUsers(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    HiringGroupForm form = (HiringGroupForm) params[0];
    form.validateGroupUpdate();
    
    List<HiringUserListingDTO> addedUsers = factory.addUsers(user, form);
    return resp.add(MODULE_NAME + "Users", addedUsers);
  }
  
  /**
   * Helper method to remove the users from the group.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the remove request
   */
  public SPResponse removeUsers(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    HiringGroupForm form = (HiringGroupForm) params[0];
    form.validateGroupUpdate();
    
    factory.removeUsers(user, form);
    return resp.isSuccess();
  }
  
  /**
   * The helper method for the get groups for user.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the get request
   */
  public SPResponse userGroups(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    String userId = (String) params[0];
    return resp.add(MODULE_NAME, factory.getGroupsForUser(user, userId));
  }
  
  /**
   * The helper method for the get all the portraits for the users in the group.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the get request
   */
  public SPResponse getPortraits(User user, Object[] params) {
    HiringGroupForm form = (HiringGroupForm) params[0];
    form.validateGet();
    
    return new SPResponse().add(MODULE_NAME + "Listing", factory.getGroupUserPortraits(user, form));
  }
  
}
