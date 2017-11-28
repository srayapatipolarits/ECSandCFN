package com.sp.web.model.log;

/**
 * @author Dax Abraham
 * 
 *         The enumeration to store the log action types.
 */
public enum LogActionType {

  Default(LogType.Default, true, false),
  SignupWelcome(LogType.Signup, false, false),
  ProfileWelcome(LogType.Profile, false, false),
  FeedbackInvite(LogType.Prism, false, true, false), 
  FeedbackReminder(LogType.Prism, false, false),
  FeedbackArchived(LogType.Prism, false, false),
  FeedbackDeactive(LogType.Prism, false, false), 
  FeedbackCompleted(LogType.Prism, true, true, true), 
  HiringInvite(LogType.Hiring, false, false), 
  HiringReminder(LogType.Hiring, false, false), 
  HiringArchive(LogType.Hiring, false, false), 
  GrowthInvite(LogType.Growth),
  GrowthReminder(LogType.Growth),
  GrowthDeactive(LogType.Growth), 
  GrowthExpired(LogType.Growth), 
  GrowthArchive(LogType.Growth), 
  GrowthDecline(LogType.Growth), 
  GrowthAccept(LogType.Growth), 
  GrowthDeleted(LogType.Growth),
  GrowthSubmitted(LogType.Growth), 
  AddMember(LogType.Profile, true, false), 
  AssessmentCompleted(LogType.Profile, false, true), 
  AssessmentCompletedCandidate(LogType.Hiring, false, false), 
  AssessmentCompletedCoordinator(LogType.Hiring, false, false), 
  HiringProfileAccess(LogType.Hiring),
 ChangePassword(LogType.Password, true, false),
 ChangePasswordRequest(LogType.Password, false, false),
 AltBillSignupWelcome(LogType.AlternateBilling, true, false),
 AltBillPASignupWelcome(LogType.AlternateBilling, false, false),
  RequestFeedback(LogType.PracticeFeedback, true , true),
  SubmitRequestFeedback(LogType.PracticeFeedback, true, true, true),
  RequestFeedbackExternal(LogType.PracticeFeedback, false, false),
  SubmitRequestFeedbackExternal(LogType.PracticeFeedback, true, false, true),
  FeedbackDeclined(LogType.PracticeFeedback, false, false), 
  BlueprintApproval(LogType.Blueprint, false, false), 
  BlueprintShare(LogType.Blueprint, false, false), 
  BlueprintApproved(LogType.Blueprint, true, false, true), 
  BlueprintFeedback(LogType.Blueprint, true, false, true), 
  BlueprintApprovalRevise(LogType.Blueprint, true, false, true), 
  BlueprintApprovalReminder(LogType.Blueprint, false, false), 
  BlueprintApprovalCancel(LogType.Blueprint, false, false), 
  BlueprintPublishShare(LogType.Blueprint, false, false), 
  CompetencyEvaluationActivated(LogType.Competency, true, false, true), 
  CompetencyProfileAssigned(LogType.Competency, true, false), 
  CompetencyEvaluationRequestManager(LogType.Competency, false, false), 
  CompetencyEvaluationRequestPeer(LogType.Competency, false, false), 
  CompetencyEvaluationReminder(LogType.Competency, false, false), 
  CompetencyEvaluationReminderSelf(LogType.Competency, false, false), 
  CompetencyEvaluationSelf(LogType.Competency, false, false), 
  CompetencyEvaluationManager(LogType.Competency, false, false), 
  CompetencyEvaluationReminderInitiate(LogType.Competency, false, false), 
  CompetencyEvaluationReminderConsolidated(LogType.Competency, false, false),
  ActionPlanAssign(LogType.ActionPlan, true, false, true), 
  ActionPlanRemove(LogType.ActionPlan, true, false),
  ActionPlanOnHold(LogType.ActionPlan, true, false),
  ActionPlanResume(LogType.ActionPlan, true, false),
  GroupDiscussionAdd(LogType.GroupDiscussion, false, false), 
  EmailNotes(LogType.EmailNotes,false, false), 
  DashboardMessageTagged(LogType.DashboardMessage, true, false),
  ArticleCompleted(LogType.Prism, false, false), 
  ActionPlanCompleteAction(LogType.ActionPlan, false, false), 
  RelationshipAdvisor(LogType.Prism, false, false),
  EmailDevFeedbacks(LogType.EmailDevFeedbacks,false, false), 
  GroupDiscussionComment(LogType.GroupDiscussion, false, false), 
  DashboardMessageComment(LogType.DashboardMessage, false, false), 
  DashboardMessageCommentTag(LogType.DashboardMessage,false, false),
  PublicChannelCommentTag(LogType.PublicChannel,false, false),
  PublicChannelComment(LogType.PublicChannel,false, false), 
  PulseAssessmentComplete(LogType.Pulse, false, false), 
  ArticleShare(LogType.Prism, true, true), 
  InternalMarketingArticle(LogType.Prism, false, false), 
  PulseAssessmentStart(LogType.Pulse, true, false, true), 
  GroupLead(LogType.Account, true, false), 
  CompetencyAdminPermission(LogType.Account, true, false), 
  WorkspacePulsePermission(LogType.Account, true, false), 
  SpectrumPermission(LogType.Account, true, false), 
  HiringAccount(LogType.Account, true, false), 
  PrimaryAccount(LogType.Account, true, false), 
  DashboardMessageLike(LogType.DashboardMessage, true, false), 
  DashboardMessageTaggedLike(LogType.DashboardMessage, true, false), 
  DashboardMessageTaggedComment(LogType.DashboardMessage, true, false),
  BadgeCompletedErti(LogType.Badge,true,false),
  BadgeCompleted(LogType.Badge,true,false),
  MiniPoll(LogType.MiniPoll, false, false), 
  DashboardMessageAnnouncement(LogType.DashboardMessage, false, false), 
  DashboardMessageCommunication(LogType.DashboardMessage, false, false), 
  HiringLensRequest(LogType.Hiring, false, false), 
  HiringLensReminder(LogType.Hiring, false, false), 
  HiringSharePortrait(LogType.Hiring, false, false), 
  HiringPortraitMatchAssign(LogType.Hiring, false, false), 
  CompetencyManagerResult(LogType.Competency, false, false);
  
