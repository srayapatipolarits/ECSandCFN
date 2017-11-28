package com.sp.web.dto.navigation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for navigation.
 */
public class NavigationDTO implements Serializable {
  
  private static final long serialVersionUID = -2024062395340239338L;
  
  private Map<String, Map<String, List<NavigationSectionNodeDTO>>> navigationList = 
                      new HashMap<String, Map<String, List<NavigationSectionNodeDTO>>>();
  
  @Override
  public String toString() {
    return "NavigationDTO [navigationList=" + getNavigationList() + "]";
  }

  /**
   * Add the given navigation node.
   * 
   * @param navName
   *          - navigation name
   * @param navigationNodeMap
   *          - navigation nodes
   */
  public void add(String navName, Map<String, List<NavigationSectionNodeDTO>> navigationNodeMap) {
    if (navigationNodeMap != null && !navigationNodeMap.isEmpty()) {
      getNavigationList().put(navName, navigationNodeMap);
    }
  }

  public Map<String, Map<String, List<NavigationSectionNodeDTO>>> getNavigationList() {
    return navigationList;
  }

  public void setNavigationList(Map<String, Map<String, List<NavigationSectionNodeDTO>>> navigationList) {
    this.navigationList = navigationList;
  }

}

