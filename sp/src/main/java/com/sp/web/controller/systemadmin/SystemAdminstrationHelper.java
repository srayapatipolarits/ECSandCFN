package com.sp.web.controller.systemadmin;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityFactory;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.PersonalityTypeMapper;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.processing.AssessmentProgressStoreRepoistory;
import com.sp.web.assessment.processing.ConflictManagementComparator;
import com.sp.web.assessment.questions.AssessmentType;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.controller.competency.CompetencyFactory;
import com.sp.web.controller.i18n.MessagesFactory;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.controller.profile.ProfileControllerHelper;
import com.sp.web.controller.pulse.WorkspacePulseFactory;
import com.sp.web.controller.systemadmin.media.MediaHelper;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.competency.CompetencyDao;
import com.sp.web.dao.competency.CompetencyProfileDao;
import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.dto.CompanyDTO;
import com.sp.web.dto.ReadabilityDTO;
import com.sp.web.dto.SystemAdminUserDTO;
import com.sp.web.dto.competency.CompetencyProfileSummaryDTO;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.PromotionsValidationException;
import com.sp.web.exception.SPException;
import com.sp.web.form.Operation;
import com.sp.web.form.SystemAdminForm;
import com.sp.web.model.Account;
import com.sp.web.model.AccountType;
import com.sp.web.model.BookMarkTracking;
import com.sp.web.model.Company;
import com.sp.web.model.CreditNotePaymentInstrument;
import com.sp.web.model.FeedbackRequest;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.FeedbackUserArchive;
import com.sp.web.model.Gender;
import com.sp.web.model.HiringUser;
import com.sp.web.model.Password;
import com.sp.web.model.PaymentInstrument;
import com.sp.web.model.PaymentType;
import com.sp.web.model.Product;
import com.sp.web.model.ProductType;
import com.sp.web.model.ProductValidityType;
import com.sp.web.model.Promotion;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.SPMedia;
import com.sp.web.model.SPMediaType;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.model.account.BillingCycle;
import com.sp.web.model.account.BillingCycleType;
import com.sp.web.model.account.PlanStatus;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.article.Article;
import com.sp.web.model.article.ArticleStatus;
import com.sp.web.model.assessment.AssessmentProgressTracker;
import com.sp.web.model.audit.AuditLogBean;
import com.sp.web.model.badge.BadgeType;
import com.sp.web.model.badge.UserBadgeActivity;
import com.sp.web.model.badge.UserBadgeProgress;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.competency.BaseCompetencyEvaluationScore;
import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.CompetencyEvaluationByProfile;
import com.sp.web.model.competency.CompetencyProfile;
import com.sp.web.model.competency.PeerCompetencyEvaluationScore;
import com.sp.web.model.competency.RatingConfiguration;
import com.sp.web.model.competency.RatingConfigurationType;
import com.sp.web.model.competency.UserCompetency;
import com.sp.web.model.competency.UserCompetencyEvaluation;
import com.sp.web.model.competency.UserCompetencyEvaluationDetails;
import com.sp.web.model.competency.UserCompetencyEvaluationScore;
import com.sp.web.model.competency.UserEvaluationResult;
import com.sp.web.model.feed.DashboardMessage;
import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.ActionPlanProgress;
import com.sp.web.model.goal.ActionPlanType;
import com.sp.web.model.goal.CompanyActionPlanSettings;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.DevelopmentStrategy;
import com.sp.web.model.goal.GoalSource;
import com.sp.web.model.goal.GoalSourceType;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.GroupPermissions;
import com.sp.web.model.goal.PersonalityPracticeArea;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.StepType;
import com.sp.web.model.goal.UserActionPlan;
import com.sp.web.model.goal.UserArticleProgress;
import com.sp.web.model.goal.UserGoal;
import com.sp.web.model.goal.UserGoalProgress;
import com.sp.web.model.hiring.group.HiringGroup;
import com.sp.web.model.hiring.user.HiringUserArchive;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.pubchannel.PublicChannel;
import com.sp.web.model.pulse.PulseAssessment;
import com.sp.web.model.pulse.PulseQuestionBean;
import com.sp.web.model.pulse.PulseQuestionSet;
import com.sp.web.model.pulse.PulseRequest;
import com.sp.web.model.pulse.PulseResults;
import com.sp.web.model.pulse.PulseSelection;
import com.sp.web.model.pulse.QuestionOptions;
import com.sp.web.model.readability.ReadabilityScore;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.model.tracking.ActivityTracking;
import com.sp.web.model.tutorial.TutorialActivityData;
import com.sp.web.model.tutorial.UserTutorialActivity;
import com.sp.web.model.usertracking.UserActivityTracking;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.AccountFactory;
import com.sp.web.product.CompanyFactory;
import com.sp.web.product.ProductHelper;
import com.sp.web.product.ProductRepository;
import com.sp.web.promotions.PromotionsRepository;
import com.sp.web.repository.FeedbackRepository;
import com.sp.web.repository.archive.ArchiveRepository;
import com.sp.web.repository.badge.BadgeRepository;
import com.sp.web.repository.feed.DashboardMessageRepository;
import com.sp.web.repository.goal.GoalsRepository;
import com.sp.web.repository.goal.UserGoalsRepository;
import com.sp.web.repository.hiring.HiringRepository;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.repository.library.TrackingRepository;
import com.sp.web.repository.media.MediaRepository;
import com.sp.web.repository.payment.PaymentInstrumentRepository;
import com.sp.web.repository.pulse.WorkspacePulseRepository;
import com.sp.web.repository.readability.ReadabilityRepository;
import com.sp.web.repository.team.GroupRepository;
import com.sp.web.repository.tracking.ActivityTrackingRepository;
import com.sp.web.repository.tracking.UserActivityTrackingRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.respository.systemadmin.SystemAdminRepository;
import com.sp.web.scheduler.AccountUpdationScheduler;
import com.sp.web.scheduler.InternalMarketingScheduler;
import com.sp.web.scheduler.NewsCredSchedular;
import com.sp.web.scheduler.competency.CompetencyScheduler;
import com.sp.web.scheduler.feed.CompanyActivityFeedScheduler;
import com.sp.web.scheduler.goal.ActionPlanScheduler;
import com.sp.web.service.badge.BadgeFactory;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.service.goals.GoalsAlgorithm;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.hiring.group.HiringGroupFactory;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.service.pc.PublicChannelFactory;
import com.sp.web.service.pc.PublicChannelHelper;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.service.translation.TranslationHandlerService;
import com.sp.web.service.tutorial.SPTutorialFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.DateTimeUtil;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletResponse;

/**
 * @author pradeepruhil
 *
 */
@Controller
// @PreAuthorize("hasRole('[SuperAdministrator]")
public class SystemAdminstrationHelper {
  
  /** Initializng the logger. */
  private static final Logger LOG = Logger.getLogger(SystemAdminstrationHelper.class);
  private static final ConflictManagementComparator comparator = new ConflictManagementComparator();
  
  private static final String TSV_DELIMITTER = "\t";
  private static final BigDecimal ONE_HUNDRED_PERCENT = BigDecimal.valueOf(100L);
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private HiringRepository hiringRepository;
  
  @Autowired
  private FeedbackRepository feedbackRepository;
  
  @Autowired
  private SystemAdminRepository adminRepository;
  
  @Autowired
  private SPGoalFactory goalsFactory;
  
  @Autowired
  private NewsCredSchedular credSchedular;
  
  @Autowired
  private MessagesFactory messagesFactory;
  
  @Autowired
  private ArticlesFactory articlesFactory;
  
  @Autowired
  private DBScriptGenerator dbScriptGenerator;
  
  @Autowired
  private SessionFactory sessionFactory;
  
  @Autowired
  private InternalMarketingScheduler internalMarketingScheduler;
  
  @Autowired
  private ReadabilityRepository readabilityRepository;
  
  @Autowired
  PersonalityFactory personalityFactory;
  
  @Autowired
  private Environment environment;
  
  @Autowired
  private GoalsRepository goalsRepository;
  
  @Autowired
  private UserGoalsRepository userGoalsRepository;
  
  @Autowired
  private GoalsAlgorithm algorithm;
  
  @Autowired
  private GroupRepository groupRepository;
  
  @Autowired
  private ProductRepository productRepository;
  
  @Autowired
  private PromotionsRepository promotionsRepository;
  
  @Autowired
  private AccountFactory accountFactory;
  
  @Autowired
  private PaymentInstrumentRepository instrumentRepository;
  
  @Autowired
  private AccountUpdationScheduler accountUpdation;
  
  @Autowired
  private ArchiveRepository archiveRepository;
  
  @Autowired
  private TrackingRepository trackingRepository;
  
  @Autowired
  private ActionPlanFactory actionPlanFactory;
  
  @Autowired
  private CompetencyScheduler competencyScheduler;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private CompanyActivityFeedScheduler activityFeedScheduler;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private ActionPlanScheduler actionPlanScheduler;
  
  @Autowired
  private CacheManager cacheManager;
  
  @Autowired
  private MediaRepository mediaRepository;
  
  @Autowired
  private MediaHelper mediaHelper;
  
  @Autowired
  private TodoFactory todoFactory;
  
  @Autowired
  private PublicChannelHelper channelHelper;
  
  @Autowired
  private DashboardMessageRepository dashboardMessageRepository;
  
  @Autowired
  private PublicChannelFactory channelFactory;
  
  @Autowired
  private CompetencyFactory competencyFactory;
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  ActivityTrackingRepository activityTrackingRepository;
  
  @Autowired
  private UserActivityTrackingRepository userATrackingRepository;
  
  @Autowired
  private TranslationHandlerService handlerService;
  
  @Autowired
  private SPTutorialFactory tutorialFactory;
  
  @Autowired
  @Lazy
  private BadgeFactory badgeFactory;
  
  @Autowired
  private AssessmentProgressStoreRepoistory storeRepository;
  
  @Autowired
  private BadgeRepository badgeRepository;
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @Autowired
  private HiringGroupFactory hiringGroupFactory;
  
  @Autowired
  private WorkspacePulseFactory pulseFactory;
  
  @Autowired
  private WorkspacePulseRepository pulseRepository;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  private NotificationsProcessor notificationProcessor;
  
  /**
   * <code>GetUer</code> method will return the user from the databse
   * 
   * @param user
   *          system admin user.
   * @param param
   *          logged inuser.
   * @return the user
   */
  public SPResponse getUser(User user, Object[] param) {
    
    SPResponse response = new SPResponse();
    
    SystemAdminForm systemAdminForm = (SystemAdminForm) param[0];
    
    LOG.info("SystemAdminForm type " + systemAdminForm);
    Optional.ofNullable(systemAdminForm)
        .orElseThrow(() -> new SPException("user type is not null"));
    UserType type = systemAdminForm.getUserType();
    switch (type) {
    case HiringCandidate:
      response.add("user",
          hiringRepository.findByEmail(systemAdminForm.getCompanyId(), systemAdminForm.getEmail()));
      break;
    case Feedback:
      Map<String, String> feedbackUser = new HashMap<>();
      feedbackUser.put("email", systemAdminForm.getEmail());
      feedbackUser.put("feedbackFor", systemAdminForm.getFeedbackFor());
      response.add("feedbackUser", adminRepository.getUserFromType(feedbackUser, type));
      break;
    case Member:
      response.add("user", userRepository.findByEmail(systemAdminForm.getEmail()));
    default:
      break;
    }
    return response;
  }
  
  public SPResponse updateUserSingleProperty(User user, Object[] param) {
    SPResponse response = new SPResponse();
    String propertyName = (String) param[0];
    String propertyValue = (String) param[1];
    UserType userType = (UserType) param[2];
    String id = (String) param[3];
    
    Map<String, String> map = new HashMap<>();
    map.put(propertyName, propertyValue);
    adminRepository.updateUserProperty(userType, map, id);
    response.isSuccess();
    return response;
  }
  
  public SPResponse updateUserPerosonality(User user, Object[] param) {
    SPResponse response = new SPResponse();
    String personalityTypeStr = (String) param[0];
    String id = (String) param[1];
    UserType userType = (UserType) param[2];
    
    PersonalityType personalityType = PersonalityType.valueOf(personalityTypeStr);
    adminRepository.updateUserPersonality(userType, personalityType, id);
    response.isSuccess();
    return response;
  }
  
  public SPResponse addFeedbackUserGoals(User user, Object[] parma) {
    
    SPResponse response = new SPResponse();
    String email = (String) parma[0];
    Map<String, String> mpa = new HashMap<String, String>();
    String feedbackFor = (String) parma[1];
    
    /* Get the id for feedback user */
    User feedbackForUser = feedbackRepository.findFeedbackUserByFor(email, feedbackFor);
    /* check if feedbackForUser Goals present */
    if (feedbackForUser.getUserGoalId() == null) {
      goalsFactory.addGoalsForUser(feedbackForUser);
      userRepository.updateUser(feedbackForUser);
    }
    mpa.put("email", email);
    mpa.put("feedbackFor", feedbackForUser.getId());
    // UserGoal addOwnGoals = factory.addOwnGoals(feedbackUser);
    // feedbackUser.setGoals(addOwnGoals);
    
    FeedbackUser fbUser = (FeedbackUser) adminRepository.getUserFromType(mpa, UserType.Feedback);
    if (fbUser.getUserGoalId() == null) {
      goalsFactory.addGoalsForUser(fbUser);
      userRepository.updateUser(fbUser);
    }
    goalsFactory.addFeedbackUserGoal(fbUser, feedbackForUser);
    return response;
  }
  
  /**
   * Add the feedback goals to all the users.
   * 
   * @param user
   *          logged in users
   * @return the response.
   */
  public SPResponse addFeedbackUserGoalsForAllUsers(User user, Object[] param) {
    
    SPResponse response = new SPResponse();
    
    String email = (String) param[0];
    List<User> alllUsers = null;
    alllUsers = new ArrayList<>();
    User emailUser = userRepository.findByEmail(email);
    if (emailUser != null) {
      alllUsers.add(emailUser);
    }
    user.setUserGoalId(null);
    goalsFactory.addGoalsForUser(emailUser);
    
    alllUsers.stream().forEach(
        usr -> {
          
          List<FeedbackUser> feedbackUsers = feedbackRepository.getAllFeedbackUser(usr);
          feedbackUsers.stream().filter(fb -> fb.getUserStatus() == UserStatus.VALID)
              .forEach(fbUser -> {
                goalsFactory.addFeedbackUserGoal(fbUser, usr);
              });
          
          List<FeedbackUserArchive> allFeedbackUserArchive = feedbackRepository
              .getAllFeedbackUserArchive(user);
          allFeedbackUserArchive.stream().filter(fb -> fb.getUserStatus() == UserStatus.VALID)
              .forEach(fbar -> {
                goalsFactory.addFeedbackUserGoal(fbar, usr);
              });
          
        });
    
    return response.isSuccess();
  }
  
