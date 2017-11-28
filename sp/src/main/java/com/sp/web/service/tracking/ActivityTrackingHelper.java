package com.sp.web.service.tracking;

import com.sp.web.model.User;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.log.LogType;
import com.sp.web.model.tracking.ActivityTracking;
import com.sp.web.repository.tracking.ActivityTrackingRepository;
import com.sp.web.utils.ApplicationContextUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * ActivityTrackingFactory tracks all the activities of the user.
 * 
 * @author Dax Abraham
 *
 */
@Component
public class ActivityTrackingHelper {
  
  private static final Logger log = Logger.getLogger(ActivityTrackingHelper.class);
  
  private static final Map<LogType, ActivityTrackingProcessor> processorMap = 
      new HashMap<LogType, ActivityTrackingProcessor>();

  @Autowired
  ActivityTrackingRepository activityTrackingRepository;
  
  @Autowired
  ActivityTrackingFactory activityTrackingFactory;
  
  /**
   * Track activity method tracks the activity of the user.
   * 
   * @param user
   *          whose activity is to be tracked.
   * @param actionType
   *          of the activity.
   * @param args
   *          contains the parameter for the activity.
   */
  @Async
  public void trackActivity(User user, LogActionType actionType, Object[] args) {
    if (log.isDebugEnabled()) {
      log.debug("User :" + user + ", action Type :" + actionType);
    }
    
    try {
      ActivityTrackingProcessor processor = getProcessor(actionType);
      ActivityTracking activityTracking = new ActivityTracking(user, actionType);
      if (processor.updateActivityTracking(user, actionType, args, activityTracking)) {
        activityTrackingFactory.create(activityTracking);
      }
    } catch (Exception e) {
      log.warn("Unable to track activity.", e);
    }
  }

  /**
   * Get the activity tracking processor.
   * 
   * @param actionType
   *          - action type
   * @return
   *      
   */
  private ActivityTrackingProcessor getProcessor(LogActionType actionType) {
    final LogType logType = actionType.getLogType();
    ActivityTrackingProcessor processor = processorMap.get(logType);
    if (processor == null) {
      processor = (ActivityTrackingProcessor) ApplicationContextUtils.getBean(StringUtils
          .uncapitalize(logType.toString()) + "ActivityTrackingProcessor");
      processorMap.put(logType, processor);
    }
    return processor;
  }
}
