package com.sp.web.service.feed;

import com.sp.web.controller.generic.GenericFactory;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dto.feed.CompanyDashboardMessageDTO;
import com.sp.web.dto.feed.SPMessagePostDTO;
import com.sp.web.dto.feed.SPMessagePostListingDTO;
import com.sp.web.form.Operation;
import com.sp.web.form.feed.SPMessagePostForm;
import com.sp.web.model.User;
import com.sp.web.model.feed.DashboardMessage;
import com.sp.web.model.feed.NewsFeedType;
import com.sp.web.model.feed.SPMessagePost;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.feed.SPMessagePostRepository;
import com.sp.web.user.UserFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The factory interface for SP message post.
 */
@Component
public class SPMessagePostFactory implements
    GenericFactory<SPMessagePostListingDTO, SPMessagePostDTO, SPMessagePostForm> {
  
  private static final Logger log = Logger.getLogger(SPMessagePostFactory.class);
  
  @Autowired
  SPMessagePostRepository messagePostRepository;
  
  @Autowired
  CompanyFactory companyFactory;
  
  @Autowired
  NewsFeedFactory newsFeedFactory;
  
  @Autowired
  UserFactory userFactory;
  
  @Override
  public List<SPMessagePostListingDTO> getAll(User user) {
    return messagePostRepository.findAll().stream()
        .map(m -> new SPMessagePostListingDTO(m, companyFactory)).collect(Collectors.toList());
  }
  
  @Override
  public SPMessagePostDTO get(User user, SPMessagePostForm form) {
    SPMessagePost findById = findMessagePost(form.getId());
    return new SPMessagePostDTO(findById, companyFactory);
  }
  
  @Override
  public SPMessagePostDTO create(User user, SPMessagePostForm form) {
    SPMessagePost create = form.create(user);
    messagePostRepository.save(create);
    return new SPMessagePostDTO(create, companyFactory);
  }
  
  @Override
  public SPMessagePostDTO update(User user, SPMessagePostForm form) {
    SPMessagePost findById = findMessagePost(form.getId());
    form.update(user, findById);
    messagePostRepository.save(findById);
    if (findById.isPublished()) {
      // sending the update to the company
      process(findById, Operation.UPDATE);
    }
    return new SPMessagePostDTO(findById, companyFactory);
  }
  
  @Override
  public void delete(User user, SPMessagePostForm form) {
    delete(user, form.getId(), false);
  }
  
  /**
   * Delete the message post.
   * 
   * @param user
   *          - user
   * @param id
   *          - message post id
   * @param newsFeedOnly
   *          - flag for delete feed only
   */
  public void delete(User user, String id, boolean newsFeedOnly) {
    SPMessagePost findById = findMessagePost(id);
    
    // deleting the feed from the companies
    // if the post has been published
    if (findById.isPublished()) {
      process(findById, Operation.DELETE);
    }
    
    if (!newsFeedOnly) {
      messagePostRepository.delete(findById);
    } else {
      // updating the published status
      // for the message post
      findById.resetPublish();
      messagePostRepository.save(findById);
    }
  }
  
  /**
   * Method to publish the message post.
   * 
   * @param user
   *          - user
   * @param form
   *          - form
   * @return the response to the message post
   */
  public SPMessagePostDTO publish(User user, SPMessagePostForm form) {
    // getting the message post and validating if not already published
    SPMessagePost messagePost = findMessagePost(form.getId());
    Assert.isTrue(!messagePost.isPublished(), "Message post already published.");
    
    // update the message post
    form.updatePublish(user, messagePost);
    
    // publish the message post
    publish(messagePost);
    
    messagePost.setPublished(true);
    messagePostRepository.save(messagePost);
    
    return new SPMessagePostDTO(messagePost, companyFactory);
  }
  
  /**
   * Publish the given message post.
   * 
   * @param messagePost
   *          - message post
   */
  public void publish(SPMessagePost messagePost) {
    // iterate through all the companies and create the dashboard message
    // and news feed for each of the companies
    ArrayList<String> companyIdsToDelete = new ArrayList<String>();
    final List<String> companyIds = messagePost.getCompanyIds();
    for (String companyId : companyIds) {
      // checking if company present
      CompanyDao company = companyFactory.getCompany(companyId);
      if (company != null) {
        // creating a new dashboard message for the company
        // and the news feed and sending the SSE event
        DashboardMessage newMessage = DashboardMessage.newMessage(companyId, messagePost);
        
        // if not all company then setting the members for which the
        // message is applicable to
        if (!newMessage.isAllCompany()) {
          // getting the list of users
          List<User> memberList = userFactory.getAllMembersWithRole(companyId,
              messagePost.getUserRoles());
          if (!memberList.isEmpty()) {
            newMessage.setMemberIds(memberList.stream().map(User::getId)
                .collect(Collectors.toList()));
          } else {
            companyIdsToDelete.add(companyId);
            continue;
          }
        }
        newsFeedFactory.createDashbaordMessage(newMessage);
        NewsFeedHelper companyNewsFeedHelper = newsFeedFactory.getCompanyNewsFeedHelper(companyId);
        companyNewsFeedHelper.createNewsFeed(NewsFeedType.DashboardMessage, newMessage);
        newsFeedFactory.sendSseEvent(companyId, newMessage, Operation.ADD);
      } else {
        companyIdsToDelete.add(companyId);
      }
    }
    
    if (!companyIdsToDelete.isEmpty()) {
      companyIds.removeAll(companyIdsToDelete);
      Assert.notEmpty(companyIds, "No valid company found to publish message to.");
    }
  }
  
  /**
   * Method to get the post activity details for the given message post.
   * 
   * @param user
   *          - user
   * @param id
   *          - message post id
   * 
   * @return the list of company dashboard messages
   */
  public List<CompanyDashboardMessageDTO> getPostActivityDetails(User user, String id) {
    SPMessagePost messagePost = findMessagePost(id);
    
    final String userId = user.getId();
    return messagePost.getCompanyIds().stream().map(cid -> getCompanyDashboardMessage(cid, id, userId))
        .filter(m -> m != null).collect(Collectors.toList());
  }
  
  /**
   * Get the company dashboard message for the given company id and message id.
   * 
   * @param companyId
   *          - company id
   * @param messageId
   *          - message id
   * @param userId
   *          - user id         
   * @return
   *    the company dashboard message or null
   */
  private CompanyDashboardMessageDTO getCompanyDashboardMessage(String companyId, String messageId,
      String userId) {
    try {
      CompanyDao company = companyFactory.getCompany(companyId);
      DashboardMessage dashboardMessage = newsFeedFactory.getDashboardMessageFromSrcId(messageId,
          companyId);
      if (dashboardMessage != null) {
        return new CompanyDashboardMessageDTO(company, dashboardMessage, userId);
      }
    } catch (Exception e) {
      log.warn("Error getting company dashboard message.", e);
    }
    return null;
  }
  
  /**
   * Find the message post.
   * 
   * @param id
   *          - message post id
   * @return the message post
   */
  private SPMessagePost findMessagePost(String id) {
    SPMessagePost findById = messagePostRepository.findById(id);
    Assert.notNull(findById, "Message post not found.");
    return findById;
  }
  
  /**
   * Update the news feed for all the companies.
   * 
   * @param messagePost
   *          - message post to update
   * @param op
   *          - operation
   */
  private void process(SPMessagePost messagePost, Operation op) {
    messagePost.getCompanyIds().forEach(cId -> process(cId, messagePost, op));
  }
  
  /**
   * Updating the news feed for the given company id.
   * 
   * @param companyId
   *          - company Id
   * @param messagePost
   *          - message post
   * @param op
   *          - operation
   */
  private void process(String companyId, SPMessagePost messagePost, Operation op) {
    DashboardMessage dashboardMessage = newsFeedFactory.getDashboardMessageFromSrcId(
        messagePost.getId(), companyId);
    if (dashboardMessage != null) {
      NewsFeedHelper companyNewsFeedHelper = newsFeedFactory.getCompanyNewsFeedHelper(companyId);
      switch (op) {
      case UPDATE:
        dashboardMessage.setMessage(messagePost.getMessage());
        newsFeedFactory.updateDashboardMessage(dashboardMessage);
        companyNewsFeedHelper.updateNewsFeed(dashboardMessage);
        newsFeedFactory.sendSseEvent(companyId, dashboardMessage, op, false);
        break;
      
      case DELETE:
        newsFeedFactory.deleteDashboardMessage(dashboardMessage);
        companyNewsFeedHelper.deleteNewsFeed(dashboardMessage);
        newsFeedFactory.sendSseEvent(companyId, dashboardMessage, op, true);
        break;
      
      default:
        break;
      }
    } else {
      log.warn("Dashbaord message for the company :" + companyId + ": not found");
    }
  }
  
}
