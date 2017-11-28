package com.sp.web.form.password;

import com.sp.web.validation.ValidPassword;

public class PasswordForm {
  
  @ValidPassword
  String password;
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public String getPassword() {
    return password;
  }
}
