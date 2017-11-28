package com.sp.web.model.goal;

import java.io.Serializable;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The model to capture the key out comes.
 */
public class KeyOutcomes implements Serializable {
  
  private static final long serialVersionUID = 3088969447033389517L;
  
  private boolean active;
  private String description;
  private List<String> outcomesList;
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public List<String> getOutcomesList() {
    return outcomesList;
  }
  
  public void setOutcomesList(List<String> outcomesList) {
    this.outcomesList = outcomesList;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
