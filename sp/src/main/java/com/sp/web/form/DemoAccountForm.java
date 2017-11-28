package com.sp.web.form;

import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

/**
 * @author pruhil
 * 
 *         This the bean class to store the demo account finformation.
 */
public class DemoAccountForm implements Serializable {
  
  /**
   * Serial version uid.
   */
  private static final long serialVersionUID = 2941362104739308171L;
  
  @NotEmpty
  private String firstName;
  
  @NotEmpty
  private String lastName;
  
  @NotEmpty
  private String email;
  
  private String company;
  
  private String industry;
  
  private int noEmp;
  
  public String getFirstName() {
    return firstName;
  }
  
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  
  public String getLastName() {
    return lastName;
  }
  
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = (email != null) ? email.toLowerCase() : null;
  }
  
  public String getCompany() {
    return company;
  }
  
  public void setCompany(String company) {
    this.company = company;
  }
  
  public String getIndustry() {
    return industry;
  }
  
  public void setIndustry(String industry) {
    this.industry = industry;
  }
  
  public int getNoEmp() {
    return noEmp;
  }
  
  public void setNoEmp(int noEmp) {
    this.noEmp = noEmp;
  }
  
  @Override
  public String toString() {
    return "DemoAccountForm [firstName=" + firstName + ", lastName=" + lastName + ", email="
        + email + ", company=" + company + ", industry=" + industry + ", noEmp=" + noEmp + "]";
  }
  
}
