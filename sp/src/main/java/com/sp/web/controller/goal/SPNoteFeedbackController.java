package com.sp.web.controller.goal;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.model.RequestStatus;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <code>SPNoteFeedbackController</code> class will fetch the notes & feedback associate with the
 * user, goalid and dev strategy.
 * 
 * @author vikram
 *
 */
@Controller
public class SPNoteFeedbackController {
  
  @Autowired
  private SPNoteFeedbackHelper spnotefeedbackHelper;
  
  @RequestMapping(value = "/goals/feedback/external/complete", method = RequestMethod.GET)
  public String importListView() {
    return "feedbackCompleted";
  }
  
  @RequestMapping(value = "/goals/note/add", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addNote(@RequestParam String content,
      Authentication token, @RequestParam String goalId,
      @RequestParam String devStrategyId) {
    return ControllerHelper.process(spnotefeedbackHelper::addNote, token, content, goalId,
        devStrategyId);
  }
  
  @RequestMapping(value = "/goals/note/update", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateNote(Authentication token,
      @RequestParam String noteId, @RequestParam String desc) {
    
    return ControllerHelper.process(spnotefeedbackHelper::updateNote, token, noteId, desc);
  }
  
  @RequestMapping(value = "/goals/note/delete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteNote(Authentication token,
      @RequestParam String noteId) {
    return ControllerHelper.process(spnotefeedbackHelper::deleteNote, token, noteId);
  }

  /**
   * Controller method to delete all the notes and feedback for a given practice area.
   * 
   * @param token
   *          - logged in user
   * @param goalId
   *          - practice area id
   * @param deleteNote
   *          - flag to delete all notes
   * @param deleteFeedback
   *          - flag to delete all feedback
   * @return
   *      the response to the delete request
   */
  @RequestMapping(value = "/goals/notesFeedback/deleteAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteNote(Authentication token,
      @RequestParam String goalId, @RequestParam(required = false) boolean deleteNote,
      @RequestParam(required = false) boolean deleteFeedback) {
    return ControllerHelper.process(spnotefeedbackHelper::deleteAll, token, goalId, deleteNote,
        deleteFeedback);
  }
  
  @RequestMapping(value = "/goals/note/detail", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getNoteDetail(Authentication token,
      @RequestParam String noteId) {
    
    return ControllerHelper.process(spnotefeedbackHelper::getNoteDetail, token, noteId);
  }
  
  @RequestMapping(value = "/goals/feedback/detail", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getFeedbackDetail(Authentication token,
      @RequestParam String feedbackId) {
    
    return ControllerHelper.process(spnotefeedbackHelper::getFeedbackDetail, token, feedbackId);
  }
  
  /**
   * Controller method to create the feedback.
   * 
   * @param content
   *          - content for the feedback request
   * @param token
   *          - logged in user
   * @param goalId
   *          - practice area id
   * @param devStrategyId
   *          - development strategy id
   * @param feedbackUserEmail
   *          - feedback user email
   * @return the response to the feedback request
   */
  @RequestMapping(value = "/goals/feedback/request", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse createRequestFeedback(@RequestParam(required = false) String content,
      Authentication token, @RequestParam String goalId,
      @RequestParam(required = false) String devStrategyId,
      @RequestParam List<String> feedbackUserEmail) {
    
    return ControllerHelper.process(spnotefeedbackHelper::createRequestFeedback, token, content,
        goalId, devStrategyId, feedbackUserEmail);
  }
  
  @RequestMapping(value = "/goals/feedback/delete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteRequestFeedback(Authentication token,
      @RequestParam String feedbackId) {
    
    return ControllerHelper.process(spnotefeedbackHelper::deleteRequestFeedback, token, feedbackId);
  }
  
  @RequestMapping(value = "/goals/feedback/external/updateStatus", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateRequestFeedbackExternalStatus(Authentication token,
      @RequestParam String feedbackId, @RequestParam RequestStatus feedbackStatus,
      @RequestParam(required = false, defaultValue = "") String feedbackResponse) {
    return ControllerHelper.process(spnotefeedbackHelper::updateRequestFeedbackStatus, token,
        feedbackId, feedbackStatus, feedbackResponse);
  }
  
  @RequestMapping(value = "/goals/feedback/updateStatus", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateRequestFeedbackStatus(Authentication token,
      @RequestParam String feedbackId, @RequestParam RequestStatus feedbackStatus,
      @RequestParam(required = false, defaultValue = "") String feedbackResponse) {
    return ControllerHelper.process(spnotefeedbackHelper::updateRequestFeedbackStatus, token,
        feedbackId, feedbackStatus, feedbackResponse);
  }
  
  /**
   * Get the blueprint details for the given token.
   * 
   * @param token
   *          - logged in user
   * @param feedbackId
   *          - the feedback id         
   * @return
   *    the blueprint details
   */
  @RequestMapping(value = "/goals/token/feedback/blueprint/details", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getBlueprintDetails(Authentication token, @RequestParam String feedbackId) {
    return ControllerHelper.process(spnotefeedbackHelper::getBlueprintDetails, token, feedbackId);
  }
  

  /**
   * Give the request feedback details.
   * 
   * @param token
   *          - logged in user
   * @param feedbackId
   *          - feedback id
   * @param feedbackResponse
   *          - feedback response
   * @return
   *    the response to the give feedback request
   */
  @RequestMapping(value = "/goals/feedback/external/response", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse giveRequestFeedback(Authentication token,
      @RequestParam String feedbackId, @RequestParam String feedbackResponse) {
    return ControllerHelper.process(spnotefeedbackHelper::giveRequestFeedback, token, feedbackId,
        feedbackResponse);
  }
  
  @RequestMapping(value = "/goals/notesfeedback/all", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getAllNotesFeedback(Authentication token) {
    return ControllerHelper.process(spnotefeedbackHelper::getNotesFeedbackForGoal, token,
        (String) null, (String) null);
  }
  
  @RequestMapping(value = "/goals/notesfeedback", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getNotesFeedbackForGoal(Authentication token,
      @RequestParam String goalId,
      @RequestParam(defaultValue = "", required = false) String memberEmail) {
    return ControllerHelper.process(spnotefeedbackHelper::getNotesFeedbackForGoal, token, goalId,memberEmail);
  }
  
  @RequestMapping(value = "/goals/feedback/received", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getRequestFeedbackReceived(Authentication token) {
    return ControllerHelper.process(spnotefeedbackHelper::getRequestFeedbackReceived, token);
  }
  
  /**
   * View For Notes & Feedback Landing Page.
   * 
   */
  @RequestMapping(value = "/goals/notesfeedback/view", method = RequestMethod.GET)
  public String getNotesAndFeedbackView(Authentication token) {
    return "notesAndFeedbackHome";
  }
  
}
