package com.sp.web.model.navigation;

import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The model to store the two different navigations the drop down and the main navigation.
 */
public class Navigation {
  
  private List<NavigationSectionNode> erti;
  private List<NavigationSectionNode> superAdmin;
  private List<NavigationSectionNode> pplAnalytics;
  
  public List<NavigationSectionNode> getErti() {
    return erti;
  }
  
  public void setErti(List<NavigationSectionNode> erti) {
    this.erti = erti;
  }
  
  public List<NavigationSectionNode> getSuperAdmin() {
    return superAdmin;
  }
  
  public void setSuperAdmin(List<NavigationSectionNode> superAdmin) {
    this.superAdmin = superAdmin;
  }
  
  public List<NavigationSectionNode> getPplAnalytics() {
    return pplAnalytics;
  }
  
  public void setPplAnalytics(List<NavigationSectionNode> pplAnalytics) {
    this.pplAnalytics = pplAnalytics;
  }
  
}
