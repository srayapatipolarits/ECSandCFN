package com.sp.web.controller.blueprint;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.Constants;
import com.sp.web.form.blueprint.BlueprintForm;
import com.sp.web.form.blueprint.BlueprintResponseForm;
import com.sp.web.form.blueprint.BlueprintShareForm;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <code>BlueprintController</code> class will create and manage the blueprint associate with the
 * user.
 * 
 * @author Dax Abraham
 *
 */
@Controller
public class BlueprintController {
  
  @Autowired
  private BlueprintControllerHelper helper;
  
  /* view for create blueprint */
  @RequestMapping(value = "/blueprint", method = RequestMethod.GET)
  public String validateCreateBlueprint(Authentication token) {
    return "blueprint";
  }
  
  @RequestMapping(value = "/blueprint/getStarted", method = RequestMethod.GET)
  public String blueprintGetStartedCreateBlueprint(Authentication token) {
    return "blueprintGetStarted";
  }
  
  /* view for Thankyou Share blueprint */
  @RequestMapping(value = "/processToken/blueprint/thankYouShare", method = RequestMethod.GET)
  public String validatethankYouShare(Authentication token) {
    return "thankYouShare";
  }
  
  /* view for Thank you approval blueprint */
  @RequestMapping(value = "/processToken/blueprint/thankYouApproval", method = RequestMethod.GET)
  public String validatethankYouApproval(Authentication token) {
    return "thankYouApproval";
  }
  
  /* view for Thank you approval blueprint */
  @RequestMapping(value = "/processToken/blueprint/thankYouRevise", method = RequestMethod.GET)
  public String validatethankYouRevise(Authentication token) {
    return "thankYouRevise";
  }
  
  @RequestMapping(value = "/blueprint/share/{feedbackUserId}", method = RequestMethod.GET)
  public String blueprintShare(Authentication token) {
    return "blueprintShare";
  }
  
