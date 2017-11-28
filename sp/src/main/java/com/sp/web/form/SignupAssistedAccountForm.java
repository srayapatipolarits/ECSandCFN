package com.sp.web.form;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author Dax Abraham
 * 
 *         This is the form bean for inputing the assisted user information.
 */
public class SignupAssistedAccountForm {
  
  /**
   * Assisted sign up details.
   */
  @NotEmpty
  private String name;
  @NotEmpty
  private String phone;
  @NotEmpty
  private String company;
  @NotNull
  private int numEmp; // number of employees
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getPhone() {
    return phone;
  }
  
  public void setPhone(String phoneNumber) {
    this.phone = phoneNumber;
  }
  
  public int getNumEmp() {
    return numEmp;
  }
  
  public void setNumEmp(int numEmp) {
    this.numEmp = numEmp;
  }
  
  public String getCompany() {
    return company;
  }
  
  public void setCompany(String company) {
    this.company = company;
  }
  
}
