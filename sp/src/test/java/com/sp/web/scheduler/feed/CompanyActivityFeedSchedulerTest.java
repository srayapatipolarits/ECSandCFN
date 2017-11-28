package com.sp.web.scheduler.feed;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.sp.web.Constants;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.feed.DashboardMessage;
import com.sp.web.model.feed.DashboardMessageType;
import com.sp.web.model.feed.SPActivityFeed;
import com.sp.web.model.feed.SPDashboardPostData;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.tracking.ActivityTracking;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.repository.feed.SPDashboardPostDataRepository;
import com.sp.web.service.tracking.ActivityTrackingFactory;
import com.sp.web.service.tracking.ActivityTrackingHelper;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

public class CompanyActivityFeedSchedulerTest extends SPTestLoggedInBase {

  @Autowired
  CompanyActivityFeedScheduler scheduler;
  
  @Autowired
  SPDashboardPostDataRepository activityFeedsRepository;
  
  @Autowired
  ActivityTrackingFactory activityTrackingFactory;
  
  @Autowired
  ActivityTrackingHelper activityTrackingHelper;
  
  @Test
  public void testScheduler() {
    try {
      
      dbSetup.removeAll("sPDashboardPostData");
      dbSetup.removeAll("companyNewsFeed");
      dbSetup.removeAll("dashboardMessage");
      dbSetup.removeAll("activityTracking");
      
      scheduler.process();
      List<DashboardMessage> all = dbSetup.getAll(DashboardMessage.class);
      assertThat(all, is(empty()));

      // creating the dashboard post data
      SPDashboardPostData dashboardPostData = createDashboardPostData();
      final List<SPActivityFeed> activityFeeds = dashboardPostData.getActivityFeeds();
      
      scheduler.process();
      all = dbSetup.getAll(DashboardMessage.class);
      assertThat(all, is(empty()));
      
      SPActivityFeed activityFeed = new SPActivityFeed(SPFeature.Hiring,
          "Something to do with SP Match.");
      activityFeeds.add(activityFeed);
      activityFeedsRepository.save(dashboardPostData);
      
      scheduler.process();
      all = dbSetup.getAll(DashboardMessage.class);
      assertThat(all, is(empty()));
      
      dashboardPostData = activityFeedsRepository.findById(Constants.DEFAULT_COMPANY_ID);
      final List<SPActivityFeed> activityFeeds2 = dashboardPostData.getActivityFeeds();
      activityFeed = new SPActivityFeed(SPFeature.Prism, "Something to do with Prism.");
      activityFeeds2.add(activityFeed);
      activityFeedsRepository.save(dashboardPostData);

      scheduler.process();
      all = dbSetup.getAll(DashboardMessage.class);
      assertThat(all, hasSize(1));
      assertThat(all.get(0).getType(), is(DashboardMessageType.SPActivityFeed));

      scheduler.process();
      all = dbSetup.getAll(DashboardMessage.class);
      assertThat(all, hasSize(2));
      assertThat(all.get(0).getType(), is(DashboardMessageType.SPActivityFeed));
      assertThat(all.get(1).getType(), is(DashboardMessageType.SPActivityFeed));
      
      User user = dbSetup.getUser("admin@admin.com");
      // adding activity tracking
      activityTrackingHelper.trackActivity(user, LogActionType.RelationshipAdvisor, null);
      
      // sleeping for data to get tracked
      Thread.sleep(1000);
      
      scheduler.process();
      all = dbSetup.getAll(DashboardMessage.class);
      assertThat(all, hasSize(3));
      assertThat(all.get(0).getType(), is(DashboardMessageType.SPActivityFeed));
      assertThat(all.get(1).getType(), is(DashboardMessageType.SPActivityFeed));
      assertThat(all.get(2).getType(), is(DashboardMessageType.UserActivity));

      List<ActivityTracking> all2 = dbSetup.getAll(ActivityTracking.class);
      ActivityTracking activityTracking = all2.get(0);
      activityTracking.setPostedToDashboard(false);
      activityTracking.setCreatedOn(LocalDateTime.now().minusDays(4));
      activityTrackingFactory.create(activityTracking);
      
      scheduler.process();
      all = dbSetup.getAll(DashboardMessage.class);
      assertThat(all, hasSize(4));
      assertThat(all.get(0).getType(), is(DashboardMessageType.SPActivityFeed));
      assertThat(all.get(1).getType(), is(DashboardMessageType.SPActivityFeed));
      assertThat(all.get(2).getType(), is(DashboardMessageType.UserActivity));
      assertThat(all.get(3).getType(), is(DashboardMessageType.SPActivityFeed));
      
      activityTracking.setCreatedOn(LocalDateTime.now().minusDays(3));
      activityTrackingFactory.create(activityTracking);

      scheduler.process();
      all = dbSetup.getAll(DashboardMessage.class);
      assertThat(all, hasSize(5));
      assertThat(all.get(0).getType(), is(DashboardMessageType.SPActivityFeed));
      assertThat(all.get(1).getType(), is(DashboardMessageType.SPActivityFeed));
      assertThat(all.get(2).getType(), is(DashboardMessageType.UserActivity));
      assertThat(all.get(3).getType(), is(DashboardMessageType.SPActivityFeed));
      assertThat(all.get(4).getType(), is(DashboardMessageType.UserActivity));

      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  private SPDashboardPostData createDashboardPostData() {
    final SPDashboardPostData activityFeeds = SPDashboardPostData.defaultInstance();
    activityFeedsRepository.save(activityFeeds);
    return activityFeeds;
  }
  
}
