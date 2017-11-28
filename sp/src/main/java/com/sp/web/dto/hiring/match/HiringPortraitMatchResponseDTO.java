package com.sp.web.dto.hiring.match;

import com.sp.web.dao.hiring.match.HiringPortraitDao;
import com.sp.web.dto.hiring.user.HiringUserPortraitMatchDTO;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Dax Abraham
 * 
 *         The DTO to store the hiring portrait match response.
 */
public class HiringPortraitMatchResponseDTO implements Serializable {
  
  private static final long serialVersionUID = 6602079420210590132L;
  private HiringPortraitMatchReportDTO portrait;
  private List<HiringUserPortraitMatchDTO> users;
  
  /**
   * Constructor.
   * 
   * @param portrait
   *          -portrait
   */
  public HiringPortraitMatchResponseDTO(HiringPortraitDao portrait) {
    this.portrait = new HiringPortraitMatchReportDTO(portrait);
  }

  public HiringPortraitMatchReportDTO getPortrait() {
    return portrait;
  }
  
  public void setPortrait(HiringPortraitMatchReportDTO portrait) {
    this.portrait = portrait;
  }
  
  public List<HiringUserPortraitMatchDTO> getUsers() {
    return users;
  }

  public void setUsers(List<HiringUserPortraitMatchDTO> users) {
    this.users = users;
  }
  
}
