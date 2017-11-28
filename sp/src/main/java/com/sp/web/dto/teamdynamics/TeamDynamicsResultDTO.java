package com.sp.web.dto.teamdynamics;

import com.sp.web.assessment.questions.CategoryPairs;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.dto.user.UserAnalysisDTO;
import com.sp.web.model.User;
import com.sp.web.model.teamdynamics.CategoryPairData;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TeamDynamicsResultDTO contains the result to be sent to the front end.
 * 
 * @author pradeepruhil
 *
 */
public class TeamDynamicsResultDTO implements Serializable {
  
  private static final long serialVersionUID = -336865650541182731L;
  
  private List<UserAnalysisDTO> usersList;
  
  private Map<CategoryType, Object> results;
  
  public TeamDynamicsResultDTO(List<User> users) {
    usersList = users.stream().map(UserAnalysisDTO::new).collect(Collectors.toList());
  }
  
  public void setResults(Map<CategoryType, Object> results) {
    this.results = results;
  }
  
  /**
   * getResult method will give the result back.
   * 
   * @return
   */
  public Map<CategoryType, Object> getResults() {
    if (results == null) {
      results = new LinkedHashMap<CategoryType, Object>();
    }
    return results;
  }
  
  public void setUsersList(List<UserAnalysisDTO> usersList) {
    this.usersList = usersList;
  }
  
  public List<UserAnalysisDTO> getUsersList() {
    return usersList;
  }
  
  @Override
  public String toString() {
    return "TeamDynamicsResultDTO [usersList=" + usersList + ", results=" + results + "]";
  }
  
}
