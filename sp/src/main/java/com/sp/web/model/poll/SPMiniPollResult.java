package com.sp.web.model.poll;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * <code>SPMiniPollResult</code> class hold the result of the mini poll for the user.
 * 
 * @author pradeepruhil
 *
 */
public class SPMiniPollResult implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 8535078485566044667L;
  
  private List<Integer> results;
  
  private BigDecimal averageScore;
  
  public SPMiniPollResult() {
  }
  
  @JsonIgnore
  private Map<String, List<Integer>> userPollSelection;
  
  public SPMiniPollResult(int totalOptions) {
    results = new ArrayList<Integer>(totalOptions);
    IntStream.range(0, totalOptions).forEach(index -> results.add(0));
    
  }
  
  public List<Integer> getResults() {
    return results;
  }
  
  public void setResults(List<Integer> results) {
    this.results = results;
  }
  
  public BigDecimal getAverageScore() {
    return averageScore;
  }
  
  public void setAverageScore(BigDecimal averageScore) {
    this.averageScore = averageScore;
  }
  
  /**
   * getUserPollSelection method will get the user poll selection.
   * 
   * @return the map of user and its selections.
   */
  public Map<String, List<Integer>> getUserPollSelection() {
    if (userPollSelection == null) {
      userPollSelection = new HashMap<String, List<Integer>>();
    }
    return userPollSelection;
  }
  
  public void setUserPollSelection(Map<String, List<Integer>> userPollSelection) {
    this.userPollSelection = userPollSelection;
  }
  
  /**
   * updateResult method will update the result.
   * 
   * @param options
   *          contains the options.
   * @param userId
   *          of the user.
   */
  public synchronized void updateResult(List<Integer> options, String userId) {
    for (Integer option : options) {
      Integer integer = results.get(option);
      results.set(option, integer + 1);
    }
    /* calucate average */
    int sum = 0;
    for (int i = 0; i < results.size(); i++) {
      int totalCount = results.get(i);
      sum += (totalCount) * (i + 1);
    }
    
    Map<String, List<Integer>> userPollSelection2 = getUserPollSelection();
    userPollSelection2.put(userId, options);
    averageScore = new BigDecimal(sum).divide(new BigDecimal(userPollSelection2.size()), 2,
        RoundingMode.HALF_UP);
  }
  
}