  /**
   * Get blueprint settings for the user.
   * 
   * @param token
   *          - logged in user
   * @param feedbackId
   *          - the feedback id
   * @return the blueprint settings
   */
  @RequestMapping(value = "/blueprint/getSettings", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getBlueprint(Authentication token,
      @RequestParam(required = false) String feedbackId) {
    return process(helper::getSettings, token, feedbackId);
  }
  
  /**
   * Get the blueprint of the logged in user or for the member email passed.
   * 
   * @param memberEmail
   *          - member email to get blueprint for
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = "/blueprint/get", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getBlueprint(@RequestParam(required = false) String memberEmail,
      Authentication token) {
    return process(helper::getBlueprint, token, memberEmail);
  }
  
  /**
   * Controller method to create or update the blueprint for the user.
   * 
   * @param blueprintForm
   *          - blueprint data
   * @param token
   *          - logged in user
   * @return the result for the create or update request
   */
  @RequestMapping(value = "/blueprint/createOrUpdate", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse createOrUpdate(@RequestBody BlueprintForm blueprintForm, Authentication token) {
    return process(helper::createOrUpdate, token, blueprintForm);
  }
  
  /**
   * Controller method to share the blueprint.
   * 
   * @param shareForm
   *          - the form data
   * @param token
   *          - logged in user
   * @return the response to the share request
   */
  @RequestMapping(value = "/blueprint/share", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse share(@RequestBody BlueprintShareForm shareForm, Authentication token) {
    return process(helper::share, token, shareForm);
  }
  
  /**
   * Controller method to get the blueprint details for the share request.
   *
   * @param feedbackUserId
   *          - feedback user id
   * @param token
   *          - logged in user
   * @return the response to the get details request
   */
  @RequestMapping(value = "/blueprint/share/details", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse shareBlueprintDetails(@RequestParam String feedbackUserId, Authentication token) {
    return process(helper::shareBlueprintDetails, token, feedbackUserId);
  }
  
  /**
   * To process the blueprint share request.
   * 
   * @param responseForm
   *          - response form
   * @param token
   *          - logged in user
   * @return the response for the process request
   */
  @RequestMapping(value = "/blueprint/share/response", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse shareBlueprintResponse(@RequestBody BlueprintResponseForm responseForm,
      Authentication token) {
    return process(helper::shareBlueprintResponse, token, responseForm);
  }
  
  /**
   * Controller method to delete the blueprint comment.
   * 
   * @param uid
   *          - uid
   * @param by
   *          - by
   * @param token
   *          - logged in user
   * @return the response to the delete request
   */
  @RequestMapping(value = "/blueprint/deleteComment", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteComment(@RequestParam String uid, @RequestParam String by,
      Authentication token) {
    return process(helper::deleteComment, token, uid, by);
  }
  
  /**
   * Controller method to send the reminder for approval.
   * 
   * @param comment
   *          - optional comment
   * @param token
   *          - logged in user
   * @return the response to the send reminder request
   */
  @RequestMapping(value = "/blueprint/sendReminder", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse sendReminder(@RequestParam(required = false) String comment,
      Authentication token) {
    return process(helper::sendReminder, token, comment);
  }
  
  /**
   * Controller method to cancel the approval request.
   * 
   * @param comment
   *          - optional comment
   * @param token
   *          - logged in user
   * @return the response for the cancel request
   */
  @RequestMapping(value = "/blueprint/cancelApprovalRequest", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse cancelApprovalRequest(@RequestParam(required = false) String comment,
      Authentication token) {
    return process(helper::cancelApprovalRequest, token, comment);
  }
  
  /**
   * Controller method to cancel the approval that the user has received.
   * 
   * @param token
   *          - logged in user
   * @return the response to the cancel request
   */
  @RequestMapping(value = "/blueprint/cancelApproval", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse cancelApproval(Authentication token) {
    return process(helper::cancelApproval, token);
  }
  
  /**
   * Remove the feedback received data.
   * 
   * @param uid
   *          - user id
   * @param token
   *          - logged in user
   * @return the response to the delete feedback
   */
  @RequestMapping(value = "/blueprint/deleteFeedbackReceived", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteFeedbackReceived(@RequestParam String uid, Authentication token) {
    return process(helper::deleteFeedbackReceived, token, uid);
  }
  
  /**
   * Controller method to reset the flag for new feedback messages.
   * 
   * @param token
   *          - logged in user
   * @return the response to the update flag
   */
  @RequestMapping(value = "/blueprint/viewedFeedbackMessages", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse viewedFeedbackMessages(Authentication token) {
    return process(helper::viewedFeedbackMessages, token);
  }
  
  /**
   * Controller method to publish the blueprint.
   * 
   * @param token
   *          - logged in user
   * @return the response to the publish request
   */
  @RequestMapping(value = "/blueprint/publish", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse publish(Authentication token) {
    return process(helper::publish, token);
  }
  
  /**
   * Controller method to mark the success measure complete.
   * 
   * @param uid
   *          - UID
   * @param isComplete
   *          - flag to indicate if is completed
   * @param token
   *          - logged in user
   * @return the response to the complete request
   */
  @RequestMapping(value = "/blueprint/completeSuccessMeasure", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse completeSuccessMeasure(@RequestParam String uid,
      @RequestParam(defaultValue = "true") boolean isComplete, Authentication token) {
    return process(helper::completeSuccessMeasure, token, uid, isComplete);
  }
  
  /**
   * Controller method to cancel the published blueprint for edit.
   * 
   * @param token
   *          - logged in user
   * @return the response to the cancel publish request
   */
  @RequestMapping(value = "/blueprint/cancelPublish", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse cancelPublish(Authentication token) {
    return process(helper::cancelPublish, token);
  }
  
  /**
   * Controller method to cancel the blueprint edit and revert back to the previous blueprint from
   * backup.
   * 
   * @param token
   *          - logged in user
   * @return the response to the cancel request
   */
  @RequestMapping(value = "/blueprint/cancelEdit", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse cancelEdit(Authentication token) {
    return process(helper::cancelEdit, token);
  }
  
  /**
   * Controller method to share the blueprint after publish.
   * 
   * @param shareForm
   *          - the share form
   * @param token
   *          - logged in user
   * @return the response to the share request
   */
  @RequestMapping(value = "/blueprint/publishShare", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse publishShare(@RequestBody BlueprintShareForm shareForm, Authentication token) {
    return process(helper::publishShare, token, shareForm);
  }
  
  /**
   * Controller method to share the blueprint after publish.
   * 
   * @param feedbackUserId
   *          - feedback user id
   * @param token
   *          - logged in user
   * @return the response to the share request
   */
  @RequestMapping(value = "/blueprint/share/getDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse publishShare(@RequestParam String feedbackUserId, Authentication token) {
    return process(helper::publishShareGetDetails, token, feedbackUserId);
  }
  
  /**
   * get the blueprint share details in case blueprint is in published state.
   * 
   * @return the publish share view.
   */
  @RequestMapping(value = "/blueprint/share/publish/{feedbackUserId}", method = RequestMethod.GET)
  public String getPublishShareDetails() {
    return Constants.VIEW_BLUEPRINT_PUBLISH_SHARE;
  }
}
