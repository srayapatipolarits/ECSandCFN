package com.sp.web.form.password;

import com.sp.web.validation.ValidPassword;

public class ChangePasswordForm {
  
  String oldPassword;
  
  @ValidPassword
  String newPassword;
  
  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
  
  public String getNewPassword() {
    return newPassword;
  }
  
  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }
  
  public String getOldPassword() {
    return oldPassword;
  }
}
