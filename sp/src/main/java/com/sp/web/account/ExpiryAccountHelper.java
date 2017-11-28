package com.sp.web.account;

import com.sp.web.assessment.processing.AssessmentProgressStoreRepoistory;
import com.sp.web.controller.competency.CompetencyFactory;
import com.sp.web.controller.discussion.GroupDiscussionControllerHelper;
import com.sp.web.controller.pulse.WorkspacePulseFactory;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.goal.UserGoalDao;
import com.sp.web.dao.goal.UserGoalProgressDao;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.Account;
import com.sp.web.model.AccountType;
import com.sp.web.model.Company;
import com.sp.web.model.DeletedUser;
import com.sp.web.model.FeedbackArchiveRequest;
import com.sp.web.model.FeedbackRequest;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.HiringUser;
import com.sp.web.model.PaymentRecord;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.RequestType;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.assessment.AssessmentProgressTracker;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.blueprint.BlueprintBackup;
import com.sp.web.model.blueprint.BlueprintSettings;
import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.CompetencyProfile;
import com.sp.web.model.competency.UserCompetency;
import com.sp.web.model.competency.UserCompetencyEvaluationDetails;
import com.sp.web.model.discussion.UserGroupDiscussion;
import com.sp.web.model.email.EmailManagement;
import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.ActionPlanProgress;
import com.sp.web.model.goal.CompanyActionPlanSettings;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.UserActionPlan;
import com.sp.web.model.hiring.group.HiringGroup;
import com.sp.web.model.hiring.match.HiringPortrait;
import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.model.hiring.user.HiringUserArchive;
import com.sp.web.model.library.ArticleLocation;
import com.sp.web.model.library.TrainingLibraryHomeArticle;
import com.sp.web.model.note.UserNoteDao;
import com.sp.web.model.pubchannel.PublicChannel;
import com.sp.web.model.pulse.PulseAssessment;
import com.sp.web.model.pulse.PulseQuestionSet;
import com.sp.web.model.pulse.PulseRequest;
import com.sp.web.model.pulse.PulseResults;
import com.sp.web.model.pulse.QuestionSetType;
import com.sp.web.model.todo.UserTodoRequests;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.FeedbackRepository;
import com.sp.web.repository.archive.ArchiveRepository;
import com.sp.web.repository.goal.GoalsRepository;
import com.sp.web.repository.growth.GrowthRepository;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.repository.library.TrainingLibraryArticleRepository;
import com.sp.web.repository.payment.PaymentInstrumentRepository;
import com.sp.web.repository.pulse.WorkspacePulseRepository;
import com.sp.web.repository.team.GroupRepository;
import com.sp.web.repository.tracking.UserActivityTrackingRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.blueprint.BlueprintFactory;
import com.sp.web.service.discussion.GroupDiscussionFactory;
import com.sp.web.service.email.EmailManagementFactory;
import com.sp.web.service.feed.NewsFeedFactory;
import com.sp.web.service.feed.NewsFeedHelper;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.hiring.group.HiringGroupFactoryCache;
import com.sp.web.service.hiring.match.AdminHiringPortraitMatchFactory;
import com.sp.web.service.hiring.match.HiringPortraitMatchFactoryCache;
import com.sp.web.service.hiring.role.HiringRoleFactoryCache;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.service.lndfeedback.DevelopmentFeedbackFactory;
import com.sp.web.service.note.NoteFactory;
import com.sp.web.service.pc.PublicChannelFactory;
import com.sp.web.service.pc.PublicChannelHelper;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.user.UserFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The helper class to handle account expiry.
 */
@Component
public class ExpiryAccountHelper {
  
  private static final Logger log = Logger.getLogger(ExpiryAccountHelper.class);
  
  @Autowired
  AccountRepository accountRepository;
  
  @Autowired
  ArchiveRepository archiveRepository;
  
  @Autowired
  UserRepository userRepository;
  
