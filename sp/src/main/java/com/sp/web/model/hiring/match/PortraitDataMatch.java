package com.sp.web.model.hiring.match;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The entity class to store the portrait match data.
 */
public class PortraitDataMatch {
  
  private boolean enabled;
  private List<MatchCriteria> matchCriterias;
  
  public boolean isEnabled() {
    return enabled;
  }
  
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
  
  public List<MatchCriteria> getMatchCriterias() {
    return matchCriterias;
  }
  
  public void setMatchCriterias(List<MatchCriteria> matchCriterias) {
    this.matchCriterias = matchCriterias;
  }
}
