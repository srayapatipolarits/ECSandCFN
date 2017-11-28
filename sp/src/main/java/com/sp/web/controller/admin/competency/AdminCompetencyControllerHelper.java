package com.sp.web.controller.admin.competency;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.controller.competency.CompetencyFactory;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.article.ArticleDao;
import com.sp.web.dao.competency.CompetencyDao;
import com.sp.web.dao.competency.CompetencyProfileDao;
import com.sp.web.dto.BaseCompanyDTO;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.dto.CompetencyUserDTO;
import com.sp.web.dto.competency.BaseCompetencyEvaluationDTO;
import com.sp.web.dto.competency.BaseCompetencyProfileDTO;
import com.sp.web.dto.competency.CompanyCompetencyProfileDTO;
import com.sp.web.dto.competency.CompetencyProfileDTO;
import com.sp.web.dto.library.ArticleSelectListingDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.form.competency.CompetencyProfileForm;
import com.sp.web.form.competency.ManageCompetencyForm;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.model.UserStatus;
import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.CompetencyEvaluationRequest;
import com.sp.web.model.competency.CompetencyProfile;
import com.sp.web.model.competency.EvaluationType;
import com.sp.web.model.competency.UserEvaluationRequest;
import com.sp.web.model.goal.GroupPermissions;
import com.sp.web.model.pubchannel.PublicChannel;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.goal.GoalsRepository;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.repository.team.GroupRepository;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.lndfeedback.DevelopmentFeedbackFactory;
import com.sp.web.service.pc.PublicChannelFactory;
import com.sp.web.service.pc.PublicChannelHelper;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <code>AdminCompetencyControllerHelper</code> is the helper class for the competency Admin
 * Interface. SP.
 * 
 * @author Dax Abraham
 *
 */
@Component
public class AdminCompetencyControllerHelper {
  
  private static final Logger log = Logger.getLogger(AdminCompetencyControllerHelper.class);
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private CompetencyFactory competencyFactory;
  
  @Autowired
  private GoalsRepository goalsRepository;
  
  @Autowired
  private SPGoalFactory goalsFactory;
  
  @Autowired
  private ArticlesFactory articlesFactory;
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  NotificationsProcessor notificationProcessor;
  
  @Autowired
  @Qualifier("noActivityNotificationProcessor")
  NotificationsProcessor noActivityNotificationProcessor;
  
  @Autowired
  private GroupRepository groupRepository;
  
  @Autowired
  private PublicChannelHelper channelHelper;
  
  @Autowired
  private TodoFactory todoFactory;
  
  @Autowired
  private DevelopmentFeedbackFactory feedbackFactory;
  
  @Autowired
  private PublicChannelFactory publicChannelFactory;
  
  /**
   * Helper method to return all the companies that have the feature.
   * 
   * @param user
   *          - logged in user
   * @return the list of companies
   */
  public SPResponse getCompanies(User user) {
    final SPResponse resp = new SPResponse();
    // get all the companies with the feature enabled
    return resp.add(
        Constants.PARAM_COMPANY_LIST,
        companyFactory.findCompaniesByFeature(SPFeature.Competency).stream()
            .collect(mapping(BaseCompanyDTO::new, Collectors.toList())));
  }
  
  /**
   * Get the list of competency profiles configured in the system.
   * 
   * @param user
   *          - logged in user
   * @return the list of companies along with the competency profiles
   */
  public SPResponse getAll(User user) {
    final SPResponse resp = new SPResponse();
    // get the list of competencies
    List<CompetencyProfile> competencyProfileList = competencyFactory.getAll();
    // group them by the company
    Map<String, List<CompetencyProfile>> companyCompetencyProfileMap = competencyProfileList
        .stream().collect(groupingBy(CompetencyProfile::getCompanyId));
    // creating the list to store the final list to
    final List<CompanyCompetencyProfileDTO> companyCompetencyProfileList = new ArrayList<CompanyCompetencyProfileDTO>();
    // convert the map to the company competency DTO
    companyCompetencyProfileMap.forEach((key, value) -> companyCompetencyProfileList
        .add(new CompanyCompetencyProfileDTO(companyFactory.getCompany(key), value)));
    // sending the results back
    return resp.add(Constants.PARAM_COMPANY_COMPETENCY_LIST, companyCompetencyProfileList);
  }
  
