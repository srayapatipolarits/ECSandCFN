package com.sp.web.scheduler;

import com.sp.web.controller.pulse.WorkspacePulseFactory;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.pulse.PulseAssessment;
import com.sp.web.model.pulse.PulseRequest;
import com.sp.web.repository.pulse.WorkspacePulseRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.utils.GenericUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The scheduler class that runs daily to create the pulse results for pulse requests that
 *         have been completed.
 */
@Component
public class WorkspacePulseDailyProcessor {
  
  private static final Logger log = Logger.getLogger(WorkspacePulseDailyProcessor.class);
  
  @Autowired
  WorkspacePulseRepository pulseRepository;
  
  @Autowired
  WorkspacePulseFactory pulseFactory;
  
  @Autowired
  UserRepository userRepository;
  
  @Autowired
  private Environment enviornment;
  
  /**
   * The scheduler method that will read the pulse requests that are older than a day old.
   */
  @Scheduled(cron = "${accountValidator.schedule}")
  public void processPulseRequests() {
    
    if (!GenericUtils.isJobServerNode(enviornment)) {
      return;
    }
    if (log.isDebugEnabled()) {
      log.debug("Thread " + Thread.currentThread().getName()
          + ", Going to finalize all pluse requests that are past the end date !!!");
    }
    
    List<PulseRequest> allPulseRequests = pulseRepository.findAllPulseRequests();
    
    LocalDate now = LocalDate.now();
    Period period;
    for (PulseRequest pulseRequest : allPulseRequests) {
      period = Period.between(now, pulseRequest.getEndDate());
      if (log.isDebugEnabled()) {
        log.debug("Period.getDays" + period.getDays());
      }
      if (period.getDays() < 0) {
        pulseFactory.finishPulse(pulseRequest, true);
        continue;
      }
      String companyId = pulseRequest.getCompanyId();
      // get the total number of people in the company
      int numberOfActiveMembers = userRepository.getNumberOfActiveMembers(companyId, SPPlanType.Primary);
      // get the number responses for the given pulse
      List<PulseAssessment> allPulseAssessments = pulseRepository
          .getAllPulseAssessments(pulseRequest.getId());
      // close the pulse if all the people in the company have responded to the pulse
      if (numberOfActiveMembers == allPulseAssessments.size()) {
        pulseFactory.finishPulse(pulseRequest, true);
      }
    }
    
    // added new logic to close the pulse when all the people in the company have taken the
    // pulse assessment
    // 26 May 2014
    // List<PulseRequest> pulseRequests =
    // pulseRepository.findPulseRequestsByEndDate(LocalDate.now()
    // .minusDays(1));
    // pulseRequests.forEach(r -> pulseFactory.finishPulse(r, true));
    
    if (log.isDebugEnabled()) {
      log.debug("Pulse request processing completed !!!");
    }
    
  }
  
}
