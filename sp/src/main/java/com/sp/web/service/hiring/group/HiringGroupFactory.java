package com.sp.web.service.hiring.group;

import com.sp.web.controller.generic.GenericFactory;
import com.sp.web.dto.hiring.group.HiringGroupBaseDTO;
import com.sp.web.dto.hiring.group.HiringGroupDTO;
import com.sp.web.dto.hiring.group.HiringGroupListingDTO;
import com.sp.web.dto.hiring.user.HiringUserListingDTO;
import com.sp.web.dto.hiring.user.HiringUserPrismAndMatchDTO;
import com.sp.web.form.hiring.group.HiringGroupForm;
import com.sp.web.model.User;
import com.sp.web.model.hiring.group.HiringGroup;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 * 
 *         The factory class for people analytics group functionality.
 */
@Component
public class HiringGroupFactory implements
    GenericFactory<HiringGroupListingDTO, HiringGroupDTO, HiringGroupForm> {
  
  @Autowired
  HiringGroupFactoryCache factoryCache;
  
  @Autowired
  HiringUserFactory userFactory;
  
  @Override
  public List<HiringGroupListingDTO> getAll(User user) {
    return factoryCache.getAll(user.getCompanyId()).stream().map(HiringGroupListingDTO::new)
        .collect(Collectors.toList());
  }
  
  @Override
  public HiringGroupDTO get(User user, HiringGroupForm form) {
    return new HiringGroupDTO(getGroup(user, form), userFactory);
  }
  
  public HiringGroup get(String groupId) {
    return factoryCache.get(groupId);
  }
  
  @Override
  public HiringGroupDTO create(User user, HiringGroupForm form) {
    final HiringGroup newGroup = form.create(user);
    HiringGroup existingGroup = factoryCache.findByNameIgnoreCase(newGroup.getCompanyId(),
        newGroup.getName());
    Assert.isTrue(existingGroup == null,
        MessagesHelper.getMessage("error.hire.group.exist", user.getLocale()));
    return new HiringGroupDTO(factoryCache.save(newGroup));
  }
  
  @Override
  public HiringGroupDTO update(User user, HiringGroupForm form) {
    HiringGroup hiringGroup = getGroup(user, form);
    form.update(user, hiringGroup);
    HiringGroup existingGroup = factoryCache.findByNameIgnoreCase(hiringGroup.getCompanyId(),
        hiringGroup.getName());
    if (existingGroup != null) {
      Assert.isTrue(hiringGroup.getId().equals(existingGroup.getId()),
          MessagesHelper.getMessage("error.hire.group.exist", user.getLocale()));
    }
    factoryCache.save(hiringGroup);
    return new HiringGroupDTO(hiringGroup);
  }
  
  public void update(HiringGroup hiringGroup) {
    factoryCache.save(hiringGroup);
  }
  
  @Override
  public void delete(User user, HiringGroupForm form) {
    factoryCache.remove(getGroup(user, form));
  }
  
  /**
   * Adds the given users to the group.
   * 
   * @param user
   *          - user
   * @param form
   *          - details of the users and the group
   * @return the list of newly added users
   */
  public List<HiringUserListingDTO> addUsers(User user, HiringGroupForm form) {
    HiringGroup hiringGroup = getGroup(user, form);
    List<HiringUserListingDTO> collect = form.getUserIds().stream().map(userFactory::getUser)
        .filter(Objects::nonNull).map(hiringGroup::add).map(HiringUserListingDTO::new)
        .collect(Collectors.toList());
    factoryCache.save(hiringGroup);
    return collect;
  }
  
  /**
   * Method to remove the users from the group.
   * 
   * @param user
   *          - user
   * @param form
   *          - the group and user details
   */
  public void removeUsers(User user, HiringGroupForm form) {
    HiringGroup hiringGroup = getGroup(user, form);
    form.getUserIds().forEach(uid -> remove(uid, hiringGroup));
  }
  
  /**
   * Create a map of all the users and their groups.
   * 
   * @param companyId
   *          - company id
   * @return the map of users and their groups
   */
  public Map<String, List<HiringGroupBaseDTO>> getUserGroupMap(String companyId) {
    List<HiringGroup> all = factoryCache.getAll(companyId);
    final Map<String, List<HiringGroupBaseDTO>> userGroupMap = new HashMap<String, List<HiringGroupBaseDTO>>();
    for (HiringGroup group : all) {
      final Set<String> userIds = group.getUserIds();
      if (!userIds.isEmpty()) {
        final HiringGroupBaseDTO groupDTO = new HiringGroupBaseDTO(group);
        userIds.forEach(u -> addToMap(u, userGroupMap, groupDTO));
      }
    }
    return userGroupMap;
  }
  
  /**
   * Gets the groups the user belongs to.
   * 
   * @param user
   *          - user
   * @param userId
   *          - user id
   * @return the list of groups
   */
  public List<HiringGroupBaseDTO> getGroupsForUser(User user, String userId) {
    List<HiringGroup> groups = factoryCache.getGroupsForUser(user.getCompanyId(), userId);
    return groups.stream()
        .collect(Collectors.mapping(HiringGroupBaseDTO::new, Collectors.toList()));
  }
  
  /**
   * Remove the user from all the groups.
   * 
   * @param user
   *          - user
   * @param userId
   *          - user id
   */
  public void removeUserFromGroups(User user, String userId) {
    List<HiringGroup> groups = factoryCache.getGroupsForUser(user.getCompanyId(), userId);
    groups.forEach(g -> remove(userId, g));
  }
  
  /**
   * Get the portraits for the users in the group.
   * 
   * @param user
   *          - user
   * @param form
   *          - form
   * @return
   *      the portraits for the user
   */
  public List<HiringUserPrismAndMatchDTO> getGroupUserPortraits(User user, HiringGroupForm form) {
    HiringGroup group = getGroup(user, form);
    return group.getUserIds().stream().map(userFactory::getUser).filter(Objects::nonNull)
        .filter(usr -> usr.getAnalysis() != null).map(HiringUserPrismAndMatchDTO::new)
        .collect(Collectors.toList());
  }

  /**
   * Remove the user from the given hiring group.
   * 
   * @param userId
   *          - user id
   * @param hiringGroup
   *          - hiring group
   */
  private void remove(String userId, HiringGroup hiringGroup) {
    if (hiringGroup.remove(userId)) {
      factoryCache.save(hiringGroup);
    }
  }
  
  /**
   * Add the group to the given user.
   * 
   * @param uid
   *          - user
   * @param userGroupMap
   *          - user group map
   * @param groupDTO
   *          - group dto
   */
  private void addToMap(String uid, Map<String, List<HiringGroupBaseDTO>> userGroupMap,
      HiringGroupBaseDTO groupDTO) {
    List<HiringGroupBaseDTO> list = userGroupMap.get(uid);
    if (list == null) {
      list = new ArrayList<HiringGroupBaseDTO>();
      userGroupMap.put(uid, list);
    }
    list.add(groupDTO);
  }
  
  /**
   * Authorize the given user against the group.
   * 
   * @param user
   *          - user
   * @param group
   *          - group
   */
  private void authorize(User user, HiringGroup group) {
    Assert.isTrue(user.getCompanyId().equals(group.getCompanyId()), "Unauthorized request.");
  }
  
  /**
   * Get the group from the form, check non null and also authorize the request.
   * 
   * @param user
   *          - user
   * @param form
   *          - form
   * @return the group
   */
  private HiringGroup getGroup(User user, HiringGroupForm form) {
    final String groupId = form.getId();
    HiringGroup group = factoryCache.get(groupId);
    Assert.notNull(group, "Group not found :" + groupId);
    authorize(user, group);
    return group;
  }

  /**
   * Get the group with the given group name.
   * 
   * @param groupName
   *          - group name
   * @param companyId
   *          - company id
   */
  public HiringGroup getByName(String groupName, String companyId) {
    return factoryCache.findByNameIgnoreCase(companyId, groupName);
  }
}
