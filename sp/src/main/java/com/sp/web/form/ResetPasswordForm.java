package com.sp.web.form;

import org.hibernate.validator.constraints.Email;
import org.springframework.util.StringUtils;

import java.util.Optional;

import javax.validation.constraints.NotNull;

/**
 * Been for rest password form
 * 
 * @author pruhil
 * 
 */
public class ResetPasswordForm {
  
  @Email
  @NotNull
  private String email;
  
  @NotNull
  private String password;
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    Optional.ofNullable(email).ifPresent(e -> this.email = StringUtils.trimWhitespace(e.toLowerCase()));
  }
  
  public String getPassword() {
    return password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
}
