package com.sp.web.model.sse;

/**
 * EventActionType is the enum class for the event.
 * 
 * @author pradeepruhil
 *
 */
@Deprecated
public enum EventActionType {
  
  UpdatePermission("UpdateProfileActionProcessor"), GroupDiscussion("groupDiscussionProcessor"), Logout(
      "SendMessageActionProcessor"), ServerLogout("ServerLogoutActionProcessor"), SendMessage(
      "SendMessageActionProcessor"), PulseUpdate("UpdateProfileActionProcessor"), FeatureUpdate(
      "UpdateProfileActionProcessor"), UpdateNavigation("UpdateNavigationActionProcessor"), UpdateParams(
      "UpdateParamsActionProcessor"), DashboardMessage("dashboardMessageProcessor");
  
  private String eventProcessor;
  
  /**
   * Constructor for the eventActionType.
   */
  private EventActionType(String eventProcessor) {
    this.eventProcessor = eventProcessor;
  }
  
  public String getEventProcessor() {
    return eventProcessor;
  }
}
