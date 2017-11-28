package com.sp.web.dto.hiring.user;

import com.sp.web.dto.hiring.role.HiringRoleBaseDTO;
import com.sp.web.model.HiringUser;
import com.sp.web.model.SPMedia;
import com.sp.web.service.hiring.role.HiringRoleFactory;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * 
 * @author Dax Abraham
 * 
 *         The DTO class for the hiring candidates.
 */
public class HiringCandidateDTO extends HiringEmployeeDTO {
  
  private static final long serialVersionUID = -2732416518568141309L;
  
  private String linkedInUrl;
  private List<SPMedia> urls;
  private List<HiringRoleBaseDTO> hiringRoles;
  
  /**
   * Profile Urls will be used for storing the linked Urls and other experience URLS for the
   * candidate.
   */
  private List<SPMedia> profileUrls;
  
  /**
   * Constructor.
   * 
   * @param user
   *          - user
   */
  public HiringCandidateDTO(HiringUser user, HiringRoleFactory roleFactory) {
    super(user);
    final Set<String> hiringRoleIds = user.getHiringRoleIds();
    if (CollectionUtils.isNotEmpty(hiringRoleIds)) {
      hiringRoles = roleFactory.get(hiringRoleIds);
    }
  }
  
  /**
   * Constructor.
   * 
   * @param user
   *          - constructor from user
   */
  public HiringCandidateDTO(HiringUser user) {
    super(user);
  }
  
  public String getLinkedInUrl() {
    return linkedInUrl;
  }
  
  public void setLinkedInUrl(String linkedInUrl) {
    this.linkedInUrl = linkedInUrl;
  }
  
  public List<SPMedia> getUrls() {
    return urls;
  }
  
  public void setUrls(List<SPMedia> urls) {
    this.urls = urls;
  }
  
  public List<HiringRoleBaseDTO> getHiringRoles() {
    return hiringRoles;
  }
  
  public void setHiringRoles(List<HiringRoleBaseDTO> hiringRoles) {
    this.hiringRoles = hiringRoles;
  }
  
  public void setProfileUrls(List<SPMedia> profileUrls) {
    this.profileUrls = profileUrls;
  }
  
  public List<SPMedia> getProfileUrls() {
    return profileUrls;
  }
  
}
