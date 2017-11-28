package com.sp.web.dto.spectrum;

import com.sp.web.dto.PracticeAreaDetailsDTO;
import com.sp.web.model.usertracking.TopPracticeTracking;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class TopPracticeAreaDTO {
  
  private int month;
  
  private List<PracticeAreaDetailsDTO> practiceAreasGoals;
  
  private int year;
  
  public TopPracticeAreaDTO(TopPracticeTracking top) {
    BeanUtils.copyProperties(top, this);
    this.month = top.getDate().getMonth().getValue();
    this.year = top.getDate().getYear();
  }
  
  public int getMonth() {
    return month;
  }
  
  public void setMonth(int month) {
    this.month = month;
  }
  
  public void setPracticeAreasGoals(List<PracticeAreaDetailsDTO> practiceAreasGoals) {
    this.practiceAreasGoals = practiceAreasGoals;
  }
  
  public List<PracticeAreaDetailsDTO> getPracticeAreasGoals() {
    if (practiceAreasGoals == null) {
      practiceAreasGoals = new ArrayList<PracticeAreaDetailsDTO>();
    }
    return practiceAreasGoals;
  }
  
  public int getYear() {
    return year;
  }
  
  public void setYear(int year) {
    this.year = year;
  }
  
}
