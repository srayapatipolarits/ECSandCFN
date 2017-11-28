package com.sp.web.dto.hiring.role;

import com.sp.web.dao.hiring.match.HiringPortraitDao;
import com.sp.web.dto.hiring.match.HiringPortraitMatchReportDTO;
import com.sp.web.dto.hiring.match.HiringPortraitMatchResultDTO;
import com.sp.web.model.hiring.role.HiringRole;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the hiring role and the user portrait match information.
 */
public class HiringRoleAndMatchDTO extends HiringRoleBaseDTO {
  
  private static final long serialVersionUID = -1198109082646424298L;
  private HiringPortraitMatchReportDTO portrait;
  private HiringPortraitMatchResultDTO matchResult;
  
  public HiringRoleAndMatchDTO(HiringRole role) {
    super(role);
  }
  
  public HiringPortraitMatchReportDTO getPortrait() {
    return portrait;
  }
  
  public void setPortrait(HiringPortraitMatchReportDTO portrait) {
    this.portrait = portrait;
  }
  
  public HiringPortraitMatchResultDTO getMatchResult() {
    return matchResult;
  }
  
  public void setMatchResult(HiringPortraitMatchResultDTO matchResult) {
    this.matchResult = matchResult;
  }

  /**
   * Update the match result and the portrait.
   * 
   * @param matchResult
   *            - match result
   * @param portrait
   *            - portrait
   */
  public void update(HiringPortraitMatchResultDTO matchResult, HiringPortraitDao portrait) {
    this.matchResult = matchResult;
    this.portrait = new HiringPortraitMatchReportDTO(portrait);
  }
  
}