  @Autowired
  GroupRepository groupRepository;
  
  @Autowired
  AssessmentProgressStoreRepoistory progressStoreRepository;
  
  @Autowired
  PaymentInstrumentRepository paymentInstrumentRepository;
  
  @Autowired
  AccountRechargeRepository rechargeRepository;
  
  @Autowired
  private GrowthRepository growthRepository;
  
  @Autowired
  private FeedbackRepository feedbackRepository;
  
  @Autowired
  private BlueprintFactory blueprintFactory;
  
  @Autowired
  private SPGoalFactory goalsFactory;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private EmailManagementFactory emailManagementFactory;
  
  @Autowired
  private ActionPlanFactory actionPlanFactory;
  
  @Autowired
  private CompetencyFactory competencyFactory;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private WorkspacePulseFactory pulseFactory;
  
  @Autowired
  private WorkspacePulseRepository pulseRepository;
  
  @Autowired
  private TrainingLibraryArticleRepository articleRepository;
  
  @Autowired
  private ArticlesFactory articlesFactory;
  
  @Autowired
  private GoalsRepository goalsRepository;
  
  @Autowired
  private NoteFactory notesFactory;
  
  @Autowired
  private PublicChannelFactory publicChannelFactory;
  
  @Autowired
  private NewsFeedFactory newsFeedFactory;
  
  @Autowired
  private TodoFactory todoFactory;
  
  @Autowired
  private DevelopmentFeedbackFactory feedbackFactory;
  
  @Autowired
  private PublicChannelHelper publicChannelHelper;
  
  @Autowired
  private GroupDiscussionFactory groupDiscussionFactory;
  
  @Autowired
  private GroupDiscussionControllerHelper groupDiscussionHelper;
  
  @Autowired
  private UserActivityTrackingRepository userActivtyRepository;
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @Autowired
  private HiringGroupFactoryCache factoryCache;
  
  @Autowired
  private HiringRoleFactoryCache roleFactoryCache;
  
  @Autowired
  private AdminHiringPortraitMatchFactory matchFactory;
  
  @Autowired
  private HiringPortraitMatchFactoryCache matchFactoryCache;
  
  /**
   * The method to expire the given account.
   * 
   * @param account
   *          - account to expire
   */
  // @Async
  
