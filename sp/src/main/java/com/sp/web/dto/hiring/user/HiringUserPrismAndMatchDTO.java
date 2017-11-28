package com.sp.web.dto.hiring.user;

import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.model.HiringUser;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for the hiring user with the details for Prism Portrait as well as Portrait
 *         Match.
 */
public class HiringUserPrismAndMatchDTO extends HiringUserPortraitMatchDTO {
  
  private static final long serialVersionUID = -8608812607756019761L;
  private AnalysisBean portrait;
  
  /**
   * Constructor.
   * 
   * @param user
   *          -user
   */
  public HiringUserPrismAndMatchDTO(HiringUser user) {
    this(user, true);
  }

  /**
   * Constructor.
   * 
   * @param user
   *          - user
   * @param addPrism
   *          - flag to indicate if prism portrait should be added or not.
   */
  public HiringUserPrismAndMatchDTO(HiringUser user, boolean addPrism) {
    super(user);
    if (addPrism) {
      portrait = user.getAnalysis();
    }
  }
  
  public AnalysisBean getPortrait() {
    return portrait;
  }

  public void setPortrait(AnalysisBean portrait) {
    this.portrait = portrait;
  }
}
