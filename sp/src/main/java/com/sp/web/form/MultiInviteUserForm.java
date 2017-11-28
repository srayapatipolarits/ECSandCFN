package com.sp.web.form;

import org.springframework.util.StringUtils;

public class MultiInviteUserForm extends BaseUserForm {
  
  private String email;
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = StringUtils.trimWhitespace(email.toLowerCase());
  }
  
}