  /**
   * Helper method to create/update competencies.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the create/update request
   */
  public SPResponse createUpdate(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    // get the form
    CompetencyProfileForm form = (CompetencyProfileForm) params[0];
    // validate the form
    form.validate();
    
    CompanyDao company = companyFactory.getCompany(form.getCompanyId());
    CompetencyProfileDao competencyProfile = null;
    final String competencyId = form.getId();
    boolean updateRequest = false;
    if (StringUtils.isEmpty(competencyId)) {
      // create new
      // validating the company
      Assert.notNull(company, "Company not found.");
      competencyProfile = new CompetencyProfileDao(form);
      try {
        // creating a blank competency profile
        competencyFactory.updateCompetencyProfile(competencyProfile);
      } catch (Exception e) {
        log.warn("Error creating new competency profile.", e);
        throw new InvalidRequestException("Competency profile already exists.");
      }
    } else {
      if (company.isEvaluationInProgress()) {
        throw new InvalidRequestException(MessagesHelper.getMessage("competency.admin.dashboard.evaluationInProgress"));
      }
      // update existing
      competencyProfile = competencyFactory.getCompetencyProfile(competencyId);
      updateRequest = true;
    }
    
    // update the data
    form.addUpdateCompetencyProfile(competencyProfile, competencyFactory);
    // store the information in the database
    competencyFactory.updateCompetencyProfile(competencyProfile);
    // Get the Competency profile dao so that articles can be fetched. Articles will be updated in
    // the dao.
    competencyProfile = competencyFactory.getCompetencyProfile(competencyProfile.getId());
    
    if (updateRequest) {
      updatePublicChannels(competencyProfile);
    }
    // send the data back to the front end with updated id's etc.
    return resp.add(Constants.PARAM_COMPETENCY_PROFILE, new CompetencyProfileDTO(competencyProfile,
        company));
  }
  
  /**
   * Update the public channel.
   * 
   * @param competencyProfile
   *          - competency profile
   */
  private void updatePublicChannels(CompetencyProfileDao competencyProfile) {
    List<CompetencyDao> competencyList = competencyProfile.getCompetencyList();
    final String companyId = competencyProfile.getCompanyId();
    final String competencyProfileName = competencyProfile.getName();
    for (CompetencyDao competencyDao : competencyList) {
      PublicChannel publicChannel = publicChannelFactory.getPublicChannel(competencyDao.getId(),
          companyId);
      if (publicChannel != null) {
        boolean doSave = false;
        doSave = publicChannel.updateTitle(competencyDao.getName());
        if (publicChannel.updateName(competencyProfileName)) {
          doSave = true;
        }
        if (doSave) {
          publicChannelFactory.updatePublicChannel(publicChannel);
        }
      }
    }
  }
  
  /**
   * Helper method to get the competency profile details.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - the competency profile id
   * @return the competency profile details
   */
  public SPResponse get(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    // get the competency profile id
    String competencyProfileId = (String) params[0];
    Assert.hasText(competencyProfileId, "Competency profile id is required.");
    
    // get the competency profile
    CompetencyProfileDao competency = competencyFactory.getCompetencyProfile(competencyProfileId);
    
    // sending the competency details back
    return resp.add(Constants.PARAM_COMPETENCY_PROFILE, new CompetencyProfileDTO(competency,
        companyFactory.getCompany(competency.getCompanyId()), true));
  }
  
