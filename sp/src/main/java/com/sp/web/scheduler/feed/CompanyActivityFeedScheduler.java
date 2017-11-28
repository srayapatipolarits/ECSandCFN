package com.sp.web.scheduler.feed;

import com.sp.web.Constants;
import com.sp.web.form.Operation;
import com.sp.web.model.Company;
import com.sp.web.model.feed.DashboardMessage;
import com.sp.web.model.feed.NewsFeedType;
import com.sp.web.model.feed.SPActivityFeed;
import com.sp.web.model.feed.SPDashboardPostData;
import com.sp.web.model.tracking.ActivityTracking;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.feed.SPDashboardPostDataRepository;
import com.sp.web.service.feed.NewsFeedFactory;
import com.sp.web.service.feed.NewsFeedHelper;
import com.sp.web.service.tracking.ActivityTrackingFactory;
import com.sp.web.utils.GenericUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         The scheduler class for company activity feeds.
 */
@Component
public class CompanyActivityFeedScheduler {
  
  private static final Random rand = new Random();
  
  @Autowired
  private Environment enviornment;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private ActivityTrackingFactory activityTrackingFactory;
  
  @Autowired
  private SPDashboardPostDataRepository dashboardPostDataRepository;
  
  @Autowired
  private NewsFeedFactory newsFeedFactory;
  
  /**
   * The method to add activity feeds to all the companies.
   */
  //@Scheduled(cron = "${activityFeed.scheduler}")
  public void process() {
    // run only on job server
    if (!GenericUtils.isJobServerNode(enviornment)) {
      return;
    }
    
    final SPDashboardPostData dashboardPostData = dashboardPostDataRepository
        .findById(Constants.DEFAULT_COMPANY_ID);

    // getting a list of all the companies
    List<Company> allCompanies = companyFactory.findAllCompanies();
    allCompanies.forEach(c -> addActivityFeed(c, dashboardPostData));
    
  }
  
  /**
   * Add an activity feed for the company.
   * 
   * @param company
   *          - company
   * @param dashboardPostData 
   *          - dashboard post data
   */
  private void addActivityFeed(Company company, SPDashboardPostData dashboardPostData) {
    
    final String companyId = company.getId();
    
    // get the activity tracking from the user activity tracking db
    ActivityTracking activityTracking = null;
    /*
    LocalDateTime dateToCheck = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
    for (int i = 0; i < Constants.USER_ACTIVITY_FEED_TRIES; i++) {
      dateToCheck = dateToCheck.minusDays(1);
      List<ActivityTracking> activityFeeds = activityTrackingFactory.findActivityFeedAfter(
          companyId, dateToCheck);
      if (!activityFeeds.isEmpty()) {
        activityTracking = activityFeeds.get(rand.nextInt(activityFeeds.size()));
        activityTracking.setPostedToDashboard(true);
        activityTrackingFactory.updateActivityTracking(activityTracking);
        break;
      }
    }*/
    
    
    DashboardMessage message = null;
    /*
    if (activityTracking != null) {
      // adding the activity tracking to the company news feed
      message = DashboardMessage.newMessage(companyId, activityTracking);
    } else 
    */  
    if (dashboardPostData != null) {
      // getting an activity from the SP activity feeds
      Set<String> companyActivityFeed = dashboardPostData.getCompanyActivityFeed(companyId);
      if (companyActivityFeed == null) {
        companyActivityFeed = new HashSet<String>();
        dashboardPostData.setCompanyActivityFeed(companyId, companyActivityFeed);
      }
      
      // getting the SP activity feed 
      SPActivityFeed activityFeed = getSPActivityFeed(company, dashboardPostData.getActivityFeeds(),
          companyActivityFeed);
      dashboardPostDataRepository.save(dashboardPostData);
      
      // create the dashboard message from the activity feed
      if (activityFeed != null) {
        message = DashboardMessage.newMessage(companyId, activityFeed);
      }
    }

    // save the dashboard message and add to the news feed for the company
    if (message != null) {
      newsFeedFactory.createDashbaordMessage(message);
      NewsFeedHelper companyNewsFeedHelper = newsFeedFactory.getCompanyNewsFeedHelper(companyId);
      companyNewsFeedHelper.createNewsFeed(NewsFeedType.DashboardMessage, message);
      newsFeedFactory.sendSseEvent(companyId, message, Operation.ADD);
    }
  }
  
  /**
   * Get the activity feed for the company.
   * @param company 
   *            - company
   * @param activityFeeds
   *            - activity feeds
   * @param companyActivityFeed
   *            - company activity feeds
   * @return
   *    the activity feed found
   */
  private SPActivityFeed getSPActivityFeed(Company company, List<SPActivityFeed> activityFeeds,
      Set<String> companyActivityFeed) {
    SPActivityFeed activityFeed = null;
    if (!CollectionUtils.isEmpty(activityFeeds)) {
      // get the activity feed
      Optional<SPActivityFeed> findFirst = findSPActivityFeed(company, activityFeeds, companyActivityFeed);
      if (!findFirst.isPresent()) {
        // if not found try again
        companyActivityFeed.clear();
        findFirst = findSPActivityFeed(company, activityFeeds, companyActivityFeed);
      }
      
      if (findFirst.isPresent()) {
        activityFeed = findFirst.get();
        companyActivityFeed.add(activityFeed.getId());
      }
    }
    return activityFeed;
  }

  /**
   * Get the activity feed that is not present in company feed.
   * @param company 
   *            - company
   * @param activityFeeds
   *            - activity feeds
   * @param companyActivityFeed
   *            - company activity feed
   * @return
   *    the options of activity feed found
   */
  private Optional<SPActivityFeed> findSPActivityFeed(Company company, List<SPActivityFeed> activityFeeds,
      Set<String> companyActivityFeed) {
    Optional<SPActivityFeed> findFirst = activityFeeds
        .stream()
        .filter(
            af -> company.hasFeature(af.getFeature()) && !companyActivityFeed.contains(af.getId()))
        .findFirst();
    return findFirst;
  }
  
}
