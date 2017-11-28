package com.sp.web.controller.hiring.dashboard;

import com.sp.web.dto.hiring.dashboard.CandidateSummaryDTO;
import com.sp.web.dto.hiring.dashboard.EmployeeSummaryDTO;
import com.sp.web.dto.hiring.dashboard.HiringAdminDashboardSettingsListingDTO;
import com.sp.web.dto.hiring.dashboard.HiringDashboardBaseSettingsDTO;
import com.sp.web.dto.hiring.role.HiringRoleListingDTO;
import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.service.hiring.dasboard.AdminHiringDashboardFactory;
import com.sp.web.service.hiring.role.HiringRoleFactory;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.user.UserFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HiringDashboardHelper class is the helper class for request of people analytics dashboard
 * controller.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class HiringDashboardControllerHelper {
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private HiringRoleFactory hiringRoleFactory;
  
  @Autowired
  private LoginHelper LoginHelper;
  
  @Autowired
  private AdminHiringDashboardFactory adminHiringDashboardFactory;
  
  /**
   * getEmployeeCandidate method will return the employee candidate statistics.
   * 
   * @param user
   *          is the sysadmin user.
   * @return the employee candidate stats.
   */
  public SPResponse getEmployeeCandidate(User user) {
    
    List<HiringUser> all = hiringUserFactory.getAll(user.getCompanyId());
    
    final EmployeeSummaryDTO employeeSummaryDTO = new EmployeeSummaryDTO();
    final CandidateSummaryDTO candidateSummaryDTO = new CandidateSummaryDTO();
    
    all.stream().forEach(husr -> {
      
      switch (husr.getType()) {
      case Member:
        calcuateStatistics(employeeSummaryDTO, husr);
        break;
      
      default:
        calcuateStatistics(candidateSummaryDTO, husr);
      }
      
    });
    SPResponse response = new SPResponse();
    response.add("employee", employeeSummaryDTO);
    response.add("candidate", candidateSummaryDTO);
    /* Get the roles data */
    List<HiringRoleListingDTO> allRoles = hiringRoleFactory.getAll(user);
    if (!CollectionUtils.isEmpty(allRoles)) {
      Comparator<HiringRoleListingDTO> updatedOnComp = (db1, db2) -> db2.getUpdatedOn().compareTo(
          db1.getUpdatedOn());
      List<HiringRoleListingDTO> roles = allRoles.stream().sorted(updatedOnComp)
          .collect(Collectors.toList());
      response.add("roles", roles);
    }
    
    //response.add("visited", user.getProfileSettings().isPeopleAnalyticsVisited());
    return response;
    
  }
  
  /**
   * calucateStatics method will caludate the user summary data.
   * 
   * @param employeeSummaryDTO
   *          emplyee sumaarry data.
   * @param user
   *          is the people analyics admin user.
   */
  private void calcuateStatistics(EmployeeSummaryDTO employeeSummaryDTO, HiringUser user) {
    
    employeeSummaryDTO.increaseTotalUsers();
    
    switch (user.getUserStatus()) {
    case VALID:
      employeeSummaryDTO.increasePrismCompleted();
      break;
    case INVITATION_SENT:
    case PROFILE_INCOMPLETE:
      employeeSummaryDTO.increaseProfileIncomplete();
      break;
    case ASSESSMENT_PENDING:
      employeeSummaryDTO.increasePrismPending();
      break;
    case ASSESSMENT_PROGRESS:
      employeeSummaryDTO.increasePrismInProgress();
      break;
    
    case ADD_REFERENCES:
      ((CandidateSummaryDTO) (employeeSummaryDTO)).increaseReferencesPending();
      break;
    default:
      break;
    }
  }
  
  /**
   * getHiringDashboardSettings method will get the hiring dashboard settings for people analytics.
   * 
   * @param user
   *          is the logged in user.
   * @return the user.
   */
  public SPResponse getHiringDashboardSettings(User user) {
    List<HiringDashboardBaseSettingsDTO> all = adminHiringDashboardFactory.getAllDashboard(user);
    
    SPResponse response = new SPResponse();
    response.add("dashboardSettings", all);
    return response;
  }
  
  /**
   * visistedDashboard Request will capture the request whether the user has closed the welcome
   * emssage for people analytics.
   * 
   * @param user
   *          logged in user.
   * @return the success/false
   */
  public SPResponse visitedDashboard(User user) {
    user.getProfileSettings().setPeopleAnalyticsVisited(true);
    LoginHelper.updateUser(user);
    userFactory.updateUser(user);
    return new SPResponse().isSuccess();
  }
}
