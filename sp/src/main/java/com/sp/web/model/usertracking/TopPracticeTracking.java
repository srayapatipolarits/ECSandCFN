package com.sp.web.model.usertracking;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TopArticlesTracking will track the the top rated practice area for all users of the company.
 * 
 * @author pradeepruhil
 *
 */
public class TopPracticeTracking {
  
  private String companyId;
  
  private String id;
  
  private LocalDate date;
  
  private List<String> practiceAreas;
  
  
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  
  public void setDate(LocalDate date) {
    this.date = date;
  }
  
  public LocalDate getDate() {
    return date;
  }
  
  public void setPracticeAreas(List<String> practiceAreas) {
    this.practiceAreas = practiceAreas;
  }
  
  /**
   * TopPracticeAreas
   * 
   * @return list of top practice areas.
   */
  public List<String> getPracticeAreas() {
    if (practiceAreas == null) {
      practiceAreas = new ArrayList<String>();
    }
    return practiceAreas;
  }
  
}
