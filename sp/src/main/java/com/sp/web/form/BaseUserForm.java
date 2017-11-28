package com.sp.web.form;

import com.sp.web.model.User;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.BeanUtils;

/**
 * @author Dax Abraham
 * 
 *         The base user profile form.
 */
public class BaseUserForm {

  protected String firstName;
  protected String lastName;
  protected String title;

  /**
   * Default constructor.
   */
  public BaseUserForm() {}
  
  /**
   * Constructor.
   * 
   * @param user
   *          - user
   */
  public BaseUserForm(User user) {
    BeanUtils.copyProperties(user, this);
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = WordUtils.capitalizeFully(firstName);
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = WordUtils.capitalizeFully(lastName);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
