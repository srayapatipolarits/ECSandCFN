package com.sp.web.controller.notifications;

import com.sp.web.Constants;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.dao.CompanyDao;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.product.CompanyFactory;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Dax Abraham
 *
 *         The notifications processor for hiring candidates.
 */
@Component("hiringCandidateNotificationProcessor")
public class HiringCandidateNotificationsProcessor extends NotificationsProcessor {
  
  private static final BigDecimal EQUAL_TO_50 = BigDecimal.valueOf(50);
  
  @Autowired
  UserFactory userFactory;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @Override
  protected List<User> getUserList(EmailParams emailParams, User user) {
    final ArrayList<User> memberList = new ArrayList<User>();
    
    User userFor = (User) emailParams.getValue(Constants.PARAM_FOR_USER);
    if (userFor == null) {
      emailParams.getTos().stream().map(hiringUserFactory::getUser).filter(Objects::nonNull)
          .forEach(memberList::add);
      
      Assert.notEmpty(memberList, "User not found.");
      if (memberList.size() == 1) {
        emailParams.addParam(Constants.PARAM_FOR_USER, memberList.get(0));
      }
      emailParams.addParam(Constants.PARAM_MEMBER, user);
    } else {
      memberList.add(userFor);
    }
    return memberList;
  }
  
  /**
   * Process the hiring candidate completed assessment.
   * 
   * @param type
   *          - notification type
   * @param user
   *          - candidate
   */
  public void process(NotificationType type, User user) {
    if (user instanceof HiringUser) {
      CompanyDao company = companyFactory.getCompany(user.getCompanyId());
      if (company.isSharePortrait()) {
        process(type, user, user);
      }
    } else {
      throw new InvalidRequestException("User not a hiring candidate !!!");
    }
  }
  
  /**
   * Process hiring coordinator notifications.
   * 
   * @param user
   *          - candidate
   */
  public void notifyHiringCoordinator(User user) {
    if (user instanceof HiringUser) {
      Set<String> hiringCoordinatorIds = ((HiringUser) user).getHiringCoordinatorIds();
      if (CollectionUtils.isNotEmpty(hiringCoordinatorIds)) {
        for (String hiringCoordinatorId : hiringCoordinatorIds) {
          User userFor = userFactory.getUser(hiringCoordinatorId);
          if (userFor != null) {
            Map<String, Object> paramsMap = new HashMap<String, Object>();
            paramsMap.putAll(MessagesHelper.getGenderText(user));
            paramsMap.put("isCandidate", user.getType() == UserType.HiringCandidate);
            process(NotificationType.AssessmentCompletedCoordinator, user, userFor, paramsMap);
          }
        }
      }
    } else {
      throw new InvalidRequestException("User not a hiring candidate !!!");
    }
  }
  
  @Override
  protected void updateEmailParams(List<User> userList, EmailParams emailParams, User user,
      List<User> errorList) {
    super.updateEmailParams(userList, emailParams, user, errorList);
    if (!userList.isEmpty()) {
      User hiringUser = userList.get(0);
      emailParams.addParam("isCandidate", hiringUser.getType() == UserType.HiringCandidate);
      final AnalysisBean analysis = hiringUser.getAnalysis();
      Assert.notNull(analysis, "User analysis not found.");
      PersonalityType primaryPersonality = analysis.getPersonality().get(RangeType.Primary)
          .getPersonalityType();
      PersonalityType underPressurePersonality = analysis.getPersonality()
          .get(RangeType.UnderPressure).getPersonalityType();
      emailParams.addParam("introText", MessagesHelper.getMessage(
          "portrait.share.personality.primary." + primaryPersonality, hiringUser.getLocale()));
      emailParams.addParam("underPressurePersonality",
          MessagesHelper.getMessage(underPressurePersonality + "", hiringUser.getLocale()));
      emailParams.addParam("underPressureText", MessagesHelper.getMessage(
          "portrait.share.personality.pressure."
              + ((primaryPersonality == underPressurePersonality) ? "same" : "different"),
          hiringUser.getLocale()));
      emailParams.addParam("processingText", MessagesHelper.getMessage("portrait.share.processing."
          + analysis.getProcessing().get(TraitType.External).compareTo(EQUAL_TO_50),
          hiringUser.getLocale()));
      emailParams.addParam(
          "motivationText",
          MessagesHelper.getMessage(
              "portrait.share.motivation."
                  + analysis.getMotivationWhy().get(TraitType.AttainmentOfGoals)
                      .compareTo(EQUAL_TO_50), hiringUser.getLocale()));
      final Map<TraitType, BigDecimal> decisionMaking = analysis.getDecisionMaking();
      emailParams.addParam(
          "decisionMakingCarefulText",
          MessagesHelper.getMessage(
              "portrait.share.decision.making.careful."
                  + decisionMaking.get(TraitType.Careful).compareTo(EQUAL_TO_50),
              hiringUser.getLocale()));
      emailParams.addParam(
          "decisionMakingOutwardText",
          MessagesHelper.getMessage(
              "portrait.share.decision.making.outward."
                  + decisionMaking.get(TraitType.Outward).compareTo(EQUAL_TO_50),
              hiringUser.getLocale()));
    }
  }
}
