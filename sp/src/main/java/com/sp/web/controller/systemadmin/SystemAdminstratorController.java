package com.sp.web.controller.systemadmin;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.PersonalityTypeMapper;
import com.sp.web.controller.ControllerHelper;
import com.sp.web.form.SystemAdminForm;
import com.sp.web.model.UserType;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * <code>SysteAdminstration</code> controller provides operaiton to perform on the user profile.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class SystemAdminstratorController {
  
  @Autowired
  private SystemAdminstrationHelper systemAdminstrationHelper;
  
  /**
   * View For SuperAdminControl.
   * 
   */
  @RequestMapping(value = "/sysAdmin/trainingLibrary", method = RequestMethod.GET)
  public String validatefeedsendReminder(Authentication token,
      @RequestParam(required = false) String theme) {
    return "updateTL";
  }
  
  @RequestMapping(value = "/sysAdmin/ehcache", method = RequestMethod.GET)
  public String getEhCache(Authentication token) {
    return "ehcacheAdmin";
  }
  
  /**
   * View For SuperAdminControl.
   * 
   */
  @RequestMapping(value = "/sysAdmin/home", method = RequestMethod.GET)
  public String homeView(Authentication token, @RequestParam(required = false) String theme) {
    return "getCompanyList";
  }
  
  @RequestMapping(value = "/sysAdminMember/landing", method = RequestMethod.GET)
  public String sysAdminMember(Authentication token, @RequestParam(required = false) String theme) {
    return "sysAdminMemberHome";
  }
  
  @RequestMapping(value = "/sysAdmin/logs", method = RequestMethod.GET)
  public String updateLog4j(Authentication token) {
    return "log";
  }
  
  /**
   * View For loggedInUser.
   * 
   */
  @RequestMapping(value = "/sysAdmin/users/loggedInUser", method = RequestMethod.GET)
  public String validateloggedInUser(Authentication token,
      @RequestParam(required = false) String theme) {
    return "loggedInUser";
  }
  
  /**
   * View For userList.
   * 
   */
  @RequestMapping(value = "/sysAdmin/users/getUserList", method = RequestMethod.GET)
  public String validategetUserList(Authentication token,
      @RequestParam(required = false) String theme) {
    return "getUserList";
  }
  
  /**
   * View For userList.
   * 
   */
  @RequestMapping(value = "/sysAdmin/getCompanyList", method = RequestMethod.GET)
  public String validategetCompanyList(Authentication token) {
    return "getCompanyList";
  }
  
  @RequestMapping(value = "/sysAdmin/getUser", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getUser(SystemAdminForm systemAdminForm, Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::getUser, token, systemAdminForm);
  }
  
  /**
   * Listing View For Partner Accounts.
   */
  @RequestMapping(value = "/sysAdmin/partners", method = RequestMethod.GET)
  public String partnerAccounts(Authentication token) {
    return "partners";
  }
  
  @RequestMapping(value = "/sysAdmin/updateUserSingleProperty", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateUserSingleProperty(@RequestParam String property,
      @RequestParam String value, @RequestParam UserType userType, @RequestParam String id,
      Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::updateUserSingleProperty, token,
        property, value, userType, id);
  }
  
  @RequestMapping(value = "/sysAdmin/updateProfilePersonality", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateUserPerosonality(@RequestParam String primaryPersonality,
      @RequestParam String id, @RequestParam UserType userType, Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::updateUserPerosonality, token,
        primaryPersonality, id, userType);
  }
  
  @RequestMapping(value = "/sysAdmin/addFeedbackUserGoals", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse addFeedbackUserGoals(@RequestParam String email, Authentication token,
      @RequestParam String feedbackFor) {
    return ControllerHelper.process(systemAdminstrationHelper::addFeedbackUserGoals, token, email,
        feedbackFor);
  }
  
  @RequestMapping(value = "/sysAdmin/addAllFeedbackUserGoals", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse addAllFeedbackUserGoals(Authentication token, @RequestParam String feedbackFor) {
    return ControllerHelper.process(systemAdminstrationHelper::addFeedbackUserGoalsForAllUsers,
        token, feedbackFor, feedbackFor);
  }
  
  @RequestMapping(value = "/sysAdmin/learning/trainingLibraryReload", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse trainingLibraryReload(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::syncArticleFromNewsCred, token);
  }
  
  @RequestMapping(value = "/sysAdmin/learning/trainingLibrary/RemoveDuplicateArticles", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse removeDuplicateArticles(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::removeDuplicateArticles, token);
  }
  
  @RequestMapping(value = "/sysAdmin/learning/messagesReload", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse messagesReload(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::messagesReload, token);
  }
  
  @RequestMapping(value = "/sysAdmin/db/getCompany", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getCompany(Authentication token, @RequestParam String companyId,
      @RequestParam List<String> products) {
    return ControllerHelper.process(systemAdminstrationHelper::getCompany, token, companyId,
        products);
  }
  
  @RequestMapping(value = "/sysAdmin/db/getUser", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getDBUser(Authentication token, @RequestParam String email,
      @RequestParam List<String> products) {
    return ControllerHelper.process(systemAdminstrationHelper::getDBUser, token, email, products);
  }
  
  @RequestMapping(value = "/sysAdmin/hk/getLoggedInUsers", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getLoggedInUsers(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::getLoggedInUsers, token);
  }
  
  @RequestMapping(value = "/sysAdmin/hk/getUserList", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getUserList(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::getUserList, token);
  }
  
  @RequestMapping(value = "/sysAdmin/hk/getCompanyList", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getCompanyList(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::getCompanyList, token);
  }
  
  @RequestMapping(value = "/sysAdmin/hk/getSurePeopleCompanies", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getSurePeopleCompanies(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::getSurePeopleCompanies, token);
  }
  
  @RequestMapping(value = "/sysAdmin/hk/fixProfile", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse fixUserProfile(@RequestParam String email, Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::fixUserProfile, token, email);
  }
  
  @RequestMapping(value = "/sysAdmin/marketing/sendEmail", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getCompany(Authentication token,
      @RequestParam(required = false) List<String> users,
      @RequestParam(defaultValue = "false") boolean isAllUsers) {
    return ControllerHelper.process(systemAdminstrationHelper::sendMarketingEmail, token, users,
        isAllUsers);
  }
  
  /**
   * Ajax Call to retreive Readability Average Score
   * 
   * @return Readability Score
   */
  
  @RequestMapping(value = "/sysAdmin/learning/readability", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getReadabilityAverageScore(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::getReadabilityAverageScore, token);
  }
  
  @RequestMapping(value = "/sysAdmin/initializePracticeArea", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse initializePracticeArea(
      @RequestParam(required = false) PersonalityType personalityType,
      @RequestParam(required = false) List<String> goalNames, Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::initializePracticeArea, token,
        personalityType, goalNames);
  }
  
  @RequestMapping(value = "/sysAdmin/updateOldUgp", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateOldUgp(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::updateUGPforExistingUsers, token);
  }
  
  @RequestMapping(value = "/sysAdmin/updateMds", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateMds(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::addDummyDS, token);
  }
  
  @RequestMapping(value = "/sysAdmin/updateArticleGP", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateArticleGP(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::updateArticleProgressForExitingUser,
        token);
  }
  
  @RequestMapping(value = "/sysAdmin/migrateBuisnessAccount", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse migrateAccount(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::migrateAccount, token);
  }
  
  @RequestMapping(value = "/sysAdmin/migrateIndividualAccount", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse migrateIndividualAccount(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::migrateIndvidualAccounts, token);
  }
  
  // @RequestMapping(value = "/sysAdmin/updateUserGroupAssociation", method = RequestMethod.GET)
  // @ResponseBody
  // public SPResponse updateUserGroupAssociation(Authentication token) {
  // return ControllerHelper.process(systemAdminstrationHelper::updateUserGroupAssociation, token);
  // }
  
  @RequestMapping(value = "/sysAdmin/updatePrismLens", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updatePrismLens(@RequestParam String fbUserId,
      @RequestParam PersonalityTypeMapper primaryPersonality, Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::updatePrismLens, token, fbUserId,
        primaryPersonality);
  }
  
  @RequestMapping(value = "/sysAdmin/runAccountSchedular", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse runAccountSchedular(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::runAccountSchedular, token);
  }
  
  @RequestMapping(value = "/sysAdmin/updateArticlesUgp", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateArticlesUgp(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::updateArticlesUgp, token);
  }
  
  @RequestMapping(value = "/sysAdmin/updateBlueprintTotalActionCount", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateBlueprintTotalActionCount(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::updateBlueprintTotalActionCount,
        token);
  }
  
  // @RequestMapping(value = "/sysAdmin/updateOrgPlanCompletedAction", method = RequestMethod.GET)
  // @ResponseBody
  // public SPResponse updateOrgPlan(Authentication token) {
  // return ControllerHelper.process(systemAdminstrationHelper::updateOrgPlan, token);
  // }
  
  @RequestMapping(value = "/sysAdmin/runCompetencySchedular", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse runCompetencySchedular(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::runCompetencySchedular, token);
  }
  
  @RequestMapping(value = "/sysAdmin/updateFeedbackUserCreatedOn", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateFeedbackUserCreatedOn(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::updateFeedbackUserCreatedOn, token);
  }
  
  @RequestMapping(value = "/sysAdmin/addToNewsFeed", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse addToNewsFeed(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::addToNewsFeed, token);
  }
  
  @RequestMapping(value = "/sysAdmin/loadUsersToCompany", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse loadUsersToCompany(@RequestParam String copyFromEmail,
      @RequestParam int numberOfUsers, @RequestParam String emailPrefix, Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::loadUsersToCompany, token,
        copyFromEmail, numberOfUsers, emailPrefix);
  }
  
  @RequestMapping(value = "/sysAdmin/migrateOrgPlan", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse migrateOrgPlan(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::migrateOrgPlan, token);
  }
  
  @RequestMapping(value = "/sysAdmin/updateOrgPlanStep", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateOrgPlanStep(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::updateOrgPlanStep, token);
  }
  
  @RequestMapping(value = "/sysAdmin/updateActionPlan", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateActionPlan(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::updateActionPlan, token);
  }
  
  @RequestMapping(value = "/sysAdmin/updateActionPlanImage", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateActionPlanImage(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::updateActionPlanImage, token);
  }
  
  @RequestMapping(value = "/sysAdmin/runLearningProgramSchedular", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse runLearningProgramSchedular(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::runLearningProgramSchedular, token);
  }
  
  @RequestMapping(value = "/sysAdmin/clearCache", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse clearCache(@RequestParam String cacheName, Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::clearCache, token, cacheName);
  }
  
  @RequestMapping(value = "/sysAdmin/updateCompetencyRatingConfig", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateCompetencyRatingConfig(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::updateCompetencyRatingConfig, token);
  }
  
  @RequestMapping(value = "/sysAdmin/updateCid", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateCommentCounter(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::updateCommentCounter, token);
  }
  
  @RequestMapping(value = "/sysAdmin/loadEngagmentData", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse loadEngagmentData(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::loadEngagmentData, token);
  }
  
  @RequestMapping(value = "/sysAdmin/assignDefaultTutorial", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse assignDefaultTutorial(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::assignDefaultTutorial, token);
  }
  
//  @RequestMapping(value = "/sysAdmin/getDataForPrecisionConsulting", method = RequestMethod.GET)
//  public void getDataForPrecisionConsulting(HttpServletResponse response) {
//    systemAdminstrationHelper.getDataForPrecisionConsulting(response);
//  }
  
  @RequestMapping(value = "/sysAdmin/getAnalysisDataForPrecisionConsulting", method = RequestMethod.GET)
  public void getAnalysisDataForPrecisionConsulting(HttpServletResponse response) {
    systemAdminstrationHelper.getAnalysisDataForPrecisionConsulting(response);
  }
  
//  @RequestMapping(value = "/sysAdmin/getAssessmentStats", method = RequestMethod.GET)
//  public void getAssessmentStats(HttpServletResponse response) {
//    systemAdminstrationHelper.getAssessmentStats(response);
//  }
  
  // @RequestMapping(value = "/sysAdmin/fixAssessment", me`thod = RequestMethod.GET)
  // @ResponseBody
  // public SPResponse fixAssessment(Authentication token) {
  // return ControllerHelper.process(systemAdminstrationHelper::fixAssessment, token);
  // }
  
  @RequestMapping(value = "/sysAdmin/updateUserBadges", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateUserBadges(Authentication authentication) {
    return ControllerHelper.process(systemAdminstrationHelper::updateUserBadges, authentication);
    
  }
  
  @RequestMapping(value = "/sysAdmin/updateUserBadgesTime", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateUserBadgesTime(Authentication authentication) {
    return ControllerHelper
        .process(systemAdminstrationHelper::updateUserBadgesTime, authentication);
  }
  
  @RequestMapping(value = "/sysAdmin/updateHiringUsers", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateHiringUsers(Authentication authentication) {
    return ControllerHelper.process(systemAdminstrationHelper::updateHiringUsers, authentication);
  }
  
  @RequestMapping(value = "/sysAdmin/updateHiringUsersArchive", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateHiringUsersArchive(Authentication authentication) {
    return ControllerHelper.process(systemAdminstrationHelper::updateHiringUsersArchive,
        authentication);
  }
  
  @RequestMapping(value = "/sysAdmin/addMembersToPplAnalytics", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse addMembersToPplAnalytics(@RequestParam(required = false) String email,
      Authentication authentication) {
    return ControllerHelper.process(systemAdminstrationHelper::addMembersToPplAnalytics,
        authentication, email);
  }
  
  @RequestMapping(value = "/sysAdmin/fixCompletedOn", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse fixCompletedOn(Authentication authentication) {
    return ControllerHelper.process(systemAdminstrationHelper::fixCompletedOn, authentication);
  }
  
  @RequestMapping(value = "/sysAdmin/deleteAccount", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse deleteAccount(@RequestParam String accountId, Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::deleteAccount, token, accountId);
  }
  
  @RequestMapping(value = "/sysAdmin/updateUserAssessmentPriorities", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateUserAssessmentPriorities(@RequestParam(required = false) String email,
      Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::updateUserAssessmentPriorities,
        token, email);
  }
  
  @RequestMapping(value = "/sysAdmin/updateHiringUserAssessmentPriorities", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateHiringUserAssessmentPriorities(
      @RequestParam(required = false) String email,
      @RequestParam(required = false) String companyId, Authentication token) {
    return ControllerHelper.process(
        systemAdminstrationHelper::updateHiringUserAssessmentPriorities, token, email, companyId);
  }
  
  @RequestMapping(value = "/sysAdmin/migrateCompetencyEvaluations", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse migrateCompetencyEvaluations(Authentication token) {
    return ControllerHelper.process(systemAdminstrationHelper::migrateCompetencyEvaluations, token);
  }
  
  @RequestMapping(value = "/sysAdmin/migrateLinkedInUrlsForCandidate", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse migrateLinkedInUrlsForCandidate(Authentication authentication) {
    return ControllerHelper.process(systemAdminstrationHelper::migrateLinkedInUrlsForCandidate,
        authentication);
  }
  
  @RequestMapping(value = "/sysAdmin/exportPrismPortraits", method = RequestMethod.GET)
  public void exportPrismPortraits(@RequestParam String companyId,
      @RequestParam(required = false) String groupName,
      @RequestParam(required = false) List<String> emailIds,
      @RequestParam(defaultValue = "ca") String platform, HttpServletResponse response) {
    systemAdminstrationHelper.exportPrismPortraits(response, companyId, groupName, emailIds,
        platform);
  }
  
  @RequestMapping(value = "/sysAdmin/competencyDateHelper", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse competencyDateHelper(@RequestParam String companyId,
      @RequestParam(defaultValue = "startDate") String dateType,
      @RequestParam(defaultValue = "inc") String opType, @RequestParam int numDays,
      Authentication authentication) {
    return ControllerHelper.process(systemAdminstrationHelper::competencyDateHelper,
        authentication, companyId, dateType, opType, numDays);
  }
  
  /**
   * Fix the pulse request for the given user. The company must have an active pulse request.
   * 
   * @param email
   *          - email to fix
   * @param authentication
   *          - logged in user
   * @return true if operation was successful.
   */
  @RequestMapping(value = "/sysAdmin/pulseReset", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse pulseReset(@RequestParam String email, Authentication authentication) {
    return ControllerHelper.process(systemAdminstrationHelper::pulseReset, authentication, email);
  }
  
  /**
   * update the user passwords to the new field.
   * 
   * @param authentication
   * @return
   */
  public SPResponse updatePasswords(Authentication authentication) {
    return ControllerHelper.process(systemAdminstrationHelper::updatePasswords, authentication);
  }
  
  /**
   * Reset static cache for profile messages. 
   * 
   * @param authentication
   *            - logged in user
   * @return
   *    success if cache reset
   */
  @RequestMapping(value = "/sysAdmin/profileMessagesStaticCacheReset", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse profileMessagesStaticCacheReset(Authentication authentication) {
    return ControllerHelper.process(systemAdminstrationHelper::profileMessagesStaticCacheReset, authentication);
  }

  @RequestMapping(value = "/sysAdmin/exportPulsePrecisionData", method = RequestMethod.GET)
  public void exportPrismPortraits(@RequestParam String pulseResultId, HttpServletResponse response) {
    systemAdminstrationHelper.exportPulsePrecisionData(response, pulseResultId);
  }
}