package com.sp.web.controller.relationship;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.assessment.personality.RangeType;
import com.sp.web.audit.Audit;
import com.sp.web.model.audit.ServiceType;
import com.sp.web.model.log.LogActionType;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Dax Abraham
 * 
 *         The controller class for all the relationship manager interfaces.
 */
@Controller
public class RelationshipController {

  @Autowired
  RelationshipControllerHelper relationshipControllerHelper;

  /**
   * The controller method to return the list of users that the currently logged in user has access
   * to.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get members request
   */
  
  @RequestMapping(value = "/prism/relationship-advisor", method = RequestMethod.GET)
  public String getMembersView() {
    return "getMembers";
  }
  
  /**
   * The controller method to return the list of users that the currently logged in user has access
   * to.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get members request
   */
  
  @RequestMapping(value = "/alexa/relationship/prism/relationship-advisor", method = RequestMethod.GET)
  public String getMembersViewAlexa() {
    return "getMembers";
  }
  
  /**
   * Get the members for the relationship advisor.
   * 
   * @param token
   *          - currently logged in user
   * @return
   *      the response to the get members request
   */
  @RequestMapping(value = "/relationship/getMembers", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse getMembers(Authentication token) {

    // process the add single user from user controller helper
    return process(relationshipControllerHelper::getMembers, token);
  }
  
  /**
   * Get the members for the hiring tool relationship advisor.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the list of users
   */
  @RequestMapping(value = "/hiring/relationship/getMembers", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse getMembersForHiring(Authentication token) {

    // process the add single user from user controller helper
    return process(relationshipControllerHelper::getMembersForHiring, token);
  }

  /**
   * Gets the report for the given users.
   * 
   * @param user1
   *          - user1 email
   * @param user2
   *          - user2 email
   * @param user1PersonalityType
   *          - user 1 personality type
   * @param user2PersonalityType
   *          - user 2 personality type
   * @param token
   *          - logged in user
   * @return
   *          the comparison report
   */
  @RequestMapping(value = "/relationship/getReport", method = RequestMethod.POST)
  @ResponseBody
  @Audit(actionType = LogActionType.RelationshipAdvisor, type = ServiceType.RELATIONSHIP_ADVISOR)
  public SPResponse getRelation(@RequestParam String user1, @RequestParam String user2,
      @RequestParam(defaultValue = "Primary") RangeType user1PersonalityType,
      @RequestParam(defaultValue = "Primary") RangeType user2PersonalityType,
      Authentication token) {

    // process the add single user from user controller helper
    return process(relationshipControllerHelper::getRelation, token, user1, user2,
        user1PersonalityType, user2PersonalityType);
  }

  /**
   * The controller method to get the comparison information between two profiles.
   * 
   * @param user1
   *          - user 1
   * @param user2
   *          - user 2
   * @param token
   *          - logged in user
   * @return
   *    the response to the compare request
   */
  @RequestMapping(value = "/relationship/getCompare", method = RequestMethod.POST)
  @ResponseBody 
  @Audit(type = ServiceType.RELATIONSHIP_ADVISOR)
  public SPResponse getCompare(@RequestParam String user1, @RequestParam String user2,
      Authentication token) {

    // process the add single user from user controller helper
    return process(relationshipControllerHelper::getCompare, token, user1, user2);
  }
  
}
