package com.sp.web.dto.hiring.user;

import java.io.Serializable;

/**
 * 
 * @author Dax Abraham
 *
 *         The entity to store the details for the match.
 */
public class MatchDetailsDTO implements Serializable {

  private static final long serialVersionUID = -8374017146192677211L;
  private Integer matchPercent;
  private Object portraitData;
  
  /**
   * Constructor.
   * 
   * @param portraitData
   *          - portrait data to set
   * @param matchPercent
   *          - match percent
   */
  public MatchDetailsDTO(Object portraitData, Integer matchPercent) {
    this.portraitData = portraitData;
    this.matchPercent = matchPercent;
  }

  /**
   * Constructor.
   * 
   * @param portraitData
   *            - portrait data
   */
  public MatchDetailsDTO(Object portraitData) {
    this.portraitData = portraitData;
  }

  public Integer getMatchPercent() {
    return matchPercent;
  }
  
  public void setMatchPercent(Integer matchPercent) {
    this.matchPercent = matchPercent;
  }

  public Object getPortraitData() {
    return portraitData;
  }

  public void setPortraitData(Object portraitData) {
    this.portraitData = portraitData;
  }
}
