package com.sp.web.form;

import com.sp.web.model.Company;

import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;

/**
 * @author Dax Abraham
 * 
 *         The company form to use for updating company information.
 */
public class CompanyForm {

  /** company name. */
  @NotNull
  private String name;

  /** company industry. */
  @NotNull
  private String industry;

  /** companies approx number of employees. */
  @NotNull
  private int numberOfEmployees;

  /** Company phone number. */
  @NotNull
  private String phoneNumber;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIndustry() {
    return industry;
  }

  public void setIndustry(String industry) {
    this.industry = industry;
  }

  public int getNumberOfEmployees() {
    return numberOfEmployees;
  }

  public void setNumberOfEmployees(int numberOfEmployees) {
    this.numberOfEmployees = numberOfEmployees;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  /**
   * Updates the company information from the given form data.
   * 
   * @param company
   *          - company to update
   */
  public void updateCompany(Company company) {
    BeanUtils.copyProperties(this, company);
  }
  

}
