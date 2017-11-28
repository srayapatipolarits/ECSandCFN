package com.sp.web.model;

import org.springframework.social.linkedin.api.Education;
import org.springframework.social.linkedin.api.Position;

import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The linked in profile of the user.
 */
public class SPLinkedInProfile {

  private String summary;
  private List<Education> educationList;
  private List<Position> positionList;

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public List<Education> getEducationList() {
    return educationList;
  }

  public void setEducationList(List<Education> educationList) {
    this.educationList = educationList;
  }

  public List<Position> getPositionList() {
    return positionList;
  }

  public void setPositionList(List<Position> positionList) {
    this.positionList = positionList;
  }
}
