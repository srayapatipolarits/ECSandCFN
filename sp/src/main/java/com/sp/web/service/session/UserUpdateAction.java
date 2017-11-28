package com.sp.web.service.session;


/**
 * @author vikram
 * 
 *         The enum represents actions performed on the logged in user.
 */
public enum UserUpdateAction {

  //UpdateProfile("UpdateProfileActionProcessor"),
  UpdatePermission("UpdateProfileActionProcessor"),
  Logout("SendMessageActionProcessor"),
  ServerLogout("ServerLogoutActionProcessor"),
  SendMessage("SendMessageActionProcessor"),
  PulseUpdate("UpdateProfileActionProcessor"),
  FeatureUpdate("UpdateProfileActionProcessor"),
  UpdateNavigation("UpdateNavigationActionProcessor"),
  UpdateParams("UpdateParamsActionProcessor");
  
  private String actionProcessor;
  
  private UserUpdateAction(String processor) {
    this.actionProcessor = processor;
  }
  
  public String getUpdateSessionActionProcessor() {
    return actionProcessor;
  }
}
