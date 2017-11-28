package com.sp.web.service.hiring.role;

import com.sp.web.controller.generic.GenericFactory;
import com.sp.web.dao.hiring.match.HiringPortraitDao;
import com.sp.web.dto.hiring.role.HiringRoleAndMatchDTO;
import com.sp.web.dto.hiring.role.HiringRoleBaseDTO;
import com.sp.web.dto.hiring.role.HiringRoleDTO;
import com.sp.web.dto.hiring.role.HiringRoleListingDTO;
import com.sp.web.dto.hiring.user.HiringUserPrismAndMatchDTO;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.form.hiring.role.HiringRoleForm;
import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.service.hiring.match.HiringPortraitMatchFactory;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The factory class for the hiring role functions.
 */
@Component
public class HiringRoleFactory implements
    GenericFactory<HiringRoleListingDTO, HiringRoleDTO, HiringRoleForm> {
  
  @Autowired
  HiringRoleFactoryCache factoryCache;
  
  @Autowired
  HiringUserFactory userFactory;
  
  @Autowired
  HiringPortraitMatchFactory portraitFactory;
  
  @Override
  public List<HiringRoleListingDTO> getAll(User user) {
    List<HiringRole> all = factoryCache.getAll(user.getCompanyId());
    return all.stream().map(r -> new HiringRoleListingDTO(r, userFactory, portraitFactory))
        .collect(Collectors.toList());
  }
  
  public List<HiringRole> getAll(String companyId) {
    return factoryCache.getAll(companyId);
  }
  
  @Override
  public HiringRoleDTO get(User user, HiringRoleForm form) {
    HiringRole role = getRole(user, form);
    // get the users for the given role
    List<HiringUser> users = userFactory.getUsersWithRole(role.getCompanyId(), role.getId());
    final String portraitId = role.getPortraitId();
    final boolean addPrism = form.isAddPrism();
    HiringRoleDTO roleDTO = null;
    if (portraitId != null) {
      HiringPortraitDao portrait = portraitFactory.getPortrait(portraitId);
      roleDTO = new HiringRoleDTO(role, portrait);
      roleDTO.setUsers(portraitFactory.processMatch(portrait, users, addPrism));
    } else {
      roleDTO = new HiringRoleDTO(role);
      roleDTO.setUsers(users.stream().map(u -> new HiringUserPrismAndMatchDTO(u, addPrism))
          .collect(Collectors.toList()));
    }
    return roleDTO;
  }
  
  public HiringRole get(String roleId) {
    return factoryCache.get(roleId);
  }
  
  public List<HiringRoleBaseDTO> get(Set<String> roleIds) {
    return roleIds.stream().map(this::get).filter(Objects::nonNull).map(HiringRoleBaseDTO::new)
        .collect(Collectors.toList());
  }
  
  @Override
  public HiringRoleDTO create(User user, HiringRoleForm form) {
    // check if there is an existing one with the same name
    Assert.isNull(factoryCache.findByNameIgnoreCase(user.getCompanyId(), form.getName()),
        MessagesHelper.getMessage("error.hire.role.exist", user.getLocale()));
    HiringRole create = form.create(user);
    HiringPortraitDao portrait = validatePortrait(create);
    factoryCache.save(create);
    return new HiringRoleDTO(create, portrait);
  }
  
  @Override
  public HiringRoleDTO update(User user, HiringRoleForm form) {
    final HiringRole role = getRole(user, form);
    form.update(user, role);
    HiringRole existingRole = factoryCache
        .findByNameIgnoreCase(user.getCompanyId(), form.getName());
    Optional.ofNullable(existingRole).ifPresent(
        r -> Assert.isTrue(role.getId().equals(r.getId()),
            MessagesHelper.getMessage("error.hire.role.exist", user.getLocale())));
    HiringPortraitDao portrait = validatePortrait(role);
    factoryCache.save(role);
    return new HiringRoleDTO(role, portrait);
  }
  
  public void update(HiringRole hiringRole) {
    factoryCache.save(hiringRole);
  }
  
  @Override
  public void delete(User user, HiringRoleForm form) {
    HiringRole role = getRole(user, form);
    // removing the role for the users
    final String roleId = role.getId();
    List<HiringUser> users = userFactory.getUsersWithRole(user.getCompanyId(), roleId);
    users.forEach(u -> userFactory.removeRole(u, roleId));
    factoryCache.delete(role);
  }
  
  /**
   * Assign the given portrait to the role.
   * 
   * @param user
   *          - user
   * @param form
   *          - form data
   * @return the updated hiring role
   */
  public HiringRoleDTO assignPortrait(User user, HiringRoleForm form) {
    HiringRole role = getRole(user, form);
    final String portraitId = form.getPortraitId();
    role.setPortraitId(portraitId);
    final HiringPortraitDao portrait = validatePortrait(role);
    role.setUpdatedBy(new UserMarkerDTO(user));
    role.setUpdatedOn(LocalDateTime.now());
    factoryCache.save(role);
    return new HiringRoleDTO(role, portrait);
  }
  
  /**
   * Remove the portrait from the role.
   * 
   * @param user
   *          - user
   * @param form
   *          - form
   */
  public void removePortrait(User user, HiringRoleForm form) {
    HiringRole role = getRole(user, form);
    if (role.removePortriat()) {
      factoryCache.save(role);
    }
  }
  
  /**
   * Method to remove the given portrait from all the roles.
   * 
   * @param portrait
   *          - portrait
   */
  public void removePortraitFromRoles(HiringPortraitDao portrait) {
    List<HiringRole> roles = factoryCache.getByPortrait(portrait.getId());
    roles.stream().filter(HiringRole::removePortriat).forEach(factoryCache::save);
  }
  
  /**
   * Remove the portrait for the given company.
   * 
   * @param portrait
   *          - portrait
   * @param companyId
   *          - company id
   */
  public void removePortraitFromRoles(HiringPortraitDao portrait, String companyId) {
    List<HiringRole> roles = factoryCache.getByPortrait(portrait.getId());
    roles.stream().filter(r -> r.getCompanyId().equals(companyId))
        .filter(HiringRole::removePortriat).forEach(factoryCache::save);
  }
  
  /**
   * Get the hiring roles as well as match details for the given user.
   * 
   * @param userId
   *          - user id
   * @return the list of hiring role and match data for the user.
   */
  public List<HiringRoleAndMatchDTO> getUserRoleMatch(String userId) {
    final HiringUser user = userFactory.getUser(userId);
    Assert.notNull(user, MessagesHelper.getMessage("error.user.not.found"));
    return getUserRoleMatch(user);
  }
  
  /**
   * Get the hiring roles as well as match details for the given user.
   * 
   * @param user
   *          - user id
   * @return the list of hiring role and match data for the user.
   */
  public List<HiringRoleAndMatchDTO> getUserRoleMatch(final HiringUser user) {
    final List<HiringRoleAndMatchDTO> userRoleMatchList = new ArrayList<HiringRoleAndMatchDTO>();
    Set<String> hiringRoleIds = user.getHiringRoleIds();
    if (CollectionUtils.isNotEmpty(hiringRoleIds)) {
      hiringRoleIds.stream().map(factoryCache::get).filter(Objects::nonNull)
          .map(r -> getUserRoleMatch(user, r)).forEach(userRoleMatchList::add);
    }
    return userRoleMatchList;
  }
  
  /**
   * Get the portrait match for the given role.
   * 
   * @param user
   *          - user
   * @param role
   *          - role
   * @return the hiring role and match instance
   */
  public HiringRoleAndMatchDTO getUserRoleMatch(HiringUser user, HiringRole role) {
    HiringRoleAndMatchDTO hiringRoleAndMatchDTO = new HiringRoleAndMatchDTO(role);
    portraitFactory.processMatch(user, role, hiringRoleAndMatchDTO);
    return hiringRoleAndMatchDTO;
  }
  
  /**
   * Method to validate if the role is present for the given role id.
   * 
   * @param user
   *          - user
   * @param roleId
   *          - role id
   */
  public void validateRole(User user, String roleId) {
    getRole(user, roleId);
  }
  
  /**
   * Get all the roles for the given portrait id in the company.
   * 
   * @param portraitId
   *          - portrait id
   * @param companyId
   *          - company id
   * @return the list of roles
   */
  public List<HiringRoleBaseDTO> getRolesForPortrait(String portraitId, String companyId) {
    return factoryCache.getByPortrait(portraitId, companyId).stream().map(HiringRoleBaseDTO::new)
        .collect(Collectors.toList());
  }
  
  /**
   * Get all the roles without any portraits assigned to it.
   * 
   * @param user
   *          - user
   * @return the list of roles without portrait
   */
  public List<HiringRoleBaseDTO> getRolesWithoutPortrait(User user) {
    return getAll(user.getCompanyId()).stream().filter(r -> r.getPortraitId() == null)
        .map(HiringRoleBaseDTO::new).collect(Collectors.toList());
  }
  
  /**
   * Get the hiring role from the db and also validate if found. This method additionally authorizes
   * the access of the role with the given user.
   * 
   * @param user
   *          - user
   * @param form
   *          - form
   * @return the hiring role
   */
  private HiringRole getRole(User user, HiringRoleForm form) {
    return getRole(user, form.getId());
  }
  
  /**
   * Get the hiring role from the db and also validate if found. This method additionally authorizes
   * the access of the role with the given user.
   * 
   * @param user
   *          - user
   * @param roleId
   *          - role id
   * @return the hiring role
   */
  private HiringRole getRole(User user, String roleId) {
    HiringRole role = factoryCache.get(roleId);
    Assert.notNull(role, "Role not found.");
    Assert.isTrue(user.getCompanyId().equals(role.getCompanyId()), "Unauthorized access.");
    return role;
  }
  
  /**
   * Check if the portrait is valid in the role.
   * 
   * @param role
   *          - role
   * @return the portrait
   */
  private HiringPortraitDao validatePortrait(HiringRole role) {
    final String portraitId = role.getPortraitId();
    if (portraitId != null) {
      HiringPortraitDao portrait = portraitFactory.getPortrait(portraitId);
      Assert.notNull(portrait, "Portrait not found.");
      Assert.isTrue(portrait.isAuthorized(role.getCompanyId()),
          MessagesHelper.getMessage("portrait.notAssigned"));
      return portrait;
    }
    return null;
  }

  /**
   * Method to add the role to the given list of users in the hiring role form.
   * 
   * @param user
   *          - logged in user
   * @param form
   *          - form to get the list of users
   * @return
   *    the list of users 
   */
  public List<HiringUserPrismAndMatchDTO> addToRole(User user, HiringRoleForm form) {
    final HiringRole role = getRole(user, form);
    final Optional<HiringPortraitDao> portrait = Optional.ofNullable(role.getPortraitId())
        .map(portraitFactory::getPortrait);
    List<HiringUser> users = form.getUserIds().stream().map(userFactory::getUser)
        .filter(Objects::nonNull)
        .map(usr -> addRole(usr, role))
        .collect(Collectors.toList());
    if (portrait.isPresent()) {
      return portraitFactory.processMatch(portrait.get(), users, false);
    } else {
      return users.stream().map(u -> new HiringUserPrismAndMatchDTO(u, false))
          .collect(Collectors.toList());
    }
  }

  /**
   * Add the given role to the user.
   * 
   * @param user
   *          - user
   * @param role
   *          - role
   * @return
   *      the updated user
   */
  private HiringUser addRole(HiringUser user, HiringRole role) {
    if (user.addHiringRole(role)) {
      userFactory.updateUser(user);
    }
    return user;
  }
}
