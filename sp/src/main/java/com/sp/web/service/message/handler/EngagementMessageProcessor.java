package com.sp.web.service.message.handler;

import com.sp.web.model.Company;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.goal.UserGoal;
import com.sp.web.model.goal.UserGoalProgress;
import com.sp.web.model.usertracking.TopPracticeTracking;
import com.sp.web.model.usertracking.UserActivityTracking;
import com.sp.web.model.usertracking.UserTrackingType;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.goal.UserGoalsRepository;
import com.sp.web.repository.tracking.UserActivityTrackingRepository;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.service.message.MessageHandlerProcessor;
import com.sp.web.service.message.SPMessageEnvelop;
import com.sp.web.user.UserFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * <code>EngagementMessageProcessor</code> method will process the message and will store the user
 * activity tracking.
 * 
 * @author pradeepruhil
 *
 */
@Component("engagementMatrixHandler")
public class EngagementMessageProcessor implements MessageHandlerProcessor {
  
  /** Intiializing the logger. */
  private static final Logger log = Logger.getLogger(EngagementMessageProcessor.class);
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private UserActivityTrackingRepository userActivtyRepository;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private UserGoalsRepository userGoalsRepository;
  
  @Autowired
  private SPGoalFactory spGoalFactory;
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @Override
  public void process(Message<SPMessageEnvelop> message) {
    
    SPMessageEnvelop payload = message.getPayload();
    
//    if (log.isDebugEnabled()) {
//      log.debug("message to be processoed is " + payload);
//    }
    Assert.notNull(payload.getPayLoadData().get("activityType"), "Activity Type is not present.");
    final UserTrackingType trackingType = (UserTrackingType) payload.getPayLoadData().get("activityType");
    
    String userId = (String) payload.getPayLoadData().get("userId");
//    Assert.notNull(userId, "User not present.");
    if (StringUtils.isEmpty(userId)) {
      return;
    }
    
    User user = userFactory.getUser(userId);
    if (user == null) {
      // checking for hiring user
      user = hiringUserFactory.getUser(userId);
    }
    
    if (user != null) {
      LocalDate date = LocalDate.now();
      //TODO: Why are we doing this get? [dax  06-09-17]
      UserActivityTracking userActivityTracking = userActivtyRepository
          .findUserActivityTrackingByIdDate(userId, date);
      if (userActivityTracking == null) {
        userActivityTracking = new UserActivityTracking();
        userActivityTracking.setUserId(user.getId());
        userActivityTracking.setCompanyId(user.getCompanyId());
        userActivityTracking.setEmail(user.getEmail());
        userActivityTracking.setLastAccessedTime(LocalDateTime.now());
        userActivityTracking.setDate(LocalDate.now());
      }
      
      switch (trackingType) {
      case LoggedIn:
        userActivityTracking.inreaseLoggedInCount();
        userActivityTracking.setLastAccessedTime(LocalDateTime.now());
        break;
      case ArticleCompleted:
        userActivityTracking.inreaseArticleCompletedCount();
        break;
      
      case ArticleViewed:
        userActivityTracking.increaseArticleView();
        break;
      case Session:
        
        /* Get the current time as well */
        LocalDateTime now = LocalDateTime.now();
        
        /* Get the last accessed time */
        LocalDateTime lastAccessedTime = userActivityTracking.getLastAccessedTime();
        //TODO: What does this mean? Why are we taking session time as this parameter. [dax  06-09-17]
        long seconds = Duration.between(lastAccessedTime, now).getSeconds();
        userActivityTracking.addSessionDuration(seconds);
        userActivityTracking.setLastAccessedTime(now);
        break;
      default:
        break;
      }
      userActivtyRepository.save(userActivityTracking);
    } else {
      log.warn("Activity not logged user not found :" + userId + ": activity :" + trackingType);
    }
  }
  
  /**
   * processTopPracticeArea method will process the top practice area of the company.
   */
  public void processTopPracticeArea() {
    /* Get all the users of the company */
    
    List<Company> companies = companyFactory.findAllCompanies();
    
    Map<String, Integer> practiceArea = new HashMap<String, Integer>();
    for (Company company : companies) {
      
      /* get all the valid users of the company */
      
      List<User> users = userFactory.getAllMembersForCompany(company.getId());
      
      /* iterate all the users */
      
      for (User user : users) {
        /* check if user is valid and get the user goa */
        
        if (user.getUserStatus() != UserStatus.VALID) {
          continue;
        }
        if (StringUtils.hasText(user.getUserGoalId())) {
          UserGoal userGoal = userGoalsRepository.findById(user.getUserGoalId());
          if (userGoal == null) {
            continue;
          }
          List<UserGoalProgress> goalProgress = userGoal.getGoalProgress();
          if (!goalProgress.isEmpty()) {
            for (UserGoalProgress ugp : goalProgress) {
              if (ugp != null) {
                if (ugp.isSelected()) {
                  Integer integer = practiceArea.get(ugp.getGoalId());
                  if (integer == null) {
                    integer = new Integer(0);
                    practiceArea.put(ugp.getGoalId(), integer);
                  }
                  integer = integer + 1;
                  practiceArea.put(ugp.getGoalId(), integer);
                }
              }
              
            }
          }
        }
        
      }
      
      LinkedHashMap<String, Integer> collect = practiceArea
          .entrySet()
          .stream()
          .sorted(Entry.comparingByValue())
          .collect(
              Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
      
      LocalDate localDate = LocalDate.now();
      
      TopPracticeTracking findTopPracticeArea = userActivtyRepository.findTopPracticeArea(
          company.getId(), localDate);
      if (findTopPracticeArea == null) {
        findTopPracticeArea = new TopPracticeTracking();
        findTopPracticeArea.setCompanyId(company.getId());
        findTopPracticeArea.setDate(localDate);
      }
      List<String> list = findTopPracticeArea.getPracticeAreas();
      int index = 0;
      for (Map.Entry<String, Integer> entry : collect.entrySet()) {
        if (index >= 5) {
          break;
        }
        list.add(entry.getKey());
        index ++;
      }
      
      userActivtyRepository.saveTopPracticeArea(findTopPracticeArea);
    }
  }
  
}
