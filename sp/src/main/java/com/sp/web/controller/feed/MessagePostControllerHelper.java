package com.sp.web.controller.feed;

import com.sp.web.controller.generic.GenericControllerHelper;
import com.sp.web.dto.feed.SPMessagePostDTO;
import com.sp.web.dto.feed.SPMessagePostListingDTO;
import com.sp.web.form.feed.SPMessagePostForm;
import com.sp.web.model.User;
import com.sp.web.model.feed.SPMessagePost;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.feed.SPMessagePostFactory;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.inject.Inject;

/**
 * @author Dax Abraham
 * 
 *         The helper method for message post controller.
 */
@Component
public class MessagePostControllerHelper extends
    GenericControllerHelper<SPMessagePost, 
                            SPMessagePostListingDTO, 
                            SPMessagePostDTO, 
                            SPMessagePostForm, 
                            SPMessagePostFactory> {
  
  @Inject
  public MessagePostControllerHelper(SPMessagePostFactory factory) {
    super("messagePost", factory);
  }
  
  /**
   * Helper method to publish the message post.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    the response to the publish request
   */
  public SPResponse publish(User user, Object[] params) {
    SPMessagePostForm form = (SPMessagePostForm) params[0];
    form.validatePublish();
    return new SPResponse().add(moduleName, factory.publish(user, form));
  }
  
  /**
   * Get the details for the given instance.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    the response to the get request
   */
  public SPResponse delete(User user, Object[] params) {

    // get the id
    String id = (String) params[0];
    Assert.hasText(id, "Id is required.");
    
    boolean newsFeedOnly = (boolean) params[1];
    
    // delete the instance
    factory.delete(user, id, newsFeedOnly);
    
    // sending the response
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to get the message activity details.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *    the response to activity details request
   */
  public SPResponse postActivityDetails(User user, Object[] params) {

    // get the id
    String id = (String) params[0];
    Assert.hasText(id, "Id is required.");
    
    // sending the response
    return new SPResponse().add(moduleName, factory.getPostActivityDetails(user, id));
  }  
}
