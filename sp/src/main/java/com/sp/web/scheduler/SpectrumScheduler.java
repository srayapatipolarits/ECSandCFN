package com.sp.web.scheduler;

import com.sp.web.service.message.handler.EngagementMessageProcessor;
import com.sp.web.spectrum.BlueprintAnalyticsGenerator;
import com.sp.web.spectrum.ErtiAnalyticsGenerator;
import com.sp.web.spectrum.ErtiInsightsGenerator;
import com.sp.web.spectrum.OrganizationPlanActivitiesGenerator;
import com.sp.web.spectrum.ProfileGenerator;
import com.sp.web.utils.GenericUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author pradeepruhil
 * 
 *         The scheduler for spectrum related tasks.
 */
@Component
public class SpectrumScheduler {
  
  /** INitializing the logger. */
  private static final Logger LOG = Logger.getLogger(SpectrumScheduler.class);
  
  @Autowired
  private ProfileGenerator profileGenerator;
  
  // @Autowired
  // private LearnerStatusGenerator learnerStatusGenerator;
  
  // @Autowired
  // private HiringInsightsGenerator hiringInsightsGenerator;
  
  @Autowired
  private BlueprintAnalyticsGenerator blueprintAnalyticsGenerator;
  
  @Autowired
  private ErtiInsightsGenerator ertiInsightsGenerator;
  
  @Autowired
  private ErtiAnalyticsGenerator ertiAnalyticsGenerator;
  
  @Autowired
  private OrganizationPlanActivitiesGenerator organizationPlanActivitiesGenerator;
  
  @Autowired
  private Environment environment;
  
  @Autowired
  @Qualifier("engagementMatrixHandler")
  private EngagementMessageProcessor engagementProcessor;
  
  /**
   * LoadBalancs method will load the profile balances.
   */
  @Scheduled(cron = "${spectrum.profileBalances.schedule}")
  public void processProfileBalances() {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug(Thread.currentThread().getName()
          + ":Spectrum -> Profile Balance scheduller got called !!!");
    }
    profileGenerator.reset();
  }
  
  /**
   * LoadBalancs method will load the profile balances.
   */
  @Scheduled(cron = "${spectrum.blueprintAnalytics.schedule}")
  public void processBlueprintAnalytics() {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug(Thread.currentThread().getName()
          + ":Spectrum -> BlueprintAnalytics scheduller got called !!!");
    }
    blueprintAnalyticsGenerator.reset();
  }
  
  /**
   * LoadBalancs method will load the profile balances.
   */
  @Scheduled(cron = "${spectrum.organizationPlanActivites.schedule}")
  public void processOrganizationPlanActivitiesGenerator() {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug(Thread.currentThread().getName()
          + ":Spectrum -> processOrganizationPlanActivitiesGenerator scheduller got called !!!");
    }
    organizationPlanActivitiesGenerator.reset();
  }
  
  /**
   * LoadBalancs method will load the profile balances.
   */
  @Scheduled(cron = "${spectrum.ertiAnalytics.schedule}")
  public void processErtiAnalytics() {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug(Thread.currentThread().getName()
          + ":Spectrum -> processErtiAnalytics Generator scheduller got called !!!");
    }
    ertiAnalyticsGenerator.reset();
  }
  
  /**
   * LoadBalancs method will load the profile balances.
   */
  @Scheduled(cron = "${spectrum.ertiInsights.schedule}")
  public void processErtiInsights() {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug(Thread.currentThread().getName()
          + ":Spectrum -> ertiInsightsGenerator scheduller got called !!!");
    }
    ertiInsightsGenerator.reset();
  }
  
  /**
   * processTopPracticeArea method will process the top practice area of the companyies.
   */
  @Scheduled(cron = "${spectrum.engagmentProcessor.schedule}")
  public void processTopPracticeArea() {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug(Thread.currentThread().getName()
          + ":Spectrum -> processTopPracticeArea scheduller got called !!!");
    }
    if (!GenericUtils.isJobServerNode(environment)) {
      return;
    }
    engagementProcessor.processTopPracticeArea();
  }
  
  // /**
  // * LoadBalancs method will load the profile balances.
  // */
  // @Scheduled(cron = "${spectrum.learnerStatus.schedule}")
  // public void processLearnerStatus() {
  // LOG.debug(Thread.currentThread().getName() + ":Before Spectrum Learner Status");
  // if (!GenericUtils.isJobServerNode(environment)) {
  // return;
  // }
  // if (LOG.isDebugEnabled()) {
  // LOG.debug(Thread.currentThread().getName()
  // + ":Spectrum Learner Status scheduller got called !!!");
  // }
  // learnerStatusGenerator.loadLearnerStatus();
  //
  // }
  
  // /**
  // * Load the hiring profile balance.
  // */
  // @Scheduled(cron = "${spectrum.hiringFilterProfileBalances.schedule}")
  // public void processHiringFilterProfileBalances() {
  //
  // if (!GenericUtils.isJobServerNode(environment)) {
  // return;
  // }
  // if (LOG.isDebugEnabled()) {
  // LOG.debug(Thread.currentThread().getName()
  // + ":The Hiring Filter Profile Balance scheduller got called !!!");
  // }
  // hiringInsightsGenerator.loadHiringFilterProfileBalances();
  // }
  //
  // /**
  // * LoadBalancs method will load the profile balances.
  // */
  // @Scheduled(cron = "${spectrum.hiringfilterInsights.schedule}")
  // public void loadHiringFilterInsights() {
  //
  // if (!GenericUtils.isJobServerNode(environment)) {
  // return;
  // }
  // if (LOG.isDebugEnabled()) {
  // LOG.debug(Thread.currentThread().getName()
  // + ":The Hiring Filter Insights scheduller got called !!!");
  // }
  // hiringInsightsGenerator.loadHiringFilterInsights();
  // }
}