  /**
   * Reloads the news cred articles.
   * 
   * @param user
   *          - logged in user
   * @return the response
   */
  public SPResponse syncArticleFromNewsCred(User user) {
    LOG.info("News Cred sysn run by user :" + user.getFirstName());
    try {
      credSchedular.processSync();
    } catch (Exception e) {
      throw new SPException("Unable to load articles from News Cred, Please check.", e);
    }
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to remove the duplicate articles.
   * 
   * @param user
   *          - user
   * @return the response to the remove request
   */
  public SPResponse removeDuplicateArticles(User user) {
    LOG.info("News Cred sysn run by user :" + user.getFirstName());
    try {
      goalsFactory.removeDuplicateArticles();
      articlesFactory.resetAllCache();
    } catch (Exception e) {
      LOG.warn("Unable to remove duplicate articles.", e);
      throw new SPException("Unable to remove duplicate articles.", e);
    }
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to reset cache and reload all the messages.
   * 
   * @param user
   *          - logged in user
   * @return the response to the reset cache
   */
  public SPResponse messagesReload(User user) {
    SPResponse resp = new SPResponse();
    messagesFactory.resetCache();
    return resp.isSuccess();
  }
  
  /**
   * Get the db script for the company to migrate.
   * 
   * @param user
   *          - user
   * @param params
   *          - parameters
   * @return the response to the get request
   */
  @SuppressWarnings("unchecked")
  public SPResponse getCompany(User user, Object[] params) {
    SPResponse resp = new SPResponse();
    String companyId = (String) params[0];
    List<String> products = (List<String>) params[1];
    String scriptForCompany = dbScriptGenerator.getScriptForCompany(companyId, products);
    resp.add("dbScript", scriptForCompany);
    return resp;
  }
  
  /**
   * Get the db script for the user to migrate.
   * 
   * @param user
   *          - user
   * @param params
   *          - parameters
   * @return the response to the get request
   */
  @SuppressWarnings("unchecked")
  public SPResponse getDBUser(User user, Object[] params) {
    SPResponse resp = new SPResponse();
    String email = (String) params[0];
    List<String> products = (List<String>) params[1];
    String scriptForUser = dbScriptGenerator.getScriptForUser(email, products);
    resp.add("dbScript", scriptForUser);
    return resp;
  }
  
  /**
   * Get all the logged in users in the system.
   * 
   * @param user
   *          - current user
   * @return the response to the get request
   */
  public SPResponse getLoggedInUsers(User user) {
    SPResponse resp = new SPResponse();
    resp.add("loggedInUsers", sessionFactory.getLoggedInUsers());
    return resp;
  }
  
  /**
   * Get all the logged in users in the system.
   * 
   * @param user
   *          - current user
   * @return the response to the get request
   */
  public SPResponse getUserList(User user) {
    SPResponse resp = new SPResponse();
    List<User> findAllMembers = userRepository.findAllMembers(true);
    resp.add("users", findAllMembers.stream().map(u -> new SystemAdminUserDTO(u, companyFactory))
        .collect(Collectors.toList()));
    return resp;
  }
  
  /**
   * Get all the logged in users in the system.
   * 
   * @param user
   *          - current user
   * @return the response to the get request
   */
  public SPResponse getCompanyList(User user) {
    SPResponse resp = new SPResponse();
    List<Company> findAllCompanies = accountRepository.findAllCompanies();
    resp.add("companies",
        findAllCompanies.stream().map(CompanyDTO::new).collect(Collectors.toList()));
    return resp;
  }
  
  /**
   * Get all the logged in users in the system.
   * 
   * @param user
   *          - current user
   * @return the response to the get request
   */
  public SPResponse getSurePeopleCompanies(User user) {
    SPResponse resp = new SPResponse();
    String surePeopleCompanies = environment.getProperty(Constants.PARAM_SUREPEOPLE_COMPANIES_ID);
    if (StringUtils.isBlank(surePeopleCompanies)) {
      throw new SPException("No Surepeople companies configured in properties file. Please check.");
    }
    String[] companyArray = surePeopleCompanies.split(",");
    List<String> companiesList = Arrays.asList(companyArray);
    List<Company> findAllCompanies = accountRepository
        .findCompanyById(new HashSet<>(companiesList));
    resp.add("companies",
        findAllCompanies.stream().map(CompanyDTO::new).collect(Collectors.toList()));
    return resp;
  }
  
  /**
   * Helper method to fix the user profile.
   * 
   * @param user
   *          - user logged in
   * @param params
   *          - params
   * @return the response to the fix request
   */
  public SPResponse fixUserProfile(User user, Object[] params) {
    SPResponse resp = new SPResponse();
    String email = (String) params[0];
    
    User userToFix = userRepository.findByEmailValidated(email);
    
    boolean doSave = personalityFactory.fixPersonality(userToFix);
    
    if (doSave) {
      userRepository.updateUserAnalysis(userToFix);
    }
    return resp.isSuccess();
  }
  
  /**
   * <code>sendMarketingEmail</code> methdo will send the marketing email to the specific user.
   * 
   * @param user
   *          logged in user.
   * @param params
   *          contain sthe list of users for which marketing emails to be sent.
   * @return the response whether success for failure.
   */
  public SPResponse sendMarketingEmail(User user, Object[] params) {
    
    @SuppressWarnings("unchecked")
    List<String> usersEmail = (List<String>) params[0];
    
    boolean isAllUsers = (boolean) params[1];
    
    List<User> allUsers;
    if (isAllUsers) {
      allUsers = userRepository.findAllMembers(false);
    } else {
      allUsers = userRepository.findByEmail(usersEmail);
    }
    internalMarketingScheduler.sendMarketingEmail(allUsers);
    
    SPResponse response = new SPResponse();
    response.isSuccess();
    return response;
  }
  
  /**
   * Helper method to retreive the Average Readability score of all articles
   * 
   * @return the response to the get request
   */
  public SPResponse getReadabilityAverageScore(User user) {
    LOG.info("Get Readability score run by :" + user.getFirstName());
    SPResponse response = new SPResponse();
    try {
      // ID is hardcoded as it is for internal use only.
      ReadabilityScore score = readabilityRepository.getScoreById("zxy12345abc");
      ReadabilityDTO readDto = new ReadabilityDTO();
      if (score != null) {
        readDto.setCurrentScore(score.getCurrentScore());
        readDto.setCalculatedOn(score.getCalculatedOn());
      }
      
      response.add("readability", readDto);
      
    } catch (Exception e) {
      LOG.warn("Unable to get Average Readability score for all articles", e);
      throw new SPException("Unable to get Average Readability score for all articles", e);
    }
    
    return response;
  }
  
  /**
   * <code>initializePracticeArea</code> method will initialzie the practice areas.
   * 
   * @param user
   *          logged in user
   * @param param
   *          contains the parameters.
   * @return the response success or failures.
   */
  public SPResponse initializePracticeArea(User user, Object[] param) {
    
    SPResponse response = new SPResponse();
    
    PersonalityType personalityType = (PersonalityType) param[0];
    if (personalityType == null) {
      
      /* intialize all the rpactive areas from the beginining */
      
      PersonalityType[] values = PersonalityType.values();
      
      for (PersonalityType pt : values) {
        
        String goals = environment.getProperty("practiceArea." + pt.toString());
        LOG.info("Practice Area" + pt + ", goa" + goals);
        if (goals == null) {
          continue;
        }
        String[] goalsNames = goals.split(",");
        List<String> goalsListName = new ArrayList<String>();
        for (String goalName : goalsNames) {
          if (goalName.contains("''")) {
            goalName = goalName.replace("''", "'");
          }
          goalsListName.add(goalName);
        }
        PersonalityPracticeArea personalityPracticeArea = goalsRepository
            .findPersonalityPracticeArea(pt.toString());
        if (personalityPracticeArea == null) {
          personalityPracticeArea = new PersonalityPracticeArea();
          personalityPracticeArea.setPersonalityType(pt);
        }
        List<String> goalids = goalsListName.stream().map(gl -> {
          SPGoal spGoal = goalsRepository.findGoalByName(gl);
          LOG.info("spGoal" + spGoal + ", gl " + gl);
          return spGoal;
        }).map(spG -> spG.getId()).collect(Collectors.toList());
        
        personalityPracticeArea.setGoalIds(goalids);
        goalsRepository.updatePersonalityPracticeArea(personalityPracticeArea);
      }
      
    }
    
    response.isSuccess();
    return response;
  }
  
  /**
   * updateUGP for existing users.
   * 
   * @param user2
   * @return
   */
  public SPResponse updateUGPforExistingUsers(User user2) {
    
    List<UserGoal> allUserGoals = userGoalsRepository.getAllUserGoals();
    
    allUserGoals.stream().forEach(
        ug -> {
          List<UserGoalProgress> goalProgress = ug.getGoalProgress();
          List<UserArticleProgress> articleProgress = ug.getArticleProgress();
          
          User goalUser = userRepository.findUserByGoalId(ug.getId());
          if (goalUser != null) {
            
            if (goalProgress.isEmpty()) {
              List<UserGoalProgress> userGoalsProgress = goalsFactory.getUserGoalsProgress(
                  goalUser, false);
              
              ug.setGoalProgress(userGoalsProgress);
              goalProgress.addAll(userGoalsProgress);
            }
            
            PersonalityType primaryPersonality = goalUser.getAnalysis().getPersonality()
                .get(RangeType.Primary).getPersonalityType();
            
            PersonalityPracticeArea personalityPractice = goalsRepository
                .findPersonalityPracticeArea(primaryPersonality.toString());
            PersonalityType underPressure = goalUser.getAnalysis().getPersonality()
                .get(RangeType.UnderPressure).getPersonalityType();
            PersonalityPracticeArea underPressurePracticeArea = goalsRepository
                .findPersonalityPracticeArea(underPressure.toString());
            
            goalProgress.stream().forEach(
                userGoalProgress -> {
                  userGoalProgress.setAllGoalWeight(0);
                  userGoalProgress.setPrismLensWeight(0);
                  userGoalProgress.setPrismWeight(0);
                  userGoalProgress.getSourceList().clear();
                  SPGoal goal = goalsFactory.getGoal(userGoalProgress.getGoalId());
                  
                  userGoalProgress.setDevelopmentStrategyLists(IntStream
                      .range(0, goal.getDevelopmentStrategyList().size()).mapToObj(i -> i)
                      .collect(Collectors.toList()));
                  
                  if (personalityPractice != null) {
                    List<String> goalIds = personalityPractice.getGoalIds();
                    int weight = goalIds.indexOf(userGoalProgress.getGoalId());
                    if (weight > -1) {
                      /* Goal is part of primary persnaltiy. Add the weight for the same */
                      GoalSource goalSource = new GoalSource();
                      goalSource.setGoalSourceType(GoalSourceType.Prism);
                      goalSource.setValue(GoalSourceType.PrismPrimary.toString());
                      userGoalProgress.getSourceList().add(goalSource);
                      userGoalProgress.addToAllWeight(weight);
                      userGoalProgress.addToPrismWeight(weight);
                    }
                  }
                  
                  if (underPressurePracticeArea != null) {
                    List<String> goalIdsUnderPressure = underPressurePracticeArea.getGoalIds();
                    int weightUnderPressure = goalIdsUnderPressure.indexOf(userGoalProgress
                        .getGoalId());
                    if (weightUnderPressure > -1) {
                      /* Goal is part of primary persnaltiy. Add the weight for the same */
                      GoalSource goalSource = new GoalSource();
                      goalSource.setGoalSourceType(GoalSourceType.Prism);
                      goalSource.setValue(GoalSourceType.PrismUnderPressure.toString());
                      userGoalProgress.getSourceList().add(goalSource);
                      userGoalProgress.addToAllWeight(weightUnderPressure);
                      userGoalProgress.addToPrismWeight(weightUnderPressure);
                    }
                  }
                  goal.getMandatoryArticles().stream().map(UserArticleProgress::new)
                      .filter(uap -> !articleProgress.contains(uap))
                      .forEach(ug::addArticleProgress);
                });
            
            /* find the feedback users for the this user */
            List<FeedbackUser> allFeedbackuUsers = feedbackRepository.getAllFeedbackUser(goalUser);
            /*
             * Iterate through all the feedback users and add the prims weight for the feedback goal
             * and
             */
            allFeedbackuUsers.stream().forEach(fbuser -> {
              String userGoalId = fbuser.getUserGoalId();
              UserGoal feedbackUserGoalds = userGoalsRepository.findById(userGoalId);
              if (feedbackUserGoalds != null) {
                List<UserGoalProgress> feedbackGoalPgoress = feedbackUserGoalds.getGoalProgress();
                /* check if the ugp contains all the goals */
                feedbackGoalPgoress.stream().forEach(fbUgp -> {
                  for (UserGoalProgress mainUgp : goalProgress) {
                    if (fbUgp.getGoalId().equalsIgnoreCase(mainUgp.getGoalId())) {
                      GoalSource goalSource = new GoalSource(GoalSourceType.PrismLens);
                      goalSource.setValue(fbuser.getFirstName() + " " + fbuser.getLastName());
                      mainUgp.getSourceList().add(goalSource);
                      mainUgp.addToAllWeight(1);
                      mainUgp.addToPrismLensWeight(1);
                      
                    }
                  }
                  
                });
              }
              
            });
            
            List<FeedbackUserArchive> allFeedbackUserArchive = feedbackRepository
                .getAllFeedbackUserArchive(goalUser);
            /*
             * Iterate through all the feedback users and add the prims weight for the feedback goal
             * and
             */
            allFeedbackUserArchive.stream().forEach(fbuser -> {
              String userGoalId = fbuser.getUserGoalId();
              UserGoal feedbackUserGoalds = userGoalsRepository.findById(userGoalId);
              if (feedbackUserGoalds != null) {
                List<UserGoalProgress> feedbackGoalPgoress = feedbackUserGoalds.getGoalProgress();
                /* check if the ugp contains all the goals */
                feedbackGoalPgoress.stream().forEach(fbUgp -> {
                  for (UserGoalProgress mainUgp : goalProgress) {
                    if (fbUgp.getGoalId().equalsIgnoreCase(mainUgp.getGoalId())) {
                      GoalSource goalSource = new GoalSource(GoalSourceType.PrismLens);
                      goalSource.setValue(fbuser.getFirstName() + " " + fbuser.getLastName());
                      mainUgp.getSourceList().add(goalSource);
                      mainUgp.addToAllWeight(1);
                      mainUgp.addToPrismLensWeight(1);
                      
                    }
                  }
                  
                });
              }
              
            });
            List<UserGoalProgress> sortedUgp = algorithm.sortedUserGoalProgressList(goalProgress);
            /* assign the order fo the first six */
            int order = 0;
            for (UserGoalProgress userGoalProgress : sortedUgp) {
              if (order < 6) {
                userGoalProgress.setOrderIndex(order + 1);
              } else {
                break;
              }
              order = order + 1;
            }
            ug.setGoalProgress(sortedUgp);
            userGoalsRepository.save(ug);
          }
          
        });
    return new SPResponse().isSuccess();
  }
  
  public SPResponse addDummyDS(User user) {
    
    List<SPGoal> allGoals = goalsRepository.findAllGoals();
    allGoals.stream()
        .forEach(
            goal -> {
              /* Create three development Strategy */
              goal.getDevelopmentStrategyList().clear();
              for (int i = 0; i < 3; i++) {
                DevelopmentStrategy developmentStrategy = new DevelopmentStrategy();
                developmentStrategy.setId(String.valueOf(i));
                developmentStrategy.setDsDescription("Lorem Ipsum, Lorem Impsum ->"
                    + goal.getName() + ", ds no: " + i);
                developmentStrategy.setActive(true);
                goal.getDevelopmentStrategyList().add(developmentStrategy);
              }
              DevelopmentStrategy inactive = new DevelopmentStrategy();
              inactive.setId("3");
              inactive.setDsDescription("Lorem Ipsum, Lorem Impsum ->" + goal.getName()
                  + ", Inactive");
              inactive.setActive(false);
              goal.getDevelopmentStrategyList().add(inactive);
              goalsRepository.updateGoal(goal);
            });
    SPResponse response = new SPResponse();
    return response.isSuccess();
  }
  
  public SPResponse updateArticleProgressForExitingUser(User user) {
    List<UserGoal> allUserGoals = userGoalsRepository.getAllUserGoals();
    
    allUserGoals.stream().forEach(
        ug -> {
          List<UserGoalProgress> goalProgress = ug.getGoalProgress();
          List<UserArticleProgress> articleProgress = ug.getArticleProgress();
          articleProgress.clear();
          User goalUser = userRepository.findUserByGoalId(ug.getId());
          if (goalUser != null) {
            
            if (!goalProgress.isEmpty()) {
              for (UserGoalProgress ugp1 : goalProgress) {
                SPGoal validatedGoal = goalsRepository.findById(ugp1.getGoalId());
                validatedGoal.getMandatoryArticles().stream().map(UserArticleProgress::new)
                    .filter(uap -> !articleProgress.contains(uap)).forEach(ug::addArticleProgress);
              }
            }
          }
        });
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to change the personality profile for the given feedback user.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the update request
   */
  public SPResponse updatePrismLens(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    String feedbackUserId = (String) params[0];
    PersonalityTypeMapper primaryPersonality = (PersonalityTypeMapper) params[1];
    
    // check for the feedback user
    FeedbackUser fbUser = userRepository.findFeedbackUser(feedbackUserId);
    Assert.notNull(fbUser, "Feedback User not found.");
    
    // get the user analysis to update
    AnalysisBean analysis = fbUser.getAnalysis();
    Assert.notNull(analysis, "User has not completed assessment.");
    
    // update the primary personality
    PersonalityBeanResponse personalityBeanResponse = analysis.getPersonality().get(
        RangeType.Primary);
    personalityBeanResponse.setPersonalityType(primaryPersonality.getMapsTo());
    
    userRepository.updateGenericUser(fbUser);
    
    return resp.isSuccess();
  }
  
  // public SPResponse updateUserGroupAssociation(User user) {
  // final SPResponse resp = new SPResponse();
  //
  // List<Company> findAllCompanies = accountRepository.findAllCompanies();
  // for (Company company : findAllCompanies) {
  // final String companyId = company.getId();
  // List<UserGroup> groupList = groupRepository.findAllGroups(companyId);
  // Map<String, String> groupNameIdMap = groupList.stream().collect(
  // Collectors.toMap(UserGroup::getName, UserGroup::getId));
  // // get the users in the company
  // List<User> allMembersForCompany = accountRepository.getAllMembersForCompany(companyId);
  // allMembersForCompany.forEach(u -> updateGroupAssociation(u, groupNameIdMap));
  // }
  // return resp.isSuccess();
  // }
  //
  // private void updateGroupAssociation(User user, Map<String, String> groupNameIdMap) {
  // if (user.getGroupAssociationList().size() > 0) {
  // for (GroupAssociation ga : user.getGroupAssociationList()) {
  // final String groupId = groupNameIdMap.get(ga.getName());
  // if (StringUtils.isEmpty(groupId)) {
  // throw new InvalidRequestException("Group not found :" + ga.getName() + ": Company :"
  // + user.getCompanyId() + ": user :" + user.getEmail());
  // }
  // ga.setGroupId(groupId);
  // }
  // userRepository.updateUser(user);
  // }
  // }
  
  @SuppressWarnings("deprecation")
  public SPResponse migrateAccount(User user) {
    SPResponse spResponse = new SPResponse();
    List<Company> companies = accountRepository.findAllCompanies();
    
    for (Company comp : companies) {
      
      // if (!CollectionUtils.isEmpty(comp.getFeatureList())) {
      // continue;
      // }
      try {
        Account account = accountRepository.findValidatedAccountByAccountId(comp.getAccountId());
        
        account.getSpPlanMap().forEach((planType, plan) -> {
          if (plan != null) {
            if (plan.getAggreementEndDate() == null) {
              Date aggreementEndDate = account.getAggreementEndDate();
              if (aggreementEndDate != null) {
                LocalDateTime localDateTime = DateTimeUtil.getLocalDateTime(aggreementEndDate);
                plan.setAggreementEndDate(localDateTime);
              }
              
            }
            
            if (plan.getAgreementTerm() == 0) {
              plan.setAgreementTerm(plan.getAggrementTerm());
            }
            
            if (plan.getAuthorizedNetProfileId() == null) {
              plan.setAuthorizedNetProfileId(account.getAuthorizedNetProfileId());
            }
            
            if (plan.getBillingCycle() == null) {
              plan.setBillingCycle(account.getBillingCycle());
            }
            
            if (plan.getBillingCycleStartDate() == null) {
              plan.setBillingCycleStartDate(account.getBillingCycleStartDate());
            }
            
            plan.setCreditBalance(account.getCreditBalance());
            plan.setCustomerProfileId(account.getCustomerProfileId());
            plan.setDeactivated(account.isDeactivated());
            plan.setExpiresTime(account.getExpiresTime());
            if (account.getLastPaymentId() != null) {
              plan.setLastPaymentId(account.getLastPaymentId());
            }
            plan.setNextChargeAmount(account.getNextChargeAmount());
            plan.setNextPaymentDate(account.getNextPaymentDate());
            plan.setPaymentInstrumentId(account.getPaymentInstrumentId());
            plan.setPaymentType(account.getPaymentType());
            switch (account.getStatus()) {
            case VALID:
              plan.setPlanStatus(PlanStatus.ACTIVE);
              break;
            case EXPIRED:
              plan.setPlanStatus(PlanStatus.EXPIRED);
              break;
            case BLOCKED:
              plan.setPlanStatus(PlanStatus.INACTIVE);
              break;
            case CANCEL:
              plan.setPlanStatus(PlanStatus.CANCEL);
              break;
            case NEW:
              plan.setPlanStatus(PlanStatus.NEW);
              break;
            case RENEWAL_PAYMENT_FAILED:
              plan.setPlanStatus(PlanStatus.RENEWAL_PAYMENT_FAILED);
              break;
            case SUSPENDED:
              plan.setPlanStatus(PlanStatus.INACTIVE);
              break;
            case TRIAL:
              plan.setPlanStatus(PlanStatus.ACTIVE);
              break;
            
            default:
              break;
            }
            if (plan.getPlanType() == SPPlanType.IntelligentHiring) {
              comp.addFeature(SPFeature.Hiring);
              comp.setSharePortrait(true);
              companyFactory.updateCompany(comp);
              
            }
            
            plan.setPreviousPaymentInstrumentId(account.getPreviousPaymentInstrumentId());
            plan.setReferSource(account.getReferSource());
            plan.setTagsKeywords(account.getTagsKeywords());
          }
          
        });
        accountRepository.updateAccount(account);
        // accountRepository.updateCompany(comp);
      } catch (Exception e) {
        LOG.error("Account Not Migrated  for company :" + comp.getName(), e);
        spResponse.add(comp.getName(), e.getMessage());
        
      }
      
    }
    
    return spResponse.isSuccess();
  }
  
  /**
   * migrate individual accounts will migrate the individual accounts in the system.
   */
  public SPResponse migrateIndvidualAccounts(User user) {
    
    // Import the inidividual accounts */
    SPResponse spResponse = new SPResponse();
    List<Account> individualsAccounts = accountRepository
        .getAllAccountForAccountType(AccountType.Individual);
    
    for (Account account : individualsAccounts) {
      
      if (account.getPaymentType() == PaymentType.CREDIT_CARD || account.getPaymentType() == null) {
        account.setPreviousPaymentInstrumentId(account.getPaymentInstrumentId());
        
        CreditNotePaymentInstrument creditNotePI = new CreditNotePaymentInstrument();
        creditNotePI.setPaymentType(PaymentType.WIRE);
        creditNotePI.setCreditBalance(0);
        creditNotePI.setFinancialReferenceNo("");
        creditNotePI.setComment("Old Account migrated");
        accountRepository.createPaymentInstrument(creditNotePI);
        
        account.setPaymentInstrumentId(creditNotePI.getId());
        account.setPaymentType(PaymentType.WIRE);
      }
      /* create the primry plan */
      if (account.getSpPlanMap().isEmpty()) {
        
        try {
          BillingCycle billingCycle = new BillingCycle();
          List<String> productList = account.getProducts();
          Set<SPFeature> features = new HashSet<>();
          features.add(SPFeature.Prism);
          features.add(SPFeature.PrismLens);
          SPPlan primaryPlan = updatePrimaryPlan(account, billingCycle, productList, features);
          
          account.getSpPlanMap().put(SPPlanType.Primary, primaryPlan);
          
          account.setBillingCycle(billingCycle);
          account.setAccountNumber(accountRepository.getNextAccountNumber());
          
        } catch (Exception e) {
          LOG.error("Account Not Migrated  for account :" + account.getId(), e);
          spResponse.add(account.getId(), e.getMessage());
          continue;
        }
      }
      accountRepository.updateAccount(account);
    }
    return new SPResponse().isSuccess();
  }
  
  /**
   * updatePrimaryPlan will update the primary plan.
   * 
   * @param comp
   *          for which primary plan is to be updated.
   * @param account
   *          of the user.
   * @param billingCycle
   *          of the account.
   * @param productList
   *          list of product list.
   * @param features
   *          features set.
   * @return the SPPLan.
   */
  private SPPlan updatePrimaryPlan(Company comp, Account account, BillingCycle billingCycle,
      List<String> productList, Set<SPFeature> features) {
    features.add(SPFeature.Prism);
    features.add(SPFeature.PrismLens);
    features.add(SPFeature.Pulse);
    features.add(SPFeature.Spectrum);
    features.add(SPFeature.OrganizationPlan);
    features.add(SPFeature.RelationShipAdvisor);
    SPPlan primaryPlan = updatePrimaryPlan(account, billingCycle, productList, features);
    primaryPlan.setName(comp.getName());
    return primaryPlan;
  }
  
  /**
   * updatePrimaryPlan will update the primary plan.
   * 
   * @param account
   *          of the user.
   * @param billingCycle
   *          of the account.
   * @param productList
   *          list of product list.
   * @param features
   *          features set.
   * @return the SPPLan.
   */
  private SPPlan updatePrimaryPlan(Account account, BillingCycle billingCycle,
      List<String> productList, Set<SPFeature> features) {
    SPPlan primaryPlan = new SPPlan();
    primaryPlan.setActive(true);
    if (account.getAggreementEndDate() == null) {
      DateTime dateTime = new DateTime(account.getStartDate());
      DateTime endDate = dateTime.plusDays(360);
      primaryPlan.setAggreementEndDate(DateTimeUtil.getLocalDateTime(endDate.toDate()));
    } else {
      primaryPlan
          .setAggreementEndDate(DateTimeUtil.getLocalDateTime(account.getAggreementEndDate()));
    }
    
    primaryPlan.setFeatures(features);
    primaryPlan.setLicensePrice(new BigDecimal(0));
    /* find all the members of the company have role of account administrator */
    primaryPlan.setNumAdmin(10);
    primaryPlan.setNumMember(account.getAvailableSubscriptions());
    primaryPlan.setOverrideAdminPrice(new BigDecimal(0));
    primaryPlan.setOverrideMemberPrice(new BigDecimal(0));
    primaryPlan.setPlanAdminNextChargeAmount(new BigDecimal(0));
    primaryPlan.setPlanMemberNextChargeAmount(new BigDecimal(account.getNextChargeAmount()));
    primaryPlan.setPlanStatus(PlanStatus.ACTIVE);
    primaryPlan.setPlanType(SPPlanType.Primary);
    primaryPlan.setUnitAdminPrice(new BigDecimal(0));
    if (account.getAgreementTerm() == 0) {
      primaryPlan.setAgreementTerm(1);
    } else {
      primaryPlan.setAgreementTerm(account.getAgreementTerm());
    }
    
    Optional<String> productOps = productList.stream().filter(pd -> {
      Product product = productRepository.findByIdValidated(pd);
      if (product != null) {
        if (product.getProductType() == ProductType.BUSINESS) {
          return true;
        } else {
          return false;
        }
      } else {
        return false;
      }
    }).findFirst();
    if (productOps.isPresent()) {
      LOG.debug(productOps);
      final String productId = productOps.get();
      
      Product product;
      product = productRepository.findByIdValidated(productId);
      
      ProductValidityType validityType = product.getValidityType();
      if (validityType == ProductValidityType.MONTHLY) {
        billingCycle.setBillingCycleType(BillingCycleType.Monthly);
      } else {
        billingCycle.setBillingCycleType(BillingCycleType.Anually);
      }
      Optional<String> findFirst = account.getPromotions().stream().findFirst();
      String promoitonId = null;
      if (findFirst.isPresent()) {
        promoitonId = findFirst.get();
      }
      Promotion promotion = getPromotion(account, promoitonId, product);
      
      // get the product helper
      ProductHelper productHelper = accountFactory.getProductHelper(product);
      Optional<Promotion> promotionOps = Optional.ofNullable(promotion);
      Double unitPrice = productHelper.getUnitPrice(account, product, promotionOps);
      primaryPlan.setUnitMemberPrice(new BigDecimal(unitPrice));
    }
    return primaryPlan;
  }
  
  /**
   * @param comp
   * @param account
   * @param productList
   * @return
   */
  private SPPlan addHiringPlan(Company comp, Account account, List<String> productList) {
    SPPlan intelligentHiring = new SPPlan();
    intelligentHiring.setActive(true);
    if (account.getAggreementEndDate() == null) {
      DateTime dateTime = new DateTime(account.getStartDate());
      DateTime endDate = dateTime.plusDays(360);
      intelligentHiring.setAggreementEndDate(DateTimeUtil.getLocalDateTime(endDate.toDate()));
    } else {
      intelligentHiring.setAggreementEndDate(DateTimeUtil.getLocalDateTime(account
          .getAggreementEndDate()));
    }
    intelligentHiring.setAgreementTerm(account.getAgreementTerm());
    Set<SPFeature> hiringFeature = new HashSet<>();
    hiringFeature.add(SPFeature.Hiring);
    intelligentHiring.setFeatures(hiringFeature);
    intelligentHiring.setLicensePrice(new BigDecimal(0));
    intelligentHiring.setName(comp.getName());
    /* find all the members of the company have role of account administrator */
    intelligentHiring.setNumAdmin(10);
    intelligentHiring.setNumMember(account.getHiringSubscription());
    intelligentHiring.setOverrideAdminPrice(new BigDecimal(0));
    intelligentHiring.setOverrideMemberPrice(new BigDecimal(0));
    intelligentHiring.setPlanAdminNextChargeAmount(new BigDecimal(0));
    intelligentHiring.setPlanMemberNextChargeAmount(new BigDecimal(0));
    intelligentHiring.setPlanStatus(PlanStatus.ACTIVE);
    intelligentHiring.setPlanType(SPPlanType.IntelligentHiring);
    intelligentHiring.setUnitAdminPrice(new BigDecimal(0));
    intelligentHiring.setUnitMemberPrice(new BigDecimal(0));
    intelligentHiring.setAgreementTerm(1);
    // Optional<String> hiringProductOps = productList.stream().filter(pd -> {
    // Product product = productRepository.findByIdValidated(pd);
    // if (product.getProductType() == ProductType.HIRING) {
    // return true;
    // } else {
    // return false;
    // }
    // }).findFirst();
    // if (hiringProductOps.isPresent()) {
    //
    // final String productId = hiringProductOps.get();
    // Product product = productRepository.findByIdValidated(productId);
    // Optional<String> findFirst = account.getPromotions().stream().findFirst();
    // String promoitonId = null;
    // if (findFirst.isPresent()) {
    // promoitonId = findFirst.get();
    // }
    // Promotion promotion = getPromotion(account, promoitonId, product);
    //
    // // get the product helper
    // ProductHelper productHelper = accountFactory.getProductHelper(product);
    // Optional<Promotion> promotionOps = Optional.ofNullable(promotion);
    // Double unitPrice = productHelper.getUnitPrice(account, product, promotionOps);
    // intelligentHiring.setUnitMemberPrice(new BigDecimal(unitPrice));
    // }
    return intelligentHiring;
  }
  
  private Promotion getPromotion(Account account, String promotionId, Product product) {
    Promotion promotion = null;
    // get the promotion's unit pricing if it exists
    if (promotionId != null) {
      promotion = promotionsRepository.findByIdValidated(promotionId);
      try {
        // Turned of promotions validation as it was
        // interfering with the count based promotions validator
        // promotionsFactory.validate(promotion, product);
        // check if the promotion already exists in the account
        if (!account.getPromotions().contains(promotionId)) {
          // remove any previous promotion
          removePromotionForProduct(account, product);
          // add the promotion to the account
          account.addPromotion(promotion);
          // updating the account
          accountRepository.updateAccount(account);
        }
      } catch (PromotionsValidationException exp) {
        // check if promotion exists in the account
        // then we need to invalidate the same
        if (account.getPromotions().contains(promotionId)) {
          // remove the promotion from the account
          account.removePromotion(promotion);
          accountRepository.updateAccount(account);
        }
        throw exp;
      }
    }
    return promotion;
  }
  
  /**
   * Remove the promotion for the product if it exists.
   * 
   * @param account
   *          - account
   * @param product
   *          - product
   */
  private void removePromotionForProduct(Account account, Product product) {
    List<Promotion> promotionList = promotionsRepository.getAllPromotionsById(account
        .getPromotions());
    // removing the associated promotion
    promotionList.stream().filter(p -> p.getProductIdList().contains(product.getId())).findFirst()
        .ifPresent(p -> account.removePromotion(p));
  }
  
  /**
   * runAccountSchedular will run the schedular on demand.
   */
  public SPResponse runAccountSchedular(User user) {
    
    accountUpdation.processAccounts();
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * updateArticlesUgp method will update the article progress for all the user and update the book
   * marks of the user.
   * 
   * @param user
   *          - logged in user
   * @return the response to the update request
   */
  public SPResponse updateArticlesUgp(User user) {
    final SPResponse resp = new SPResponse();
    
    List<User> allUsers = userRepository.findAllMembers(false);
    
    allUsers.stream().forEach(
        usr -> {
          if (usr.getUserGoalId() != null) {
            UserGoal userGoal = userGoalsRepository.findById(usr.getUserGoalId());
            if (userGoal != null) {
              List<UserArticleProgress> articleProgress = userGoal.getArticleProgress();
              if (articleProgress != null) {
                List<UserArticleProgress> complatedArticleProgress = articleProgress.stream()
                    .filter(ap -> ap.getArticleStatus() == ArticleStatus.COMPLETED)
                    .collect(Collectors.toList());
                
                Map<String, UserArticleProgress> collect = complatedArticleProgress.stream()
                    .collect(Collectors.toMap(UserArticleProgress::getArticleId, uap -> uap));
                userGoal.setArticleProgress(new ArrayList<>());
                collect.forEach((k, value) -> {
                  userGoal.getArticleProgress().add(value);
                });
                
                List<BookMarkTracking> bookMarkTrackingBeans = trackingRepository
                    .findAllBookMarkTrackingBean(usr.getId(), -1);
                Set<String> bookMarkArticles = bookMarkTrackingBeans.stream()
                    .map(bmt -> bmt.getArticleId()).collect(Collectors.toSet());
                userGoal.setBookMarkedArticles(bookMarkArticles);
                userGoalsRepository.save(userGoal);
                
              }
              
            }
          }
        });
    
    return resp.isSuccess();
  }
  
  public SPResponse updateBlueprintTotalActionCount(User user) {
    
    List<User> findAllMembers = userRepository.findAllMembers(false);
    for (User usr : findAllMembers) {
      Blueprint blueprint = goalsFactory.getBlueprint(usr);
      if (blueprint == null) {
        continue;
      }
      if (blueprint.getTotalActionCount() == 0 && blueprint.getStatus() == GoalStatus.PUBLISHED) {
        blueprint.setTotalActionCount(blueprint.getDevStrategyActionCategoryList().stream()
            .mapToInt(DSActionCategory::getActionCount).sum());
      }
      goalsFactory.updateBlueprint(blueprint);
      
    }
    
    return new SPResponse().isSuccess();
  }
  
  // public SPResponse updateOrgPlan(User user) {
  //
  // SPResponse response = new SPResponse();
  //
  // List<Company> companies = accountRepository.findAllCompanies();
  // for (Company company : companies) {
  // List<ActionPlan> plansForCompany = actionPlanFactory.getAllActionPlansForCompany(company
  // .getId());
  // if (CollectionUtils.isEmpty(plansForCompany)) {
  // continue;
  // }
  // List<User> users = userRepository.findUsers("companyId", company.getId());
  // for (User usr : users) {
  //
  // String userActionId = usr.getUserActionPlanId();
  // if (userActionId == null) {
  // continue;
  // }
  // UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(usr);
  //
  // if (userActionPlan == null) {
  // continue;
  // }
  // if (CollectionUtils.isEmpty(userActionPlan.getCompletedActions())) {
  // continue;
  // }
  // for (String completdAction : userActionPlan.getCompletedActions()) {
  // for (ActionPlan actionPlan : plansForCompany) {
  // try {
  // ActionPlanDao actionPlanDao = actionPlanFactory.getActionPlan(actionPlan.getId());
  // Set<String> completedUserActions = userActionPlan.getCompleteActionsMap().get(
  // actionPlan.getId());
  // if (completedUserActions == null) {
  // completedUserActions = new HashSet<>();
  // userActionPlan.getCompleteActionsMap()
  // .put(actionPlan.getId(), completedUserActions);
  // }
  // if (actionPlanDao.validateUID(completdAction)) {
  // completedUserActions.add(completdAction);
  // }
  // } catch (Exception ex) {
  // response.addError(usr.getEmail(), actionPlan.getId());
  // }
  // }
  // }
  // actionPlanFactory.updateUserActionPlan(userActionPlan);
  // }
  // }
  // return response;
  // }
  
  public SPResponse updateUsersOrgPlans(User user) {
    
    SPResponse response = new SPResponse();
    
    List<Company> companies = accountRepository.findAllCompanies();
    for (Company company : companies) {
      List<User> users = userRepository.findUsers("companyId", company.getId());
      users.stream().forEach(usr -> {
        UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
        Set<String> actionPlanIds = userActionPlan.getCompleteActionsMap().keySet();
        actionPlanIds.stream().forEach(act -> {
          ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(act);
          if (actionPlan != null && !actionPlan.getUserIdList().contains(usr.getId())) {
            Set<String> set = userActionPlan.getCompleteActionsMap().get(act);
            userActionPlan.getRemovedActionPlanMap().put(act, set);
            LOG.debug("removed org plans " + act);
            userActionPlan.getCompleteActionsMap().remove(act);
          } else {
            LOG.debug("removed org plans " + act);
            userActionPlan.getCompleteActionsMap().remove(act);
          }
        });
        actionPlanFactory.updateUserActionPlan(userActionPlan);
        
      });
      
    }
    return response.isSuccess();
  }
  
  /**
   * @param ap
   * @param usr
   */
  public void updateUserActionPlan(ActionPlan ap, User usr) {
    UserActionPlan actionPlan = actionPlanFactory.getUserActionPlan(usr);
    Set<String> set = actionPlan.getCompleteActionsMap().get(ap.getId());
    if (set == null) {
      set = new HashSet<>();
      actionPlan.getCompleteActionsMap().put(ap.getId(), set);
    }
    actionPlanFactory.updateUserActionPlan(actionPlan);
  }
  
  private void addGroupMemberIds(GroupPermissions gp, List<String> groupMemberIds) {
    UserGroup findById = groupRepository.findById(gp.getGroupId());
    if (findById != null) {
      List<String> groupMemberEmails = new ArrayList<String>(findById.getMemberList());
      if (!gp.isExcludeGroupLead()) {
        groupMemberEmails.add(findById.getGroupLead());
      }
      List<User> findByEmail = userRepository.findByEmail(groupMemberEmails);
      findByEmail.stream().map(User::getId).filter(uId -> !groupMemberIds.contains(uId))
          .forEach(usr -> {
            groupMemberIds.add(usr);
          });
    }
  }
  
  /**
   * runAccountSchedular will run the schedular on demand.
   */
  public SPResponse runCompetencySchedular(User user) {
    
    competencyScheduler.processCompetnecyEvaluations();
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Run the learning program scheduler.
   * 
   * @param user
   *          - user
   * @return success
   */
  public SPResponse runLearningProgramSchedular(User user) {
    actionPlanScheduler.process();
    return new SPResponse().isSuccess();
  }
  
  /**
   * Method to update all the feedback users.
   * 
   * @param user
   *          - logged in user
   * @return success
   */
  public SPResponse updateFeedbackUserCreatedOn(User user) {
    
    List<FeedbackRequest> allFeedbackRequest = feedbackRepository.getAllFeedbackRequest();
    for (FeedbackRequest req : allFeedbackRequest) {
      FeedbackUser fbUser = feedbackRepository.findByIdValidated(req.getFeedbackUserId());
      fbUser.setCreatedOn(req.getStartDate());
      feedbackRepository.updateFeedbackUser(fbUser);
    }
    return new SPResponse().isSuccess();
  }
  
  public SPResponse addToNewsFeed(User user) {
    activityFeedScheduler.process();
    return new SPResponse().isSuccess();
  }
  
  /**
   * Load dummy users to company.
   * 
   * @param user
   *          contains thecompany id and the uesrs
   * @param params
   *          logged in user.
   * @return the users.
   */
  public SPResponse loadUsersToCompany(User user, Object[] params) {
    
    String copyFrom = (String) params[0];
    int numberOfUsers = (int) params[1];
    String emailPrefix = (String) params[2];
    User copyUser = userRepository.findByEmail(copyFrom);
    PersonalityType[] types = { PersonalityType.Actuary, PersonalityType.Ambassador,
        PersonalityType.Designer, PersonalityType.Encourager, PersonalityType.Examiner,
        PersonalityType.Innovator, PersonalityType.Instructor, PersonalityType.Motivator,
        PersonalityType.Navigator, PersonalityType.Pragmatist, PersonalityType.Promoter,
        PersonalityType.Refiner, PersonalityType.Researcher, PersonalityType.Supporter,
        PersonalityType.Visionary };
    
    Assert.notNull(copyUser);
    String firstName = copyUser.getFirstName();
    String lastName = copyUser.getLastName();
    String title = copyUser.getTitle();
    for (int i = 0; i < numberOfUsers; i++) {
      
      copyUser.setId(null);
      copyUser.setFirstName(firstName + i);
      copyUser.setLastName(lastName + i);
      
      // Set the perosnality
      PersonalityBeanResponse primaryPersonality = copyUser.getAnalysis().getPersonality()
          .get(RangeType.Primary);
      primaryPersonality.setPersonalityType(randomFrom(types));
      PersonalityBeanResponse underPressure = copyUser.getAnalysis().getPersonality()
          .get(RangeType.UnderPressure);
      underPressure.setPersonalityType(randomFrom(types));
      
      // Set the age.
      LocalDate[] dbs = { LocalDate.of(1955, 12, 23), LocalDate.of(1975, 6, 23),
          LocalDate.of(1985, 1, 23), LocalDate.of(1995, 12, 23), LocalDate.of(2000, 12, 23),
          LocalDate.of(1935, 12, 23) };
      copyUser.setDob(randomFrom(dbs));
      copyUser.setBlueprintId(null);
      copyUser.setCompetencyProfileId(null);
      copyUser.setGender(randomFrom(Gender.F, Gender.M));
      copyUser.setMessages(null);
      copyUser.setTitle(title + i);
      copyUser.setEmail(emailPrefix + i + "@yopmail.com");
      copyUser.setUserActionPlanId(null);
      copyUser.setUserGoalId(null);
      userRepository.createUser(copyUser);
    }
    
    return new SPResponse().isSuccess();
  }
  
  private static <T> T randomFrom(T... items) {
    return items[new Random().nextInt(items.length)];
  }
  
  /**
   * Migrating the org plans to 2.0.
   * 
   * @param user
   *          - user
   * @return success
   */
  public SPResponse migrateOrgPlan(User user) {
    
    // get all the action plans
    List<ActionPlan> allActionPlans = actionPlanFactory.getAllActionPlans();
    
    final HashMap<String, UserActionPlan> userActionPlanMap = new HashMap<String, UserActionPlan>();
    
    for (ActionPlan actionPlan : allActionPlans) {
      final String actionPlanId = actionPlan.getId();
      LOG.info("Processing action plan :" + actionPlanId);
      ActionPlanDao actionPlanDao = actionPlanFactory.getActionPlan(actionPlanId);
      // setting the created by company
      final String companyId = actionPlanDao.getCompanyId();
      // if (StringUtils.isBlank(companyId)) {
      // continue;
      // }
      CompanyDao company = companyFactory.getCompany(companyId);
      UserMarkerDTO companyUser = new UserMarkerDTO(company);
      
      actionPlanDao.setCreatedByCompanyId(companyId);
      actionPlanDao.setType(ActionPlanType.Company);
      actionPlanDao.setStepType(StepType.All);
      CompanyActionPlanSettings capSettings = actionPlanDao
          .addCompany(companyId, actionPlanFactory);
      
      actionPlanDao.setCreatedBy(companyUser);
      actionPlanDao.setEditedBy(companyUser);
      actionPlanDao.setCreatedOn(LocalDateTime.now());
      actionPlanDao.setEditedOn(LocalDateTime.now());
      
      final List<String> userIdList = actionPlanDao.getUserIdList();
      final boolean userPresent = !CollectionUtils.isEmpty(userIdList);
      if (userPresent) {
        capSettings.setMemberIds(userIdList);
      }
      
      if (actionPlanDao.isActive()) {
        actionPlanDao.setPublishedOn(LocalDateTime.now());
        actionPlanDao.setPublishedBy(companyUser);
        if (userPresent) {
          for (String userId : userIdList) {
            UserActionPlan userActionPlan = userActionPlanMap.get(userId);
            if (userActionPlan == null) {
              User usr = userFactory.getUser(userId);
              if (usr == null) {
                continue;
              }
              userActionPlan = actionPlanFactory.getUserActionPlan(usr);
              if (userActionPlan == null) {
                usr.setUserActionPlanId(null);
                userActionPlan = actionPlanFactory.getUserActionPlan(usr);
              }
              userActionPlanMap.put(userId, userActionPlan);
            }
            ActionPlanProgress actionPlanProgress = userActionPlan
                .getOrCreateActionPlanProgress(actionPlanId);
            final Map<String, Set<String>> completeActionsMap = userActionPlan
                .getCompleteActionsMap();
            Set<String> completedActions = null;
            if (completeActionsMap != null) {
              completedActions = completeActionsMap.get(actionPlanId);
            }
            updateActionPlanProgress(actionPlanProgress, completedActions, actionPlanDao);
          }
        }
      } else {
        if (userPresent) {
          capSettings.setOnHold(true);
          actionPlanDao.setActive(true);
          actionPlanDao.setPublishedOn(LocalDateTime.now());
          actionPlanDao.setPublishedBy(companyUser);
        }
      }
      updateStepStatus(actionPlanDao);
      actionPlanDao.updateActionCount();
      if (!CollectionUtils.isEmpty(actionPlanDao.getPracticeAreaList())) {
        actionPlanDao.getPracticeAreaList().stream().forEach(spGoal -> {
          goalsRepository.updateGoal(spGoal);
        });
      }
      actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
      actionPlanFactory.updateActionPlan(actionPlanDao);
    }
    
    userActionPlanMap.values().forEach(actionPlanFactory::updateUserActionPlan);
    
    // get all companies with feature org plan
    List<Company> allCompanies = companyFactory.findCompaniesByFeature(SPFeature.OrganizationPlan);
    for (Company company : allCompanies) {
      // get all the users for the given company
      List<User> allMembers = userFactory.getAllMembersForCompany(company.getId());
      for (User usr : allMembers) {
        if (usr.getUserActionPlanId() == null) {
          continue;
        }
        
        UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
        final Map<String, Set<String>> removedActionPlanMap = userActionPlan
            .getRemovedActionPlanMap();
        if (CollectionUtils.isEmpty(removedActionPlanMap)) {
          continue;
        }
        
        removedActionPlanMap.forEach((key, value) -> updateActionPlanProgress(userActionPlan, key,
            value, usr));
        
        actionPlanFactory.updateUserActionPlan(userActionPlan);
      }
    }
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Migrating the org plans to 2.0 update the goal status.
   * 
   * @param user
   *          - user
   * @return success
   */
  public SPResponse updateOrgPlanStep(User user) {
    
    // get all the action plans
    List<ActionPlan> allActionPlans = actionPlanFactory.getAllActionPlans();
    
    for (ActionPlan actionPlan : allActionPlans) {
      final String actionPlanId = actionPlan.getId();
      ActionPlanDao actionPlanDao = actionPlanFactory.getActionPlan(actionPlanId);
      updateStepStatus(actionPlanDao);
    }
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Update the user action plans with added org plans.
   * 
   * @param user
   *          - user
   * @return success
   */
  public SPResponse updateActionPlan(User user) {
    
    final HashMap<String, ActionPlanDao> actionPlanMap = new HashMap<String, ActionPlanDao>();
    List<Company> allCompanies = companyFactory.findCompaniesByFeature(SPFeature.OrganizationPlan);
    for (Company company : allCompanies) {
      // get all the users for the given company
      List<User> allMembers = userFactory.getAllMembersForCompany(company.getId());
      for (User usr : allMembers) {
        if (usr.getUserActionPlanId() == null) {
          continue;
        }
        
        final UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(usr);
        final Map<String, ActionPlanProgress> actionPlanProgressMap = userActionPlan
            .getActionPlanProgressMap();
        if (CollectionUtils.isEmpty(actionPlanProgressMap)) {
          continue;
        }
        actionPlanProgressMap.forEach((key, value) -> updateActionPlan(usr, key, userActionPlan,
            actionPlanMap));
        actionPlanFactory.updateUserActionPlan(userActionPlan);
      }
    }
    return new SPResponse().isSuccess();
  }
  
  private void updateActionPlan(User usr, String actionPlanId, UserActionPlan userActionPlan,
      HashMap<String, ActionPlanDao> actionPlanMap) {
    ActionPlanDao actionPlanDao = actionPlanMap.get(actionPlanId);
    if (actionPlanDao == null) {
      actionPlanDao = actionPlanFactory.getActionPlan(actionPlanId);
      actionPlanMap.put(actionPlanId, actionPlanDao);
    }
    actionPlanFactory
        .addUpdateActionPlanProgress(usr, userActionPlan, actionPlanDao, Operation.ADD);
  }
  
  private void updateStepStatus(ActionPlanDao actionPlanDao) {
    List<SPGoal> practiceAreaList = actionPlanDao.getPracticeAreaList();
    if (!CollectionUtils.isEmpty(practiceAreaList)) {
      for (SPGoal step : practiceAreaList) {
        if (step.getStatus() == GoalStatus.HIDDEN) {
          step.setStatus(GoalStatus.INACTIVE);
          goalsFactory.updateGoal(step);
        }
      }
    }
  }
  
  private void updateActionPlanProgress(UserActionPlan userActionPlan, String actionPlanId,
      Set<String> completedActions, User usr) {
    ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(actionPlanId);
    if (actionPlan == null) {
      return;
    }
    
    Map<String, ActionPlanProgress> removedActionPlanProgressMap = userActionPlan
        .getRemovedActionPlanProgressMap();
    if (CollectionUtils.isEmpty(removedActionPlanProgressMap)) {
      removedActionPlanProgressMap = new HashMap<String, ActionPlanProgress>();
      userActionPlan.setRemovedActionPlanProgressMap(removedActionPlanProgressMap);
    }
    
    ActionPlanProgress actionPlanProgress = removedActionPlanProgressMap.get(actionPlanId);
    if (actionPlanProgress == null) {
      actionPlanProgress = ActionPlanProgress.newInstance();
      removedActionPlanProgressMap.put(actionPlanId, actionPlanProgress);
    }
    
    updateActionPlanProgress(actionPlanProgress, completedActions, actionPlan);
    CompanyActionPlanSettings companyActionPlanSettings = actionPlanFactory
        .getCompanyActionPlanSettings(actionPlanId, usr.getCompanyId());
    if (companyActionPlanSettings.isOnHold()) {
      userActionPlan.removeActionPlan(usr, actionPlanId, false, todoFactory, channelHelper);
    }
    
  }
  
  private void updateActionPlanProgress(ActionPlanProgress actionPlanProgress,
      Set<String> completedActions, ActionPlanDao actionPlan) {
    for (SPGoal step : actionPlan.getPracticeAreaList()) {
      actionPlanProgress.addPracticeArea(step);
      if (!CollectionUtils.isEmpty(completedActions)) {
        final String stepId = step.getId();
        completedActions.stream().filter(step::validateUID)
            .forEach(uid -> actionPlanProgress.completeAction(stepId, uid, true));
      }
    }
  }
  
  public SPResponse clearCache(User user, Object[] params) {
    
    String cacheName = (String) params[0];
    Assert.hasText(cacheName, "Invalid Request");
    
    Cache cache = cacheManager.getCache(cacheName);
    
    Assert.notNull(cache, "No cache found with name " + cacheName);
    
    cache.clear();
    return new SPResponse().isSuccess();
  }
  
  @SuppressWarnings("deprecation")
  public SPResponse updateActionPlanImage(User user) {
    
    final HashMap<String, ActionPlanDao> actionPlanMap = new HashMap<String, ActionPlanDao>();
    List<ActionPlan> allActionPlans = actionPlanFactory.getAllActionPlans();
    for (ActionPlan actPlan : allActionPlans) {
      if (StringUtils.isNotBlank(actPlan.getImageUrl())) {
        String[] split = actPlan.getImageUrl().split(",");
        if (split.length < 2) {
          continue;
        }
        String contentTypeString = split[0];
        String contentType = "image/jpeg";
        
        String base64String = split[1];
        if (Base64.isBase64(base64String)) {
          
          /* get the content type from the base64 sting */
          if (org.apache.commons.lang3.StringUtils.isNotBlank(contentTypeString)) {
            contentType = contentTypeString.substring(contentTypeString.indexOf(":") + 1,
                contentTypeString.indexOf(";"));
          }
          
          byte[] bytes = java.util.Base64.getDecoder().decode(base64String);
          
          /* check if file size is of zero size */
          LocalDateTime now = actPlan.getCreatedOn() != null ? actPlan.getCreatedOn()
              : LocalDateTime.now();
          
          SPMedia media = new SPMedia();
          media.setCompanyId(actPlan.getCompanyId());
          media.setCreatedOn(LocalDateTime.now());
          media.setMediaType(SPMediaType.IMAGE);
          media.setStatus("Active");
          media.setCompanyName(companyFactory.getCompany(actPlan.getCompanyId()).getName());
          mediaRepository.saveMedia(media);
          mediaHelper.saveMedia(media.getId(), SPMediaType.IMAGE, actPlan.getCompanyId(), bytes,
              contentType, media);
          
          if (!media.getUrl().endsWith("jpeg")) {
            actPlan.setImageUrl(media.getUrl().replace("{0}", "") + "jpeg");
            media.setUrl(media.getUrl() + "jpeg");
            mediaRepository.saveMedia(media);
          }
          actPlan.setImageUrl(media.getUrl().replace("{0}", ""));
          actionPlanFactory.updateActionPlan(actPlan);
        }
        
      }
    }
    return new SPResponse().isSuccess();
  }
  
  public SPResponse updateCommentCounter(User user) {
    
    /* get all the dashboard message */
    List<DashboardMessage> findAll = dashboardMessageRepository.findAll();
    findAll.stream().forEach(db -> {
      if (db.getCidCounter() == 0) {
        db.setCidCounter(100);
        dashboardMessageRepository.save(db);
      }
      
    });
    
    List<PublicChannel> findAllPubliChannel = channelFactory.findAllPubliChannel();
    findAllPubliChannel.stream().forEach(pc -> {
      if (pc.getCidCounter() == 0) {
        pc.setCidCounter(100);
      }
      channelFactory.updatePublicChannel(pc);
    });
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to update the competency with rating configuration.
   * 
   * @param user
   *          - user
   * @return the response to the update request.
   */
  public SPResponse updateCompetencyRatingConfig(User user) {
    
    List<CompetencyProfile> all = competencyFactory.getAll();
    for (CompetencyProfile competencyProfile : all) {
      if (competencyProfile.getRatingConfiguration() == null) {
        CompetencyProfileDao competencyProfileDao = competencyFactory
            .getCompetencyProfile(competencyProfile.getId());
        
        final List<CompetencyDao> competencyList = competencyProfileDao.getCompetencyList();
        if (!CollectionUtils.isEmpty(competencyList)) {
          CompetencyDao competencyDao = competencyList.get(0);
          int size = competencyDao.getRating().getRatingList().size();
          RatingConfiguration ratingConfiguration = new RatingConfiguration(
              RatingConfigurationType.Numeric, size);
          competencyProfileDao.setRatingConfiguration(ratingConfiguration);
          competencyFactory.updateCompetencyProfile(competencyProfileDao);
        }
      }
    }
    
    return new SPResponse().isSuccess();
  }
  
  public SPResponse loadEngagmentData(User user) {
    
    List<ActivityTracking> findAll = activityTrackingRepository.findAll();
    List<ActivityTracking> articleCompletedTracking = findAll.stream()
        .filter(at -> at.getActionType() == LogActionType.ArticleCompleted)
        .collect(Collectors.toList());
    articleCompletedTracking.stream().forEach(
        act -> {
          User userByEmail = userFactory.getUser(act.getUserId());
          UserActivityTracking userActivityTracking = userATrackingRepository
              .findUserActivityTrackingByIdDate(userByEmail.getId(), act.getCreatedOn()
                  .toLocalDate());
          if (userActivityTracking == null) {
            userActivityTracking = new UserActivityTracking();
            userActivityTracking.setUserId(act.getUserId());
            userActivityTracking.setCompanyId(act.getCompanyId());
            userActivityTracking.setEmail(userByEmail.getEmail());
            userActivityTracking.setLastAccessedTime(act.getCreatedOn());
            userActivityTracking.setDate(act.getCreatedOn().toLocalDate());
          }
          userActivityTracking.inreaseArticleCompletedCount();
          userATrackingRepository.save(userActivityTracking);
          
        });
    
    List<AuditLogBean> auditLogs = adminRepository.findAuditLogs(LocalDate.now().minusMonths(3),
        LocalDate.now());
    
    for (AuditLogBean alb : auditLogs) {
      User userByEmail = userFactory.getUserByEmail(alb.getEmail());
      if (alb.getEmail().equalsIgnoreCase("oct29@yopmail.com")) {
        LOG.info(alb);
      }
      if (userByEmail != null) {
        UserActivityTracking userActivityTracking = userATrackingRepository
            .findUserActivityTrackingByIdDate(userByEmail.getId(), alb.getCreatedOn().toLocalDate());
        if (userActivityTracking == null) {
          userActivityTracking = new UserActivityTracking();
          userActivityTracking.setUserId(userByEmail.getId());
          userActivityTracking.setCompanyId(userByEmail.getCompanyId());
          userActivityTracking.setEmail(userByEmail.getEmail());
          userActivityTracking.setLastAccessedTime(alb.getCreatedOn());
          userActivityTracking.setDate(alb.getCreatedOn().toLocalDate());
          
          userActivityTracking.inreaseLoggedInCount();
          userActivityTracking.setLastAccessedTime(alb.getCreatedOn());
        }
        if (alb.getLogMessage().contains("getArticleDetail")) {
          userActivityTracking.increaseArticleView();
        }
        LocalDateTime now = alb.getCreatedOn();
        
        /* Get the last accessed time */
        LocalDateTime lastAccessedTime = userActivityTracking.getLastAccessedTime();
        
        long seconds = Math.abs(Duration.between(lastAccessedTime, now).getSeconds());
        if (seconds > 7200) {
          userActivityTracking.inreaseLoggedInCount();
          userActivityTracking.setLastAccessedTime(now);
        } else {
          userActivityTracking.addSessionDuration(seconds);
        }
        userActivityTracking.setLastAccessedTime(now);
        userATrackingRepository.save(userActivityTracking);
      }
      
    }
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to assign the default tutorial to all the members.
   * 
   * @param user
   *          - user
   * @return success
   */
  public SPResponse assignDefaultTutorial(User user) {
    final SPResponse resp = new SPResponse();
    String defaultTutorialId = environment.getProperty("sp.tutorial.default");
    
    if (!StringUtils.isBlank(defaultTutorialId)) {
      tutorialFactory.getTutorail(defaultTutorialId, user.getUserLocale());
      List<User> allMembers = userRepository.findAllMembers(false);
      for (User member : allMembers) {
        UserTutorialActivity userTutorialActivity = tutorialFactory.getUserTutorialActivity(member);
        TutorialActivityData userActivityData = userTutorialActivity
            .getUserActivityData(defaultTutorialId);
        if (userActivityData == null) {
          userTutorialActivity.addTutorial(defaultTutorialId);
          userTutorialActivity.setSelectedId(defaultTutorialId);
          tutorialFactory.save(userTutorialActivity);
          badgeFactory.addToBadgeProgress(member, defaultTutorialId, BadgeType.Tutorial);
        }
      }
      resp.isSuccess();
    } else {
      resp.addError(Constants.PARAM_ERROR, "Default tutorial not set.");
    }
    return resp;
  }
  
  /**
   * Generate the data for precision consulting.
   * 
   * @param response
   *          - HTTP response
   */
//  public void getDataForPrecisionConsulting(HttpServletResponse response) {
//    try {
//      response.setContentType("text/csv;charset=utf-8");
//      response.setHeader("Content-Disposition", "attachment; filename=precisionConsultingData.tsv");
//      OutputStream resOs = response.getOutputStream();
//      OutputStream buffOs = new BufferedOutputStream(resOs);
//      final OutputStreamWriter outputwriter = new OutputStreamWriter(buffOs);
//      ArrayList<Integer> filterList = new ArrayList<Integer>(Arrays.asList(2, 3, 4, 5, 7, 11, 13,
//          17, 19, 21, 24, 26, 27, 32, 33, 35, 37, 38, 41, 42, 45, 48, 51, 54, 56, 58, 60, 66, 68,
//          69));
//      
//      List<User> allMembers = userRepository.findAllMembers(false);
//      int index = 0;
//      StringBuffer sb = new StringBuffer();
//      for (User user : allMembers) {
//        if (user.getUserStatus() == UserStatus.VALID) {
//          AssessmentProgressTracker assessmentStore = storeRepository.getAssessmentTracker(user
//              .getId());
//          if (assessmentStore != null) {
//            sb.append(user.getEmail()).append(TSV_DELIMITTER);
//            sb.append(user.getId()).append(TSV_DELIMITTER);
//            int[][][] assessment = assessmentStore.getAssessment();
//            index = 1;
//            boolean doFilter = (assessment[0].length == 70);
//            for (int i = 0; i < assessment.length; i++) {
//              for (int j = 0; j < assessment[i].length; j++) {
//                if (doFilter) {
//                  if (!filterList.contains(index)) {
//                    sb.append(Arrays.toString(assessment[i][j])).append(TSV_DELIMITTER);
//                  }
//                } else {
//                  sb.append(Arrays.toString(assessment[i][j])).append(TSV_DELIMITTER);
//                }
//                index++;
//              }
//            }
//            sb.append("\n");
//          }
//        }
//      }
//      LOG.debug("The assessment data for precision consulting.");
//      LOG.debug(sb.toString());
//      outputwriter.write(sb.toString());
//      outputwriter.flush();
//      outputwriter.close();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }
  
  /**
   * Generate the data for precision consulting.
   * 
   * @param response
   *          - HTTP response
   */
  public void getAnalysisDataForPrecisionConsulting(HttpServletResponse response) {
    try {
      response.setContentType("text/csv;charset=utf-8");
      response.setHeader("Content-Disposition",
          "attachment; filename=precisionConsultingAnalysisData.tsv");
      OutputStream resOs = response.getOutputStream();
      OutputStream buffOs = new BufferedOutputStream(resOs);
      final OutputStreamWriter outputwriter = new OutputStreamWriter(buffOs);
      
      List<User> allMembers = userRepository.findAllMembers(false);
      StringBuffer sb = new StringBuffer();
      for (User user : allMembers) {
        if (user.getUserStatus() == UserStatus.VALID) {
          AssessmentProgressTracker assessmentStore = storeRepository.getAssessmentTracker(user
              .getId());
          if (assessmentStore != null) {
            sb.append(user.getEmail()).append(TSV_DELIMITTER);
            sb.append(user.getId()).append(TSV_DELIMITTER);
            addUserAnalysis(sb, user);
          }
        }
      }
      LOG.debug("The assessment anaylisys data for precision consulting.");
      LOG.debug(sb.toString());
      outputwriter.write(sb.toString());
      outputwriter.flush();
      outputwriter.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private void addUserAnalysis(StringBuffer sb, User user) {
    AnalysisBean analysis = user.getAnalysis();
    addData(sb, analysis.getAccuracy());
    HashMap<RangeType, PersonalityBeanResponse> personality = analysis.getPersonality();
    addData(sb, personality.get(RangeType.Primary).getPersonalityType());
    addData(sb, personality.get(RangeType.UnderPressure).getPersonalityType());
    addData(sb, personality.get(RangeType.Primary).getSegmentScore());
    addData(sb, personality.get(RangeType.UnderPressure).getSegmentScore());
    final Map<TraitType, BigDecimal> processing = analysis.getProcessing();
    if (processing != null) {
      addData(sb, processing.get(TraitType.External));
      addData(sb, processing.get(TraitType.Internal));
      addData(sb, processing.get(TraitType.Concrete));
      addData(sb, processing.get(TraitType.Intuitive));
      addData(sb, processing.get(TraitType.Cognitive));
      addData(sb, processing.get(TraitType.Affective));
      addData(sb, processing.get(TraitType.Orderly));
      addData(sb, processing.get(TraitType.Spontaneous));
    }
    
    Map<TraitType, BigDecimal> motivationWhy = analysis.getMotivationWhy();
    if (motivationWhy != null) {
      addData(sb, motivationWhy.get(TraitType.AttainmentOfGoals));
      addData(sb, motivationWhy.get(TraitType.RecognitionForEffort));
      addData(sb, motivationWhy.get(TraitType.Power));
      addData(sb, motivationWhy.get(TraitType.Compliance));
      addData(sb, motivationWhy.get(TraitType.Activity));
      addData(sb, motivationWhy.get(TraitType.Affiliation));
    }
    Map<TraitType, BigDecimal> motivationHow = analysis.getMotivationHow();
    if (motivationHow != null) {
      addData(sb, motivationHow.get(TraitType.SelfAffirmed));
      addData(sb, motivationHow.get(TraitType.AffirmedByOthers));
      addData(sb, motivationHow.get(TraitType.ExchangeOfIdeas));
      addData(sb, motivationHow.get(TraitType.ReceiveDirection));
      addData(sb, motivationHow.get(TraitType.Freedom));
      addData(sb, motivationHow.get(TraitType.Consistency));
      addData(sb, motivationHow.get(TraitType.TaskCompletion));
      addData(sb, motivationHow.get(TraitType.PrefersProcess));
    }
    Map<TraitType, BigDecimal> motivationWhat = analysis.getMotivationWhat();
    if (motivationWhat != null) {
      addData(sb, motivationWhat.get(TraitType.Hygiene));
      addData(sb, motivationWhat.get(TraitType.Accomplishment));
    }
    
    Map<TraitType, BigDecimal> conflict = analysis.getConflictManagement();
    if (conflict != null) {
      addData(sb, conflict.get(TraitType.Collaborate));
      addData(sb, conflict.get(TraitType.Compromise));
      addData(sb, conflict.get(TraitType.Accommodate));
      addData(sb, conflict.get(TraitType.Avoid));
      addData(sb, conflict.get(TraitType.Compete));
    }
    
    Map<TraitType, BigDecimal> fundamentalNeeds = analysis.getFundamentalNeeds();
    if (fundamentalNeeds != null) {
      addData(sb, fundamentalNeeds.get(TraitType.Control));
      addData(sb, fundamentalNeeds.get(TraitType.Security));
      addData(sb, fundamentalNeeds.get(TraitType.Significance));
    }
    
    Map<TraitType, BigDecimal> decisionMaking = analysis.getDecisionMaking();
    if (decisionMaking != null) {
      addData(sb, decisionMaking.get(TraitType.Outward));
      addData(sb, decisionMaking.get(TraitType.Inward));
      addData(sb, decisionMaking.get(TraitType.Careful));
      addData(sb, decisionMaking.get(TraitType.Rapid));
    }
    
    Map<TraitType, BigDecimal> learning = analysis.getLearningStyle();
    if (learning != null) {
      addData(sb, learning.get(TraitType.Analytical));
      addData(sb, learning.get(TraitType.Global));
    }
    sb.append("\n");
  }


//  /**
//   * Generate the data for precision consulting.
//   * 
//   * @param response
//   *          - HTTP response
//   */
//  public void getAssessmentStats(HttpServletResponse response) {
//    try {
//      response.setContentType("text/csv;charset=utf-8");
//      response.setHeader("Content-Disposition", "attachment; filename=assessmentStats.tsv");
//      OutputStream resOs = response.getOutputStream();
//      OutputStream buffOs = new BufferedOutputStream(resOs);
//      final OutputStreamWriter outputwriter = new OutputStreamWriter(buffOs);
//      
//      List<User> allMembers = userRepository.findAllMembers(true);
//      StringBuffer sb = new StringBuffer();
//      for (User user : allMembers) {
//        if (user.getUserStatus() == UserStatus.VALID) {
//          AssessmentProgressTracker assessmentStore = storeRepository.getAssessmentTracker(user
//              .getId());
//          if (assessmentStore != null) {
//            final LocalDateTime completedOn = assessmentStore.getCompletedOn();
//            final LocalDateTime createdOn = assessmentStore.getCreatedOn();
//            if (completedOn != null && createdOn != null) {
//              sb.append(user.getEmail()).append(TSV_DELIMITTER);
//              sb.append(user.getId()).append(TSV_DELIMITTER);
//              sb.append(createdOn).append(TSV_DELIMITTER);
//              sb.append(completedOn).append(TSV_DELIMITTER);
//              Duration duration = Duration.between(createdOn, completedOn);
//              sb.append(duration.getSeconds()).append(TSV_DELIMITTER);
//              sb.append("\n");
//            }
//          }
//        }
//      }
//      LOG.debug("The assessment stats data.");
//      LOG.debug(sb.toString());
//      outputwriter.write(sb.toString());
//      outputwriter.flush();
//      outputwriter.close();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }

  private void addData(StringBuffer sb, PersonalityType personalityType) {
    sb.append(PersonalityTypeMapper.getPersonalityTypeMapper(personalityType)).append(
        TSV_DELIMITTER);
  }
  
  private void addData(StringBuffer sb, int segmentScore) {
    sb.append(segmentScore).append(TSV_DELIMITTER);
    int divideBy = 1000;
    for (int i = 0; i < 4; i++) {
      int lastDigit = (segmentScore / divideBy) % 10;
      divideBy = divideBy / 10;
      sb.append(
          BigDecimal.valueOf((lastDigit / 7d) * 100)
              .setScale(Constants.PRICE_ROUNDING_PRECISION, Constants.ROUNDING_MODE).intValue())
          .append(TSV_DELIMITTER);
    }
  }
  
  private void addData(StringBuffer sb, BigDecimal data) {
    sb.append(data.toString()).append(TSV_DELIMITTER);
  }
  
  // public SPResponse fixAssessment(User user) {
  // final SPResponse resp = new SPResponse();
  // User usr = userFactory.getUserByEmail("prodfix@yopmail.com");
  // Assert.notNull(usr);
  //
  // AssessmentProgressStore assessmentStore = storeRepository.getAssessmentFromStore(usr.getId());
  // Assert.notNull(assessmentStore);
  //
  // int[][][] assessment = assessmentStore.getAssessment();
  // StringBuffer sb = new StringBuffer();
  // for (int i = 0; i < assessment.length; i++) {
  // sb.append(assessment[i].length).append(",");
  // }
  // resp.add("Results", sb.toString());
  //
  // // fixing
  // ArrayList<Integer> filterList = new ArrayList<Integer>(Arrays.asList(2, 3, 4, 5, 7, 11, 13,
  // 17, 19, 21, 24, 26, 27, 32, 33, 35, 37, 38, 41, 42, 45, 48, 51, 54, 56, 58, 60, 66, 68,
  // 69));
  //
  // int[][] newArray = new int[40][];
  // int counter = 0;
  // sb = new StringBuffer();
  // for (int i = 0; i < assessment[0].length; i++) {
  // if (!filterList.contains(i + 1)) {
  // newArray[counter] = assessment[0][i];
  // counter++;
  // } else {
  // sb.append(i + 1).append(",");
  // }
  // }
  // resp.add("Skipped", sb.toString());
  //
  // LOG.info("New size : " + newArray.length);
  // assessment[0] = newArray;
  // storeRepository.update(assessmentStore);
  //
  // return resp;
  // }
  //
  
  public SPResponse updateUserBadges(User user) {
    
    List<User> findAllMembers = userRepository.findAllMembers(true);
    
    findAllMembers.stream().filter(us -> us.getUserStatus() == UserStatus.VALID).forEach(usr -> {
      
      /* Reset the all the badges for the user */
      badgeFactory.resetBadge(usr);
      String userGoalId = usr.getUserGoalId();
      if (StringUtils.isNotBlank(userGoalId)) {
        UserGoal userGoal = userGoalsRepository.findById(userGoalId);
        if (userGoal != null) {
          
          List<UserArticleProgress> articleProgress = userGoal.getArticleProgress();
          articleProgress.stream().forEach(ua -> {
            String articleId = ua.getArticleId();
            if (ua.getArticleStatus() == ArticleStatus.COMPLETED) {
              Article article = articlesFactory.getArticle(articleId);
              if (article != null) {
                if (!CollectionUtils.isEmpty(article.getGoals())) {
                  String goal = article.getGoals().stream().findFirst().get();
                  badgeFactory.updateBadgeProgress(usr, goal, BadgeType.Erti);
                }
                
              }
              
            }
          });
        }
      }
      
      /* update the badges for learnign plan */
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(usr);
      if (userActionPlan != null) {
        Set<String> keySet = userActionPlan.getActionPlanProgressMap().keySet();
        keySet.stream().forEach(acId -> {
          badgeFactory.updateBadgeProgress(usr, acId, BadgeType.OrgPlan);
        });
        
      }
      
      /* update badge progress for tutoril */
      
      UserTutorialActivity userTutorialActivity = tutorialFactory.getUserTutorialActivity(usr);
      if (userTutorialActivity != null) {
        
        if (userTutorialActivity.getTutorialActivityMap() != null) {
          Set<String> keySet = userTutorialActivity.getTutorialActivityMap().keySet();
          keySet.stream().forEach(tutId -> {
            badgeFactory.updateBadgeProgress(usr, tutId, BadgeType.Tutorial);
          });
        }
        
      }
      
    });
    return new SPResponse().isSuccess();
  }
  
  public SPResponse updateUserBadgesTime(User user) {
    List<UserBadgeActivity> findAll = badgeRepository.findAll();
    for (UserBadgeActivity useBgage : findAll) {
      Map<String, UserBadgeProgress> userBadgeProgress = useBgage.getUserBadgeProgress();
      if (userBadgeProgress != null) {
        userBadgeProgress.forEach((uid, ubp) -> {
          if (ubp.getAwarededOn() == null) {
            ubp.setAwarededOn(LocalDateTime.now());
          }
        });
      }
      badgeRepository.save(useBgage);
    }
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to update the existing hiring users.
   * 
   * @param user
   *          - user
   * @return response to the update request
   */
  public SPResponse updateHiringUsers(User user) {
    List<Company> allCompanies = companyFactory.findAllCompanies();
    for (Company company : allCompanies) {
      List<HiringUser> allUsers = hiringRepository.getAllUsers(company.getId());
      for (HiringUser usr : allUsers) {
        usr.removeRole(RoleType.HiringCandidate);
        switch (usr.getUserStatus()) {
        case HIRED:
          usr.setType(UserType.Member);
          usr.addRole(RoleType.HiringEmployee);
          usr.setHired(true);
          break;
        case HIRED_MOVED_TO_MEMBER:
          usr.setType(UserType.Member);
          usr.addRole(RoleType.HiringEmployee);
          usr.setHired(true);
          usr.setInErti(true);
          break;
        default: // INVITATION_NOT_SENT, ASSESSMENT_PROGRESS, ASSESSMENT_PENDING,
                 // PROFILE_INCOMPLETE, INVITATION_SENT, ADD_REFERENCES, VALID
          usr.setType(UserType.HiringCandidate);
          usr.addRole(RoleType.HiringCandidate);
          break;
        }
        fixAnalysisCompletedOn(usr);
        hiringRepository.updateHiringUser(usr);
      }
    }
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to update the existing hiring users.
   * 
   * @param user
   *          - user
   * @return response to the update request
   */
  @SuppressWarnings("unchecked")
  public SPResponse updateHiringUsersArchive(User user) {
    List<Company> allCompanies = companyFactory.findAllCompanies();
    for (Company company : allCompanies) {
      List<HiringUserArchive> allUsers = (List<HiringUserArchive>) hiringRepository
          .getAllArchivedUsers(company.getId());
      for (HiringUserArchive usr : allUsers) {
        usr.removeRole(RoleType.HiringCandidate);
        switch (usr.getUserStatus()) {
        case HIRED:
          usr.setType(UserType.Member);
          usr.addRole(RoleType.HiringEmployee);
          usr.setHired(true);
          break;
        case HIRED_MOVED_TO_MEMBER:
          usr.setType(UserType.Member);
          usr.addRole(RoleType.HiringEmployee);
          usr.setHired(true);
          usr.setInErti(true);
          break;
        default: // INVITATION_NOT_SENT, ASSESSMENT_PROGRESS, ASSESSMENT_PENDING,
                 // PROFILE_INCOMPLETE, INVITATION_SENT, ADD_REFERENCES, VALID
          usr.setType(UserType.HiringCandidate);
          usr.addRole(RoleType.HiringCandidate);
          break;
        }
        fixAnalysisCompletedOn(usr);
        hiringRepository.save(usr);
      }
    }
    return new SPResponse().isSuccess();
  }
  
  private void fixAnalysisCompletedOn(HiringUser usr) {
    final AnalysisBean analysis = usr.getAnalysis();
    if (analysis != null) {
      fixAnalyisisCompletedOn(usr, analysis);
    }
  }
  
  private void fixAnalyisisCompletedOn(User usr, final AnalysisBean analysis) {
    LocalDateTime createdOn = analysis.getCreatedOn();
    if (createdOn == null) {
      LocalDate usrCreatedOn = usr.getCreatedOn();
      if (usrCreatedOn == null) {
        usrCreatedOn = LocalDate.now();
      }
      analysis.setCreatedOn(LocalDateTime.of(usrCreatedOn, LocalTime.MIDNIGHT));
    }
  }
  
  /**
   * Helper method to add the company members to people analytics.
   * 
   * @param user
   *          - user
   * @return response to the add request
   */
  public SPResponse addMembersToPplAnalytics(User user, Object[] params) {
    
    String email = (String) params[0];
    
    if (!StringUtils.isBlank(email)) {
      User userByEmail = userFactory.getUserByEmail(email);
      Optional.ofNullable(userByEmail)
          .ifPresent(u -> addMembersToPplAnalytics(u.getCompanyId(), u));
    } else {
      List<Company> allCompanies = companyFactory.findAllCompanies();
      for (Company company : allCompanies) {
        final String companyId = company.getId();
        Account account = accountRepository.findAccountByCompanyId(companyId);
        if (account == null || account.getPlan(SPPlanType.IntelligentHiring) == null) {
          continue;
        }
        List<User> allUsers = userFactory.getAllMembersForCompany(companyId);
        for (User usr : allUsers) {
          addMembersToPplAnalytics(companyId, usr);
        }
      }
    }
    return new SPResponse().isSuccess();
  }
  
  private void addMembersToPplAnalytics(final String companyId, User usr) {
    HiringUser hiringUser;
    switch (usr.getUserStatus()) {
    case VALID:
      // checking existing user in hiring
      hiringUser = hiringRepository.findByEmail(companyId, usr.getEmail());
      if (hiringUser == null) {
        hiringUser = new HiringUser(usr);
      } else {
        hiringUser.setHired(true);
        hiringUser.removeRole(RoleType.HiringCandidate);
        hiringUser.addRole(RoleType.HiringEmployee);
        hiringUser.setType(UserType.Member);
      }
      break;
    default:
      hiringUser = null;
      break;
    }
    if (hiringUser != null) {
      fixAnalysisCompletedOn(hiringUser);
      hiringUser.setInErti(true);
      if (usr.hasRole(RoleType.Hiring)) {
        hiringUser.addRole(RoleType.Hiring);
      }
      hiringRepository.updateHiringUser(hiringUser);
    }
  }
  
  /**
   * Fix the completed on for the hiring users.
   * 
   * @param user
   *          -user
   * @return response
   */
  public SPResponse fixCompletedOn(User user) {
    List<Company> allCompanies = companyFactory.findAllCompanies();
    for (Company company : allCompanies) {
      List<HiringUser> allUsers = hiringRepository.getAllUsers(company.getId());
      for (HiringUser usr : allUsers) {
        fixAnalysisCompletedOn(usr);
        hiringRepository.updateHiringUser(usr);
      }
    }
    return new SPResponse().isSuccess();
  }
  
  public SPResponse deleteAccount(User user, Object[] params) {
    String accountId = (String) params[0];
    Company companyForAccount = accountRepository.getCompanyForAccount(accountId);
    Account account = accountRepository.findValidatedAccountByAccountId(accountId);
    List<User> findUsers = userRepository.findUsers("companyId", companyForAccount.getId());
    findUsers.stream().forEach(usr -> {
      userRepository.removeUser(usr);
    });
    List<HiringUser> allUsers = hiringRepository.getAllUsers(companyForAccount.getId());
    allUsers.stream().forEach(hrs -> {
      hiringRepository.delete(hrs);
    });
    accountRepository.removeCompany(companyForAccount);
    account
        .getSpPlanMap()
        .values()
        .forEach(
            plan -> {
              PaymentInstrument paymentInstrument = accountRepository
                  .findPaymentInstrumentById(plan.getPaymentInstrumentId());
              accountRepository.removePaymentInstrument(paymentInstrument);
            });
    accountRepository.removeAccount(account);
    return new SPResponse().isSuccess();
    
  }
  
  /**
   * Update the user assessment priorities.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the status for the update request
   */
  public SPResponse updateUserAssessmentPriorities(User user, Object[] params) {
    String email = (String) params[0];
    
    if (!StringUtils.isBlank(email)) {
      User userByEmail = userFactory.getUserByEmail(email);
      Assert.notNull(userByEmail, "User not found");
      updatePriorities(userByEmail);
    } else {
      List<Company> allCompanies = accountRepository.findAllCompanies();
      allCompanies.stream().map(Company::getId).map(userFactory::getAllMembersForCompany)
          .flatMap(List::stream).forEach(this::updatePriorities);
    }
    return new SPResponse().isSuccess();
  }
  
  /**
   * Update the user assessment priorities.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the status for the update request
   */
  public SPResponse updateHiringUserAssessmentPriorities(User user, Object[] params) {
    String email = (String) params[0];
    String companyId = (String) params[1];
    
    if (!StringUtils.isBlank(email)) {
      Assert.notNull(companyId, "Company id required.");
      HiringUser hiringUser = hiringUserFactory.getUserByEmail(email, companyId);
      Assert.notNull(hiringUser, "User not found");
      updatePriorities(hiringUser);
    } else {
      List<Company> allCompanies = accountRepository.findAllCompanies();
      allCompanies.stream().map(Company::getId).map(hiringUserFactory::getAll)
          .flatMap(List::stream).forEach(this::updatePriorities);
    }
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to migrate the existing competency evaluations.
   * 
   * @param user
   *          - user
   * @return success
   */
  public SPResponse migrateCompetencyEvaluations(User user) {
    
    for (Company company : companyFactory.findAllCompanies()) {
      
      // check if an evaluation is in progress
      if (company.isEvaluationInProgress()) {
        continue;
      }
      
      // get all the evaluations for the given company
      List<CompetencyEvaluation> competencyEvaluations = competencyFactory
          .getCompletedCompetencyEvaluation(company.getId());
      
      CompetencyEvaluation lastCompetencyEvaluation = null;
      // iterate over all the competency evaluations
      for (CompetencyEvaluation competencyEvaluation : competencyEvaluations) {
        // creating list to store the user id's in the evaluation
        final Set<String> userIds = new HashSet<String>();
        // creating a map to store the competency profile
        final Map<String, CompetencyProfileSummaryDTO> competencyProfileMap = new HashMap<String, CompetencyProfileSummaryDTO>();
        
        // iterate over all the evaluations and extract the
        // user id the competency profile
        competencyEvaluation
            .getEvaluationMap()
            .values()
            .forEach(
                cebp -> updateFromCompetencyEvaluationByProfile(cebp, userIds,
                    competencyProfileMap, competencyEvaluation));
        
        // setting the user id's
        competencyEvaluation.setUserIds(userIds);
        // setting the competency evaluation profiles
        competencyEvaluation.setCompetencyProfileMap(competencyProfileMap);
        // updating the competency evaluation
        competencyFactory.update(competencyEvaluation);
        
        // creating the spectrum competency summary
        competencyFactory.createSpectrumSummary(competencyEvaluation);
        if (lastCompetencyEvaluation != null) {
          if (lastCompetencyEvaluation.getStartDate().isBefore(competencyEvaluation.getStartDate())) {
            lastCompetencyEvaluation = competencyEvaluation;
          }
        } else {
          lastCompetencyEvaluation = competencyEvaluation;
        }
      }
      
      // setting the count of evaluations, last competency evaluation id in the company
      company.setCompetencyEvaluationCount(competencyEvaluations.size());
      company.setLastCompetencyEvalutationId(company.getCompetencyEvaluationId());
      company.setCompetencyEvaluationId(null);
      if (lastCompetencyEvaluation != null) {
        company.setLastCompetencyEvalutationId(lastCompetencyEvaluation.getId());
      }
      companyFactory.updateCompany(company);
    }
    
    return new SPResponse().isSuccess();
  }
  
  private void updateFromCompetencyEvaluationByProfile(CompetencyEvaluationByProfile cebp,
      Set<String> userIds, Map<String, CompetencyProfileSummaryDTO> competencyProfileMap,
      CompetencyEvaluation competencyEvaluation) {
    
    // get the competency profile
    CompetencyProfileSummaryDTO competencyProfile = cebp.getCompetencyProfile();
    // adding the competency profile to the map
    final String competencyProfileId = competencyProfile.getId();
    competencyProfileMap.put(competencyProfileId, competencyProfile);
    
    cebp.getUserEvaluationMap()
        .values()
        .forEach(
            evaluationResult -> updateFromEvaluationResult(evaluationResult, userIds,
                competencyEvaluation, competencyProfileId));
  }
  
  private void updateFromEvaluationResult(UserEvaluationResult evaluationResult,
      Set<String> userIds, CompetencyEvaluation competencyEvaluation, String competencyProfileId) {
    
    BaseUserDTO user = evaluationResult.getMember();
    final String userId = user.getId();
    User userInDb = userFactory.getUser(userId);
    if (userInDb == null) {
      return;
    }
    UserCompetency userCompetency = competencyFactory.getUserCompetency(userId);
    UserCompetencyEvaluation evaluation = userCompetency.addEvaluation(
        competencyEvaluation.getId(), competencyProfileId);
    evaluation.setCompetencyProfileId(competencyProfileId);
    evaluation.setSelf(updateScoreArray(evaluationResult.getSelfEvaluation()));
    evaluation.setManager(updateScoreArray(evaluationResult.getManagerEvaluation()));
    List<UserCompetencyEvaluationScore> peerEvaluations = evaluationResult.getPeerEvaluation();
    if (peerEvaluations != null && !peerEvaluations.isEmpty()) {
      PeerCompetencyEvaluationScore peers = PeerCompetencyEvaluationScore.newInstance();
      peerEvaluations.forEach(pe -> peers.add(updateScoreArray(pe)));
      peers.updateAverageScore();
      evaluation.setPeers(peers);
    }
    competencyFactory.update(userCompetency);
    userIds.add(userId);
  }
  
  private BaseCompetencyEvaluationScore updateScoreArray(BaseCompetencyEvaluationScore evaluation) {
    if (evaluation != null) {
      UserCompetencyEvaluationDetails competencyEvaluationDetails = competencyFactory
          .getCompetencyEvaluationDetails(evaluation.getCompetencyEvaluationScoreDetailsId());
      if (competencyEvaluationDetails != null) {
        evaluation.setScoreArray(competencyEvaluationDetails.getScoreArray());
      }
    }
    return evaluation;
  }
  
  private UserCompetencyEvaluationScore updateScoreArray(UserCompetencyEvaluationScore evaluation) {
    if (evaluation != null) {
      UserCompetencyEvaluationDetails competencyEvaluationDetails = competencyFactory
          .getCompetencyEvaluationDetails(evaluation.getCompetencyEvaluationScoreDetailsId());
      if (competencyEvaluationDetails != null) {
        evaluation.setScoreArray(competencyEvaluationDetails.getScoreArray());
      }
    }
    return evaluation;
  }
  
  private void updatePriorities(User user) {
    if (user.getAnalysis() != null) {
      try {
        AnalysisBean analysis = user.getAnalysis();
        
        Set<CategoryType> categories = analysis.getCompletedCategories();
        if (categories == null || categories.isEmpty()) {
          categories = new HashSet<CategoryType>(AssessmentType.All.getCategories());
          analysis.setCompletedCategories(categories);
        }
        
        for (CategoryType type : categories) {
          switch (type) {
          case ConflictManagement:
            analysis.setConflictManagement(convertTo100(analysis.getConflictManagement()));
            break;
          case FundamentalNeeds:
            analysis.setFundamentalNeeds(convertTo100(analysis.getFundamentalNeeds()));
            break;
          case Processing:
            analysis.setProcessing(fixProcessing(analysis.getProcessing()));
            break;
          case MotivationHow:
            analysis.setMotivationHow(fixMotivationHow(analysis.getMotivationHow()));
            break;
          case MotivationWhy:
            analysis.setMotivationWhy(fixMotivationWhy(analysis.getMotivationWhy()));
            break;
          case LearningStyle:
            analysis.setLearningStyle(fixLearningStyle(analysis.getLearningStyle()));
            break;
          case DecisionMaking:
            analysis.setDecisionMaking(fixDecisionMaking(analysis.getDecisionMaking()));
            break;
          default:
            break;
          }
        }
        
        analysis.updatePriorities();
        fixAnalyisisCompletedOn(user, analysis);
        userFactory.updateUser(user);
      } catch (Exception e) {
        LOG.warn("Failed for user :" + user.getEmail(), e);
        throw e;
      }
    }
  }
  
  private Map<TraitType, BigDecimal> fixDecisionMaking(Map<TraitType, BigDecimal> decisionMaking) {
    Map<TraitType, BigDecimal> tempMap = new HashMap<TraitType, BigDecimal>();
    fixPercentage(tempMap, decisionMaking, TraitType.Careful, TraitType.Rapid);
    fixPercentage(tempMap, decisionMaking, TraitType.Outward, TraitType.Inward);
    return tempMap;
  }
  
  private Map<TraitType, BigDecimal> fixLearningStyle(Map<TraitType, BigDecimal> learningStyle) {
    Map<TraitType, BigDecimal> tempMap = new HashMap<TraitType, BigDecimal>();
    fixPercentage(tempMap, learningStyle, TraitType.Global, TraitType.Analytical);
    return tempMap;
  }
  
  private Map<TraitType, BigDecimal> fixMotivationWhy(Map<TraitType, BigDecimal> motivationWhy) {
    Map<TraitType, BigDecimal> tempMap = new HashMap<TraitType, BigDecimal>();
    fixPercentage(tempMap, motivationWhy, TraitType.AttainmentOfGoals,
        TraitType.RecognitionForEffort);
    fixPercentage(tempMap, motivationWhy, TraitType.Power, TraitType.Compliance);
    fixPercentage(tempMap, motivationWhy, TraitType.Activity, TraitType.Affiliation);
    return tempMap;
  }
  
  private Map<TraitType, BigDecimal> fixMotivationWhat(Map<TraitType, BigDecimal> motivationWhat) {
    Map<TraitType, BigDecimal> tempMap = new HashMap<TraitType, BigDecimal>();
    fixPercentage(tempMap, motivationWhat, TraitType.Hygiene, TraitType.Accomplishment);
    return tempMap;
  }
  
  private Map<TraitType, BigDecimal> fixMotivationHow(Map<TraitType, BigDecimal> motivationHow) {
    Map<TraitType, BigDecimal> tempMap = new HashMap<TraitType, BigDecimal>();
    fixPercentage(tempMap, motivationHow, TraitType.SelfAffirmed, TraitType.AffirmedByOthers);
    fixPercentage(tempMap, motivationHow, TraitType.ExchangeOfIdeas, TraitType.ReceiveDirection);
    fixPercentage(tempMap, motivationHow, TraitType.Freedom, TraitType.Consistency);
    fixPercentage(tempMap, motivationHow, TraitType.TaskCompletion, TraitType.PrefersProcess);
    return tempMap;
  }
  
  private Map<TraitType, BigDecimal> fixProcessing(Map<TraitType, BigDecimal> fundamentalNeeds) {
    Map<TraitType, BigDecimal> tempMap = new HashMap<TraitType, BigDecimal>();
    fixPercentage(tempMap, fundamentalNeeds, TraitType.External, TraitType.Internal);
    fixPercentage(tempMap, fundamentalNeeds, TraitType.Concrete, TraitType.Intuitive);
    fixPercentage(tempMap, fundamentalNeeds, TraitType.Cognitive, TraitType.Affective);
    fixPercentage(tempMap, fundamentalNeeds, TraitType.Orderly, TraitType.Spontaneous);
    return tempMap;
  }
  
  private void fixPercentage(Map<TraitType, BigDecimal> tempMap,
      Map<TraitType, BigDecimal> scoreMap, TraitType trait1, TraitType trait2) {
    BigDecimal score1 = getToBelow100(scoreMap.get(trait1));
    BigDecimal score2 = getToBelow100(scoreMap.get(trait2));
    Assert.isTrue(score1 != null && score2 != null, "Scores not found. " + trait1 + ":" + trait2);
    final BigDecimal totalScore = score1.add(score2);
    switch (totalScore.compareTo(ONE_HUNDRED_PERCENT)) {
    case -1:
      if (score1.compareTo(score2) == -1) {
        score2 = score2.add(BigDecimal.ONE);
      } else {
        score1 = score1.add(BigDecimal.ONE);
      }
      break;
    case 1:
      if (score1.compareTo(score2) == -1) {
        score1 = score1.subtract(BigDecimal.ONE);
      } else {
        score2 = score2.subtract(BigDecimal.ONE);
      }
      break;
    default:
      // do nothing
    }
    tempMap.put(trait1, score1);
    tempMap.put(trait2, score2);
    
  }
  
  private BigDecimal getToBelow100(BigDecimal score) {
    while (score.compareTo(ONE_HUNDRED_PERCENT) == 1) {
      score = score.divide(ONE_HUNDRED_PERCENT);
    }
    return score.setScale(0, Constants.ROUNDING_MODE);
  }
  
  private Map<TraitType, BigDecimal> convertTo100(Map<TraitType, BigDecimal> valueMapToConvert) {
    HashMap<TraitType, BigDecimal> tempMap = new HashMap<TraitType, BigDecimal>();
    if (valueMapToConvert != null) {
      double total = 0d;
      int size = valueMapToConvert.size();
      int counter = 1;
      List<Entry<TraitType, BigDecimal>> collect = valueMapToConvert.entrySet().stream()
          .sorted(comparator.reversed()).collect(Collectors.toList());
      for (Entry<TraitType, BigDecimal> entry : collect) {
        BigDecimal value = getToBelow100(entry.getValue());
        total += value.doubleValue();
        if (total > 100.0) {
          value = value.subtract(BigDecimal.ONE);
        } else if (total == 99 && counter == size) {
          value = value.add(BigDecimal.ONE);
        }
        tempMap.put(entry.getKey(), value);
        counter++;
      }
    }
    return tempMap;
  }
  
  /**
   * migrate all the previous linkedUrls for Candidate to the new profile urls.
   * 
   * @param user
   *          system admin user
   * @return the response.
   */
  public SPResponse migrateLinkedInUrlsForCandidate(User user) {
    
    List<Company> findAllCompanies = accountRepository.findAllCompanies();
    findAllCompanies.stream().forEach(
        com -> {
          List<HiringUser> allUsers = hiringRepository.getAllUsers(com.getId());
          allUsers.stream().forEach(hru -> {
            if (StringUtils.isNotBlank(hru.getLinkedInUrl())) {
              SPMedia media = new SPMedia();
              media.setUrl(hru.getLinkedInUrl());
              hru.addProfileUrl(media);
              hiringRepository.updateHiringUser(hru);
            }
          });
          Collection<? extends HiringUser> allArchivedUsers = hiringRepository
              .getAllArchivedUsers(com.getId());
          
          allArchivedUsers.stream().forEach(hru -> {
            if (StringUtils.isNotBlank(hru.getLinkedInUrl())) {
              SPMedia media = new SPMedia();
              media.setUrl(hru.getLinkedInUrl());
              hru.addProfileUrl(media);
              hiringRepository.save((HiringUserArchive) hru);
            }
          });
          
        });
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Export the Prism Portrait Data.
   * 
   * @param response
   *          - HTTP response
   * 
   * @param platform
   *          - the platform to get the data from ca or pa
   * @param emailIds
   *          - email ids
   * @param groupName
   *          - group name
   * @param companyId
   *          - company id
   */
  public void exportPrismPortraits(HttpServletResponse response, String companyId,
      String groupName, List<String> emailIds, String platform) {
    try {
      
      Assert.hasText(companyId, "Company id required.");
      CompanyDao company = companyFactory.getCompany(companyId);
      Assert.notNull(company, "Company not found.");
      
      List<User> users = null;
      // check if the groups name exists
      if (StringUtils.isBlank(groupName)) {
        Assert.notEmpty(emailIds, "Email ids required.");
        
        if (platform.equalsIgnoreCase("ca")) {
          users = emailIds.stream().map(userFactory::getUserByEmail).filter(Objects::nonNull)
              .collect(Collectors.toList());
        } else {
          users = emailIds.stream()
              .map(email -> hiringUserFactory.getUserByEmail(email, companyId))
              .filter(Objects::nonNull).collect(Collectors.toList());
        }
      } else {
        if (platform.equalsIgnoreCase("ca")) {
          UserGroup findByName = groupRepository.findByName(companyId, groupName);
          users = findByName.getMemberList().stream().map(userFactory::getUserByEmail)
              .filter(Objects::nonNull).collect(Collectors.toList());
          Optional.ofNullable(findByName.getGroupLead()).map(userFactory::getUserByEmail)
              .filter(Objects::nonNull).ifPresent(users::add);
        } else {
          HiringGroup group = hiringGroupFactory.getByName(groupName, companyId);
          users = group.getUserIds().stream().map(hiringUserFactory::getUser)
              .filter(Objects::nonNull).collect(Collectors.toList());
        }
      }
      
      if (users != null) {
        response.setContentType("text/csv;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=prismDataExport.tsv");
        OutputStream resOs = response.getOutputStream();
        OutputStream buffOs = new BufferedOutputStream(resOs);
        final OutputStreamWriter outputwriter = new OutputStreamWriter(buffOs);
        StringBuffer sb = new StringBuffer();
        // add titles
        addTitles(sb);
        for (User user : users) {
          if (user.getUserStatus() == UserStatus.VALID) {
            sb.append(user.getFirstName()).append(TSV_DELIMITTER);
            sb.append(user.getLastName()).append(TSV_DELIMITTER);
            sb.append(user.getTitle()).append(TSV_DELIMITTER);
            sb.append(user.getEmail()).append(TSV_DELIMITTER);
            addUserAnalysis(sb, user);
          }
        }
        LOG.debug("The export for prism portrait.");
        LOG.debug(sb.toString());
        outputwriter.write(sb.toString());
        outputwriter.flush();
        outputwriter.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private void addTitles(StringBuffer sb) {
    sb.append("First Name").append(TSV_DELIMITTER);
    sb.append("Last Name").append(TSV_DELIMITTER);
    sb.append("Title").append(TSV_DELIMITTER);
    sb.append("Email").append(TSV_DELIMITTER);
    sb.append("Accuracy").append(TSV_DELIMITTER);
    sb.append(RangeType.Primary).append(TSV_DELIMITTER);
    sb.append(RangeType.UnderPressure).append(TSV_DELIMITTER);
    sb.append("Primary Score").append(TSV_DELIMITTER);
    sb.append("Power").append(TSV_DELIMITTER);
    sb.append("Versatile").append(TSV_DELIMITTER);
    sb.append("Adaptable").append(TSV_DELIMITTER);
    sb.append("Precise").append(TSV_DELIMITTER);
    sb.append("Secondary Score").append(TSV_DELIMITTER);
    sb.append("Power").append(TSV_DELIMITTER);
    sb.append("Versatile").append(TSV_DELIMITTER);
    sb.append("Adaptable").append(TSV_DELIMITTER);
    sb.append("Precise").append(TSV_DELIMITTER);
    
    sb.append(TraitType.External).append(TSV_DELIMITTER);
    sb.append(TraitType.Internal).append(TSV_DELIMITTER);
    sb.append(TraitType.Concrete).append(TSV_DELIMITTER);
    sb.append(TraitType.Intuitive).append(TSV_DELIMITTER);
    sb.append("Head").append(TSV_DELIMITTER);
    sb.append("Heart").append(TSV_DELIMITTER);
    sb.append(TraitType.Orderly).append(TSV_DELIMITTER);
    sb.append(TraitType.Spontaneous).append(TSV_DELIMITTER);
    
    sb.append(TraitType.AttainmentOfGoals).append(TSV_DELIMITTER);
    sb.append(TraitType.RecognitionForEffort).append(TSV_DELIMITTER);
    sb.append(TraitType.Power).append(TSV_DELIMITTER);
    sb.append(TraitType.Compliance).append(TSV_DELIMITTER);
    sb.append(TraitType.Activity).append(TSV_DELIMITTER);
    sb.append(TraitType.Affiliation).append(TSV_DELIMITTER);
    sb.append(TraitType.SelfAffirmed).append(TSV_DELIMITTER);
    sb.append(TraitType.AffirmedByOthers).append(TSV_DELIMITTER);
    sb.append(TraitType.ExchangeOfIdeas).append(TSV_DELIMITTER);
    sb.append(TraitType.ReceiveDirection).append(TSV_DELIMITTER);
    sb.append(TraitType.Freedom).append(TSV_DELIMITTER);
    sb.append(TraitType.Consistency).append(TSV_DELIMITTER);
    sb.append(TraitType.TaskCompletion).append(TSV_DELIMITTER);
    sb.append(TraitType.PrefersProcess).append(TSV_DELIMITTER);
    sb.append("Work Conditions").append(TSV_DELIMITTER);
    sb.append("Work Function").append(TSV_DELIMITTER);
    
    sb.append(TraitType.Collaborate).append(TSV_DELIMITTER);
    sb.append(TraitType.Compromise).append(TSV_DELIMITTER);
    sb.append(TraitType.Accommodate).append(TSV_DELIMITTER);
    sb.append(TraitType.Avoid).append(TSV_DELIMITTER);
    sb.append(TraitType.Compete).append(TSV_DELIMITTER);
    
    sb.append(TraitType.Control).append(TSV_DELIMITTER);
    sb.append(TraitType.Security).append(TSV_DELIMITTER);
    sb.append(TraitType.Significance).append(TSV_DELIMITTER);
    
    sb.append(TraitType.Outward).append(TSV_DELIMITTER);
    sb.append(TraitType.Inward).append(TSV_DELIMITTER);
    sb.append(TraitType.Careful).append(TSV_DELIMITTER);
    sb.append(TraitType.Rapid).append(TSV_DELIMITTER);
    
    sb.append(TraitType.Analytical).append(TSV_DELIMITTER);
    sb.append(TraitType.Global).append(TSV_DELIMITTER);
    sb.append("\n");
  }
  
  /**
   * Tool to update the competency evaluation start date and end date to test out the auto
   * generation of email's.
   * 
   * <p>
   * Date type : startDate or endDate : start date or end date to update Op Type: inc / dec :
   * increment or decrement Num days : number of days to increment or decrement by
   * </p>
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the change request
   */
  public SPResponse competencyDateHelper(User user, Object[] params) {
    String companyId = (String) params[0];
    final String dateType = (String) params[1];
    String opType = (String) params[2];
    int numDays = (int) params[3];
    
    CompanyDao company = companyFactory.getCompany(companyId);
    Assert.notNull(company, "Company not found");
    Assert.isTrue(StringUtils.containsIgnoreCase(company.getName(), "Test"),
        "Must have test in name found :" + company.getName());
    Assert.isTrue(company.isEvaluationInProgress(), "Evaluation not in progress.");
    
    CompetencyEvaluation competencyEvaluation = competencyFactory.getCompetencyEvaluation(company
        .getCompetencyEvaluationId());
    Assert.notNull(competencyEvaluation, "Evalaution not found.");
    
    switch (dateType) {
    case "startDate":
      competencyEvaluation.setStartDate(updateDate(competencyEvaluation.getStartDate(), opType,
          numDays));
      break;
    case "endDate":
      competencyEvaluation
          .setEndDate(updateDate(competencyEvaluation.getEndDate(), opType, numDays));
      break;
    default:
      throw new InvalidRequestException("Date type not supported :" + dateType);
    }
    
    Assert.isTrue(competencyEvaluation.getStartDate().isBefore(competencyEvaluation.getEndDate()),
        "Start date cannot be after end date.");
    competencyFactory.update(competencyEvaluation);
    
    return new SPResponse().isSuccess();
  }
  
  private LocalDateTime updateDate(LocalDateTime dateToChange, String opType, int numDays) {
    return opType.equalsIgnoreCase("inc") ? dateToChange.plusDays(numDays) : dateToChange
        .minusDays(numDays);
  }
  
  /**
   * Fix the pulse request for the given user. The company must have an active pulse request.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return true if success else false
   */
  public SPResponse pulseReset(User user, Object[] params) {
    
    String email = (String) params[0];
    
    // getting the user
    User userToFix = userFactory.getUserByEmail(email);
    Assert.notNull(userToFix, "User not found :" + email);
    
    // get the pulse request
    PulseRequest pulseRequest = pulseFactory.getPulseRequest(userToFix.getCompanyId());
    Assert.notNull(pulseRequest, "No pulse request running for company.");
    
    final String pulseQuestionSetId = pulseRequest.getPulseQuestionSetId();
    PulseQuestionSet pulseQuestionSet = pulseFactory.getPulseQuestionSet(pulseQuestionSetId);
    Assert.notNull(pulseQuestionSet, "Pulse question set not found. " + pulseQuestionSetId);
    
    // check if there exists previous response by that user
    List<User> memberList = new ArrayList<User>();
    memberList.add(userToFix);
    final String pulseRequestId = pulseRequest.getId();
    List<PulseAssessment> pulseResult = pulseFactory.getPulseResults(pulseRequestId, memberList);
    if (!pulseResult.isEmpty()) {
      pulseResult.forEach(pulseFactory::delete);
    }
    
    final String pulseQuestionSetName = pulseQuestionSet.getName();
    final TodoRequest todoRequest = TodoRequest.newPulseRequest(pulseQuestionSetName,
        pulseRequestId, pulseRequest.getEndDate());
    
    final HashMap<String, Object> param = new HashMap<String, Object>();
    param.put(Constants.PARAM_END_DATE, MessagesHelper.formatDate(pulseRequest.getEndDate()));
    param.put(Constants.PARAM_PULSE_NAME, pulseQuestionSetName);
    param.put(Constants.PARAM_PULSE_REQUEST_ID, pulseRequestId);
    param.put(Constants.PARAM_DAY,
        pulseRequest.getEndDate().getDayOfWeek()
            .getDisplayName(TextStyle.FULL, Locale.getDefault()));
    param.put(Constants.PARAM_NOTIFICATION_URL_PARAM, pulseRequestId);
    
    todoFactory.addTodo(userToFix, todoRequest);
    param.put(
        Constants.PARAM_NOTIFICATION_MESSAGE,
        MessagesHelper.getMessage(LogActionType.PulseAssessmentStart.getMessageKey(),
            userToFix.getLocale(), pulseQuestionSetName));
    notificationProcessor.process(NotificationType.PulseStart, userToFix, userToFix, param, false);
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Added script for updating passwords to the field where password doesnot exist for old users.
   * 
   * @param user
   *          authenticated user.
   * @param param
   *          if any.
   * @return success of failure.
   */
  public SPResponse updatePasswords(User user, Object[] param) {
    
    List<User> find = mongoTemplate.find(Query.query(Criteria.where("passwords").exists(false)),
        User.class);
    find.stream().forEach(usr -> {
      
      if (StringUtils.isNotBlank(usr.getPassword())) {
        Password password = Password.newPassword(usr.getPassword(), usr.getPassword());
        usr.setPasswords(password);
        userRepository.updateGenericUser(usr);
      }
      
    });
    return new SPResponse();
  }

  /**
   * Reset the profile messages cache.
   * 
   * @param user
   *          logged in user
   * @return
   *      success
   */
  public SPResponse profileMessagesStaticCacheReset(User user) {
    ProfileControllerHelper.resetCache();
    return new SPResponse().isSuccess();
  }
 
  /**
   * Export the pulse data for the precision consulting.
   * 
   * @param response
   *            - HTTP response
   * @param pulseResultId
   *            - pulse result id
   */
  public void exportPulsePrecisionData(HttpServletResponse response, String pulseResultId) {
    try {

      PulseResults pulseResult = pulseRepository.findPulseResultById(pulseResultId);
      Assert.notNull(pulseResult, "Pulse result not found.");
      
      PulseQuestionSet pulseQuestionSet = pulseFactory.getPulseQuestionSet(pulseResult.getPulseQuestionSetId());
      Assert.notNull(pulseQuestionSet, "Pulse question set not found.");
      
      final List<PulseAssessment> allPulseAssessments = pulseRepository
          .getAllPulseAssessments(pulseResult.getPulseRequestId());

      response.setContentType("text/csv;charset=utf-8");
      response.setHeader("Content-Disposition", "attachment; filename=pulseDataExport.tsv");
      OutputStream resOs = response.getOutputStream();
      OutputStream buffOs = new BufferedOutputStream(resOs);
      final OutputStreamWriter outputwriter = new OutputStreamWriter(buffOs);
      StringBuffer sb = new StringBuffer();
      
      // adding the headers
      sb.append(TSV_DELIMITTER); // for the questions
      for (PulseAssessment assessment : allPulseAssessments) {
        sb.append(assessment.getMemberId()).append(TSV_DELIMITTER); // adding the users
      }
      sb.append("\n");
      
      // adding the questions
      final Map<String, List<PulseQuestionBean>> questions = pulseQuestionSet.getQuestions();
      final int assessmentResponseSize = allPulseAssessments.size();
      for (String categoryKey : pulseQuestionSet.getCategoryKeys()) {
        // adding the category name to the file
        addNameAndBlanks(sb, categoryKey, assessmentResponseSize);
        List<PulseQuestionBean> questionsList = questions.get(categoryKey);
        for (PulseQuestionBean question : questionsList) {
          final List<QuestionOptions> optionsList = question.getOptionsList();
          final int questionNumber = question.getNumber();
          sb.append(question.getDescription()).append(TSV_DELIMITTER);
          for (PulseAssessment assessment : allPulseAssessments) {
            Map<String, List<PulseSelection>> assessmentResponses = assessment.getAssessment();
            List<PulseSelection> list = assessmentResponses.get(categoryKey);
            PulseSelection pulseSelection = list.get(questionNumber);
            sb.append(optionsList.get(pulseSelection.getSelectionIndex()).getText()).append(TSV_DELIMITTER);
          }
          sb.append("\n");
        }
      }
      LOG.debug("The export for prism portrait.");
      LOG.debug(sb.toString());
      outputwriter.write(sb.toString());
      outputwriter.flush();
      outputwriter.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void addNameAndBlanks(StringBuffer sb, String categoryKey, int assessmentResponseSize) {
    sb.append(categoryKey).append(TSV_DELIMITTER);
    for (int i = 0; i < assessmentResponseSize; i++) {
      sb.append(TSV_DELIMITTER);
    }
    sb.append("\n");
  }
}
