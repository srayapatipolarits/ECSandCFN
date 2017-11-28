package com.sp.web.dto.navigation;

import java.io.Serializable;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The DTO class for the navigation node.
 */
public class NavigationNodeDTO implements Serializable {
  
  private static final long serialVersionUID = 518567713160760095L;
  
  private String id;
  private String name;
  private String url;
  private boolean hideOnMobile;
  private String switchNavTo;
  private List<NavigationNodeDTO> children;
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getUrl() {
    return url;
  }
  
  public void setUrl(String url) {
    this.url = url;
  }
  
  public List<NavigationNodeDTO> getChildren() {
    return children;
  }
  
  public void setChildren(List<NavigationNodeDTO> children) {
    this.children = children;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public boolean isHideOnMobile() {
    return hideOnMobile;
  }
  
  public void setHideOnMobile(boolean hideOnMobile) {
    this.hideOnMobile = hideOnMobile;
  }
  
  public String getSwitchNavTo() {
    return switchNavTo;
  }
  
  public void setSwitchNavTo(String switchNavTo) {
    this.switchNavTo = switchNavTo;
  }
  
  @Override
  public String toString() {
    return "NavigationNodeDTO [id=" + id + ", name=" + name + ", url=" + url
        + ", hideOnMobile=" + hideOnMobile + ", children=" + children
        + ", switchNavTo=" + switchNavTo + "]";
  }
  
}
