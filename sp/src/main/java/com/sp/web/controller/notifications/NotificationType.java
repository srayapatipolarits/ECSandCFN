package com.sp.web.controller.notifications;

import com.sp.web.model.log.LogActionType;

/**
 * @author Dax Abraham
 *
 *         The enumeration to hold the notification types and their templates.
 */
public enum NotificationType {
  
  RegistrationWelcome("registrationWelcome.stg"), ProfileCreated("profileCreated.stg"), ProfileIncompleteReminder(
      "profileIncompleteReminder.stg"), AssessmentPendingReminder("assessmentPendingReminder.stg"), GrowthRequestDeleted(
      "growthRequestUserDeleted.stg"), GrowthDeclineRequest("growthDecline.stg",
      LogActionType.GrowthDecline, "growthNotificationProcessor"), GrowthAcceptRequest(
      "growthRequestAccept.stg", LogActionType.GrowthAccept, "growthNotificationProcessor"), GrowthFeedbackSubmitted(
      "growthFeedbackSubmitted.stg", LogActionType.GrowthSubmitted, "growthNotificationProcessor"), GrowthFeedbackAcceptReminder(
      "growthFeedbackAcceptReminder.stg", LogActionType.GrowthReminder,
      "growthNotificationProcessor"), GrowthFeedbackReminder("growthFeedbackReminder.stg",
      LogActionType.GrowthReminder, "growthNotificationProcessor"), GrowthInviteMember(
      "growthInviteMember.stg", LogActionType.GrowthInvite, "growthNotificationProcessor"), GrowthInviteExternal(
      "growthInviteExternal.stg", LogActionType.GrowthInvite, "growthNotificationProcessor"), FeedbackInviteMember(
      "feedbackInviteMember.stg", LogActionType.FeedbackInvite), FeedbackInviteExternal(
      "feedbackInviteExternal.stg", LogActionType.FeedbackInvite), FeedbackReminder(
      "feedbackReminder.stg", LogActionType.FeedbackReminder, "feedbackNotificationsProcessor"), 
      HiringCandidateReminder("hiringCandidateReminder.stg", LogActionType.HiringReminder, 
          "hiringReminderNotificationsProcessor"), GenericMessage("genericMessage.stg"), 
      FeedbackCompleted("feedbackCompleted.stg", LogActionType.FeedbackCompleted, "feedbackNotificationsProcessor"), 
      HiringCandidateInvite("addCandidate.stg", LogActionType.HiringInvite), AddMember("addMember.stg",
      LogActionType.AddMember), Signup("signupWelcome.stg", LogActionType.SignupWelcome), AltBillSignup(
      "altBillSignupWelcome.stg", LogActionType.AltBillSignupWelcome), AssessmentCompleted(
      "assessmentCompleted.stg", LogActionType.AssessmentCompleted), AssessmentCompletedMember(
      "assessmentCompletedMember.stg", LogActionType.AssessmentCompleted), AssessmentCompletedCandidate(
      "assessmentCompletedCandidate.stg", LogActionType.AssessmentCompletedCandidate), AssessmentCompletedCoordinator(
      "assessmentCompletedCoordinator.stg", LogActionType.AssessmentCompletedCoordinator), DemoAccount(
      "demoaccount/demoAccountEmail.stg"), ShareResume("resume/shareResume.stg"), InternalMarketingArticle(
      "internalMarketingArticles.stg", LogActionType.InternalMarketingArticle), InternalMarketingGoalsNotSet(
      "internalMarketingGoalsNotSet.stg"), PulseStart("pulseStart.stg", LogActionType.PulseAssessmentStart), PulseReminder(
      "pulseReminder.stg"), ChangePassword("changePassword.stg", LogActionType.ChangePassword), ShareArticle(
      "shareArticle.stg", LogActionType.ArticleShare), RequestFeedback("requestFeedback.stg", LogActionType.RequestFeedback), SubmitRequestFeedback(
      "submitRequestFeedback.stg", LogActionType.SubmitRequestFeedback), RequestFeedbackExternal(
      "requestFeedbackExternal.stg", LogActionType.RequestFeedbackExternal), SubmitRequestFeedbackExternal(
      "submitRequestFeedbackExternal.stg", LogActionType.SubmitRequestFeedbackExternal), FeedbackEmailReplyFailure(
      "feedbackEmailReplyFailure.stg"), AccountRechargeSuccess("account/accountRechargeSuccess.stg"), AccountRenewalPaymentFailed(
      "account/accountRenewalPaymentFailed.stg"), AccountExpiredAccount(
      "account/accountExpired.stg"), OverdueAccountsRecharge("account/overdueAccountsRecharge.stg"), AccountPreRechargeNotification(
      "account/preRechargeAccountsRecharge.stg"), AccountToBeExpiredAccount(
      "account/accountToBeExpired.stg"), BlueprintApproval("blueprintApproval.stg",
      LogActionType.BlueprintApproval), BlueprintShare("blueprintShare.stg",
      LogActionType.BlueprintShare), BlueprintApproved("blueprintApproved.stg",
      LogActionType.BlueprintApproved), BlueprintFeedback("blueprintFeedback.stg",
      LogActionType.BlueprintFeedback), BlueprintApprovalRevise("blueprintApprovalRevise.stg",
      LogActionType.BlueprintApprovalRevise), BlueprintApprovalReminder(
      "blueprintApprovalReminder.stg", LogActionType.BlueprintApprovalReminder), BlueprintApprovalCancel(
      "blueprintApprovalCancel.stg", LogActionType.BlueprintApprovalCancel), BlueprintPublishShare(
      "blueprintPublishShare.stg", LogActionType.BlueprintPublishShare), CompetencyEvalautionActivated(
      "competencyEvaluationActivated.stg", LogActionType.CompetencyEvaluationActivated), CompetencyProfileAssigned(
      "competencyProfileAssigned.stg", LogActionType.CompetencyProfileAssigned), CompetencyEvaluationRequestManager(
      "competencyEvaluationRequestManager.stg", LogActionType.CompetencyEvaluationRequestManager), CompetencyEvaluationRequestPeer(
      "competencyEvaluationRequestPeer.stg", LogActionType.CompetencyEvaluationRequestPeer), CompetencyEvaluationReminder(
      "competencyEvaluationReminder.stg", LogActionType.CompetencyEvaluationReminder), CompetencyEvaluationReminderSelf(
      "competencyEvaluationReminderSelf.stg", LogActionType.CompetencyEvaluationReminderSelf), CompetencyEvaluationSelf(
      "competencyEvaluationSelf.stg", LogActionType.CompetencyEvaluationSelf), CompetencyEvaluationManager(
      "competencyEvaluationManager.stg", LogActionType.CompetencyEvaluationManager), CompetencyEvaluationReminderInitiate(
      "competencyEvaluationReminderInitiate.stg",
      LogActionType.CompetencyEvaluationReminderInitiate), CompetencyEvaluationReminderConsolidated(
      "competencyEvaluationReminderConsolidated.stg",
      LogActionType.CompetencyEvaluationReminderConsolidated), ActionPlanAssign(
      "actionPlanAssign.stg", LogActionType.ActionPlanAssign), ActionPlanRemove(
      "actionPlanRemove.stg", LogActionType.ActionPlanRemove), ActionPlanOnHold(
      "actionPlanOnHold.stg", LogActionType.ActionPlanOnHold), ActionPlanResume(
      "actionPlanResume.stg", LogActionType.ActionPlanResume), GroupDiscussionAdd(
      "groupDiscussionAdd.stg", LogActionType.GroupDiscussionAdd), EmailNotes("emailNotes.stg",
      LogActionType.EmailNotes), DashboardMessageTagged("dashboardMessageTagged.stg",
      LogActionType.DashboardMessageTagged), DashboardMessageComment("dashboardMessageComment.stg",
      LogActionType.DashboardMessageComment), DashboardMessageCommentTag(
      "dashboardMessageCommentTag.stg", LogActionType.DashboardMessageCommentTag), EmailFeedbacks(
      "emailDevFeedbacks.stg", LogActionType.EmailDevFeedbacks), GroupDiscussionComment(
      "groupDiscussionComment.stg", LogActionType.GroupDiscussionComment), PublicChannelCommentTag(
      "publicChannelCommentTagged.stg", LogActionType.PublicChannelCommentTag), PublicChannelComment(
      "publicChannelComment.stg", LogActionType.PublicChannelComment), ResetPassword(
      "resetPassword.stg", LogActionType.ChangePasswordRequest), InternalMarkettingAnalytics(
      "internalMarketingAnalytics.stg"), COPY_PROFILE_TEMPLATE("copyProfile.stg"), MiniPollCreated(
      "miniPollCreated.stg", LogActionType.MiniPoll), MiniPollGdCreated("miniPollGdCreated.stg",
      LogActionType.MiniPoll), DashboardMessageAnnouncement("dashboardMessageAnnouncement.stg",
      LogActionType.DashboardMessageAnnouncement), DashboardMessageCommunication(
      "dashboardMessageCommunication.stg", LogActionType.DashboardMessageCommunication), 
      HiringEmployeeInvite("hiringEmployeeInvite.stg", LogActionType.HiringInvite), 
      AssessmentCompletedEmployee("hiringSharePortrait.stg", LogActionType.AssessmentCompletedCandidate),
      SalesForceNewAccount("sf/salesForceSignup.stg"), 
      HiringLensCandidateInvite("hiringLensCandidateInvite.stg", LogActionType.HiringLensRequest), 
      HiringLensEmployeeInvite("hiringLensEmployeeInvite.stg", LogActionType.HiringLensRequest), 
      HiringLensReminder("hiringLensReminder.stg", LogActionType.HiringLensReminder, 
      "hiringLensReminderNotificationProcessor"), HiringSharePortrait("hiringSharePortrait.stg", 
          LogActionType.HiringSharePortrait, "hiringCandidateNotificationProcessor"), 
      HiringShareUserPortrait("hiringShareUserPortrait.stg", LogActionType.HiringSharePortrait), HiringEmployeeReminder(
          "hiringEmployeeReminder.stg", LogActionType.HiringReminder, "hiringReminderNotificationsProcessor"),
 AltBillingNewToolAssessmentPending(
      "account/newToolAssessmentPending.stg", LogActionType.AltBillSignupWelcome), 
      AltBillingPASubscribed("account/paSubscribed.stg", LogActionType.AltBillPASignupWelcome),
      AltBillingPAValidERTIUser("account/paValidErtiUser.stg", LogActionType.AltBillPASignupWelcome),
      
