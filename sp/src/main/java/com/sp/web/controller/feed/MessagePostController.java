package com.sp.web.controller.feed;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.controller.generic.GenericController;
import com.sp.web.dto.feed.SPMessagePostDTO;
import com.sp.web.dto.feed.SPMessagePostListingDTO;
import com.sp.web.form.feed.SPMessagePostForm;
import com.sp.web.model.feed.SPMessagePost;
import com.sp.web.mvc.SPResponse;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

/**
 * @author Dax Abraham
 * 
 *         The controller for the message posts.
 */
@Controller
@RequestMapping("/sysAdmin/message/post")
public class MessagePostController extends
                        GenericController<SPMessagePost, 
                        SPMessagePostListingDTO, 
                        SPMessagePostDTO, 
                        SPMessagePostForm, 
                        MessagePostControllerHelper> {
  
  @Inject
  public MessagePostController(MessagePostControllerHelper helper) {
    super(helper);
  }
  
  /**
   * Controller method to publish the message post.
   * 
   * @param form
   *          - form
   * @param token
   *          - user
   * @return the response to the publish request
   */
  @RequestMapping(value = "/publish", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse publish(@RequestBody SPMessagePostForm form, Authentication token) {
    return process(helper::publish, token, form);
  }
  
  /**
   * Controller method to delete.
   * 
   * @param id
   *          - id
   * @param deleteFeedOnly
   *          - flag for delete feed only
   * @param token
   *          - user
   * @return the response to delete
   */
  @RequestMapping(value = "/deletePost", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse delete(@RequestParam String id,
      @RequestParam(defaultValue = "false") boolean newsFeedOnly, Authentication token) {
    return process(helper::delete, token, id, newsFeedOnly);
  }
  
  /**
   * Controller method to get the message activity details.
   * 
   * @param id
   *          - id
   * @param token
   *          - user
   * @return the response to activity details request
   */
  @RequestMapping(value = "/postActivityDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse postActivityDetails(@RequestParam String id, Authentication token) {
    return process(helper::postActivityDetails, token, id);
  }
}