  /**
   * Helper method to delete the competency profile.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - competency profile id
   * @return the response to the delete request
   */
  public SPResponse delete(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    // get the competency profile id
    String competencyProfileId = (String) params[0];
    Assert.hasText(competencyProfileId, "Competency profile id is required.");
    
    // get the competency profile
    CompetencyProfileDao competency = competencyFactory.getCompetencyProfile(competencyProfileId);
    
    // get the company for the given competency
    final String companyId = competency.getCompanyId();
    CompanyDao company = companyFactory.getCompany(companyId);
    
    // check if evaluation is under process
    Assert.isTrue(!company.isEvaluationInProgress(), "Competency profile in evaluation period.");
    
    // delete the competency
    competencyFactory.deleteCompetencyProfile(competency);
    
    // remove the competency from the member of the company
    List<User> memberList = userFactory.getAllMembersWithCompetencyProfile(competencyProfileId);
    memberList.forEach(u -> removeCompetency(u));
    
    /* delete the public channle associated with the competency profile */
    channelHelper.deletePublicChannelByParent(competency.getCompanyId(), competencyProfileId);
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to get all the articles.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - the company id
   * @return the list of articles
   */
  public SPResponse getArticles(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    // get the company id
    String companyId = (String) params[0];
    Assert.hasText(companyId, "Company is required.");
    
    // validating the company
    companyFactory.getCompany(companyId);
    
    // get all the applicable goals
    Map<String, String> allThemesForCompany = goalsFactory.getAllThemesForCompany(companyId,
        user.getUserLocale());
    
    // get all the articles filtered by the company goals
    final Set<String> goalIdList = allThemesForCompany.keySet();
    List<ArticleDao> allArticles = articlesFactory.getAllArticles(user.getUserLocale());
    List<ArticleSelectListingDTO> collect = allArticles.stream()
        .filter(a -> checkIfGoalPresent(a.getArticle().getGoals(), goalIdList))
        .map(ArticleSelectListingDTO::new).collect(Collectors.toList());
    return resp.add(Constants.PARAM_ARTICLE_LIST, collect);
  }
  
  /**
   * Check if the article goals matches the goal id list.
   * 
   * @param articleGoals
   *          - article goals
   * @param goalIdList
   *          - goal Id list for the company
   * @return true if present else false
   */
  private boolean checkIfGoalPresent(Set<String> articleGoals, Set<String> goalIdList) {
    return articleGoals.stream().filter(goalIdList::contains).findFirst().isPresent();
  }
  
  /**
   * Helper method to get all the users of the company along with the competency profiles assigned
   * to them.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params for the request
   * @return the list of users of the company
   */
  public SPResponse getUserCompetency(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    // get all the users of the company
    final String companyId = user.getCompanyId();
    // add the flag if the competency evaluation is in progress
    resp.add(Constants.PARAM_EVALUATION_IN_PROGRESS, companyFactory.getCompany(companyId)
        .isEvaluationInProgress());
    // get the competency profiles for the company
    List<CompetencyProfile> companyCompetency = competencyFactory
        .getCompanyCompetencyProfiles(companyId);
    // get the base competency profiles created
    resp.add(
        Constants.PARAM_COMPETENCY_PROFILE,
        companyCompetency.stream().collect(
            Collectors.mapping(BaseCompetencyProfileDTO::new, Collectors.toList())));
    
    List<User> memberList = null;
    String groupId = (String) params[0];
    if (StringUtils.isBlank(groupId)) {
      memberList = accountRepository.getAllMembersForCompany(companyId);
    } else {
      memberList = userFactory.getAllGroupMembers(groupId);
    }
    
    // create the DTO and add to the response
    return resp.add(
        Constants.PARAM_MEMBER_LIST,
        memberList.stream().collect(
            Collectors.mapping(m -> new CompetencyUserDTO(m), Collectors.toList())));
  }
  
  /**
   * Helper method to assign competency to a particular set of members.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params for assign request
   * @return the response to the assign request
   */
  public SPResponse assignCompetency(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    final ManageCompetencyForm competencyForm = (ManageCompetencyForm) params[0];
    
    final String competencyProfileId = competencyForm.getCompetencyProfileId().get(0);
    final List<User> memberIdList = getMemberListToProcess(competencyForm);
    
    // check if there is an evaluation in progress
    checkIfEvaluationInProgress(user);
    
    // validate if the competency profile
    final CompetencyProfileDao competencyProfile = competencyFactory
        .getCompetencyProfile(competencyProfileId);
    
    // validate if the competency profile is for the same company
    Assert.isTrue(competencyProfile.getCompanyId().equals(user.getCompanyId()),
        "Competency profile not authorized for company.");
    
    CompanyDao company = companyFactory.getCompany(competencyProfile.getCompanyId());
    // for each of the members assign the competency profile
    final Map<String, Object> paramsMap = new HashMap<String, Object>();
    final String competencyProfileName = competencyProfile.getName();
    paramsMap.put(Constants.PARAM_COMPETENCY_PROFILE, competencyProfileName);
    paramsMap.put(Constants.PARAM_MESSAGE, MessagesHelper.getMessage(
        "competency.profile.assigned.notification.message", competencyProfileName));
    paramsMap.put(
        Constants.PARAM_SUBJECT,
        MessagesHelper.getMessage("notification.subject.CompetencyProfileAssigned",
            company.getName()));
    memberIdList.stream().forEach(u -> assignCompetency(u, competencyProfileId, paramsMap, user));
    
    return resp.isSuccess();
  }
  
  /**
   * Assign the competency for the given user.
   * 
   * @param member
   *          - user
   * @param competencyProfileId
   *          - competency profile id
   * @param paramsMap
   *          - params map for notification
   * @param user
   *          - user
   */
  private void assignCompetency(User member, String competencyProfileId,
      Map<String, Object> paramsMap, User user) {
    // check and remove any pending feedbacks
    String existingCompetencyProfileId = member.getCompetencyProfileId();
    if (existingCompetencyProfileId != null) {
      removeFeedbackAndPublicChannels(member, existingCompetencyProfileId);
    }
    member.setCompetencyProfileId(competencyProfileId);
    userFactory.updateUserAndSession(member);
    // sending the notification for the user
    noActivityNotificationProcessor.process(NotificationType.CompetencyProfileAssigned, user,
        member, paramsMap);
    channelHelper.addByParentUser(member, competencyProfileId);
  }
  
  private void removeFeedbackAndPublicChannels(User member, String existingCompetencyProfileId) {
    feedbackFactory.deleteByFeedParentRefId(member, existingCompetencyProfileId);
    channelHelper.removeByParentUser(member, existingCompetencyProfileId);
  }
  
  /**
   * Check if there is an evaluation in progress.
   * 
   * @param user
   *          - user for company id
   * @return the reference to the company object
   */
  private CompanyDao checkIfEvaluationInProgress(User user) {
    CompanyDao company = companyFactory.getCompany(user.getCompanyId());
    if (company.isEvaluationInProgress()) {
      throw new InvalidRequestException(MessagesHelper.getMessage("competency.admin.dashboard.evaluationInProgress"));
    }
    return company;
  }
  
  /**
   * Helper method to remove the competency profile for the members.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - the params for the remove request
   * @return the response to the remove request
   */
  public SPResponse removeCompetency(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    final ManageCompetencyForm competencyForm = (ManageCompetencyForm) params[0];
    
    final List<User> memberIdList = getMemberListToProcess(competencyForm);
    
    // check if there is an evaluation in progress
    checkIfEvaluationInProgress(user);
    
    // for each of the members assign the competency profile
    memberIdList.stream().forEach(this::removeCompetency);
    
    return resp.isSuccess();
  }
  
  /**
   * Remove the competency for the user and update.
   * 
   * @param user
   *          - user to update
   */
  private void removeCompetency(User user) {
    /* unfollow the channel associated with the user */
    removeFeedbackAndPublicChannels(user, user.getCompetencyProfileId());
    user.setCompetencyProfileId(null);
    userFactory.updateUserAndSession(user);
  }
  
  /**
   * Helper method to get the data for competency admin listing page.
   * 
   * @param user
   *          - logged in user
   * @return the response to the get request
   */
  public SPResponse adminGetAll(User user) {
    final SPResponse resp = new SPResponse();
    
    // get the list of competencies for the company
    final String companyId = user.getCompanyId();
    
    // get the company to check if evaluation is running
    CompanyDao company = companyFactory.getCompany(companyId);
    resp.add(Constants.PARAM_EVALUATION_IN_PROGRESS, company.isEvaluationInProgress());
    
    // add the competency evaluation if it exists
    final String competencyEvaluationId = company.getCompetencyEvaluationId();
    CompetencyEvaluation competencyEvaluation = null;
    if (competencyEvaluationId != null) {
      competencyEvaluation = competencyFactory.getCompetencyEvaluation(competencyEvaluationId);
      Assert.notNull(competencyEvaluation, "Competency evaluation not found.");
      resp.add(Constants.PARAM_COMPETENCY_EVALUATION, new BaseCompetencyEvaluationDTO(
          competencyEvaluation));
    } else {
      // create a temporary competency evaluation
      competencyEvaluation = new CompetencyEvaluation();
      
      // adding the competency profiles
      competencyFactory.getCompanyCompetencyProfiles(companyId).stream()
          .map(c -> competencyFactory.getCompetencyProfile(c.getId()))
          .forEach(competencyEvaluation::addCompetencyProfile);
      
      // iterate over all the users and add them to the competency profiles user map
      List<User> memberList = accountRepository.getAllMembersForCompany(companyId);
      memberList.stream().filter(m -> m.getCompetencyProfileId() != null)
          .forEach(competencyEvaluation::addUser);
    }
    
    // adding the competency profile and users data to the response
    return resp.add(Constants.PARAM_USER_COMPETENCY_EVALUATION, competencyEvaluation
        .getCompetencyProfileMap().values());
  }
  
  /**
   * Helper method to send a reminder.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to send reminder
   */
  public SPResponse sendReminder(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the user for whom the reminder is being sent
    String userId = (String) params[0];
    User userFor = userFactory.getUser(userId);
    Assert.notNull(userFor, "User not found.");
    Assert.notNull(userFor.getCompetencyProfileId(), "User competency not set.");
    
    boolean isInitiate = (boolean) params[1];
    boolean isSelf = (boolean) params[2];
    String reviewUserId = (String) params[3];
    
    // sending the reminder
    sendReminder(userFor, isInitiate, isSelf, reviewUserId, false, userFor.getCompanyId());
    
    return resp.isSuccess();
  }
  
  /**
   * Send reminder for only initiate and self.
   * 
   * @param userFor
   *          - user for
   * @param isInitiate
   *          - flag for initiate
   * @param isSelf
   *          - flag for self assessment
   */
  public void sendReminder(User userFor, boolean isInitiate, boolean isSelf) {
    sendReminder(userFor, isInitiate, isSelf, null, false, userFor.getCompanyId());
  }
  
  /**
   * Send reminder.
   * 
   * @param userFor
   *          - user for
   * @param isInitiate
   *          - flag for initiate
   * @param isSelf
   *          - flag for self assessment
   * @param reviewUserId
   *          - review user
   * @param isConsolidate
   *          - if consolidate all reminders
   * @param companyId
   *          - company id
   */
  public void sendReminder(User userFor, boolean isInitiate, boolean isSelf, String reviewUserId,
      boolean isConsolidate, String companyId) {
    // check if the competency is in progress
    CompanyDao company = companyFactory.getCompany(companyId);
    Assert.isTrue(company.isEvaluationInProgress(), "Competency evaluation not in progress.");
    
    final String competencyEvaluationId = company.getCompetencyEvaluationId();
    CompetencyEvaluation competencyEvaluation = competencyFactory
        .getCompetencyEvaluation(competencyEvaluationId);
    final Map<String, Object> paramsMap = new HashMap<String, Object>();
    if (competencyEvaluation != null) {
      paramsMap.put(Constants.PARAM_END_DATE,
          MessagesHelper.formatDate(competencyEvaluation.getEndDate(), "competency"));
    }
    
    // check if it is reminder for initiation
    if (isInitiate) {
      paramsMap.put(Constants.PARAM_SUBJECT, MessagesHelper.getMessage(
          "notification.subject.CompetencyEvaluationReminderInitiate",
          userFor.getLocale(), company.getName()));
      paramsMap.put(Constants.PARAM_COMPETENCY_EVALUATION_MANAGER,
          competencyEvaluation.isSupported(EvaluationType.Manager));
      notificationProcessor.process(NotificationType.CompetencyEvaluationReminderInitiate, userFor,
          userFor, paramsMap);
      return;
    }
    
    // check if reminder is for self assessment
    if (isSelf) {
      paramsMap.put(Constants.PARAM_SUBJECT, MessagesHelper.getMessage(
          "notification.subject.CompetencyEvaluationReminderSelf",
          userFor.getLocale(), company.getName()));
      noActivityNotificationProcessor.process(NotificationType.CompetencyEvaluationReminderSelf,
          userFor, userFor, paramsMap);
      return;
    }
    
    // check if there is a third party user for whom the reminder has to be sent
    if (!StringUtils.isBlank(reviewUserId)) {
      FeedbackUser reviewUser = userFactory.getFeedbackUser(reviewUserId);
      Assert.notNull(reviewUser, "Feedback user not found.");
      if (isConsolidate) {
        // getting the competency evaluation requests for the review user
        CompetencyEvaluationRequest competencyEvaluationRequest = competencyFactory
            .getCompetencyEvaluationRequest(reviewUserId);
        final List<UserEvaluationRequest> requestsList = competencyEvaluationRequest
            .getRequestsList();
        if (!CollectionUtils.isEmpty(requestsList)) {
          // get the list of users in the request
          List<BaseUserDTO> collect = requestsList.stream().map(UserEvaluationRequest::getUserId)
              .map(userFactory::getUser).filter(u -> u != null)
              .collect(Collectors.mapping(BaseUserDTO::new, Collectors.toList()));
          // adding the list to the response
          paramsMap.put(Constants.PARAM_MEMBER_LIST, collect);
          paramsMap.put(Constants.PARAM_SUBJECT, MessagesHelper.getMessage(
              "notification.subject.CompetencyEvaluationReminderConsolidated",
                  reviewUser.getLocale(), company.getName()));
          // sending the notification
          notificationProcessor.process(NotificationType.CompetencyEvaluationReminderConsolidated,
              reviewUser, reviewUser, paramsMap);
        }
      } else {
        paramsMap.put(Constants.PARAM_SUBJECT, MessagesHelper.getMessage(
            "notification.subject.CompetencyEvaluationReminder",
            userFor.getLocale(), company.getName()));
        notificationProcessor.process(NotificationType.CompetencyEvaluationReminder, userFor,
            reviewUser, paramsMap);
      }
    }
  }
  
  /**
   * Helper method to get the statistics for the company.
   * 
   * @param user
   *          - user
   * @return the response to the statistics request
   */
  public SPResponse getStats(User user) {
    final SPResponse resp = new SPResponse();
    // check if the evaluation is in progress then this call is not allowed
    CompanyDao company = checkIfEvaluationInProgress(user);
    
    // get all members for the company
    List<User> memberList = accountRepository.getAllMembersForCompany(company.getId());
    int activeMemberCount = 0;
    int memberWithCompetencyCount = 0;
    for (User member : memberList) {
      if (member.getUserStatus() == UserStatus.VALID) {
        activeMemberCount++;
      }
      
      if (member.getCompetencyProfileId() != null) {
        memberWithCompetencyCount++;
      }
    }
    
    // add to the response
    resp.add(Constants.PARAM_TOTAL_MEMBER_COUNT, memberList.size());
    resp.add(Constants.PARAM_TOTAL_ACTIVE_MEMBER_COUNT, activeMemberCount);
    resp.add(Constants.PARAM_TOTAL_MEMBER_WITH_COMPETENCY_COUNT, memberWithCompetencyCount);
    
    return resp.isSuccess();
  }
  
  /**
   * Get the member list to process for both assign and removal of competency profile.
   * 
   * @param competency
   *          form - competency form
   * @return the list of members to process
   */
  private List<User> getMemberListToProcess(ManageCompetencyForm competencyForm) {
    // validate the action plan form
    competencyForm.validate();
    
    // get the list of users to update
    final List<User> userList = new ArrayList<User>();
    final List<String> userIdList = Optional.ofNullable(competencyForm.getUserIdList()).orElse(
        new ArrayList<String>());
    if (!userIdList.isEmpty()) {
      userList.addAll(userIdList.stream().map(userFactory::getUser).filter(u -> u != null)
          .collect(Collectors.toList()));
    }
    
    // iterate over all the groups and add the users to the user list
    final List<GroupPermissions> groupPermissionsList = competencyForm.getGroupPermissionsList();
    if (!CollectionUtils.isEmpty(groupPermissionsList)) {
      groupPermissionsList.forEach(gp -> addGroupUsers(gp, userList, userIdList));
    }
    return userList;
  }
  
  /**
   * Add the users for the given group.
   * 
   * @param gp
   *          - group
   * @param userList
   *          - user list
   * @param userIdList
   *          - user id list
   */
  private void addGroupUsers(GroupPermissions gp, List<User> userList, List<String> userIdList) {
    // get the user group
    UserGroup userGroup = groupRepository.findById(gp.getGroupId());
    Assert.notNull(userGroup, "Group not found.");
    
    // create a list of members to process also add group lead as per group permissions
    final List<String> memberList = new ArrayList<String>(userGroup.getMemberList());
    if (!gp.isExcludeGroupLead() && userGroup.groupLeadPresent()) {
      memberList.add(userGroup.getGroupLead());
    }
    
    // process the add for all the members
    memberList.stream().map(userFactory::getUserByEmail)
        .filter(u -> !userIdList.contains(u.getId()) && !memberList.contains(u))
        .forEach(userList::add);
  }
  
}