  public void expireAccount(Account account) {
    
    archiveErtiPlan(account);
    archiveIntelligentHiringPlan(account);
  }
  
  
  /**
   * Archiving the ERTi plan.
   * 
   * @param account
   *          - account
   */
  public void archiveErtiPlan(Account account) {
    try {
      Assert.notNull(account, "Account must not be null !!!");
      SPPlan spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
      final String accountId = account.getId();
      
      // /* check if account is expired or not */
      // LocalDate currentDate = LocalDate.now();
      // Date aggreementEndDate = account.getAggreementEndDate();
      // LocalDate aggreementEndDateLocalDate = DateTimeUtil.getLocalDate(aggreementEndDate);
      //
      // if (currentDate.isBefore(aggreementEndDateLocalDate)) {
      // return;
      // }
      List<User> userList = new ArrayList<User>();
      List<DeletedUser> deletedUserList = new ArrayList<DeletedUser>();
      
      // expire the company
      if (account.getType() == AccountType.Business) {
        Company company = accountRepository.getCompanyForAccount(accountId);
        
        // check if there is a competency evaluation in progress
        if (company.isEvaluationInProgress()) {
          throw new InvalidRequestException(
              "Competency evaluation in progress, please end evaluation.");
        }
        
        /* if only 1 plan is present then only remove the company */
        if (!account.getSpPlanMap().containsKey(SPPlanType.IntelligentHiring)) {
          archiveRepository.archive(company);
          companyFactory.removeCompany(company);
          
        } else {
          company.setErtiDeactivated(true);
          company.setBlockAllMembers(true);
          spPlan.setActive(false);
          companyFactory.updateCompany(company);
        }
        
        // get user list to archive
        final String companyId = company.getId();
        userList.addAll(accountRepository.getAllMembersForCompany(companyId));
        deletedUserList.addAll(accountRepository.getAllDeletedMemberForCompany(companyId));
        
        // archive the user groups for the company
        List<UserGroup> groupList = groupRepository.findAllGroups(companyId);
        groupList.stream().forEach(ug -> archive(ug, groupRepository::deleteGroup));
        
        // archiving the blueprint settings for the company
        BlueprintSettings blueprintSettings = blueprintFactory.getBlueprintSettings(companyId);
        if (blueprintSettings.getId() != null) {
          archiveRepository.archive(blueprintSettings);
          blueprintFactory.deleteBlueprintSettings(companyId);
        }
        
        // company email settings
        EmailManagement emailManagement = emailManagementFactory.getFromDB(companyId);
        if (emailManagement != null) {
          archiveRepository.archive(emailManagement);
          emailManagementFactory.delete(emailManagement);
        }
        
        // Organization plans
        List<ActionPlan> companyActionPlans = actionPlanFactory.getCompanyActionPlans(companyId);
        companyActionPlans.forEach(ap -> archive(ap, actionPlanFactory::deletedActionPlan));
        
        // archive the competencies
        List<CompetencyProfile> companyCompetencyProfiles = competencyFactory
            .getCompanyCompetencyProfiles(companyId);
        companyCompetencyProfiles.stream()
            .map(c -> competencyFactory.getCompetencyProfile(c.getId()))
            .forEach(cp -> archive(cp, competencyFactory::deleteCompetencyProfile));
        
        // archiving all the evaluations
        List<CompetencyEvaluation> completedCompetencyEvaluation = competencyFactory
            .getCompletedCompetencyEvaluation(companyId);
        completedCompetencyEvaluation.forEach(ce -> archive(ce,
            competencyFactory::deleteCompetencyEvaluation));
        
        // archive all the feedback users
        List<FeedbackUser> feedbackUsersForCompany = userFactory
            .getFeedbackUsersForCompany(companyId);
        feedbackUsersForCompany.forEach(fbUser -> archive(fbUser, userFactory::removeUser));
        
        // archive pulse requests
        PulseRequest pulseRequest = pulseFactory.getPulseRequest(companyId);
        if (pulseRequest != null) {
          archiveRepository.archive(pulseRequest);
          pulseFactory.delete(pulseRequest);
        }
        
        // archive the results
        List<PulseResults> pulseResults = pulseFactory.getAllPulseResults(companyId);
        pulseResults.forEach(pr -> archive(pr, pulseFactory::delete));
        
        // archive the pulse question set
        List<PulseQuestionSet> pulseQuestionSets = pulseRepository
            .findPulseQuestionSetsFor(companyId);
        pulseQuestionSets.stream()
            .filter(pqs -> !pqs.isForAll() && pqs.getQuestionSetType() == QuestionSetType.Company)
            .forEach(pqs -> removeCompany(pqs, companyId));
        
        // archive training library home articles
        List<TrainingLibraryHomeArticle> articleList = articleRepository
            .findHomepageArticlesByLocation(ArticleLocation.Content, companyId);
        articleList.forEach(tlha -> archive(tlha, null));
        articlesFactory.deleteHomePageArticles(companyId);
        
        // remove the company specific goals
        List<SPGoal> goalsByCategory = goalsRepository
            .findAllGoalsByCategory(GoalCategory.Business);
        List<SPGoal> collect = goalsByCategory.stream()
            .filter(g -> g.getAccounts().remove(companyId)).collect(Collectors.toList());
        collect.forEach(goalsFactory::updateGoal);
        
        // Archive the public channle associated with the account */
        List<PublicChannel> publicChannels = publicChannelFactory
            .findAllPubliChannelByCompanyId(companyId);
        NewsFeedHelper feedHelper = newsFeedFactory.getCompanyNewsFeedHelper(companyId);
        publicChannels.stream().forEach(pc -> {
          feedHelper.deleteNewsFeed(pc);
          publicChannelFactory.deletePublicChannel(pc);
          archiveRepository.archive(pc);
        });
        
      } else {
        userList.add(userRepository.findByAccountId(accountId));
      }
      
      // expire the users in the given list
      userList.stream().forEach(u -> doArchiveUser(u));
      
      // archive deleted users
      deletedUserList.stream().forEach(u -> {
        notesFactory.deleteNotes(u);
        archiveRepository.archive(u);
        accountRepository.removeDeletedUser(u);
      });
      
      // archive the account
      if (!account.getSpPlanMap().containsKey(SPPlanType.IntelligentHiring)) {
        archiveRepository.archive(account);
        accountRepository.removeAccount(account);
      } else {
        account.getSpPlanMap().remove(SPPlanType.Primary);
        accountRepository.updateAccount(account);
      }
      
      // archive the payment instrument
      // not storing the payment instrument for security reasons
      paymentInstrumentRepository.remove(spPlan.getPaymentInstrumentId());
      
      // archive the payment records
      List<PaymentRecord> paymentRecordList = rechargeRepository.getPaymentRecords(account,
          SPPlanType.Primary);
      paymentRecordList.stream().forEach(pr -> {
        archiveRepository.archive(pr);
        rechargeRepository.deletePaymentRecord(pr);
      });
      
    } catch (Exception e) {
      log.fatal("Could not expire account !!!", e);
    }
  }
  
