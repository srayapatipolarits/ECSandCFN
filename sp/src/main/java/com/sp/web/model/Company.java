package com.sp.web.model;

import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author pradeep
 * 
 *         The company model bean.
 */
public class Company implements Serializable {
  
  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = -5009114976242771133L;
  
  /** The ID for the company. */
  private String id;
  
  /** company name. */
  private String name;
  
  /** company Address. */
  private Address address;
  
  /** company industry. */
  private String industry;
  
  /** companies approx number of employees. */
  private int numberOfEmployees;
  
  /** Company phone number. */
  private String phoneNumber;
  
  /** Companies account. */
  private String accountId;
  
  /**
   * Flag to indicate if all members of the company are blocked from logging into the system. This
   * will be used only for members only(Erti plan only) as PA members are admins only who can login
   * into the system.
   */
  private boolean isBlockAllMembers;
  
  private int imageCount = 0;
  
  private String logoImage;
  
  /**
   * The list of tasks for the users of the company.
   */
  private Set<SPFeature> featureList;
  
  /**
   * Company Theme for the company.
   */
  private CompanyTheme companyTheme;
  
  private boolean companyThemeActive;
  private boolean deactivated;
  
  /*
   * Competency related flags
   */
  private boolean evaluationInProgress;
  private String competencyEvaluationId;
  private String lastCompetencyEvalutationId;
  private int competencyEvaluationCount;
    
  private boolean enhancePasswordSecurity;
  private boolean ertiDeactivated;
  private boolean peopleAnalyticsDeactivated;
  private boolean sharePortrait;
 
  
  /**
   * <p>
   * Flag to indicate whether relationship advisor features is available to compare between members
   * of all companies or between groups.
   * </p>
   * {@link Boolean.TRUE} indicates RA is available between groups only. {@link Boolean.FALSE}
   * indicates RA is available between all members of company.
   * 
   */
  private boolean restrictRelationShipAdvisor;
  
  /**
   * Flag to indicate if the company admins have access to action plan edit etc.
   */
  private boolean actionPlanAdminEnabled;
  
  public void setRestrictRelationShipAdvisor(boolean restrictRelationShipAdvisor) {
    this.restrictRelationShipAdvisor = restrictRelationShipAdvisor;
  }
  
  public boolean isRestrictRelationShipAdvisor() {
    return restrictRelationShipAdvisor;
  }
  

  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public Address getAddress() {
    return address;
  }
  
  public void setAddress(Address address) {
    this.address = address;
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
  
  public String getAccountId() {
    return accountId;
  }
  
  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public boolean isBlockAllMembers() {
    return isBlockAllMembers;
  }
  
  public void setBlockAllMembers(boolean isBlockAllMembers) {
    this.isBlockAllMembers = isBlockAllMembers;
  }
  
  public int getImageCount() {
    return imageCount;
  }
  
  public void setImageCount(int imageCount) {
    this.imageCount = imageCount;
  }
  
  public String getLogoImage() {
    return logoImage;
  }
  
  public void setLogoImage(String logoImage) {
    this.logoImage = logoImage;
  }
  
  /**
   * Method to increment the image count.
   */
  public void incrementImageCount() {
    imageCount++;
  }
  
  /**
   * Get the feature list.
   * 
   * @return
   *    the feature list 
   */
  public Set<SPFeature> getFeatureList() {
    if (featureList == null) {
      featureList = new HashSet<>();
    }
    return featureList;
  }
  
  public void setFeatureList(Set<SPFeature> featureList) {
    this.featureList = featureList;
  }
  
  public CompanyTheme getCompanyTheme() {
    return companyTheme;
  }
  
  public void setCompanyTheme(CompanyTheme companyTheme) {
    this.companyTheme = companyTheme;
  }
  
  public boolean isCompanyThemeActive() {
    return companyThemeActive;
  }
  
  public void setCompanyThemeActive(boolean companyThemeActive) {
    this.companyThemeActive = companyThemeActive;
  }
  
  /**
   * Add the feature to the given company.
   * 
   * @param featureType
   *            - feature to add 
   */
  public void addFeature(SPFeature featureType) {
    if (CollectionUtils.isEmpty(featureList)) {
      featureList = new HashSet<SPFeature>();
    }
    
    featureList.add(featureType);
  }
  
  /**
   * Helper method to remove the given feature.
   * 
   * @param featureType
   *          - feature type
   */
  public void removeFeature(SPFeature featureType) {
    if (!CollectionUtils.isEmpty(featureList)) {
      featureList.remove(featureType);
    }
  }
  
  /**
   * Check if the company has the given feature.
   * 
   * @param featureType
   *          - feature type
   * @return true if company has the feature else false
   */
  public boolean hasFeature(SPFeature featureType) {
    if (!CollectionUtils.isEmpty(featureList)) {
      return featureList.contains(featureType);
    }
    return false;
  }
  
  public boolean isEvaluationInProgress() {
    return evaluationInProgress;
  }
  
  public void setEvaluationInProgress(boolean evaluationInProgress) {
    this.evaluationInProgress = evaluationInProgress;
  }
  
  public void setDeactivated(boolean deactivated) {
    this.deactivated = deactivated;
  }
  
  public boolean isDeactivated() {
    return deactivated;
  }
  
  public String getCompetencyEvaluationId() {
    return competencyEvaluationId;
  }
  
  public void setCompetencyEvaluationId(String competencyEvaluationId) {
    this.competencyEvaluationId = competencyEvaluationId;
  }
  
  public boolean isActionPlanAdminEnabled() {
    return actionPlanAdminEnabled;
  }
  
  public void setActionPlanAdminEnabled(boolean actionPlanAdminEnabled) {
    this.actionPlanAdminEnabled = actionPlanAdminEnabled;
  }
  
  public void setEnhancePasswordSecurity(boolean enhancePasswordSecurity) {
    this.enhancePasswordSecurity = enhancePasswordSecurity;
  }
  
  public boolean isEnhancePasswordSecurity() {
    return enhancePasswordSecurity;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    Company other = (Company) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

  public boolean isErtiDeactivated() {
    return ertiDeactivated;
  }

  public void setErtiDeactivated(boolean ertiDeactivated) {
    this.ertiDeactivated = ertiDeactivated;
  }

  public boolean isPeopleAnalyticsDeactivated() {
    return peopleAnalyticsDeactivated;
  }

  public void setPeopleAnalyticsDeactivated(boolean peopleAnalyticsDeactivated) {
    this.peopleAnalyticsDeactivated = peopleAnalyticsDeactivated;
  }

  public boolean isSharePortrait() {
    return sharePortrait;
  }

  public void setSharePortrait(boolean sharePortrait) {
    this.sharePortrait = sharePortrait;
  }

  public String getLastCompetencyEvalutationId() {
    return lastCompetencyEvalutationId;
  }

  public void setLastCompetencyEvalutationId(String lastCompetencyEvalutationId) {
    this.lastCompetencyEvalutationId = lastCompetencyEvalutationId;
  }

  public int getCompetencyEvaluationCount() {
    return competencyEvaluationCount;
  }

  public void setCompetencyEvaluationCount(int competencyEvaluationCount) {
    this.competencyEvaluationCount = competencyEvaluationCount;
  }
  
  public void incrementCompetncyEvaluationCount() {
    competencyEvaluationCount++;
  }

  public void decrementCompetencyEvaluationCount() {
    competencyEvaluationCount--;
  }
 
}
