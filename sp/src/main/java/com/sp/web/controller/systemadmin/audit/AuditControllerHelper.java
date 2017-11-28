package com.sp.web.controller.systemadmin.audit;

import com.sp.web.account.AccountRepository;
import com.sp.web.controller.systemadmin.SessionFactory;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.dto.UserSessionDTO;
import com.sp.web.dto.audit.AuditBeanDTO;
import com.sp.web.dto.audit.AuditBeanDTO.ActivityType;
import com.sp.web.dto.audit.AuditDetailDTO;
import com.sp.web.model.AccountType;
import com.sp.web.model.Company;
import com.sp.web.model.User;
import com.sp.web.model.audit.AuditLogBean;
import com.sp.web.model.audit.ServiceType;
import com.sp.web.model.spectrum.TimeFilter;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.respository.systemadmin.SystemAdminRepository;
import com.sp.web.utils.DateTimeUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Audit controller helper.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class AuditControllerHelper {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(AuditControllerHelper.class);
  
  @Autowired
  private SystemAdminRepository adminRepository;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private SessionFactory sessionFactory;
  
  /**
   * <code>getAuditHome</code> method will return the audit home
   * 
   * @param user
   * @return
   */
  public SPResponse getAuditHome(User user, Object[] param) {
    
    String fromDate = (String) param[0];
    
    SPResponse spResponse = new SPResponse();
    List<AuditLogBean> findAuditLogs = null;
    if (StringUtils.isNotBlank(fromDate) && !fromDate.equalsIgnoreCase("null")) {
      String endDate = (String) param[1];
      findAuditLogs = adminRepository.findAuditLogs(DateTimeUtil.getLocalDate(fromDate),
          DateTimeUtil.getLocalDate(endDate));
    } else {
      
      /*
       * in case from data and edn date is not mentioned , return the audit logs for last 2 days
       * only
       */
      LocalDate endDate = LocalDate.now();
      LocalDate fromDateLocal = LocalDate.now().minusDays(1);
      findAuditLogs = adminRepository.findAuditLogs(fromDateLocal, endDate);
    }
    
    Map<String, List<AuditLogBean>> auditLogBeanGroupByEmail = findAuditLogs.stream().collect(
        Collectors.groupingBy(aug -> {
          if (StringUtils.isEmpty(aug.getEmail())) {
            return "anonymousUser@anonymous.com";
          } else {
            return aug.getEmail();
          }
        }));
    List<UserSessionDTO> loggedInUsers = sessionFactory.getLoggedInUsers();
    List<AuditBeanDTO> auditBean = new ArrayList<>();
    auditLogBeanGroupByEmail.forEach((em, albs) -> {
      
      User auditUser = userRepository.findByEmail(em);
      if (auditUser == null) {
        return;
      }
      AuditBeanDTO abDto = new AuditBeanDTO(auditUser);
      
      String companyId = auditUser.getCompanyId();
      if (StringUtils.isNotBlank(companyId)) {
        Company company = accountRepository.findCompanyById(companyId);
        if (company != null) {
          abDto.setCompanyName(company.getName());
        }
        abDto.setAccountType(AccountType.Business);
      } else {
        abDto.setAccountType(AccountType.Individual);
      }
      abDto.setActivityType(getActivityType(albs));
      loggedInUsers.stream().forEach(lIusr -> {
        if (lIusr.getEmail().equalsIgnoreCase(auditUser.getEmail())) {
          abDto.setOnline(true);
        }
      });
      
      /* get the timeFilters */
      abDto.setTimeFilters(getTimeFilters(albs));
      abDto.setServiceType(getServiceType(albs));
      auditBean.add(abDto);
    });
    spResponse.add("auditLogHome", auditBean);
    return spResponse;
  }
  
  public SPResponse getAllServices(User user) {
    
    ServiceType[] allServices = ServiceType.values();
    SPResponse response = new SPResponse();
    response.add("services", allServices);
    return response;
  }
  
  public SPResponse getAuditDetails(User user, Object[] param) {
    
    SPResponse spResponse = new SPResponse();
    String email = (String) param[0];
    
    User userLog = userRepository.findByEmail(email);
    String fromDate = (String) param[1];
    
    List<AuditLogBean> auditLogs = null;
    if (StringUtils.isNotBlank(fromDate) && !fromDate.equalsIgnoreCase("null")) {
      String endDate = (String) param[2];
      auditLogs = adminRepository.findAuditLogs(email, DateTimeUtil.getLocalDate(fromDate),
          DateTimeUtil.getLocalDate(endDate));
    } else {
      auditLogs = adminRepository.findAuditLogs(email);
    }
    
    List<AuditDetailDTO> details = auditLogs.stream().map(alb -> {
      AuditDetailDTO auditDetail = new AuditDetailDTO();
      auditDetail.setActivityDetails(alb.getLogMessage());
      auditDetail.setCreatedOn(alb.getCreatedOn().toString());
      if (alb.getServiceType() == null) {
        auditDetail.setServiceType(ServiceType.All);
      } else {
        auditDetail.setServiceType(alb.getServiceType());
      }
      TimeFilter timeFilterDate = DateTimeUtil.getTimeFilterDate(alb.getCreatedOn());
      Set<TimeFilter> filters = new HashSet<>();
      switch (timeFilterDate) {
      case DAY:
        filters.add(TimeFilter.DAY);
      case WEEK:
        filters.add(TimeFilter.WEEK);
      case MONTH:
        filters.add(TimeFilter.MONTH);
      case YEAR:
        filters.add(TimeFilter.YEAR);
        break;
      default:
        filters.add(TimeFilter.OLDER);
        break;
      }
      auditDetail.setTimeFilter(filters);
      return auditDetail;
    }).collect(Collectors.toList());
    
    spResponse.add("auditDetails", details);
    spResponse.add("userDetails", new BaseUserDTO(userLog));
    return spResponse;
  }
  
  private ActivityType getActivityType(List<AuditLogBean> alsb) {
    if (alsb.size() <= 5) {
      return ActivityType.Low;
    } else if (alsb.size() > 5 && alsb.size() <= 10) {
      return ActivityType.Medium;
    } else {
      return ActivityType.High;
    }
  }
  
  /**
   * getTimeFilters will return the timefiltes for which the user is active.
   * 
   * @param allAlbsk
   *          all audit bean logs for the specific user.
   * @return the time fitesr.
   */
  private Set<TimeFilter> getTimeFilters(List<AuditLogBean> allAlbs) {
    
    Set<TimeFilter> filters = new HashSet<TimeFilter>();
    
    for (AuditLogBean logBean : allAlbs) {
      /*
       * incase user is present in all the time filters the no need to check others as the detail
       * are to eb shown on the next request
       */
      if (filters.size() >= 4) {
        return filters;
      }
      
      TimeFilter timeFilterDate = DateTimeUtil.getTimeFilterDate(logBean.getCreatedOn());
      
      switch (timeFilterDate) {
      case DAY:
        filters.add(TimeFilter.DAY);
      case WEEK:
        filters.add(TimeFilter.WEEK);
      case MONTH:
        filters.add(TimeFilter.MONTH);
      case YEAR:
        filters.add(TimeFilter.YEAR);
        break;
      default:
        filters.add(TimeFilter.OLDER);
        break;
      }
      
    }
    return filters;
  }
  
  /**
   * getServiceType will return the all the service types where the user belongs.
   * 
   * @param allAlbsk
   *          all audit bean logs for the specific user.
   * @return the service types.
   */
  private Set<ServiceType> getServiceType(List<AuditLogBean> allAlbs) {
    
    Set<ServiceType> serviceType = new HashSet<ServiceType>();
    
    for (AuditLogBean logBean : allAlbs) {
      serviceType.add(logBean.getServiceType());
    }
    return serviceType;
  }
}
