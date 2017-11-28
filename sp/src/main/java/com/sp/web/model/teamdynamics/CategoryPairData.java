package com.sp.web.model.teamdynamics;

import com.sp.web.assessment.questions.CategoryPairs;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CategoryPairData model hold the pair data for the categories.
 * 
 * @author pradeepruhil
 *
 */
public class CategoryPairData implements Serializable {
  
  private static final long serialVersionUID = 5201914472077969735L;
  
  /**
   * TraitsMap hold the number of users exist in the trait type. List conatins the uid of the users.
   */
  private Map<TraitType, List<String>> traitsMap;
  
  /** userid in case of user is present in both the traits equally. */
  private List<String> equalTraitsUserIds;
  
  /**
   * getTraitsMap method will return the traits map.
   * 
   * @return the traits map.
   */
  public Map<TraitType, List<String>> getTraitsMap() {
    if (traitsMap == null) {
      traitsMap = new HashMap<TraitType, List<String>>();
    }
    return traitsMap;
  }
  
  public void setTraitsMap(Map<TraitType, List<String>> traitsMap) {
    this.traitsMap = traitsMap;
  }
  
  /**
   * getEqualTriatsUsersIds will hold the user list for the traits which are present in the equal
   * bracket.
   * 
   * @return the equal traits user ids.
   */
  public List<String> getEqualTraitsUserIds() {
    if (equalTraitsUserIds == null) {
      equalTraitsUserIds = new ArrayList<String>();
    }
    return equalTraitsUserIds;
  }
  
  public void setEqualTraitsUserIds(List<String> equalTraitsUserIds) {
    this.equalTraitsUserIds = equalTraitsUserIds;
  }
  
  /**
   * addTraitsMap data will add the traits data to the user.
   * 
   * @param categoryPair
   *          is the triat data.
   * @param user
   *          whose data is to be added.
   */
  public void addTraitDataMap(CategoryPairs categoryPair, User user, TraitType traitType,
      boolean equal) {
    
    if (equal) {
      if (!getEqualTraitsUserIds().contains(user.getId())) {
        getEqualTraitsUserIds().add(user.getId());
      }
      
    } else {
      List<String> list = getTraitsMap().get(traitType);
      if (list == null) {
        list = new ArrayList<String>();
        getTraitsMap().put(traitType, list);
      }
      list.add(user.getId());
    }
  }
  
  @Override
  public String toString() {
    return "CategoryPairData [traitsMap=" + traitsMap + ", equalTraitsUserIds="
        + equalTraitsUserIds + "]";
  }
  
}
