package com.sp.web.dto.hiring.user;


public class MatchDetailsWithReasonDTO extends MatchDetailsDTO {

  private static final long serialVersionUID = -6754171986843345391L;
  private String reason;

  /**
   * Constructor.
   * 
   * @param portraitData
   *          - portrait data
   * @param matchPercent
   *          - match percent
   * @param reason
   *          - reason
   */
  public MatchDetailsWithReasonDTO(Object portraitData, Integer matchPercent, String reason) {
    super(portraitData, matchPercent);
    this.reason = reason;
  }

  public String getReason() {
    return reason;
  }
  
}
