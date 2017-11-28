package com.sp.web.model.navigation;

import com.sp.web.model.RoleType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The navigation class to store the navigation nodes.
 */
public class NavigationNode {

  private String name;
  private String url;
  private String color;
  private boolean hideOnMobile;
  private List<RoleType> roles;
  private List<RoleType> rolesFilter;
  private List<NavigationNode> children;
  private boolean switchLink;
  private String switchNavTo;

  public List<RoleType> getRolesFilter() {
    return rolesFilter;
  }

  public void setRolesFilter(List<RoleType> rolesFilter) {
    this.rolesFilter = rolesFilter;
  }

  public List<RoleType> getRoles() {
    return roles;
  }

  public void setRoles(List<RoleType> roles) {
    this.roles = roles;
  }

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

  public List<NavigationNode> getChildren() {
    return children;
  }

  public void setChildren(List<NavigationNode> children) {
    this.children = children;
  }

  /**
   * Add a child node to the list of children.
   * 
   * @param childNode
   *          - child node to add
   */
  public void addChild(NavigationNode childNode) {
    if (children == null) {
      children = new ArrayList<NavigationNode>();
    }
    children.add(childNode);
  }

  @Override
  public String toString() {
    String retString = "[name=" + name + ", url=" + url + "]";
    if (children != null) {
      for (NavigationNode childNode : children) {
        retString += "\n" + childNode;
      }
    }
    return retString;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public boolean isHideOnMobile() {
    return hideOnMobile;
  }

  public void setHideOnMobile(boolean hideOnMobile) {
    this.hideOnMobile = hideOnMobile;
  }

  public boolean isSwitchLink() {
    return switchLink;
  }

  public void setSwitchLink(boolean switchLink) {
    this.switchLink = switchLink;
  }

  public String getSwitchNavTo() {
    return switchNavTo;
  }

  public void setSwitchNavTo(String switchNavTo) {
    this.switchNavTo = switchNavTo;
  }

}