  /**
   * Archive the intelligent hiring plan.
   * 
   * @param account
   *          - account
   */
  public void archiveIntelligentHiringPlan(Account account) {
    try {
      Assert.notNull(account, "Account must not be null !!!");
      SPPlan spPlan = account.getSpPlanMap().get(SPPlanType.IntelligentHiring);
      final String accountId = account.getId();
      
      // /* check if account is expired or not */
      // LocalDate currentDate = LocalDate.now();
      // Date aggreementEndDate = account.getAggreementEndDate();
      // LocalDate aggreementEndDateLocalDate = DateTimeUtil.getLocalDate(aggreementEndDate);
      //
      // if (currentDate.isBefore(aggreementEndDateLocalDate)) {
      // return;
      // }
      List<HiringUser> userList = new ArrayList<HiringUser>();
      List<HiringUser> deletedUserList = new ArrayList<HiringUser>();
      
      // expire the company
      if (account.getType() == AccountType.Business) {
        Company company = accountRepository.getCompanyForAccount(accountId);
        
        /* if only 1 plan is present then only remove the company */
        if (!account.getSpPlanMap().containsKey(SPPlanType.Primary)) {
          archiveRepository.archive(company);
          companyFactory.removeCompany(company);
          
        } else {
          company.setPeopleAnalyticsDeactivated(true);
          spPlan.setActive(false);
          companyFactory.updateCompany(company);
        }
        
        // get user list to archive
        final String companyId = company.getId();
        userList.addAll(hiringUserFactory.getAll(companyId));
        deletedUserList.addAll(hiringUserFactory.getAllArchivedUsers(companyId));
        
        // archive the user groups for the company
        List<HiringGroup> groupList = factoryCache.getAll(companyId);
        groupList.stream().forEach(ug -> {
          archiveRepository.archive(ug);
          factoryCache.remove(ug);
        });
        
        // remove the hirign portrait for the account.
        List<HiringPortrait> allByCompanyId = matchFactoryCache.getAllByCompanyId(companyId);
        allByCompanyId.forEach(hp -> {
          matchFactory.removePortraitForCompany(companyId, hp.getId());
        });
        
        // Remove all the roles.
        
        List<HiringRole> all = roleFactoryCache.getAll(companyId);
        all.stream().forEach(role -> {
          archiveRepository.archive(role);
          roleFactoryCache.delete(role);
        });
        
      }
      
      // expire the users in the given list
      userList.stream().forEach(u -> archiveRepository.archive(u));
      
      // archive deleted users
      deletedUserList.stream().forEach(u -> {
        archiveRepository.archive(u);
        hiringUserFactory.delete(u);
      });
      
      // archive the account
      if (!account.getSpPlanMap().containsKey(SPPlanType.Primary)) {
        archiveRepository.archive(account);
        accountRepository.removeAccount(account);
      } else {
        account.getSpPlanMap().remove(SPPlanType.IntelligentHiring);
        accountRepository.updateAccount(account);
      }
      
      // archive the payment instrument
      // not storing the payment instrument for security reasons
      paymentInstrumentRepository.remove(spPlan.getPaymentInstrumentId());
      
      // archive the payment records
      List<PaymentRecord> paymentRecordList = rechargeRepository.getPaymentRecords(account,
          SPPlanType.IntelligentHiring);
      paymentRecordList.stream().forEach(pr -> {
        archiveRepository.archive(pr);
        rechargeRepository.deletePaymentRecord(pr);
      });
      
    } catch (Exception e) {
      log.fatal("Could not expire account !!!", e);
    }
  }
  
