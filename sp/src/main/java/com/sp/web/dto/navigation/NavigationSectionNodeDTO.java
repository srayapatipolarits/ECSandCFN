package com.sp.web.dto.navigation;

import java.io.Serializable;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The DTO class for the different sections for navigation.
 */
public class NavigationSectionNodeDTO implements Serializable {
  
  private static final long serialVersionUID = 6451799488397970373L;
  private int section;
  private List<NavigationNodeDTO> nodes;
  
  public int getSection() {
    return section;
  }
  
  public void setSection(int section) {
    this.section = section;
  }
  
  public List<NavigationNodeDTO> getNodes() {
    return nodes;
  }

  public void setNodes(List<NavigationNodeDTO> nodes) {
    this.nodes = nodes;
  }
  
}
