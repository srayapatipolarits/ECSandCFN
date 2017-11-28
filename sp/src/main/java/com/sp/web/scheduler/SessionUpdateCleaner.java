package com.sp.web.scheduler;

import com.sp.web.controller.systemadmin.SessionFactory;
import com.sp.web.utils.GenericUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

/**
 * @author vikram
 * 
 *         This is the scheduler that periodically cleans up all the stale requests in the
 *         SessionUpdateFactory.
 * 
 */
@Component
public class SessionUpdateCleaner {
  
  @Autowired
  @Qualifier("sessionRegistry")
  private SessionRegistry sessionRegistry;
  
  private static final Logger LOG = Logger.getLogger(SessionUpdateCleaner.class);
  
  @Autowired
  private Environment environment;
  
  @Autowired
  private SessionFactory sessionFactory;
  
  /**
   * Remove user from pendingUpdates map if user does not exists in sessionRegistry principals list.
   * 
   */
  @Scheduled(cron = "${sessionUpdateCleaner.schedule}")
  public void cleanSessionUpdateRequests() {
    
    if (GenericUtils.isJobServerNode(environment)) {
      return;
    }
    
    if (LOG.isDebugEnabled()) {
      LOG.debug(Thread.currentThread().getName()
          + ":The Session Update Cleaner scheduller got called !!!");
    }
    
    sessionFactory.cleanSessionUpdateRequests();
    
    if (LOG.isDebugEnabled()) {
      LOG.debug(Thread.currentThread().getName()
          + ":The Session Update Cleaner scheduller ended successfully !!!");
    }
    
  }
}