  /**
   * Remove the company from the pulse question set.
   * 
   * @param pulseQuestionSet
   *          - pulse question set
   * @param companyId
   *          - company id
   */
  private void removeCompany(PulseQuestionSet pulseQuestionSet, String companyId) {
    pulseQuestionSet.getCompanyId().remove(companyId);
    pulseRepository.savePulseQuestionSet(pulseQuestionSet);
  }
  
  /**
   * Generic function to archive stuff.
   * 
   * @param objToArchive
   *          - object to archive
   * @param deleteObjFunction
   *          - the function to delete the object
   */
  private <T> void archive(T objToArchive, Consumer<T> deleteObjFunction) {
    if (objToArchive != null) {
      archiveRepository.archive(objToArchive);
      if (deleteObjFunction != null) {
        deleteObjFunction.accept(objToArchive);
      }
    }
  }
  
  /**
   * Archive the given user and all associate user entities.
   * 
   * @param user
   *          - user
   */
  private void doArchiveUser(User user) {
    // archive the user
    archiveRepository.archive(user);
    
    // archive any pending assessment progress
    AssessmentProgressTracker assessmentFromStore = progressStoreRepository
        .getAssessmentTracker(user.getId());
    archiveRepository.archive(assessmentFromStore);
    progressStoreRepository.remove(assessmentFromStore);
    // archive any feedback and growth sections
    
    // List<GrowthRequest> growthRequests = growthRepository.getAllGrowthRequests(user.getEmail());
    // List<GrowthRequestArchived> growthRequestsArchived =
    // growthRepository.getArchivedGrowthRequest(user.getEmail());
    // growthRequests.stream().forEach(gr -> {
    // archiveRepository.archive(gr);
    // growthRepository.removeGrowthRequest(gr.getId());
    // });
    // growthRequestsArchived.stream().forEach(gr -> {
    // archiveRepository.archive(gr);
    // growthRepository.removeGrowthRequest(gr.getId());
    // });
    
    List<FeedbackRequest> feedbackRequest = feedbackRepository.getAllFeedbackRequest(user.getId());
    feedbackRequest.stream().forEach(fr -> {
      List<FeedbackUser> feedbackUser = userRepository.findFeedbackUsers(user.getEmail());
      feedbackUser.stream().forEach(fu -> {
        archiveRepository.archive(fu);
        feedbackRepository.remove(fu);
      });
      
      archiveRepository.archive(fr);
      feedbackRepository.removeFeedbackRequest(fr);
      
    });
    
    List<? extends FeedbackRequest> feedbackArchivedRequest = feedbackRepository
        .getAllArchivedFeedbackRequests(user.getId());
    feedbackArchivedRequest.stream().forEach(fr -> {
      List<FeedbackUser> feedbackUser = userRepository.findFeedbackUsers(user.getEmail());
      feedbackUser.stream().forEach(fu -> {
        archiveRepository.archive(fu);
        feedbackRepository.remove(fu);
      });
      
      archiveRepository.archive(fr);
      feedbackRepository.removeFeedbacArchivedRequest((FeedbackArchiveRequest) fr);
      
    });
    
    // archiving the blueprint for the user
    Blueprint blueprint = goalsFactory.getBlueprint(user);
    archive(blueprint, goalsFactory::removeBlueprint);
    
    // archive the user action plan
    UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
    archive(userActionPlan, actionPlanFactory::deleteUserActionPlan);
    
    // archive the competency evaluation details
    List<UserCompetencyEvaluationDetails> competencyEvaluationDetailsForUser = competencyFactory
        .getCompetencyEvaluationDetailsForUser(user.getId());
    competencyEvaluationDetailsForUser.forEach(ed -> archive(ed, competencyFactory::delete));
    
    // archive the pulse assessment
    List<PulseAssessment> pulseAssessments = pulseRepository.getAllPulseAssessmentsForUser(user
        .getId());
    pulseAssessments.forEach(pa -> archive(pa, pulseRepository::delete));
    
    // archive the user todo requests
    UserTodoRequests userTodoRequests = todoFactory.getUserTodoRequests(user);
    archive(userTodoRequests, todoFactory::deleteUserTodoRequests);
    
    userActivtyRepository.deleteActivityTracking(user.getId());
    // Remove the user.
    
    /* in case user is an hiring people anlytics admin, don't archive the user */
    if (!user.hasRole(RoleType.Hiring)) {
      userRepository.removeUser(user);
    } else {
      for (SPFeature spFeature : SPPlanType.Primary.getFeatures()) {
        CompanyDao company = companyFactory.getCompany(user.getCompanyId());
        List<RoleType> rolesToBeRemoved = Arrays.asList(spFeature.getRoles());
        company.getRoleList().removeAll(rolesToBeRemoved);
        user.getRoles().removeAll(rolesToBeRemoved);
        user.removeRole(RoleType.User);
        userRepository.updateUser(user);
      }
    }
    
  }
  
