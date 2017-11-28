/**
 * 
 */
package com.sp.web.controller.notifications;

import com.sp.web.Constants;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.model.ExternalUser;
import com.sp.web.model.GrowthRequest;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.RequestType;
import com.sp.web.model.User;
import com.sp.web.repository.growth.GrowthRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.log.LogRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * <code>GrowthNotificationProcessor</code> method will send the notification and log activity for the growth tool.
 * 
 * @author pradeepruhil
 *
 */
@Component("growthNotificationProcessor")
public class GrowthNotificationProcessor extends NotificationsProcessor {

  @Autowired
  private GrowthRepository growthRepository;

  @Autowired
  private UserRepository userRepository;

  /**
   * <code>getUserList</code> method will add the user in the user list.
   */
  @Override
  protected List<User> getUserList(EmailParams emailParams, User user) {

    List<User> memberList = new ArrayList<User>();
    User userFor = (User) emailParams.getValue(Constants.PARAM_FOR_USER);
    if (userFor == null) {
      String growthRequestId = (String) emailParams.getValue(Constants.PARAM_GROWTH_REQUEST_ID);

      if (growthRequestId == null) {
        throw new SPException("Growth Request ID not present, cannot send the reminder ");
      }
      GrowthRequest growthRequest = growthRepository.findGrowthRequest(growthRequestId);
      String memberEmail = growthRequest.getMemberEmail();
      User memberFor = (growthRequest.getRequestType() == RequestType.EXTERNAL) ? userRepository
          .findExternalUserByEmail(memberEmail) : userRepository.findByEmail(memberEmail);

      emailParams.addParam(Constants.PARAM_GROWTH_REQUEST, growthRequest);
      emailParams.addParam(Constants.PARAM_FOR_USER, memberFor);
      emailParams.addParam(Constants.PARAM_MEMBER, user);
      memberList.add(memberFor);
    } else {
      memberList.add(userFor);
    }
    return memberList;
  }

  /**
   * <code>process</code> method will process the notification for the growth.
   * 
   * @param type
   *          notification type for growth user.
   * @param user
   *          logged in user
   * @param growthRequest
   *          - growth Request for the user.
   */
  public void process(NotificationType type, User user, GrowthRequest growthRequest) {
    process(type, user, growthRequest, false);
  }

  /**
   * <code>process</code> method will process the notification for the growth.
   * 
   * @param type
   *          notification type for growth user.
   * @param user
   *          logged in user
   * @param growthRequest
   *          - growth Request for the user.
   */
  public void process(NotificationType type, User user, GrowthRequest growthRequest, boolean isGrowthTeamUser) {

    String memberEmail = null;
    
    switch (type) {
    case GrowthFeedbackSubmitted:
      memberEmail = growthRequest.getRequestedByEmail();
      break;
    case GrowthAcceptRequest:
      memberEmail = growthRequest.getRequestedByEmail();
      break;
    case GrowthDeclineRequest:
      memberEmail = growthRequest.getRequestedByEmail();
      break;
    default:
      memberEmail = growthRequest.getMemberEmail();
    }    

    /* get the member user who will provide the feedback */
    User memberFor;
    /* check which type if user notificaiton is for */
    RequestType requestType = growthRequest.getRequestType();
    Map<String, Object> paramsMap = new HashMap<String, Object>();

    paramsMap.put(Constants.PARAM_GROWTH_REQUEST, growthRequest);
    if (growthRequest.getRequestStatus() == RequestStatus.DECLINED) {
      paramsMap.put(Constants.PARAM_COMMENT, growthRequest.getDeclineComment());
    }
    switch (requestType) {
    case EXTERNAL:
      if (isGrowthTeamUser) {
        memberFor = userRepository.findByEmail(growthRequest.getRequestedByEmail());
      } else {
        memberFor = userRepository.findExternalUserByEmail(memberEmail);
      }

      process(type, user, memberFor, paramsMap, false);
      break;
    default:
      memberFor = userRepository.findByEmail(memberEmail);
      process(type, user, memberFor, paramsMap);
      break;
    }

  }

  @Override
  protected LogRequest getLogRequest(EmailParams emailParams, User user, User userFor, NotificationType type) {
    if (userFor instanceof ExternalUser) {
      return null;
    }
    LogRequest logRequest = super.getLogRequest(emailParams, user, userFor, type);
    Map<String, Object> params = logRequest.getParams();
    GrowthRequest growthRequest = (GrowthRequest) emailParams
        .getValue(Constants.PARAM_GROWTH_REQUEST);
    params.put(Constants.PARAM_TOKEN,
        "/sp/growth/growthListing?growthMemberId=" + growthRequest.getId());
    return logRequest;
  }

  protected LogRequest getActivityLogRequest(EmailParams emailParams, User user, User userFor, NotificationType type) {
    if (user instanceof ExternalUser) {
      return null;
    }
    return super.getActivityLogRequest(emailParams, userFor, user, type);
  }
  
  /**
   * Validate the list of users if they belong to the same company.
   * 
   * @param user
   *          - logged in user
   * @param memberEmailList
   *          - member email list
   * @param type adding type to handle validation for specific type of user.
   * @return The list of member email's that are not part of the company of user
   */
  protected ArrayList<User> validateUsers(User user, List<User> memberEmailList, NotificationType type) {
    
    ArrayList<User> errorList = new ArrayList<User>();
    if (type == NotificationType.GrowthFeedbackReminder
        || type == NotificationType.GrowthFeedbackAcceptReminder) {
      return errorList;
    }
    memberEmailList.forEach(member -> {
        try {
          if (!member.equals(user.getEmail())) {
            member.isSameCompany(user);
          }
        } catch (InvalidRequestException e) {
          errorList.add(member);
        }
      });
    if (!errorList.isEmpty()) {
      throw new InvalidRequestException("User not validated :"
          + errorList.stream().map(User::getEmail).collect(Collectors.joining(",")));
    }
    return errorList;
  }

}