      AltBillingERTIValidPAUser("account/ertiValidPAUser.stg", LogActionType.AltBillSignupWelcome),
      AddPAAdminMember("account/addPAAdminMember.stg",
          LogActionType.AddMember),AddPAAdminMemberErtiUser("account/addPAAdminMemberValidErti.stg",
              LogActionType.AddMember),
      HiringPortraitMatchAssign("hiringPortraitMatchAssign.stg", LogActionType.HiringPortraitMatchAssign), 
      CompetencyManagerResult("competencyManagerResult.stg", LogActionType.CompetencyManagerResult);
  
  private LogActionType logType;
  private String templateName;
  private String notificationProcessor;
  private static final String TEMPLATE_FOLDER = "templates/email/";
  
  NotificationType(String templateName, LogActionType logType) {
    this.templateName = templateName;
    this.logType = logType;
    notificationProcessor = "defaultNotificationProcessor";
  }
  
  NotificationType(String templateName, LogActionType logType, String notificationProcessor) {
    this(templateName, logType);
    this.notificationProcessor = notificationProcessor;
  }
  
  NotificationType(String templateName) {
    this(templateName, LogActionType.Default);
  }
  
  public String getTemplateName() {
    return TEMPLATE_FOLDER + templateName;
  }
  
  public LogActionType getLogType() {
    return logType;
  }
  
  public String getNotificationProcessor() {
    return notificationProcessor;
  }
}
