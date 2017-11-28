package com.sp.web.dto.hiring.user;

import com.sp.web.dto.hiring.group.HiringGroupBaseDTO;
import com.sp.web.dto.hiring.role.HiringRoleBaseDTO;
import com.sp.web.model.HiringUser;
import com.sp.web.model.SPMedia;
import com.sp.web.model.UserStatus;
import com.sp.web.service.hiring.role.HiringRoleFactory;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the employee listing.
 */
public class HiringCandidateListingDTO extends HiringEmployeeListingDTO {

  private static final long serialVersionUID = 2273934395647795115L;
  /* The hiring roles for the candidate */
  private List<HiringRoleBaseDTO> hiringRoles;
  private List<SPMedia> urls;
  private String linkedInUrl;
  private String phoneNumber;
  
  /**
   * Constructor.
   * 
   * @param user
   *          - user
   * @param roleFactory
   *          - role factory         
   */
  public HiringCandidateListingDTO(HiringUser user, HiringRoleFactory roleFactory) {
    super(user);
    load(user, roleFactory);
  }
  
  /**
   * Constructor.
   * 
   * @param user
   *          - user
   * @param roleFactory
   *          - role factory         
   * @param userGroupsMap 
   *          - user groups mapping
   */
  public HiringCandidateListingDTO(HiringUser user, HiringRoleFactory roleFactory,
      Map<String, List<HiringGroupBaseDTO>> userGroupsMap) {
    super(user, userGroupsMap);
    load(user, roleFactory);
  }

  public List<HiringRoleBaseDTO> getHiringRoles() {
    return hiringRoles;
  }

  public void setHiringRoles(List<HiringRoleBaseDTO> hiringRoles) {
    this.hiringRoles = hiringRoles;
  }

  public List<SPMedia> getUrls() {
    return urls;
  }

  public void setUrls(List<SPMedia> urls) {
    this.urls = urls;
  }

  public String getLinkedInUrl() {
    return linkedInUrl;
  }

  public void setLinkedInUrl(String linkedInUrl) {
    this.linkedInUrl = linkedInUrl;
  }
  
  /**
   * Load the data for date to show and roles.
   * 
   * @param user
   *          - user
   * @param roleFactory
   *          - role factory
   */
  private void load(HiringUser user, HiringRoleFactory roleFactory) {
    setDateToShow((user.getUserStatus() == UserStatus.VALID) ? user.getAnalysis().getCreatedOn()
        .toLocalDate() : user.getCreatedOn());
    Set<String> hiringRoleIds = user.getHiringRoleIds();
    if (!CollectionUtils.isEmpty(hiringRoleIds)) {
      hiringRoles = roleFactory.get(hiringRoleIds);
    }
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
  
}
