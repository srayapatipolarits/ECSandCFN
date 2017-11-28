package com.sp.web.controller.relationship;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dto.RelationshipUserDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.GroupAssociation;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.log.LogActionType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;
import com.sp.web.relationship.RelationshipReportManager;
import com.sp.web.repository.team.GroupRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.log.LogGateway;
import com.sp.web.service.log.LogRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The helper class for the relationship manager controller.
 */
@Component
public class RelationshipControllerHelper {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private GroupRepository groupRepository;
  
  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private RelationshipReportManager relationshipReportManager;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  @Qualifier("notificationLog")
  LogGateway logGateway;

  /**
   * The helper method to get the list of members for the groups the group lead
   * belongs to.
   * 
   * @param user
   *          - group lead user
   * @return
   *      the response to the get member request
   */
  public SPResponse getMembers(User user) {
    final SPResponse resp = new SPResponse();

    // get the company name of the user
    // required for the group info retrieval
    String companyId = user.getCompanyId();
    
    List<User> userList = userRepository.findMemberByStatus(companyId, UserStatus.VALID);
    
    // filter the user list by the group association
    CompanyDao company = companyFactory.getCompany(companyId);
    if (company.isRestrictRelationShipAdvisor()
        && !user.hasAnyRole(RoleType.AccountAdministrator, RoleType.Administrator)) {
      final ArrayList<GroupAssociation> groupAssociationList = user.getGroupAssociationList();
      userList = userList.stream().filter(u -> u.hasGroupAssociation(groupAssociationList))
          .collect(Collectors.toList());
    }
    
    // add the member list to the response
    resp.add(Constants.PARAM_MEMBER_LIST,
        userList.stream().map(RelationshipUserDTO::new).collect(Collectors.toList()));

    // sending the updated response back
    return resp;
  }
  
  /**
   * The helper method for the hiring user to get the list of members. 
   * 
   * @param user
   *          - logged in user
   * @return
   *    the list of users
   */
  public SPResponse getMembersForHiring(User user) {
    final SPResponse resp = new SPResponse();

    // get the company name of the user
    // required for the group info retrieval
    String companyId = user.getCompanyId();
    
    List<User> userList = userRepository.findMemberByStatus(companyId, UserStatus.VALID);
    
    // add the member list to the response
    resp.add(Constants.PARAM_MEMBER_LIST,
        userList.stream().map(RelationshipUserDTO::new).collect(Collectors.toList()));

    // sending the updated response back
    return resp;
  }

  /**
   * The helper method to return back the relationship between the given members.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - the users to find the relation for
   * @return the response to the get relation request
   */
  public SPResponse getRelation(User user, Object[] params) {
    final SPResponse resp = new SPResponse();

    // get the parameters
    String user1Email = (String) params[0];
    String user2Email = (String) params[1];
    RangeType user1PersonalityType = (RangeType) params[2];
    RangeType user2PersonalityType = (RangeType) params[3];

    User user1 = userRepository.findByEmailValidated(user1Email);
    User user2 = userRepository.findByEmailValidated(user2Email);

    // validate if the users belong to the same company
    if (!user.isSameCompany(user1) || !user.isSameCompany(user2)) {
      throw new InvalidRequestException("User(s) not part of the company :" + user.getCompanyId());
    }

    resp.add(relationshipReportManager.getCompareReport(user1, user2, user1PersonalityType,
        user2PersonalityType, user.getLocale()));

    if (user1PersonalityType == RangeType.Primary && user2PersonalityType == RangeType.Primary) {
      LogRequest logRequest = new LogRequest(LogActionType.RelationshipAdvisor, user);
      logGateway.logActivity(logRequest);
    }
    
    return resp;
  }
  
  /**
   * The helper method to get the comparison report. 
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - parameters
   * @return
   *      the response to the get compare request
   */
  public SPResponse getCompare(User user, Object[] params) {
    SPResponse resp = new SPResponse();

    // get the parameters
    String user1Email = (String) params[0];
    String user2Email = (String) params[1];

    User user1 = userRepository.findByEmailValidated(user1Email);
    User user2 = userRepository.findByEmailValidated(user2Email);

    // validate if the users belong to the same company
    if (!user.isSameCompany(user1) || !user.isSameCompany(user2)) {
      throw new InvalidRequestException("User(s) not part of the company :" + user.getCompanyId());
    }
    
    resp.add(relationshipReportManager.getChartCompareReport(user1, user2));
    
    return resp;
  }
}
