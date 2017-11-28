package com.sp.web.model.tutorial;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Dax Abraham
 * 
 *         The entity to store the users activity tutorial data.
 * 
 */
public class UserTutorialActivity implements Serializable {
  
  private static final long serialVersionUID = 606765589369454585L;
  private String id;
  private String userId;
  private String selectedId;
  private Map<String, TutorialActivityData> tutorialActivityMap;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public String getSelectedId() {
    return selectedId;
  }
  
  public void setSelectedId(String selectedId) {
    this.selectedId = selectedId;
  }
  
  public Map<String, TutorialActivityData> getTutorialActivityMap() {
    return tutorialActivityMap;
  }
  
  public void setTutorialActivityMap(Map<String, TutorialActivityData> tutorialActivityMap) {
    this.tutorialActivityMap = tutorialActivityMap;
  }
  
  /**
   * Add a tutorial to the users tutorial activity.
   * 
   * @param tutorialId
   *            - tutorial id
   */
  public void addTutorial(String tutorialId) {
    Map<String, TutorialActivityData> tutorialActivityMap = getOrCreate();
    TutorialActivityData tutorialActivityData = tutorialActivityMap.get(tutorialId);
    if (tutorialActivityData == null) {
      tutorialActivityData = TutorialActivityData.create(tutorialId);
      tutorialActivityMap.put(tutorialId, tutorialActivityData);
    }
    
    if (selectedId == null) {
      selectedId = tutorialId;
    }
  }

  private Map<String, TutorialActivityData> getOrCreate() {
    if (tutorialActivityMap == null) {
      tutorialActivityMap = new HashMap<String, TutorialActivityData>();
    }
    return tutorialActivityMap;
  }

  /**
   * Gets the current selected tutorial activity data.
   * 
   * @return
   *      tutorial activity data or null
   */
  public TutorialActivityData getSelectedActivity() {
    TutorialActivityData activityData = null;
    if (selectedId != null) {
      activityData = tutorialActivityMap.get(selectedId);
    }
    return activityData;
  }

  /**
   * Get the tutorial activity for the given tutorial id.
   * 
   * @param tutorialId
   *          - tutorial id
   * @return
   *    the tutorial activity data or null
   */
  public TutorialActivityData getUserActivityData(String tutorialId) {
    Map<String, TutorialActivityData> tutorialActivityMap = getOrCreate();
    return tutorialActivityMap.get(tutorialId);
  }
}
