package com.sp.web.controller.feed;

import com.sp.web.Constants;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.form.UserMarkerForm;
import com.sp.web.form.feed.SPActivityFeedForm;
import com.sp.web.model.User;
import com.sp.web.model.feed.SPActivityFeed;
import com.sp.web.model.feed.SPDashboardPostData;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.feed.SPDashboardPostDataRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

/**
 * @author Dax Abraham
 *
 *         The controller helper for SurePeople Activity Feed controller.
 */
@Controller
public class SPActivityFeedControllerHelper {
  
  @Autowired
  private SPDashboardPostDataRepository dashboardPostDataRepository;
  
  /**
   * Helper method to get all the activity feeds configured.
   * 
   * @param user
   *          - logged in user
   * @return
   *    - the response to the get activity feeds request
   */
  public SPResponse getActivityFeeds(User user) {
    final SPResponse resp = new SPResponse();
    
    SPDashboardPostData dashboardPostData = getSPDashboardPostData();
    
    List<SPActivityFeed> activityFeeds = null;
    if (dashboardPostData != null) {
      activityFeeds = dashboardPostData.getActivityFeeds();
    }
    
    return resp.add(Constants.PARAM_ACTIVITY_FEED, activityFeeds);
  }
  
  /**
   * Helper method to add activity feed.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return
   *    the response to the add activity request
   */
  public SPResponse addActivityFeed(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the form
    SPActivityFeedForm form = (SPActivityFeedForm) params[0];
    form.validate();
    
    // get the activity feeds
    SPDashboardPostData dashboardPostData = getSPDashboardPostData();
    
    // create sp activity feed if not found
    if (dashboardPostData == null) {
      dashboardPostData = SPDashboardPostData.defaultInstance();
      dashboardPostDataRepository.save(dashboardPostData);
    }
    
    // adding the new activity feed
    SPActivityFeed activityFeed = form.addActivityFeed(dashboardPostData.getActivityFeeds());
    
    // updating the db
    dashboardPostDataRepository.save(dashboardPostData);
    
    // sending the response
    return resp.add(Constants.PARAM_ACTIVITY_FEED, activityFeed);
  }

  /**
   * Helper method to update the activity feed.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return
   *    the response to the update activity request
   */
  public SPResponse updateActivityFeed(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the form
    SPActivityFeedForm form = (SPActivityFeedForm) params[0];
    form.validateUpdate();
    
    // get the activity feeds
    SPDashboardPostData dashboardPostData = getSPDashboardPostData(true);
    
    // get the activity feeds and validate the position
    List<SPActivityFeed> activityFeeds = dashboardPostData.getActivityFeeds();
    final int position = form.getPosition();
    Assert.isTrue(activityFeeds.size() > position, "Position is required.");
    
    SPActivityFeed activityFeed = activityFeeds.get(position);
    final String feedId = form.getId();
    if (!activityFeed.getId().equals(feedId)) {
      // change in position
      // finding the activity feed
      Optional<SPActivityFeed> findFirst = activityFeeds.stream()
          .filter(af -> af.getId().equals(feedId)).findFirst();
      Assert.isTrue(findFirst.isPresent(), "Feed not found.");
      // updating the position
      activityFeed = findFirst.get();
      activityFeeds.remove(activityFeed);
      activityFeeds.add(position, activityFeed);
    }
    
    // updating the activity data
    form.updateData(activityFeed);
    
    // saving to the db
    dashboardPostDataRepository.save(dashboardPostData);
    
    // sending the response
    return resp.isSuccess();
  }
  
  /**
   * Helper method to delete the activity feed.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return
   *    the response to the delete activity request
   */
  public SPResponse deleteActivityFeed(User user, Object[] params) {
    final SPResponse resp = new SPResponse();

    // get the id of the feed to delete
    String id = (String) params[0];
    Assert.hasText(id, "Id is required.");
    
    // get the activity feeds
    SPDashboardPostData spActivityFeeds = getSPDashboardPostData(true);
    
    // get the activity feeds and validate the position
    List<SPActivityFeed> activityFeeds = spActivityFeeds.getActivityFeeds();
    
    // finding the activity feed
    Optional<SPActivityFeed> findFirst = activityFeeds.stream()
        .filter(af -> af.getId().equals(id)).findFirst();
    Assert.isTrue(findFirst.isPresent(), "Feed not found.");
    
    // removing the activity feed
    activityFeeds.remove(findFirst.get());
    
    // updating DB
    dashboardPostDataRepository.save(spActivityFeeds);
    
    // sending back the response
    return resp.isSuccess();
  }
  
