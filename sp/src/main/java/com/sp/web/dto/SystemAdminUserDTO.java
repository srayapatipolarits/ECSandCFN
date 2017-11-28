package com.sp.web.dto;

import com.sp.web.dao.CompanyDao;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.product.CompanyFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.util.Set;

/**
 * @author Dax Abraham
 *
 *         The DTO for sending back users in the system administration view.
 */
public class SystemAdminUserDTO extends BaseUserDTO {
  
  private static final long serialVersionUID = 5822543854376648029L;
  
  private static final Logger log = Logger.getLogger(SystemAdminUserDTO.class);
  
  private LocalDate createdOn;
  private String createdOnFormatted;
  private String companyName;
  private String companyId;
  private Set<RoleType> roles;
  
  /**
   * User Constructor.
   * 
   * @param user
   *          - user
   */
  public SystemAdminUserDTO(User user) {
    super(user);
    try {
      this.createdOnFormatted = MessagesHelper.formatDate(getCreatedOn());
    } catch (Exception e) {
      log.warn("Error formatting created on date.", e);
    }
  }
  
  /**
   * Constructor from user and company factory.
   * 
   * @param user
   *          - user
   * @param companyFactory
   *          - company factory
   */
  public SystemAdminUserDTO(User user, CompanyFactory companyFactory) {
    this(user);
    if (user.getCompanyId() != null) {
      try {
        CompanyDao company = companyFactory.getCompany(user.getCompanyId());
        if (company != null) {
          this.setCompanyId(company.getId());
          this.setCompanyName(company.getName());
        }
      } catch (Exception e) {
        log.warn("Company not found.", e);
      }
    }
  }
  
  public LocalDate getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDate createdOn) {
    this.createdOn = createdOn;
  }
  
  public String getCreatedOnFormatted() {
    return createdOnFormatted;
  }
  
  public String getCompanyName() {
    return companyName;
  }
  
  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public void setCreatedOnFormatted(String createdOnFormatted) {
    this.createdOnFormatted = createdOnFormatted;
  }
  
  public void setRoles(Set<RoleType> roles) {
    this.roles = roles;
  }
  
  public Set<RoleType> getRoles() {
    return roles;
  }
  
}
