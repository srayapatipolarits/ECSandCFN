package com.sp.web.controller.feed;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.form.UserMarkerForm;
import com.sp.web.form.feed.SPActivityFeedForm;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * @author Dax Abraham
 *
 *         The controller for all the dashboard related functionalities.
 */
@Controller
public class SPActivityFeedController {
  
  @Autowired
  SPActivityFeedControllerHelper helper;
  
  @RequestMapping(value = "/sysAdmin/dashboardHome", method = RequestMethod.GET)
  public String getDashboardHomeView() {
    return "dashboardHome";
  }
  
  
  /**
   * Controller method to get all the activity feeds configured.
   * 
   * @param token
   *          - logged in user
   * @return 
   *    - the response to the get activity feeds request
   */
  @RequestMapping(value = "/sysAdmin/activityFeeds/getAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getActivityFeeds(Authentication token) {
    return process(helper::getActivityFeeds, token);
  }

  /**
   * Controller method to add activity feed.
   * 
   * @param form
   *          - activity feed form
   * @param token
   *          - logged in user
   * @return
   *    the response to the add activity request
   */
  @RequestMapping(value = "/sysAdmin/activityFeeds/add", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addActivityFeed(@RequestBody SPActivityFeedForm form, Authentication token) {
    return process(helper::addActivityFeed, token, form);
  }

  /**
   * Controller method to update the activity feed.
   * 
   * @param form
   *          - activity feed form
   * @param token
   *          - logged in user
   * @return
   *    the response to the update request
   */
  @RequestMapping(value = "/sysAdmin/activityFeeds/update", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateActivityFeed(@RequestBody SPActivityFeedForm form, Authentication token) {
    return process(helper::updateActivityFeed, token, form);
  }

  /**
   * Controller method to delete the activity feed.
   * 
   * @param id
   *          - activity feed id
   * @param token
   *          - logged in user
   * @return
   *    the response to the delete request
   */
  @RequestMapping(value = "/sysAdmin/activityFeeds/delete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteActivityFeed(@RequestParam String id, Authentication token) {
    return process(helper::deleteActivityFeed, token, id);
  }

  /**
   * Controller method to get all the authors for posts.
   * 
   * @param token
   *          - logged in user
   * @return
   *     the list of authors
   */
  @RequestMapping(value = "/sysAdmin/posts/author/getAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllPostsAuthor(Authentication token) {
    return process(helper::getAllPostsAuthor, token);
  }

  /**
   * Controller method to add an author.
   * 
   * @param form
   *          - author form
   * @param token
   *          - user
   * @return
   *    the response to the add request
   */
  @RequestMapping(value = "/sysAdmin/posts/author/add", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addPostsAuthor(@RequestBody @Valid UserMarkerForm form, Authentication token) {
    return process(helper::addPostsAuthor, token, form);
  }

  /**
   * Controller method to update the post authors.
   * 
   * @param form
   *            - form 
   * @param token
   *            - user
   * @return
   *    the response to the update request
   */
  @RequestMapping(value = "/sysAdmin/posts/author/update", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updatePostsAuthor(@RequestBody @Valid UserMarkerForm form, Authentication token) {
    return process(helper::updatePostsAuthor, token, form);
  }

  /**
   * Controller method to delete the author.
   * 
   * @param authorId
   *            - author id
   * @param token
   *            - user
   * @return
   *    the response to the delete request
   */
  @RequestMapping(value = "/sysAdmin/posts/author/delete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deletePostsAuthor(@RequestParam String authorId, Authentication token) {
    return process(helper::deletePostsAuthor, token, authorId);
  }
  
}
