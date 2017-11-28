package com.sp.web.model.tutorial;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         The entity to store the tutorial activity data.
 */
public class TutorialActivityData implements Serializable {
  
  private static final long serialVersionUID = 2162884006405331790L;
  private String tutorialId;
  private int count;
  private Map<String, Set<String>> completionMap;
  
  public int getCount() {
    return count;
  }
  
  public void setCount(int count) {
    this.count = count;
  }
  
  public String getTutorialId() {
    return tutorialId;
  }

  public void setTutorialId(String tutorialId) {
    this.tutorialId = tutorialId;
  }

  public Map<String, Set<String>> getCompletionMap() {
    return completionMap;
  }

  public void setCompletionMap(Map<String, Set<String>> completionMap) {
    this.completionMap = completionMap;
  }
  
  /**
   * Create a new instance of the Tutorial Activity Data.
   * 
   * @param tutorialId
   *            - tutorial id
   * @return
   *    a shell instance of tutorial activity data
   */
  public static TutorialActivityData create(String tutorialId) {
    TutorialActivityData data = new TutorialActivityData();
    data.setTutorialId(tutorialId);
    data.setCompletionMap(new HashMap<String, Set<String>>());
    return data;
  }

  public Set<String> getCompletionForStep(String id) {
    return Optional.ofNullable(completionMap.get(id)).orElse(new HashSet<String>());
  }

  /**
   * Mark the given action in the step as completed.
   * 
   * @param stepId
   *          - step id
   * @param actionId
   *          - action id
   * @return 
   *    flag to indicate if the data was updated         
   */
  public boolean markComplete(String stepId, String actionId) {
    Set<String> completions = completionMap.get(stepId);
    if (completions == null) {
      completions = new HashSet<String>();
      completionMap.put(stepId, completions);
    }
    boolean isUpdated = false;
    if (completions.add(actionId)) {
      count++;
      isUpdated = true;
    }
    return isUpdated;
  }

  public void reduceCount(int size) {
    count -= size;
  }
}