  private static final String SEPARATOR = ".";
  private static final String PREFIX = "log.";
  private LogType logType;
  private String key;
  private String titleKey;
  private String messageKey;
  private String activityKey;
  private boolean logNotification;
  private boolean logActivity;
  private boolean logNotificationBox;

  private LogActionType(LogType logType) {
    this.logType = logType;
    key = PREFIX + logType + SEPARATOR + this + SEPARATOR;
    titleKey = key + "title";
    messageKey = key + "message";
    activityKey = messageKey + ".activity";
    logNotification = true;
    logActivity = true;
    logNotificationBox = false;
  }

  /**
   * Constructor to set the log type as well as log notification and activity.
   * 
   * @param logType
   *          - log type
   * @param logNotification
   *          - flag to log notification 
   * @param logActivity
   *          - flag to log activity
   */
  private LogActionType(LogType logType, boolean logNotification, boolean logActivity) {
    this(logType);
    this.logActivity = logActivity;
    this.logNotification = logNotification;
  }

  /**
   * Constructor for setting the log type as well as log notification, activity and notification box flags.
   * 
   * @param logType
   *          - log type
   * @param logNotification
   *          - log notification flag
   * @param logActivity
   *          - log activity flag
   * @param logNotificationBox
   *          - log notification box flag
   */
  private LogActionType(LogType logType, boolean logNotification, boolean logActivity,
      boolean logNotificationBox) {
    this(logType);
    this.logActivity = logActivity;
    this.logNotification = logNotification;
    this.logNotificationBox = logNotificationBox;
  }
  
  public LogType getLogType() {
    return logType;
  }

  public String getKey() {
    return key;
  }

  public String getTitleKey() {
    return titleKey;
  }

  public String getMessageKey() {
    return messageKey;
  }

  public boolean doLogNotification() {
    return logNotification;
  }

  public boolean doLogActivity() {
    return logActivity;
  }

  public String getActivityKey() {
    return activityKey;
  }

  public boolean isLogNotificationBox() {
    return logNotificationBox;
  }
}
