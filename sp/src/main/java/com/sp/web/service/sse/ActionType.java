package com.sp.web.service.sse;

/**
 * ActionType is the enum class for the event.
 * 
 * @author pradeepruhil
 *
 */
public enum ActionType {
  
  UpdatePermission("updatePermissionEvent"), GroupDiscussion, Logout("SendMessageActionProcessor"), ServerLogout(
      "ServerLogoutActionProcessor"), SendMessage("SendMessageActionProcessor"), PulseUpdate(
      "updateNavigationEventProcessor"), FeatureUpdate("UpdateProfileActionProcessor"), UpdateNavigation(
      "updateNavigationEventProcessor"), UpdateParams("UpdateParamsActionProcessor"), DefaultEventProcessor, PublicChannel, DashboardMessage, OrganisationPlan, ActionPlan, BadgeCompletion, NotificationLog, HiringUserDelete, HiringUserArchive, HiringUserUpdated, PartnerPrism, PartnerPrismStatus, PartnerPrismResult, DeletePartner;
  
  private String eventProcessor;
  
  private ActionType() {
    this.eventProcessor = "defaultEventProcessor";
  }
  
  /**
   * Constructor for the eventActionType.
   */
  private ActionType(String eventProcessor) {
    this.eventProcessor = eventProcessor;
  }
  
  public String getEventProcessor() {
    return eventProcessor;
  }
}
