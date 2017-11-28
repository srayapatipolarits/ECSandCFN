package com.sp.web;

import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.library.ArticleLocation;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * The constants class.
 * 
 * @author daxabraham
 * 
 */
@Component
public class Constants {
  
  @Inject
  public Constants(Environment environment) {
    BASE_URL = environment.getProperty("base.serverUrl");
    VIEW_TOKEN_ERROR = "redirect:" + BASE_URL + "?status=404";
    VIEW_BLUEPRINT_ALREADY_PROCESSED = "redirect:" + BASE_URL + "?status=blueprintRequestSubmitted";
    VIEW_BLUEPRINT_REQUEST_CANCELLED = "redirect:" + BASE_URL + "?status=blueprintRequestCancelled";
    VIEW_COMPETENCY_EVALUATION = "competencyEvaluation";
    VIEW_COMPETENCY_EVALUATION_ENDED = "redirect:" + BASE_URL + "?status=competencyEvaluationEnded";
    VIEW_FEEDBACK_ALREADY_DECLINE = "redirect:" + BASE_URL + "?status=feedbackAlreadyDecline";
    
    VIEW_FEEDBACK_DECLINE = "redirect:" + BASE_URL + "?status=feedbackDecline";
    
    VIEW_FEEDBACK_TOKEN_USED = "redirect:" + BASE_URL + "?status=feedbackTokenUsed";
    
    try {
      PASSWORD_LOCKOUT_DURATION = Long.valueOf(environment.getProperty("password.accountLock"));
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    try {
      PASSWORD_EXPIRE_DURATION = Integer.valueOf(environment.getProperty("password.expiretime"));
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
  }
  
  public static String[] SUPPORTED_LOCALE = { "en_US", "es_LA" };
  public static final Set<String> SUPPORTED_LOCALE_SET = new HashSet<String>(
      Arrays.asList(SUPPORTED_LOCALE));
  
  public static String BASE_URL;
  public static String VIEW_TOKEN_ERROR;
  public static String VIEW_BLUEPRINT_ALREADY_PROCESSED;
  public static String VIEW_BLUEPRINT_REQUEST_CANCELLED;
  public static String VIEW_COMPETENCY_EVALUATION;
  public static String VIEW_COMPETENCY_EVALUATION_ENDED;
  public static String VIEW_FEEDBACK_ALREADY_DECLINE;
  public static String VIEW_FEEDBACK_DECLINE;
  public static String VIEW_FEEDBACK_TOKEN_USED;
  public static long PASSWORD_LOCKOUT_DURATION = 7200;
  public static int PASSWORD_EXPIRE_DURATION = 30;
  
  public static final String KEY_DELIMITTER = ":";
  public static final String ASSESSMENTS_PROCESSOR = "assessmentsProcessor";
  public static final double[] ACCURACY_RANGE_HIGH = { 0.6799d, 0.7299d, 0.7599d, 0.8199d, 0.8999d,
      0.9499d, 0.9949d, 1.0d };
  public static final double[] ACCURACY_RANGE_LOW = { .5d, .7d, .73d, .76d, .82d, .90d, .95d, 1.0d };
  
  public static final int PRECISION = 2;
  public static final int POWER_OF_TEN_SCALE = 2;
  public static final int POINT_MOVEMENT = 2;
  public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
  
  public static final BigDecimal ZERO_VALUE = new BigDecimal(0).setScale(PRECISION, ROUNDING_MODE);
  public static final double PERSONALITY_MULTIPLIER = (1d / 7d);
  public static final int PERCENT_PRECISION = 2;
  public static final int PRICE_PRECISION = 2;
  public static final int PRICE_ROUNDING_PRECISION = 0;
  public static final int PULSE_PRECISION = 5;
  
  public static final String PERSONALITY_MOST = "Most";
  public static final String PERSONALITY_LEAST = "Least";
  
  public static final String[] ASSESSMENT_QUESTIONS_MALE_FORMAT = { "he", "He", "him", "himself",
      "his", "his", "His" };
  public static final String[] ASSESSMENT_QUESTIONS_FEMALE_FORMAT = { "she", "She", "her",
      "herself", "her", "hers", "Her" };
  
  public static final String[] ASSESSMENT_QUESTIONS_NEUTRAL_FORMAT = { "they", "They", "them",
    "themself", "their", "theirs", "Their" };

  
  public static final String DEFAULT_COMPANY_ID = "default";
  /**
   * Default Hiring Product Name.
   */
  public static final String HIRING_PRODUCT_NAME = "Hiring";
  public static final long HIRING_FILE_MAX_SIZE = 1000000;
  
  public static final int DAYS_OF_MONTHLY_BILLING = 30;
  public static final int DEFAULT_TRIAL_DAYS = 14;
  public static final int DEFAULT_EXPIRY_GRACE_TIME_DAYS = 20;
  public static final int DEFAULT_ACCOUNT_EXPIRY_DAYS = 5;
  public static final int DEFAULT_ACCOUNT_PRE_RECHARGE_NOTIFICATION = 10;
  
  /**
   * Email Templates.
   */
  public static final String ADD_MEMBER_TEMPLATE = "templates/email/addMember.stg";
  public static final String COPY_PROFILE_TEMPLATE = "templates/email/copyProfile.stg";
  public static final String PAYMENT_RECEIPT_TEMPLATE = "/templates/email/payment/historyReceipt.stg";
  
  /**
   * Default values.
   */
  public static final long DEFAULT_EXPIRES_TIME = 24;
  public static final String DEFAULT_EXPIRES_TIME_UNIT_STR = "HOURS";
  public static final TimeUnit DEFAULT_EXPIRES_TIME_UNIT = TimeUnit.HOURS;
  public static final String[] SEARCH_FIELDS_LIST = new String[] { "firstName", "lastName",
      "email", "title", "groupAssociationList", "userStatus", "statusMessage", "tagList", "roles" };
  public static final String[] SEARCH_PEEK_FIELDS_LIST = new String[] { "firstName", "lastName" };
  public static final String DEFAULT_TUTORIAL_ID_KEY = "sp.tutorial.default";
  
  /**
   * Constant definitions for parameters.
   */
  public static final String PARAM_USER_ID = "userId";
  public static final String PARAM_USER_EMAIL = "userEmail";
  public static final String PARAM_GROWTH_REQUEST_ID = "growthRequestId";
  public static final String PARAM_GROWTH_MEMB_EMAIL = "memberEmail";
  public static final String PARAM_TOKEN = "token";
  public static final String PARAM_ERROR = "error";
  public static final String PARAM_ERROR_CODE = "error_code";
  public static final String PARAM_EXPIRES_TIME = "expiresTime";
  public static final String PARAM_TIME_UNIT = "timeUnit";
  public static final String PARAM_TIME_ZONE = "timeZome";
  public static final String PARAM_MEMBER_LIST = "memberList";
  public static final String PARAM_USER_PROFILE_FORM = "userProfileForm";
  public static final String PARAM_QUESTION = "question";
  public static final String PARAM_ANALYSIS = "Analysis";
  public static final String PARAM_LAST_ANSWER = "lastAnswer";
  public static final String PARAM_MEMBER = "member";
  public static final String PARAM_SUBJECT = "subject";
  public static final String PARAM_EMAIL_BODY = "emailBody";
  public static final String PARAM_AVAILABLE_MEMBER_SUBSCRIPTIONS = "availalbeMemberSubscriptions";
  public static final String PARAM_AVAILABLE_ADMIN_SUBSCRIPTIONS = "availableAdminSubscriptions";
  public static final String PARAM_NAME = "name";
  public static final String PARAM_NUMBER_OF_MEMBERS = "numberOfMembers";
  public static final String PARAM_RENEWAL_TOTAL = "renewalTotal";
  public static final String PARAM_RENEWAL_DATE = "renewalDate";
  public static final String PARAM_AVAILABLE_HIRING_SUBSCRIPTIONS = "availableHiringSubscripitons";
  public static final String PARAM_USED_HIRING_SUBSCRIPTIONS = "usedHiringSubscriptions";
  public static final String PARAM_ACCOUNT = "account";
  public static final String PARAM_COMPANY = "company";
  public static final String PARAM_USER_PROFILE_SETTINGS = "profileSettings";
  public static final String PARAM_ROLES = "roles";
  public static final String PARAM_HIRING_PRODUCT = "hiring";
  public static final String PARAM_PROMOTION = "promotion";
  public static final String PARAM_PAYMENT_RECORD = "payment";
  public static final String PARAM_PRODUCT = "product";
  public static final String PARAM_SPPLANS = "spPlans";
  public static final String PARAM_PAYMENT_HISTORY = "paymentHistory";
  public static final String PARAM_ACTIVE_MEMBER_COUNT = "activeMembers";
  public static final String PARAM_PRODUCT_VALIDITY = "validityType";
  public static final String PARAM_HIRING_MEMBERS = "hiring";
  public static final String PARAM_DELETE_COUNT = "deleted";
  public static final String PARAM_ARCHIVE_COUNT = "archived";
  public static final String PARAM_HIRING_MEMBERS_ARCHIVED = "hiringArchived";
  public static final String PARAM_HIRING_CANDIDATE_ANALYSIS = "candidateAnalysis";
  public static final String PARAM_MEMBER_ANALYSIS = "memberAnalysis";
  public static final String PARAM_HIRING_REFERENCE_USER_ID = "hiringReferenceId";
  public static final String PARAM_HIRING_USER_ID = "hiringUserId";
  public static final String PARAM_HIRING_MEMBER_COUNT = "numHiringCandidates";
  public static final String PARAM_HIRING_ROLES = "hiringRoles";
  public static final String PARAM_GROWTH_MEMBER_EMAIL = "growthExternalMemberEmail";
  public static final String PARAM_RELATIONSHIP_MANAGER_REPORT_PRIMARY = "primary";
  public static final String PARAM_RELATIONSHIP_MANAGER_REPORT_SECONDARY = "secondary";
  public static final String PARAM_PROFILE_IMAGE_SMALL = "small";
  public static final String PARAM_PROFILE_IMAGE_MEDIUM = "medium";
  public static final String PARAM_PROFILE_IMAGE_LARGE = "large";
  public static final String PARAM_DONE = "done";
  public static final String PARAM_FAILURE = "failed";
  public static final String PARAM_ADDRESS = "address";
  public static final String PARAM_PULSE_REQUEST_ID = "pulseRequestId";
  public static final String PARAM_PULSE_RESULTS = "pulseResults";
  public static final String PARAM_PULSE_SCORE = "pulseScore";
  public static final String PARAM_PULSE_QUESTION = "pulseQuestion";
  public static final String PARAM_END_DATE = "endDate";
  public static final String PARAM_TASK_LIST = "tasks";
  public static final String PARAM_TAG_LIST = "tagList";
  public static final String PARAM_NOTIFICATION_LOG_MESSAGES = "notifications";
  public static final String PARAM_ACTIVITY_LOG_MESSAGES = "activityMessages";
  public static final String PARAM_GROUP_LIST = "groupList";
  public static final String PARAM_TITLE = "title";
  public static final String PARAM_MESSAGE = "message";
  public static final String PARAM_LOG_TYPE = "logType";
  public static final String PARAM_FOR_USER = "forUser";
  public static final String PARAM_GROWTH_REQUEST = "growthRequest";
  public static final String PARAM_USER_TYPE = "userType";
  public static final String PARAM_EXISTING_USER_ID = "existingUser";
  public static final String PARAM_EXISTING_USER_TYPE = "existingUserType";
  public static final String PARAM_USER = "user";
  public static final String PARAM_RELATED_ARTICLES = "relatedArticles";
  public static final String PARAM_TRAINING_SPOT_LIGHT = "trainingSpotLight";
  public static final String PARAM_NAVIGATION = "navigation";
  public static final String PARAM_NUMBER_OF_ACTIVE_MEMBER = "numActive";
  public static final String PARAM_NUMBER_OF_DELETED_MEMBER = "numDeleted";
  public static final String PARAM_GROUP_MEMBER_COUNT = "numMembers";
  public static final String PARAM_ARTICLE = "article";
  public static final String PARAM_GOAL = "goal";
  public static final String PARAM_AUTHOR = "author";
  public static final String PARAM_ARTICLE_SOURCE = "articleSource";
  public static final String PARAM_ARTICLE_IMAGE = "articleImage";
  public static final String PARAM_ARTICLE_LINK_NAME = "articleLinkName";
  public static final String PARAM_TIP_OF_THE_DAY = "tipOfTheDay";
  public static final String PARAM_ARTICLE_CONTENT = "content";
  public static final String PARAM_PTP_ANALYSIS = "ptpAnalysis";
  public static final String PARAM_MATCH_PERCENT = "matchPercent";  
  public static final String PARAM_PRACTICE_AREAS = "pratcieAreas";
  public static final String PARAM_ACTION_PLAN = "actionPlan";
  public static final String PARAM_DESCRIPTION = "description";
  public static final String PARAM_URL = "url";
  public static final String PARAM_THUMBNAIL_URL = "thumbnailURL";
  public static final String PARAM_STATUS = "status";
  public static final String PARAM_ACTION_PLAN_LIST = "actionPlanList";
  public static final String PARAM_START_DATE = "startDate";
  public static final String PARAM_MEETING_INVITE_LOCATION = "meetingLocation";
  public static final String PARAM_MEETING_INVITE_AGENDA = "meetingAgenda";
  public static final String PARAM_BLUEPRINT = "blueprint";
  public static final String PARAM_BLUEPRINT_APPROVAL_REQUEST = "blueprintApprovalRequest";
  public static final String PARAM_COMMENT_REMOVED = "commentRemoved";
  public static final String PARAM_BLUEPRINT_UPDATE = "blueprintUpdate";
  public static final String PARAM_BLUEPRINT_SHOW_PROGRESS = "showProgress";
  public static final String PARAM_BLUEPRINT_INTRO = "blueprintIntro";
  public static final String PARAM_EMAIL_MANAGEMENT = "emailManagement";
  public static final String PARAM_NOTIFICATION_LIST = "notificationList";
  public static final String PARAM_COMPANY_COMPETENCY_LIST = "companyCompetencyList";
  public static final String PARAM_COMPETENCY_PROFILE = "competencyProfile";
  public static final String PARAM_ARTICLE_LIST = "articles";
  public static final String PARAM_EVALUATION_IN_PROGRESS = "evaluationInProgress";
  public static final String PARAM_TASK_TITLE = "title";
  public static final String PARAM_TASK_DESCRIPTION = "description";
  public static final String PARAM_TASK_LINK_TEXT = "linkText";
  public static final String PARAM_TASK_LINK_URL = "linkUrl";
  public static final String PARAM_COMPETENCY_EVALUATION = "competencyEvaluation";
  public static final String PARAM_USER_COMPETENCY_EVALUATION = "competencyEvaluationUsers";
  public static final String PARAM_TOTAL_MEMBER_COUNT = "totalMemberCount";
  public static final String PARAM_TOTAL_ACTIVE_MEMBER_COUNT = "activeMemberCount";
  public static final String PARAM_TOTAL_MEMBER_WITH_COMPETENCY_COUNT = "membersWithCompetency";
  public static final String PARAM_COMPETENCY_SCORE_TOTAL = "totalScore";
  public static final String PARAM_COMPETENCY_SCORE_LIST = "competencyScoreList";
  public static final String PARAM_COMPETENCY_REVIEWER = "reviewer";
  public static final String PARAM_COMPETENCY_EVALUATION_PEER = "peerEvaluation";
  public static final String PARAM_GROUP_DISCUSSION_LISTING = "gdListing";
  public static final String PARAM_GROUP_DISCUSSION = "gd";
  public static final String PARAM_GROUP_DISCUSSION_COMMENT = "gdComment";
  public static final String PARAM_OPERATION = "op";
  public static final String PARAM_GROUP_DISCUSSION_UNREAD_COUNT = "unreadCount";
  public static final String PARAM_NEWS_FEED = "newsFeed";
  public static final String PARAM_DASHBOARD_MESSAGE = "message";
  public static final String PARAM_DASHBOARD_MESSAGE_ID = "messageId";
  public static final String PARAM_DASHBOARD_MESSAGE_LIKE_COUNT = "likeCount";
  public static final String PARAM_ACTIVITY_FEED = "activityFeeds";
  public static final String PARAM_CONTENT_REFERENCE = "contentRef";
  public static final String PARAM_DASHBOARD_MESSAGE_DO_LIKE = "doLike";
  public static final String PARAM_DASHBOARD_MESSAGE_LIKE_USERS = "likeUsers";
  public static final String PARAM_PAGINATION = "pagination";
  public static final String PARAM_TODO = "todo";
  public static final String PARAM_TODO_REF_ID = "refId";
  public static final String PARAM_TODO_PARENT_REF_ID = "parentRefId";
  public static final String PARAM_TODO_TYPE = "type";
  
  public static final String PARAM_ACTION_PLAN_LISTING = "actionPlanListing";
  public static final String PARAM_ACTION_PLAN_NAME = "actionPlanName";
  public static final String PARAM_ACTION_PLAN_HAMBURGER = "showHamburger";
  public static final String PARAM_ACTION_PLAN_DESCRIPTION = "actionPlanDescription";
  public static final String PARAM_ACTION_PLAN_ERROR = "actionPlanError";
  public static final String PARAM_ACTION_PLAN_ENABLE_NEXT_STEP = "enableNextStep";
  public static final String PARAM_START_IDX = "startIdx";
  public static final String PARAM_DASHBOARD_MESSAGE_COMMENT_ID = "cid";
  public static final String PARAM_DASHBOARD_MESSAGE_CHILD_COMMENT_ID = "childCid";
  public static final String PARAM_TONE_ANALYSIS_AGGREGATE = "toneAnalysisAgg";
  public static final String PARAM_TONE_ANALYSIS_DETAILS = "toneAnalysisDetails";
  public static final String PARAM_RELATIONSHIP_ADVISOR_SHOW = "showRelationshipAdvisor";
  public static final String PARAM_MESSAGE_ACTIVITY = "activityLogMessage";
  public static final String PARAM_NOTIFICATION_URL_PARAM = "notifyParam";
  public static final String PARAM_NOTIFICATION_MESSAGE = "notificationLogMessage";
  public static final String PARAM_NOTIFICATION_COUNT = "notifyCount";
  public static final String PARAM_NOTIFICATION_SHOW_BOX = "showBox";
  public static final String PARAM_TUTORIAL = "tutorial";
  public static final String PARAM_PRIMARY_PRACTICE_AREAS = "primaryGoals";
  public static final String PARAM_UNDER_PRESSURE_PRACTICE_AREAS = "underPressureGoals";
  public static final String PARAM_USER_MARKETING = "marketing";
  public static final String PARAM_COMPETENCY_EVALUATION_COUNT = "evaluationCount";
  public static final String PARAM_SPECTRUM_COMPETENCY_PROFILE_LIST = "competencyProfileListing";
  public static final String PARAM_SPECTRUM_COMPETENCY_PROFILE = "competencyProfileDetails";
  public static final String PARAM_COMPETENCY_EVALUATION_USERS = "users";
  public static final String PARAM_COMPETENCY_EVALUATION_ID = "evaluationId";
  public static final String PARAM_COMPETENCY_EVALUATION_MANAGER = "managerEvaluation";
  
  /**
   * Constant definitions for views.
   */
  
  public static final String VIEW_ADD_MEMBER = "signupMember";
  public static final String VIEW_ADD_INDIVIDUAL_MEMBER = "signupIndividualMember";
  public static final String VIEW_RESET_PASSWORD = "changePassword";
  public static final String VIEW_GROWTH_EXTERNAL_PROFILE_CONFIRM = "confirmDetails";
  public static final String VIEW_ERROR = "error";
  public static final String VIEW_INCOMPLETE_ASSESSMENT_HOME = "redirect:/signin?login_error=8";
  public static final String VIEW_BLUEPRINT_SHARE = "blueprintShare";
  public static final String VIEW_BLUEPRINT_PUBLISH_SHARE = "blueprintPublishShare";
  
  /**
   * Constant definitions for messages keys or application properties keys.
   */
  public static final String KEY_ADD_MEMBER_EXPIRES_TIME = "token.addMember.expires";
  public static final String KEY_ADD_MEMBER_EXPIRES_TIME_UNIT = "token.addMember.expires.timeUnit";
  public static final String KEY_PROFILE_INCOMPLETE = "user.status.profile.incomplete";
  public static final String KEY_NAME = "user.name";
  public static final String KEY_PAYMENT_REASON_PREFIX = "payments.reason.";
  public static final String KEY_PAYMENT_REASON_NEW_ACCOUNT = KEY_PAYMENT_REASON_PREFIX
      + "newAccount.";
  public static final String KEY_PAYMENT_REASON_RECHARGE_ACCOUNT = KEY_PAYMENT_REASON_PREFIX
      + "rechargeAccount.";
  
  // New Default Profile Image paths
  public static final String DEFAULT_IMAGE_PATH_KEY = "default.profile.image";
  public static final String KEY_DEFAULT_TRIAL = "account.trial.expiry.days";
  public static final String KEY_DEFAULT_PAYMENT_FAILURE_GRACE_TIME = "account.payment.grace.days";
  public static final String KEY_DEFAULT_ACCOUNT_EXPIRY_GRACE = "account.expiry.grace.days";
  
  /**
   * Default Error messages.
   */
  public static final String ERROR_MESSAGE_TOKEN_EXPIRED = "Token has expired.";
  
  /** Bundle base package name. */
  public static String messagePath = "{0}/messages";
  
  /** Bundle base package name. */
  public static String MESSAGE_BASE_NAME = "messages";
  public static String RELATIONSHIP_MESSAGE_BASE_NAME = "relationshipMessages";
  public static String CM_MESSAGE_BASE_NAME = "conflictManagmentMessages";
  public static String DM_MESSAGE_BASE_NAME = "decisonMessages";
  public static String FUNDAMENTAL_MESSAGE_BASE_NAME = "fundamentalMessages";
  public static String LS_MESSAGE_BASE_NAME = "=learningstyleMessages";
  public static String MOTIVATION_MESSAGE_BASE_NAME = "motivationMessages";
  public static String PROCESSING_MESSAGE_BASE_NAME = "processingMessages";
  public static String PROFILE_MESSAGE_BASE_NAME = "profileMessages";
  public static String GT_MESSAGE_BASE_NAME = "growthMessages";
  public static String GT_ASSESMENT_QUUESTION_MESSAGE_BASE_NAME = "growthAssessmentQuestions";
  public static String PROFILE_TRAITS_MESSAGE_BASE_NAME = "traitsMessages";
  public static String LOG_MESSAGE_BASE_NAME = "logMessages";
  public static String EXCEPTION_MESSAGE_BASE_NAME = "errorMessages";
  public static String PERSONALITY_TYPE_BASE_NAME = "personalityTypeMessages";
  public static String OG_BASE_NAME = "ogMessages";
  public static String FB_MESSAGE_BASE_NAME = "feedbackMessages";
  
  /**
   * The Entity Keys.
   */
  public static final String ENTITY_USER_ROLES = "roles";
  public static final String ENTITY_COMPANY_ID = "companyId";
  public static final String ENTITY_EMAIL = "email";
  public static final String ENTITY_HIRING_ROLES = "hiringRoleIds";
  public static final String ENTITY_USER_STATUS = "userStatus";
  public static final String ENTITY_TAGS = "tagList";
  public static final String ENTITY_MEMBER_ID = "memberId";
  public static final String ENTITY_USER_ID = "userId";
  public static final String ENTITY_CREATED_ON = "createdOn";
  public static final String ENTITY_PROFILE_TOKEN = "profileSettings.token";
  public static final String ENTITY_ACCESS_TIME = "accessTime";
  public static final String ENTITY_ID = "_id";
  public static final String ENTITY_CERTIFICATE_NUMBER = "certificateNumber";
  public static final String ENTITY_ARTICLE_LINK_LABEL = "articleLinkLabel";
  public static final String ENTITY_ARTICLE_POSITION = "articlePosition";
  public static final String ENTITY_ARTICLE_LOCATION = "articleLocation";
  public static final String ENTITY_ACCOUNT_ID = "accountId";
  public static final String ENTITY_IS_FOR_ALL = "isForAll";
  public static final String ENTITY_STATUS = "status";
  public static final String ENTITY_ACTIVE = "active";
  public static final String ENTITY_NAME = "name";
  public static final String ENTITY_FEEDBACK_FOR = "feedbackFor";
  public static final String ENTITY_FEATURE_TYPE = "featureType";
  public static final String ENTITY_BLUEPRINT_ID = "blueprintId";
  public static final String ENTITY_FEATURE_LIST = "featureList";
  public static final String ENTITY_GOAL_ID = "goalId";
  public static final String ENTITY_TYPE = "type";
  public static final String ENTITY_COMPETENCY_PROFILE_ID = "competencyProfileId";
  public static final String ENTITY_FEEDBACK_USER_ID = "feedbackUserId";
  public static final String ENTITY_COMPLETED = "completed";
  public static final String ENTITY_SRC_ID = "srcId";
  public static final String ENTITY_POSTED_TO_DASHBOARD = "postedToDashboard";
  public static final String ENTITY_ACTION_PLAN_ID = "actionPlanId";
  public static final String ENTITY_CREATED_BY_COMPANY_ID = "createdByCompanyId";
  public static final String ENTITY_COMPANY_IDS = "companyIds";
  public static final String ENTITY_ALL_COMPANY = "allCompanies";
  public static final String ENTITY_STEP_TYPE = "stepType";
  public static final String ENTITY_DEV_FEED_REF_ID = "devFeedRefId";
  public static final String ENTITY_DEV_FEED_PARENT_REF_ID = "feedParentRefId";
  public static final String ENTITY_OWNER_ID = "ownerId";
  public static final String ENTITY_USER_IDS = "userIds";
  public static final String ENTITY_PORTRAIT_ID = "portraitId";
  
  public static final String S3_FILE_PREFIX = "s3.file.url.prefix";
  /* Growth Constants */
  public static final String GROWTH_ASSESSMENT_QUESTIONS_PREFIX = "growth.assessment.questions.";
  public static final String GROWTH_ASSESSMENT_QUESTIONS_OPTIONAL_TEXT = ".optionalText";
  public static final String PARAM_GROWTH_REQ_ACCEPT_SUCCESS_KEY = "growthAccepted";
  public static final String PARAM_GROWTH_REQ_ACCEPT_FAILURE_KEY = "growthRejected";
  public static final String PARAM_GROWTH_EMAIL_ACCEPT_SUB = "growth.request.feedback.accepted.email.subject";
  public static final String PARAM_GROWTH_EMAIL_ACCEPT_BODY = "growth.request.feedback.accepted.email.body.p1";
  public static final String PARAM_GROWTH_EMAIL_REJECT_BODY = "growth.request.feedback.rejected.email.subject";
  public static final String PARAM_GROWTH_EMAIL_REJECT_SUB = "growth.request.feedback.rejected.email.subject";
  
  public static final String PARAM_GROWTH_EMAIL_FEEDBACK_SUBMITTED_BODY = "growth.request.feedback.given.email.body.p1";
  public static final String PARAM_GROWTH_EMAIL_FEEDBACK_SUBMITTED_SUB = "growth.request.feedback.given.email.subject";
  
  public static final String GT_MESSAGE_NO_TEAM_HEADING = "growth.invite.nomember.heading";
  
  public static final String GT_MESSAGE_NO_TEAM_MESSAGE = "growth.invite.nomember.message";
  public static final String VIEW_GROWTH_EXTERNAL_SET_INTERVAL = "externalDetailRequest";
  public static final String VIEW_GROWTH_EXTERNAL_FEEDBACK = "externalDetailEditRequest";
  public static final String PARAM_GROWTH_REQUESTED_BY = "requestedBy";
  public static final String PARAM_FIRSTNAME = "firstName";
  public static final String PARAM_LASTNAME = "lastName";
  public static final String PARAM_LINKEDIN_URL = "linkedin";
  public static final String PARAM_EMAIL = "email";
  public static final String PARAM_BASE_SERVER_URL = "baseUrl";
  public static final String PARAM_COMPETENCY_USER_COUNT = "userCount";
  
  
  /* Feedback contatnt */
  public static final String FB_MESSAGE_NO_TEAM_HEADING = "feedback.invite.nomember.heading";
  
  public static final String FB_MESSAGE_NO_TEAM_MESSAGE = "feedback.invite.nomember.message";
  public static final String FB_ERROR_INVALID_PARAMA = "fb.error.invalid.request.params";
  public static final String PARAM_FEEDBACK_REQUEST_ID = "feedbackRequestId";
  public static final String PARAM_FEEDBACK_USERID = "feedbackUserId";
  public static final String PARAM_FEEDBACK_FOR_ID = "feedbackForUserId";
  public static final String VIEW_FEEDBACK_EXTERNAL_PROFILE_CONFIRM = "externalFeedbackConfirmDetail";
  public static final String VIEW_FEEDBACK_DETAIL = "feedbackDetail";
  public static final String PARAM_COMMENT = "comment";
  public static final String PARAM_FEEDBACK_DEFAULT_COMMENT = "feedback.default.comment";
  public static final String FB_ERROR_NO_FR_PRESENT = "fb.error.no.requestpresent";
  public static final String FB_FEEDBACK_NOT_GIVEN = "fb.feedback.notgiven";
  public static final String FB_FEEDBACK_LAST_DAY = "fb.feedback.lastday";
  public static final String PARAM_FEEDBACK_REQUESTED_BY = "email";
  public static final String PARAM_FEEDBACK_USER = "feedbackUser";
  public static final String PARAM_HIRING_REQUEST = "hiringReferences";
  public static final String PARAM_FEEDBACK_REQUEST = "feedbackFlow";
  public static final String NEWS_CRED_URL = "newscred.url";
  public static final String NEWS_CRED_COLLECTION_URL = "newscred.collection.url";
  public static boolean isUnderPressureEnabled = false;
  public static DecimalFormat promoFormatter = new DecimalFormat("000000");
  public static DecimalFormat ACCOUNT_FORMATTER = new DecimalFormat("000000");
  
  /*
   * Dashboard limits
   */
  public static final int DASHBOARD_LOG_MESSAGE_LIMIT = 30;
  public static final String NOTIFICATION_SUBJECT_PREFIX = "notification.subject.";
  
  public static final int DEFAULT_ARTICLES_COUNT = 10;
  public static final int DEFAULT_DASHBOARD_GOALS_ARTICLE_COUNT = 5;
  public static final int DEFAULT_GOALS_CURRICULUM_COUNT = 6;
  public static final int MINIMUM_PRACTICE_AREA_COUNT = 1;
  public static final int MAXIMUM_PRACTICE_AREA_COUNT = 12;
  
  /** chhanged from 3 to 5 as requested by Yanni */
  public static final long DEFAULT_RELATED_ARTICLE_COUNT = 5;
  public static final long DEFAULT_TRENDING_ARTICLE_COUNT = 5;
  public static final int DEFAULT_TOP_RATED_ARTICLE_COUNT = 5;
  public static final int DASHBOARD_NOTES_FEEDBACK_LIMIT = 5;
  
  /* Payment gateway */
  public static final boolean PAYMENTGATEWAY_IS_TAX_EXEMPT = Boolean.TRUE;
  public static final String AUTHROIZED_ENVIORNMENT = "authroized.enviornment";
  public static final String PAYMENT_GATEWAY = "paymentGateway.";
  public static final String SUREPEOPLE_AUTHORIZED_NET_TRANSACTION_KEY = "surepeople.authorized.net.transactionKey";
  public static final String SUREPEOPLE_AUTHORIZED_NET_ACCOUNT_ID = "surepeople.authorized.net.accountId";
  
  public static final String USER_MAILING_ADDRESS_FORMAT = "user.mailingAddress.format";
  public static final String REASON = "reason";
  public static final String FEDRAL_TAX_ID = "fedralTaxId";
  public static final String PAYMENT_RECORD = "paymentRecord";
  
  /** SP RESUME. */
  public static final String SPRESUME_PDF_TEMPLATE = "templates/pdf/resume.stg";
  public static final String SPRESUME_FILE_NAME_PREFIX = "Resume_";
  public static final String CONTENT_TYPE_PDF = "application/pdf";
  
  public static final String DATE_FORMAT_DEFAULT = "date.format.default";
  public static final String DATE_FORMAT_COMPETENCY = "competency";
  
  /** UserUpdateAction */
  public static final String ACTIONS = "Actions";
  public static final String PARAMS = "Params";
  public static final String DO_LOGOUT = "doLogout";
  public static final String USER_UPDATE_ACTION = "UserUpdateAction.";
  /** Blueprint. */
  public static final String PARAM_BLUEPRINT_SETTINGS = "blueprintSettings";
  public static final String BLUEPRINT_DEFAULT_COMPANY_ID = "default";
  public static final String PARAM_BLUEPRINT_COMPANY_lIST = "blueprintCompanyList";
  public static final String BLUEPRINT_MIN_OBJECTIVES = "minObjectives";
  public static final String BLUEPRINT_MAX_OBJECTIVES = "maxObjectives";
  public static final String BLUEPRINT_MIN_INITIATIVES = "minInitiatives";
  public static final String BLUEPRINT_MAX_INITIATIVES = "maxInitiatives";
  public static final String BLUEPRINT_MIN_SUCCESS_MEASURES = "minCsm";
  public static final String BLUEPRINT_MAX_SUCCESS_MEASURES = "maxCsm";
  public static final String BLUEPRINT_OBJECTIVES = "objective";
  public static final String BLUEPRINT_INITIATIVES = "initiatives";
  public static final String BLUEPRINT_SUCCESS_MEASURES = "csm";
  public static final String BLUEPRINT_SHARE_INTRO_KEY = "blueprint.feedback.intro";
  public static final String BLUEPRINT_APPROVAL_INTRO_KEY = "blueprint.approval.intro";
  
  public static final int BLUEPRINT_DEFAULT_MIN = 1;
  public static final int BLUEPRINT_DEFAULT_MAX = 10;
  /**
   * Ajax timeout to be used for showing error message.
   */
  public static final String SESSION_TIMEOUT = "sessionTimeout";
  public static final String TRAINING_LIBRARY_HOME_INDIVIDUAL = "individual";
  public static final String TRAINING_LIBRARY_HOME_BUSINESS = "business";
  public static final String PARAM_PULSE_NAME = "pulseName";
  public static final String PARAM_DAY = "day";
  public static final String PARAM_TEMP_PASSWORD = "tempPassword";
  public static final String PARAM_PAYMENT_TYPE = "paymentType";
  
  public static final String PARAM_INTERNAL_MARKETING_ANALYTICS_EMAIL = "internalMarketingAnalyticsEmail";
  
  public static final String PARAM_INTERNAL_MARKETING_ANALYTIC = "internalMarketingAnalytic";
  
  public static final String PARAM_INTERNAL_MARKETING_EMAIL_SUBJECT = "internalMarketing.email.subject";
    
  /** Different Media Types **/
  public static final String MEDIA_IMAGE = "media/images";
  public static final String MEDIA_PDF = "media/pdfs";
  public static final String MEDIA_PPT = "media/ppts";
  public static final String MEDIA_MSDOC = "media/msdocs";
  
  /** Max file upload size through Media Manager */
  public static final long MAX_FILE_UPLOAD_SIZE = 10000000;
  
  /** practice area default personality weight */
  public static final int DEFAULT_PERSONALITY_WEIGHT = 5;
  public static final String PERSONALITY_PRIMARY = "PersonalityPrimary";
  public static final String PERSONALITY_UNDERPRESSURE = "PersonalityUnderPressure";
  
  public static final String PARAM_PLAN_BILLING_CYCLE = "planBillingCycle";
  public static final String PARAM_PLAN_BILLING_CYCLE_TYPE = "planBillingCycleType";
  public static final String PARAM_CHARGEAMOUNT = "chargeAmount";
  public static final String BILLING_AR_EMAIL = "billing.ar.email";
  
  public static final String DEFAULT_BILLING_AR_EMAIL = "ar@surepeople.com";
  
  public static final String THEME = "Themes";
  
  /** SWOT params. */
  
  /** Swot types params. */
  public static final String PARAM_SWOT_TYPES = "swotTypes";
  
  /** Company Theme Detail. */
  public static final String PARAM_COMP_THEME_DETAIL = "compThemeDetail";
  public static final String DEFAULT_COMPANY_THEME = "companyDefaultTheme";
  public static final String DEFAULT_THEME_NAME = "default";
  public static final String DEFAULT_COMPANY_THEME_URL = "companyDefaultThemeUrl";
  public static final String PARAM_COMPANY_LIST = "companyList";
  public static final String COMP_THEME_DEFAULT_COMPANY_ID = "default";
  public static final String DEFAULT_COMPANY_LOGO_URL = "/resources/images/sp-logo.svg";
  public static final String DEFAULT_COMPANY_LOGO_CSS = "default";
  
  /** Spectrum constants */
  public static final String PARAM_GROUPS = "groups";
  public static final String PARAM_TAGS = "tags";
  public static final String PARAM_AGE = "age";
  public static final String PARAM_TRAITS = "trait";
  public static final String PARAM_CATEGORY_TYPE = "categoryType";
  
  /** Email PManagement */
  public static final String PARAM_TEMPLATE_NAME = "templateName";
  public static final String PARAM_EMAIL_SUBJECT = "subject";
  public static final String PARAM_EMAIL_ORANGE_BAND = "emailHeader";
  public static final String PARAM_LINK_TEXT = "linkText";
  public static final String DEFAULT_EMAIL_TEMPLATE = "templates/email/genericEmailManagementMessage.stg";
  public static final String PARAM_ISEMAIL_MANANGEMENT = "isEmailManagement";
  public static final String PARAM_LINK_URL = "linkUrl";
  
  /** SHare ARTICLE */
  public static final String PARAM_PLAY_AUDIO = "trainingLibrary.article.cta.playaudio";
  public static final String PARAM_PLAY_PODCAST = "trainingLibrary.article.cta.playpodcast";
  public static final String PARAM_PLAY_VIDEO = "trainingLibrary.article.cta.playvideo";
  public static final String PARAM_PLAY_SLIDESHARE = "trainingLibrary.article.cta.playslidshare";
  public static final String PARAM_PLAY_READARTICLE = "trainingLibrary.article.cta.readArticle";
  public static final String PARAM_ORANGEBAND_AUDIO = "trainingLibrary.article.orangeband.audio";
  public static final String PARAM_ORANGEBAND_PODCAST = "trainingLibrary.article.orangeband.podcast";
  public static final String PARAM_ORANGEBAND_VIDEO = "trainingLibrary.article.orangeband.video";
  public static final String PARAM_ORANGEBAND_SLIDSHARE = "trainingLibrary.article.orangeband.slidshare";
  public static final String PARAM_ORANGEBAND_ARTICLE = "trainingLibrary.article.orangeband.text";
  
  /** SYS Permission Manager Constants. */
  public static final String PARAM_SYSUSERS_LIST = "sysUserList";
  public static final String PARAM_PERMISSION_GROUP = "rolesGroups";
  public static final String PARAM_SUREPEOPLE_COMPANIES_ID = "surepeopleCompanies";
  
  /** Note PARAMS. */
  public static final String PARAM_NOTELISTING = "noteListing";
  public static final String PARAM_NOTE_DETAIL = "noteDetail";
  public static final String PARAM_NOTE_DEFAULT_MESSAGE = "notes.default.note.title";
  public static final String PARAM_EMAIL_NOTES_SUB = "notification.subject.EmailNotes";
  public static final String PARAM_NOTE = "note";
  public static final String PARAM_ACTION_TYPE = "actionType";
  
  /** public channel. */
  public static final String PARAM_PUBLIC_CHANNEL = "publicChannel";
  public static final String ENTITY_PC_REF_ID = "pcRefId";
  public static final String ENTITY_PARENT_REF_ID = "parentRefId";
  public static final String PARAM_PUBLIC_CHANNEL_ID = "id";
  public static final String PARAM_PUBLIC_CHANNEL_REF_ID = "pcRefId";
  public static final String PARAM_EMAIL_FEEDBACK_SUB = "notification.subject.EmailFeedbacks";
  
  public static final int NEWS_FEED_COMMENT_LIMIT = 3;
  public static final int NEWS_FEED_COMMENT_MSG_DETAILS_LIMIT = 50;
  public static final int NEWS_FEED_DASHBOARD_LIMIT = 10;
  public static final int USER_ACTIVITY_FEED_TRIES = 3;
  public static final int PUBLIC_CHANNEL_COMMENT_LIMIT = 10;
  public static final String PARAM_ACTION_PLAN_KEYOUTCOMES = "keyOutcomes";
  public static final String PARAM_ACTION_PLAN_STEP_NAME = "stepName";
  public static final String PARAM_ACTION_PLAN_STEP_DESCRIPTION = "stepDescription";
  public static final String ENTITY_TOKENID = "tokenId";
  public static final String DEFAULT_LOCALE = "en_US";
  public static final String PARAM_NOTIFICATION_TYPE = "notificationType";
  public static final String PARAM_LOCALE = "locale";
  public static final String PARAM_MINIPOLL_RESULT = "miniPollResult";
  public static final String PARAM_MINIPOLL_SHARE = "miniPollShare";
  public static final String PARAM_COMMENTID = "cId";

  public static final String PARAM_REFERENCE_TYPES = "referenceTypes";
  public static final String PARAM_REFERENCE_CHECK = "referenceCheck";
  public static final String PARAM_HIRING_USER = "hiringUser";
  public static final String PARAM_HIRING_PUBLIC_ID = "hiringPublicId";
  public static final String ENTITY_USER_SHARE_TOKEN = "userShareToken";
  public static final String PRIMARY = "Primary";
  public static final String SECONDARY = "Secondary";
  public static final String PARAM_PA_VISITED = "paVisited";
  public static final String ENTITY_ARCHIVE = "archive";
  public static final String PARAM_SHARED_PATH = "sharedPath";
  
  /**
   * Gets the position offset to apply.
   * 
   * @param articleLocation
   *          - article location
   * @return the position offset
   */
  public static int getPositionOffset(ArticleLocation articleLocation) {
    switch (articleLocation) {
    case Content:
      return 0;
    case Hero:
      return 100;
    default:
      throw new InvalidRequestException(articleLocation + ": no offset configured");
    }
  }
}
