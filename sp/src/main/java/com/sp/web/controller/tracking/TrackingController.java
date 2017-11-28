package com.sp.web.controller.tracking;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.form.TrackingForm;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <code>TrackingCOntrller</code> method will hold the tracking information for the user.
 * 
 * @author pradeep
 *
 */
@Controller
public class TrackingController {

  /** tracking controller. */
  @Autowired
  private TrackingControllerHelper trackingControllerHelper;

  /**
   * <code>getRecntlyVisitedTrackedArticles</code> method will return the recently visited articles by the user to be
   * shown on the right trial of goals tools.
   * 
   * @param token
   *          logged in user
   * @return the sp resposne
   */
  @RequestMapping(value = "/tracking/recentVistedArticles", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getRecentlyTrackedArtlces(Authentication token) {
    return ControllerHelper.process(trackingControllerHelper::getRecentlyVisitedArticle, token);

  }

  @RequestMapping(value = "/tracking/trackArticles", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse trackArticle(Authentication token, TrackingForm trackingForm,
      @RequestParam(required = false) boolean isBookMarked) {

    return ControllerHelper.process(trackingControllerHelper::trackInformation, token, trackingForm, isBookMarked);
  }

  @RequestMapping(value = "/tracking/bookMarkArticle", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addBookMark(Authentication token, TrackingForm trackingForm,
      @RequestParam(required = false, defaultValue = "true") boolean isBookMarked) {

    return ControllerHelper.process(trackingControllerHelper::bookMarkArticle, token, trackingForm, isBookMarked);
  }

  /**
   * Gets all the bookmarked articles for the user.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the response to the get bookmark request
   */
  @RequestMapping(value = "/tracking/getAllBookMarkedArticles", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getAllBookMarkedArtcles(Authentication token) {

    return ControllerHelper.process(trackingControllerHelper::getBookMarkedArticles, token, -1);
  }
  
  /**
   * Gets the list of bookmarked articles for the user. If the count is provided the response
   * will be limited to the count.
   * 
   * @param count
   *          - optional count of articles 
   * @param token
   *          - logged in user
   * @return
   *    the response to the get bookmarked article request
   */
  @RequestMapping(value = "/tracking/getBookMarkedArticles", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getAllBookMarkedArtcles(@RequestParam(defaultValue = "0") int count,
      Authentication token) {

    return ControllerHelper.process(trackingControllerHelper::getBookMarkedArticles, token, count);
  }
  
}
