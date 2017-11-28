package com.sp.web.dao;

import com.sp.web.model.Company;
import com.sp.web.model.RoleType;
import com.sp.web.utils.ImageUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Dax Abraham
 *
 *         The DAO class for company.
 */
@Document(collection = "company")
public class CompanyDao extends Company {
  
  
  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = -791969306073727229L;
  @Transient
  private String logoURL;
  
  @Transient
  private String cssURL;
  
  /**
   * The list of tasks for the users of the company.
   */
  @Transient
  private Set<RoleType> roleList;

  /**
   * Default constructor.
   */
  public CompanyDao() {
    super();
  }

  /**
   * Constructor.
   * 
   * @param company
   *          - company
   */
  public CompanyDao(Company company) {
    BeanUtils.copyProperties(company, this);
    logoURL = ImageUtils.getCompanyImage(company);
    if (company.getCompanyTheme() != null) {
      cssURL = ImageUtils.getCssUrl(company.getCompanyTheme().getCssUrl());
    }
    
 // adding the role
    company.getFeatureList().stream().forEach(featureType -> {
        for (RoleType role : featureType.getRoles()) {
          if (!role.isAdminRole()) {
            if (CollectionUtils.isEmpty(roleList)) {
              roleList = new HashSet<RoleType>();
            }
            roleList.add(role);
          }  
        }
        
      });
    
  }

  public String getLogoURL() {
    return logoURL;
  }

  public void setLogoURL(String logoURL) {
    this.logoURL = logoURL;
  }

  /**
   * Get the original company object from this dao.
   * 
   * @return
   *      the company object
   */
  public Company getCompany() {
    Company company = new Company();
    BeanUtils.copyProperties(this, company);
    return company;
  }
  
  /**
   * Get the role list.
   * 
   * @return - the role list
   */
  public Set<RoleType> getRoleList() {
    if (roleList == null) {
      roleList = new HashSet<>();
    }
    return roleList;
  }
  
  public void setRoleList(Set<RoleType> roleList) {
    this.roleList = roleList;
  }
  
  /**
   * @return 
   *    - the cssURL.
   */
  public String getCssURL() {
    return cssURL;
  }
}
