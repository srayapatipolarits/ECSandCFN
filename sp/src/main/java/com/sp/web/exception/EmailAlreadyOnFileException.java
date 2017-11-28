package com.sp.web.exception;

import com.sp.web.utils.MessagesHelper;

/**
 * Sign-up failed because the email address the Person provided is already on file. The same email
 * address can not be shared by different people.
 * 
 * @author pruhil
 */
@SuppressWarnings("serial")
public class EmailAlreadyOnFileException extends AccountException {

  private String email;

  public EmailAlreadyOnFileException(String email) {
    super(MessagesHelper.getMessage("exception.duplicateEmail.signup"));
    this.email = email;
  }

  public String getEmail() {
    return email;
  }
}