  /**
   * Expire the given users requests etc.
   * 
   * @param user
   *          - user
   */
  public void expireUser(User user) {
    // removing the users prism lens requests
    final String userId = user.getId();
    List<FeedbackRequest> feedbackRequest = feedbackRepository.getAllFeedbackRequest(userId);
    feedbackRequest.stream().forEach(
        fr -> {
          FeedbackUser feedbackUser = userFactory.getFeedbackUser(fr.getFeedbackUserId());
          if (feedbackUser != null) {
            archive(feedbackUser, feedbackRepository::remove);
            if (fr.getRequestType() == RequestType.INTERNAL
                && fr.getRequestStatus() == RequestStatus.NOT_INITIATED) {
              todoFactory.remove(feedbackUser);
            }
          }
          archive(fr, feedbackRepository::removeFeedbackRequest);
        });
    
    // removing development feedback requests
    // not archiving
    feedbackFactory.deleteAllByUser(user);
    
    // removing blueprint
    Blueprint blueprint = goalsFactory.getBlueprint(user);
    if (blueprint != null) {
      archive(blueprint, goalsFactory::removeBlueprint);
      BlueprintBackup blueprintBackup = blueprintFactory
          .getBlueprintBackupFromBlueprintId(blueprint.getId());
      if (blueprintBackup != null) {
        archive(blueprintBackup, blueprintFactory::removeBlueprintBackup);
      }
    }
    final String companyId = user.getCompanyId();
    userFactory.removeBlueprintRequests(companyId, userId);
    
    // archive the user action plan
    UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
    if (userActionPlan != null) {
      archive(userActionPlan, actionPlanFactory::deleteUserActionPlan);
      Map<String, ActionPlanProgress> actionPlanProgressMap = userActionPlan
          .getActionPlanProgressMap();
      if (!CollectionUtils.isEmpty(actionPlanProgressMap)) {
        for (String actionPlanId : actionPlanProgressMap.keySet()) {
          CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
              actionPlanId, companyId);
          capSettings.removeMember(userId);
          actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
          // removing from the public channels
          publicChannelHelper.followUnfollowPubliChannelByParent(user, false, actionPlanId);
        }
      }
    }
    
