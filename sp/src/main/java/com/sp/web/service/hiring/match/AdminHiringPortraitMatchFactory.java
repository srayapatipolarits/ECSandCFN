package com.sp.web.service.hiring.match;

import com.sp.web.Constants;
import com.sp.web.controller.generic.GenericFactory;
import com.sp.web.controller.hiring.match.AdminHiringPortriatDetailsDTO;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dao.hiring.match.HiringPortraitDao;
import com.sp.web.dto.BaseCompanyDTO;
import com.sp.web.dto.hiring.match.AdminHiringPortraitListingDTO;
import com.sp.web.form.hiring.match.AdminHiringPortraitMatchForm;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.hiring.match.HiringPortrait;
import com.sp.web.product.CompanyFactory;
import com.sp.web.service.hiring.role.HiringRoleFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The factory class for the admin portrait match functionality.
 */
@Component
public class AdminHiringPortraitMatchFactory
    implements
    GenericFactory<AdminHiringPortraitListingDTO, AdminHiringPortriatDetailsDTO, AdminHiringPortraitMatchForm> {
  
  @Autowired
  HiringPortraitMatchFactoryCache factoryCache;
  
  @Autowired
  CompanyFactory companyFactory;
  
  @Autowired
  HiringRoleFactory rolesFactory;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  private NotificationsProcessor notificationProcessor;
  
  @Autowired
  private UserFactory userFactory;
  
  @Override
  public List<AdminHiringPortraitListingDTO> getAll(User user) {
    return factoryCache.getAll().stream()
        .map(p -> new AdminHiringPortraitListingDTO(p, companyFactory))
        .collect(Collectors.toList());
  }
  
  @Override
  public AdminHiringPortriatDetailsDTO get(User user, AdminHiringPortraitMatchForm form) {
    HiringPortraitDao hiringPortrait = getPortrait(form);
    return new AdminHiringPortriatDetailsDTO(hiringPortrait);
  }
  
  @Override
  public AdminHiringPortriatDetailsDTO create(User user, AdminHiringPortraitMatchForm form) {
    HiringPortrait create = form.create(user);
    factoryCache.create(create);
    return new AdminHiringPortriatDetailsDTO(create);
  }
  
  @Override
  public AdminHiringPortriatDetailsDTO update(User user, AdminHiringPortraitMatchForm form) {
    HiringPortraitDao portrait = getPortrait(form);
    form.update(user, portrait);
    factoryCache.adminUpdate(portrait);
    return new AdminHiringPortriatDetailsDTO(portrait);
  }
  
  @Override
  public void delete(User user, AdminHiringPortraitMatchForm form) {
    HiringPortraitDao portrait = getPortrait(form);
    factoryCache.delete(portrait);
    // removing the portrait from all the roles
    rolesFactory.removePortraitFromRoles(portrait);
  }
  
  /**
   * Assign the company to the given portrait id.
   * 
   * @param companyId
   *          - company id
   * @param portraitId
   *          - portrait id
   */
  public void assignPortraitToCompany(User user, String companyId, String portraitId) {
    // checking company
    companyFactory.getCompany(companyId);
    
    // getting the portrait
    HiringPortraitDao hiringPortraitDao = getPortrait(portraitId);
    
    if (hiringPortraitDao.addCompany(companyId)) {
      factoryCache.update(hiringPortraitDao);
      List<User> users = userFactory.getAllByCompanyAndRole(companyId, RoleType.Hiring);
      final Map<String, Object> params = new HashMap<String, Object>();
      params.put("portrait", hiringPortraitDao);
      params.put(
          Constants.PARAM_SUBJECT,
          MessagesHelper.getMessage("notification.subject.HiringPortraitMatchAssign",
              user.getLocale(), hiringPortraitDao.getName()));
      users.forEach(u -> notificationProcessor.process(NotificationType.HiringPortraitMatchAssign,
          user, u, params, false));
    }
  }
  
  /**
   * Remove the portrait for the given company.
   * 
   * @param companyId
   *          - company id
   * @param portraitId
   *          - portrait id
   */
  public void removePortraitForCompany(String companyId, String portraitId) {
    // getting the portrait
    HiringPortraitDao hiringPortraitDao = getPortrait(portraitId);
    if (hiringPortraitDao.removeCompany(companyId)) {
      factoryCache.update(hiringPortraitDao);
      rolesFactory.removePortraitFromRoles(hiringPortraitDao, companyId);
    }
  }
  
  /**
   * Get the portrait for the given portrait id.
   * 
   * @param portraitId
   *          - portrait id
   * @return the hiring portrait
   */
  private HiringPortraitDao getPortrait(String portraitId) {
    HiringPortraitDao hiringPortraitDao = factoryCache.get(portraitId);
    Assert.notNull(hiringPortraitDao, "Portrait not found.");
    return hiringPortraitDao;
  }
  
  /**
   * Get the hiring portrait from the id in the portrait form.
   * 
   * @param form
   *          - form
   * @return the hiring portrait
   */
  private HiringPortraitDao getPortrait(AdminHiringPortraitMatchForm form) {
    HiringPortraitDao hiringPortrait = factoryCache.get(form.getId());
    Assert.notNull(hiringPortrait, "Portrait not found.");
    return hiringPortrait;
  }
  
  /**
   * Add the document URL for the company to the hiring portrait.
   * 
   * @param companyId
   *          - company id
   * @param portraitId
   *          - portrait id
   * @param documentUrl
   *          - document URL
   */
  public void addCompanyDocumentUrlToPortrait(String companyId, String portraitId,
      String documentUrl) {
    // checking company
    companyFactory.getCompany(companyId);
    
    // getting the portrait
    HiringPortraitDao hiringPortraitDao = getPortrait(portraitId);
    
    hiringPortraitDao.addDocumentUrl(companyId, documentUrl);
    factoryCache.update(hiringPortraitDao);
  }
  
  /**
   * Remove the document URL associated for the given company.
   * 
   * @param companyId
   *          - company id
   * @param portraitId
   *          - portrait id
   */
  public void removeCompanyDocumentUrlFromPortrait(String companyId, String portraitId) {
    // getting the portrait
    HiringPortraitDao hiringPortraitDao = getPortrait(portraitId);
    
    if (hiringPortraitDao.removeDocumentUrl(companyId)) {
      factoryCache.update(hiringPortraitDao);
    }
  }
  
  /**
   * Get all the companies with hiring feature.
   * 
   * @return
   *      the company list
   */
  public List<BaseCompanyDTO> getAllCompaniesWithHiring() {
    return companyFactory.findCompaniesByFeature(SPFeature.Hiring).stream()
        .map(BaseCompanyDTO::new).collect(Collectors.toList());
  }
}
