package com.sp.web.model.demoaccount;

import com.sp.web.form.DemoAccountForm;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * <code>DemoUser</code> model is the user for holding the demo user profile.
 * 
 * @author pradeepruhil
 *
 */
public class DemoUser implements Serializable {

  private static final long serialVersionUID = -3714884393175129381L;

  private String id;

  private String firstName;

  private String lastName;

  private String email;

  private String company;

  private String industry;

  private String noEmp;

  /**
   * Construtor.
   * 
   * @param accountForm
   *          demo account form.
   */
  public DemoUser(DemoAccountForm accountForm) {
    BeanUtils.copyProperties(accountForm, this);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

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
    this.email = email;
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

  public String getNoEmp() {
    return noEmp;
  }

  public void setNoEmp(String noEmp) {
    this.noEmp = noEmp;
  }

}