    // archive the pulse assessment
    List<PulseAssessment> pulseAssessments = pulseRepository.getAllPulseAssessmentsForUser(user
        .getId());
    pulseAssessments.forEach(pa -> archive(pa, pulseRepository::delete));
    
    // archive the user todo requests
    UserTodoRequests userTodoRequests = todoFactory.getUserTodoRequests(user);
    archive(userTodoRequests, todoFactory::deleteUserTodoRequests);
    
    // user group discussions
    try {
      UserGroupDiscussion userGroupDiscussion = groupDiscussionFactory.getUserGroupDiscussion(user);
      if (userGroupDiscussion != null) {
        final LinkedList<String> groupDiscussionIds = userGroupDiscussion.getGroupDiscussionIds();
        if (!CollectionUtils.isEmpty(groupDiscussionIds)) {
          new ArrayList<String>(groupDiscussionIds).forEach(gdId -> groupDiscussionHelper
              .leaveGroupDiscussion(user, gdId));
        }
      }
      groupDiscussionFactory.removeUserGroupDiscussion(userGroupDiscussion);
    } catch (Exception e) {
      log.warn("Unable to delete.", e);
    }
    
    // user notes
    UserNoteDao userNote = notesFactory.getUserNote(user);
    if (userNote != null) {
      archive(userNote, notesFactory::deleteNotes);
    }
    
    // remove the user from the goals public channel
    if (!StringUtils.isEmpty(user.getUserGoalId())) {
      UserGoalDao userGoal = goalsFactory.getUserGoal(user.getUserGoalId(), user.getUserLocale());
      if (userGoal != null) {
        List<UserGoalProgressDao> goalsProgressList = userGoal.getSelectedGoalsProgressList();
        if (!CollectionUtils.isEmpty(goalsProgressList)) {
          goalsProgressList.forEach(goal -> publicChannelHelper.removeUser(user, goal.getGoal()
              .getId()));
          
        }
      }
    }
    
    Account account = accountRepository.findAccountByCompanyId(companyId);
    if (account != null && account.getPlan(SPPlanType.IntelligentHiring) != null) {
      HiringUser hiringUser = hiringUserFactory.getUserByEmail(user.getEmail(), companyId);
      if (hiringUser != null) {
        hiringUser.setInErti(false);
        hiringUserFactory.updateUser(hiringUser);
      } else {
        HiringUserArchive hiringUserArchive = hiringUserFactory.getArchivedUserByEmail(
            user.getEmail(), companyId);
        if (hiringUserArchive != null) {
          hiringUserArchive.setInErti(false);
          hiringUserFactory.updateUser(hiringUserArchive);
        }
      }
    }
    
    // removing user from existing competency evaluations
    competencyFactory.removeUserFromCompetency(user);
    
    UserCompetency userCompetency = competencyFactory.getUserCompetency(userId);
    Optional.ofNullable(userCompetency).ifPresent(uc -> archive(uc, competencyFactory::delete));
    
    // removing user competency evaluation details
    List<UserCompetencyEvaluationDetails> competencyEvaluationDetails = competencyFactory
        .getCompetencyEvaluationDetailsForUser(userId);
    competencyEvaluationDetails.forEach(ced -> archive(ced, competencyFactory::delete));
  }
}
