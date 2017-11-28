package com.sp.web.controller.pc;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.form.pchannel.PublicChannelForm;
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
 * PubliChannel control handles allt he request for public channel.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class PublicChannelController {
  
  @Autowired
  private PublicChannelControllerHelper publicChannelControllerHelper;
  
  /**
   * <code>getPublicChannel</code> will return the public channel for the user.
   * 
   * @param pcRefId
   *          reference ID for which public channel is to be retrieved.
   * @return the public channel data for the response.
   */
  @RequestMapping(value = "/pubchannel/getPublicChannel", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getPublicChannel(@RequestParam String pcRefId, Authentication authentication) {
    return ControllerHelper.process(publicChannelControllerHelper::getPublicChannel,
        authentication, pcRefId);
  }
  
  /**
   * createPublicChannel method will create the public channel for the user.
   * 
   * @param publicChannelForm
   *          public channel form for creating the public channel.
   * @param authentication
   *          the logged in user.
   * @return the response whether public channel got created or not.
   */
  @RequestMapping(value = "/pubchannel/createPublicChannel", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse createPublicChannel(@RequestBody PublicChannelForm publicChannelForm,
      Authentication authentication) {
    return ControllerHelper.process(publicChannelControllerHelper::createPubliChannel,
        authentication, publicChannelForm);
  }
  
  /**
   * followPublicChannel request will follow or un follow the user from the public channel.
   * 
   * @param follow
   *          flag indicates to follow or unfollow.
   * @param pcRefId
   *          public channle refernce Id.
   * @param authentication
   *          logged in user.
   * @return the sp response whether the request was success or failure.
   */
  @RequestMapping(value = "/pubchannel/followPublicChannel", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse followPublicChannel(@RequestParam boolean follow, @RequestParam String pcRefId,
      Authentication authentication) {
    return ControllerHelper.process(publicChannelControllerHelper::followUnfollowPublicChannel,
        authentication, follow, pcRefId);
    
  }
  
  /**
   * addComment request will add the comment to the public channel.
   * 
   * @param publicChannelForm
   *          public channel form for adding comment to the pc.
   * @param authentication
   *          logged in user.
   * @return the sp response whether the request was success or failure.
   */
  @RequestMapping(value = "/pubchannel/addComment", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addComment(@RequestBody PublicChannelForm publicChannelForm,
      Authentication authentication) {
    return ControllerHelper.process(publicChannelControllerHelper::addComment, authentication,
        publicChannelForm);
    
  }
  
  /**
   * Delete the comment.
   * 
   * @param messageId
   *          - message id
   * @param cid
   *          - comment id
   * @param childCid
   *          - child comment id
   * @param token
   *          - logged in user
   * @return the response to the delete request
   */
  @RequestMapping(value = "/pubchannel/deleteComment", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteComment(@RequestParam String pcRefId,
      @RequestParam(defaultValue = "-1") int cid, @RequestParam(defaultValue = "-1") int childCid,
      Authentication token) {
    return process(publicChannelControllerHelper::deleteComment, token, pcRefId, cid, childCid);
  }
  
  /**
   * getCommentsDetails will return comments for the public channels.
   * 
   * @param pcRefId
   *          pc reference id.
   * @param authentication
   *          logged in user.
   * @return the sp response whether the request was success or failure.
   */
  @RequestMapping(value = "/pubchannel/getComments/details", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getCommentsDetails(@RequestParam String pcRefId, Authentication authentication) {
    return ControllerHelper.process(publicChannelControllerHelper::getCommentsDetails,
        authentication, pcRefId);
    
  }
  
  /**
   * getAllComments will return all the comments of the user.
   * 
   * @param pcRefId
   *          pc reference id.
   * @param authentication
   *          logged in user.
   * @return the sp response whether the request was success or failure.
   */
  @RequestMapping(value = "/pubchannel/getAllComments", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllComments(@RequestParam String pcRefId,
      @RequestParam(defaultValue = "false") boolean all, Authentication authentication) {
    return ControllerHelper.process(publicChannelControllerHelper::getAllComments, authentication,
        pcRefId, all);
    
  }
}