  /**
   * Helper method to get all the authors for posts.
   * 
   * @param user
   *          - user
   * @return
   *      the list of authors
   */
  public SPResponse getAllPostsAuthor(User user) {
    final SPResponse resp = new SPResponse();
    
    SPDashboardPostData spDashboardPostData = getSPDashboardPostData();
    
    List<UserMarkerDTO> authors = null;
    if (spDashboardPostData != null) {
      authors = spDashboardPostData.getAuthorList();
    }
    
    // sending back the response
    return resp.add(Constants.PARAM_AUTHOR, authors);
  }
  
  /**
   * Helper method for adding an author.
   * 
   * @param user
   *          - user
   * @param params
   *          - params         
   * @return
   *    the response to the add request
   */
  public SPResponse addPostsAuthor(User user, Object[] params) {
    
    UserMarkerForm form = (UserMarkerForm) params[0];
    
    SPDashboardPostData spDashboardPostData = getSPDashboardPostData();
    if (spDashboardPostData == null) {
      spDashboardPostData = SPDashboardPostData.defaultInstance();
      dashboardPostDataRepository.save(spDashboardPostData);
    }
    
    // adding the new author
    spDashboardPostData.getAuthorList().add(form.getUserMarkerDTO());
    dashboardPostDataRepository.save(spDashboardPostData);
    
    // sending back the response
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to update the post authors.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    - the response to the update request
   */
  public SPResponse updatePostsAuthor(User user, Object[] params) {
    
    // get the form to process
    UserMarkerForm form = (UserMarkerForm) params[0];
    form.validateUpdate();
    
    // get the dashboard post data
    SPDashboardPostData dashboardPostData = getSPDashboardPostData(true);
    
    // updating the found author instance and 
    // saving to DB
    form.update(findAuthor(form.getId(), dashboardPostData));
    dashboardPostDataRepository.save(dashboardPostData);
    
    // sending back the response
    return new SPResponse().isSuccess();
  }

  /**
   * Gets the author from the author list.
   * 
   * @param authorId
   *          - author id
   * @param dashboardPostData
   *          - dashboard post data
   * @return
   *   the author
   */
  private UserMarkerDTO findAuthor(String authorId,
      SPDashboardPostData dashboardPostData) {
    // get the author list and find the author instance
    List<UserMarkerDTO> authorList = dashboardPostData.getAuthorList();
    Optional<UserMarkerDTO> findFirst = authorList.stream().filter(a -> a.getId().equals(authorId))
        .findFirst();    
    Assert.isTrue(findFirst.isPresent(), "Author not found.");
    return findFirst.get();
  }
  
  /**
   * Helper method to delete the author.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    - the response to the delete request
   */
  public SPResponse deletePostsAuthor(User user, Object[] params) {
    
    // get the form to process
    String authorId = (String) params[0];
    Assert.hasText(authorId, "Author id required.");
    
    // get the dashboard post data
    SPDashboardPostData dashboardPostData = getSPDashboardPostData(true);

    // find the author and remove it from the author list
    UserMarkerDTO author = findAuthor(authorId, dashboardPostData);
    dashboardPostData.removeAuthor(author);
    dashboardPostDataRepository.save(dashboardPostData);
    
    // sending back the response
    return new SPResponse().isSuccess();
  }
  
  /**
   * @return
   *      - Get the activity feeds.
   */
  private SPDashboardPostData getSPDashboardPostData() {
    return getSPDashboardPostData(false);
  }

  /**
   * @param validate
   *      - flag to validate if null
   * @return
   *      - Get the activity feeds.
   */
  private SPDashboardPostData getSPDashboardPostData(boolean validate) {
    final SPDashboardPostData findById = dashboardPostDataRepository.findById(Constants.DEFAULT_COMPANY_ID);
    if (validate) {
      Assert.notNull(findById, "Dashboard post data not found.");
    }
    return findById;
  }
  
}
