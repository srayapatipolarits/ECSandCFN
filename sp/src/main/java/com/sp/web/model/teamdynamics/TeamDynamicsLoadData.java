package com.sp.web.model.teamdynamics;

import com.sp.web.assessment.questions.CategoryPairs;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.model.User;

import net.authorize.api.contract.v1.CcAuthenticationType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TeamDynamicsLoadData method will load the team dyanmics data for the users.
 * 
 * @author pradeepruhil
 *
 */
public class TeamDynamicsLoadData {
  
  private List<User> users;
  
  private Map<CategoryType, Map<CategoryPairs, CategoryPairData>> categoryPairData;
  
  public List<User> getUsers() {
    return users;
  }
  
  public void setUsers(List<User> users) {
    this.users = users;
  }
  
  /**
   * get the category parit data for the category type.
   * 
   * @return the category pair data.
   */
  public Map<CategoryType, Map<CategoryPairs, CategoryPairData>> getCategoryPairData() {
    if (categoryPairData == null) {
      categoryPairData = new HashMap<CategoryType, Map<CategoryPairs, CategoryPairData>>();
    }
    return categoryPairData;
  }
  
  public void setCategoryPairData(
      Map<CategoryType, Map<CategoryPairs, CategoryPairData>> categoryPairData) {
    this.categoryPairData = categoryPairData;
  }
  
  /**
   * addCategoryPairData method will add the categoryPair data for the user.
   * 
   * @param user
   *          for which category pair data is to be added.
   * @param categoryPairForTrait
   *          the category pair
   * @param categoryType
   *          type of the category.
   */
  public void addCategoryPairData(User user, CategoryPairs categoryPairForTrait,
      CategoryType categoryType, boolean equalTraitType, TraitType traitType) {
    
    Map<CategoryPairs, CategoryPairData> map = getCategoryPairData().get(categoryType);
    if (map == null) {
      map = new LinkedHashMap<CategoryPairs, CategoryPairData>();
      categoryPairData.put(categoryType, map);
    }
    
    CategoryPairData pairData = map.get(categoryPairForTrait);
    if (pairData == null) {
      pairData = new CategoryPairData();
      map.put(categoryPairForTrait, pairData);
    }
    pairData.addTraitDataMap(categoryPairForTrait, user, traitType, equalTraitType);
  }
  
}
