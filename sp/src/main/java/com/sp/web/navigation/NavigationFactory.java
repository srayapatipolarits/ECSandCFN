package com.sp.web.navigation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.dto.navigation.NavigationDTO;
import com.sp.web.dto.navigation.NavigationNodeDTO;
import com.sp.web.dto.navigation.NavigationSectionNodeDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.navigation.Navigation;
import com.sp.web.model.navigation.NavigationNode;
import com.sp.web.model.navigation.NavigationSectionNode;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dax Abraham
 *
 *         The factory class for maintaining and managing navigation.
 */
@Component
public class NavigationFactory {
  
  private static final Logger log = Logger.getLogger(NavigationFactory.class);
  private Navigation navigation;
  
  public NavigationFactory() {
    load();
  }
  
  /**
   * Load the navigation from json.
   */
  @CacheEvict(value = "navigation", allEntries = true)
  public void load() {
    ObjectMapper om = new ObjectMapper();
    try {
      Navigation navigationTemp = om.readValue(this.getClass().getClassLoader()
          .getResourceAsStream("navigation.json"), Navigation.class);
      log.debug("Loaded new navigation !!!");
      this.navigation = navigationTemp;
    } catch (IOException e) {
      log.fatal("Could not load the default pulse question set from file !!!", e);
      throw new SPException("Could not load default pulse question set from file !!!");
    }
  }
  
  /**
   * Method to get the navigation for the given user.
   * 
   * @param user
   *          - user
   * @return the navigation for the user
   */
  @Cacheable(value = "navigation", key = "#user.navKey")
  public NavigationDTO getNavigation(User user) {
    NavigationDTO navToSend = new NavigationDTO();
    if (user.hasRole(RoleType.Erti)) {
      navToSend
          .add(
              "erti",
              addNavigationNodes(user, navigation.getErti()));
    }
    
    if (user.hasRole(RoleType.Hiring)) {
      navToSend.add(
          "pplAnalytics",
          addNavigationNodes(user, navigation.getPplAnalytics()));
    }
    
    if (user.hasAnyRole(RoleType.SuperAdministrator, RoleType.SysAdminMemberRole)) {
      navToSend.add(
          "superAdmin",
          addNavigationNodes(user, navigation.getSuperAdmin()));
    }
    return navToSend;
  }
  
  private Map<String, List<NavigationSectionNodeDTO>> addNavigationNodes(User user,
      List<NavigationSectionNode> nodeList) {
    Map<String, List<NavigationSectionNodeDTO>> filteredSectionMap = 
                  new HashMap<String, List<NavigationSectionNodeDTO>>();
    for (NavigationSectionNode sectionNode : nodeList) {
      final int section = sectionNode.getSection();
      ArrayList<NavigationNodeDTO> filteredNodes = getNavigationNodes(user, sectionNode.getNodes(),
          new ArrayList<NavigationNodeDTO>(), section + "", 0);
      if (!filteredNodes.isEmpty()) {
        NavigationSectionNodeDTO filteredSectionNode = new NavigationSectionNodeDTO();
        filteredSectionNode.setSection(section);
        filteredSectionNode.setNodes(filteredNodes);
        List<NavigationSectionNodeDTO> filteredSectionList = filteredSectionMap.get(sectionNode.getOrientation());
        if (filteredSectionList == null) {
          filteredSectionList = new ArrayList<NavigationSectionNodeDTO>();
          filteredSectionMap.put(sectionNode.getOrientation(), filteredSectionList);
        }
        filteredSectionList.add(filteredSectionNode);
      }
    }
    return filteredSectionMap;
  }
  
  private ArrayList<NavigationNodeDTO> getNavigationNodes(User user, List<NavigationNode> nodes,
      ArrayList<NavigationNodeDTO> filteredNodeList, String idPrefix, int counter) {
    
    for (NavigationNode node : nodes) {
      List<RoleType> roles = node.getRoles();
      if (roles != null) {
        if (roles.stream().anyMatch(r -> user.hasAnyRole(r))) {
          counter++;
          copyAndAddChildren(user, filteredNodeList, node, idPrefix, counter);
        }
      } else {
        roles = node.getRolesFilter();
        if (roles != null) {
          if (!roles.stream().anyMatch(r -> user.hasAnyRole(r))) {
            counter++;
            copyAndAddChildren(user, filteredNodeList, node, idPrefix, counter);
          }
        } else {
          throw new InvalidRequestException("Role or filter role not set on node :"
              + node.getName());
        }
      }
    }
    return filteredNodeList;
  }
  
  private void copyAndAddChildren(User user, List<NavigationNodeDTO> filteredNodeList,
      NavigationNode node, String idPrefix, int counter) {
    NavigationNodeDTO filteredNode = new NavigationNodeDTO();
    filteredNode.setUrl(node.getUrl());
    //filteredNode.setColor(node.getColor());
    filteredNode.setHideOnMobile(node.isHideOnMobile());
    filteredNode.setSwitchNavTo(node.getSwitchNavTo());
    final String nodeId = idPrefix + counter;
    filteredNode.setId(nodeId);
    try {
      filteredNode.setName(MessagesHelper.getMessage(node.getName(), user.getLocale()));
    } catch (Exception e) {
      log.warn("Error getting name.", e);
      filteredNode.setName(node.getName());
    }
    
    final List<NavigationNode> childNodes = node.getChildren();
    if (childNodes != null) {
      List<NavigationNodeDTO> childNodesFiltered = getNavigationNodes(user, childNodes,
          new ArrayList<NavigationNodeDTO>(), nodeId + "-", 0);
      if (!childNodesFiltered.isEmpty()) {
        filteredNode.setChildren(childNodesFiltered);
      }
    }
    filteredNodeList.add(filteredNode);
  }
}
