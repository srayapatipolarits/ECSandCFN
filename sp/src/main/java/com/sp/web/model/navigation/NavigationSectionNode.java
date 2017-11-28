package com.sp.web.model.navigation;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 * 
 *         This is the entity to store the section details of the navigation nodes.
 */
public class NavigationSectionNode {
  
  private int section;
  private String orientation;
  private List<NavigationNode> nodes;
  
  public int getSection() {
    return section;
  }
  
  public void setSection(int section) {
    this.section = section;
  }
  
  public String getOrientation() {
    return orientation;
  }
  
  public void setOrientation(String orientation) {
    this.orientation = orientation;
  }
  
  public List<NavigationNode> getNodes() {
    return nodes;
  }
  
  public void setNodes(List<NavigationNode> nodes) {
    this.nodes = nodes;
  }
  
}
