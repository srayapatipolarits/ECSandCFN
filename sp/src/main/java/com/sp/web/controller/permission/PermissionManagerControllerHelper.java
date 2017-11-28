package com.sp.web.controller.permission;

import com.sp.web.Constants;
import com.sp.web.dto.PermissionRoleDTO;
import com.sp.web.dto.PermissionRoleDTO.SysUserRoles;
import com.sp.web.dto.SystemAdminUserDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPRoleGroups;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.sse.EventGateway;
import com.sp.web.user.UserFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <code>PermissionManagerControllerHelper</code> contains the helper method for the permission
 * helper.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class PermissionManagerControllerHelper {
  
  /** Initializing the logger. */
  public static final Logger LOG = Logger.getLogger(PermissionManagerControllerHelper.class);
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private EventGateway eventGateway;
  /**
   * getUserAccessList method wil return the list of user who has assigned super admin permissions.
   * 
   * @param user
   *          super administratoor user.
   * @return the spresponse.
   */
  public SPResponse getUserAccessList(User user) {
    List<User> sysPermissionUsers = userFactory.getSysPermissionUsers();
    
    List<SystemAdminUserDTO> sysUsers = sysPermissionUsers.stream().map(usr -> {
      SystemAdminUserDTO systemAdminUserDTO = new SystemAdminUserDTO(usr);
      systemAdminUserDTO.setCompanyName(companyFactory.getCompany(usr.getCompanyId()).getName());
      if(systemAdminUserDTO.getRoles()!=null){
        systemAdminUserDTO.getRoles().removeIf(rol -> !rol.isSysAdminRole());  
      }
      return systemAdminUserDTO;
    }).collect(Collectors.toList());
    
    SPResponse response = new SPResponse();
    response.add(Constants.PARAM_SYSUSERS_LIST, sysUsers);
    return response;
  }
  
  /**
   * saveSysPermission method will save the permission to the user.
   * 
   * @param user
   *          super administrator user.
   * @param params
   *          contains the paramters.
   * @return the SPResponse.
   */
  public SPResponse saveSysPermissions(User user, Object[] params) {
    
    String userEmail = (String) params[0];
    Assert.hasText(userEmail, "User Email cannot be blank or empty");
    
    List<RoleType> sysRoles = (List<RoleType>) params[1];
    
    /* get the users */
    
    sysRoles.stream().forEach(sysRole -> {
        if (!sysRole.isSysAdminRole()) {
          throw new InvalidRequestException("Only Sys Permission Roles are allowed");
        }
      });
    User permissionUser = userRepository.findByEmail(userEmail);
    Assert.notNull(permissionUser, "No user found for the email : " + userEmail);
    
    /* Remove all the sys Admin roles so that we can update the fresh */
    permissionUser.getRoles().removeIf(role -> role.isSysAdminRole());
    permissionUser.removeRole(RoleType.SysAdminMemberRole);
    
    if (!CollectionUtils.isEmpty(sysRoles)) {
      permissionUser.getRoles().addAll(sysRoles);
      permissionUser.addRole(RoleType.SysAdminMemberRole);
    }
    
    userRepository.updateUser(permissionUser);
    
    userFactory.updateUserAndSession(permissionUser);
    return new SPResponse().isSuccess();
  }
  
  /**
   * getSysPermissions method will return the system permission group wise.
   * 
   * @param user
   *          super administrator user.
   * @return the spresponse.
   */
  public SPResponse getSysPermissions(User user, Object [] params) {
    
    String email = (String) params[0];
    
    SPResponse response = new SPResponse();
    User permissionUser = null;
    if (StringUtils.isNotBlank(email)) {
      permissionUser = userRepository.findByEmail(email);
    }
    List<PermissionRoleDTO> permissionGroups = new ArrayList<>();
    SPRoleGroups[] values = SPRoleGroups.values();
    for (SPRoleGroups roleGroups : values) {
      PermissionRoleDTO permissionRoleDTO = new PermissionRoleDTO();
      permissionRoleDTO.setRoleGroups(roleGroups);
      for (RoleType role : roleGroups.getRoles()) {
        SysUserRoles sysUserRoles = new SysUserRoles();
        sysUserRoles.setRoleType(role);
        if (permissionUser != null && permissionUser.getRoles().contains(role)) {
          sysUserRoles.setExist(true);
        }
        permissionRoleDTO.getRoles().add(sysUserRoles);
      }
      permissionGroups.add(permissionRoleDTO);
    }
    
    response.add(Constants.PARAM_PERMISSION_GROUP, permissionGroups);
    return response;
  }
}
