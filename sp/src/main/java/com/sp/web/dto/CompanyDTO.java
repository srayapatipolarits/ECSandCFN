package com.sp.web.dto;

import com.sp.web.dao.CompanyDao;
import com.sp.web.model.Company;
import com.sp.web.model.SPFeature;

import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         The DTO for the company model object.
 */
public class CompanyDTO extends BaseCompanyDTO {

  /** company industry. */
  private String industry;

  /** companies approx number of employees. */
  private int numberOfEmployees;

  /** Company phone number. */
  private String phoneNumber;
  
  private String accountId;
  
  private Set<SPFeature> featureList;
  
  /** 
   * Flag to indicate if all members of the company 
   * are blocked from logging into the system.
   */
  private boolean isBlockAllMembers;
  
  private String logoURL;
  
  /** This flag indicate whether relationship advisor is enabled within group or whole company. */
  private boolean restrictRelationShipAdvisor;
  
  private boolean enhancePasswordSecurity;
  
  private boolean sharePortrait;
  
  public void setRestrictRelationShipAdvisor(boolean restrictRelationShipAdvisor) {
    this.restrictRelationShipAdvisor = restrictRelationShipAdvisor;
  }
  
  public boolean isRestrictRelationShipAdvisor() {
    return restrictRelationShipAdvisor;
  }

  /**
   * Constructor to create the company DTO.
   * 
   * @param company
   *          - company
   */
  public CompanyDTO(Company company) {
    super(company);
  }

  /**
   * Constructor from company dao.
   * 
   * @param company
   *          - company dao
   */
  public CompanyDTO(CompanyDao company) {
    super(company);
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

  public boolean isBlockAllMembers() {
    return isBlockAllMembers;
  }

  public void setBlockAllMembers(boolean isBlockAllMembers) {
    this.isBlockAllMembers = isBlockAllMembers;
  }
  
  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }
  
  public String getAccountId() {
    return accountId;
  }
  
  public void setFeatureList(Set<SPFeature> featureList) {
    this.featureList = featureList;
  }
  
  public Set<SPFeature> getFeatureList() {
    return featureList;
  }

  public String getLogoURL() {
    return logoURL;
  }

  public void setLogoURL(String logoURL) {
    this.logoURL = logoURL;
  }
  
  /**
   * Helper to check if the company has the particular feature.
   * 
   * @param featureType
   *          - feature type
   * @return true if feature present else false
   */
  public boolean hasFeature(SPFeature featureType) {
    if (!CollectionUtils.isEmpty(featureList)) {
      return featureList.contains(featureType);
    }
    return false;
  }
  
  public void setEnhancePasswordSecurity(boolean enhancePasswordSecurity) {
    this.enhancePasswordSecurity = enhancePasswordSecurity;
  }
  
  public boolean isEnhancePasswordSecurity() {
    return enhancePasswordSecurity;
  }
  
  public void setSharePortrait(boolean sharePortrait) {
    this.sharePortrait = sharePortrait;
  }
  
  public boolean isSharePortrait() {
    return sharePortrait;
  }
}