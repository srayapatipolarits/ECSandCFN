package com.sp.web.service.feed;

import com.sp.web.Constants;
import com.sp.web.dto.feed.DashboardMessageDTO;
import com.sp.web.form.Operation;
import com.sp.web.model.feed.CompanyNewsFeed;
import com.sp.web.model.feed.DashboardMessage;
import com.sp.web.model.feed.NewsFeed;
import com.sp.web.model.feed.NewsFeedType;
import com.sp.web.repository.feed.DashboardMessageRepository;
import com.sp.web.repository.feed.NewsFeedRepository;
import com.sp.web.service.sse.ActionType;
import com.sp.web.service.sse.EventGateway;
import com.sp.web.service.sse.MessageEventRequest;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.ApplicationContextUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The factory for all the services for the news feed.
 */
@Component
public class NewsFeedFactory {
  
  private static final Logger log = Logger.getLogger(NewsFeedFactory.class);
  
  private static final Map<NewsFeedType, NewsFeedProcessor> newsFeedProcessorMap = 
                                          new HashMap<NewsFeedType, NewsFeedProcessor>();

  private static final Map<String, NewsFeedHelper> companyNewsFeedHelperMap = new HashMap<String, NewsFeedHelper>();
  
  @Autowired
  private NewsFeedRepository newsFeedRepository;
  
  @Autowired
  private DashboardMessageRepository dashboardMessageRepository;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private EventGateway eventGateway;
  
  /**
   * Reset all the news feed cache.
   */
  @CacheEvict(value = "newsFeed", allEntries = true)
  public void resetCache() {
    companyNewsFeedHelperMap.clear();
  }
  
  /**
   * Get the news feed processor.
   * 
   * @param type
   *          - news feed type
   * @return the news feed processor
   */
  public NewsFeedProcessor getProcessor(NewsFeedType type) {
    NewsFeedProcessor newsFeedProcessor = newsFeedProcessorMap.get(type);
    if (newsFeedProcessor == null) {
      synchronized (newsFeedProcessorMap) {
        newsFeedProcessor = newsFeedProcessorMap.get(type);
        if (newsFeedProcessor == null) {
          newsFeedProcessor = (NewsFeedProcessor) ApplicationContextUtils.getApplicationContext()
              .getBean(type.getHandler());
          newsFeedProcessorMap.put(type, newsFeedProcessor);
        }
      }
    }
    return newsFeedProcessor;
  }
  
  /**
   * Update the user news feed.
   * 
   * @param companyNewsFeed
   *          - company news feed to update
   */
  @CachePut(value = "newsFeed", key = "#companyNewsFeed.companyId")
  public void updateCompanyNewsFeed(CompanyNewsFeed companyNewsFeed) {
    newsFeedRepository.save(companyNewsFeed);
  }
  
  /**
   * Create a new dashboard message.
   * 
   * @param message
   *          - new message
   */
  public void createDashbaordMessage(DashboardMessage message) {
    dashboardMessageRepository.save(message);
  }
  
  /**
   * Update the given dashboard message.
   * 
   * @param message
   *          - dashboard message
   */
  @CacheEvict(value = "dashboardMessage", key = "#message.id")
  public void updateDashboardMessage(DashboardMessage message) {
    dashboardMessageRepository.save(message);
  }
  
  /**
   * Get the dashboard message with the given message id.
   * 
   * @param messageId
   *          - message id
   * @return
   *    the dashboard message
   */
  @Cacheable("dashboardMessage")
  public DashboardMessage getDashboardMessage(String messageId) {
    return dashboardMessageRepository.findById(messageId);
  }
  
  /**
   * Delete the dashboard message.
   * 
   * @param message
   *          - message
   */
  @CacheEvict(value = "dashboardMessage", key = "#message.id")
  public void deleteDashboardMessage(DashboardMessage message) {
    dashboardMessageRepository.delete(message);
  }
  
  
  /**
   * Get the companies news feed.
   * 
   * @param companyId
   *          - company id
   * @return the news feed for the company
   */
  @Cacheable(value = "newsFeed")
  public CompanyNewsFeed getCompanyNewsFeed(String companyId) {
    CompanyNewsFeed findByCompanyId = newsFeedRepository.findByCompanyId(companyId);
    if (findByCompanyId == null) {
      synchronized (this) {
        findByCompanyId = newsFeedRepository.findByCompanyId(companyId);
        if (findByCompanyId == null) {
          findByCompanyId = new CompanyNewsFeed();
          findByCompanyId.setCompanyId(companyId);
          findByCompanyId.setNewsFeeds(new LinkedList<NewsFeed>());
          newsFeedRepository.save(findByCompanyId);
        }
      }
    }
    return findByCompanyId;
  }

  /**
   * Get the news feed helper.
   * 
   * @param companyId
   *            - company id
   * @return
   *    the news feed helper for that company
   */
  public NewsFeedHelper getCompanyNewsFeedHelper(String companyId) {
    NewsFeedHelper newsFeedHelper = companyNewsFeedHelperMap.get(companyId);
    if (newsFeedHelper == null) {
      synchronized (companyNewsFeedHelperMap) {
        newsFeedHelper = companyNewsFeedHelperMap.get(companyId);
        if (newsFeedHelper == null) {
          newsFeedHelper = new NewsFeedHelper(companyId, this);
          companyNewsFeedHelperMap.put(companyId, newsFeedHelper);
        }
      }
    }
    return newsFeedHelper;
  }

  /**
   * Getting the dashboard message for the given source id.
   * 
   * @param srcId
   *          - source id
   * @param companyId 
   *          - company id
   * @return
   *      the dashboard message
   */
  public DashboardMessage getDashboardMessageFromSrcId(String srcId, String companyId) {
    return dashboardMessageRepository.findBySrcId(srcId, companyId);
  }

  
  /**
   * Send out the SSE event.
   * 
   * @param companyId
   *            - company id
   * @param message
   *            - dashboard message
   * @param op
   *            - operation                       
   */
  public void sendSseEvent(String companyId, DashboardMessage message, Operation op) {
    sendSseEvent(companyId, message, op, false);
  }

  /**
   * Send out the SSE event.
   * 
   * @param companyId
   *            - company id
   * @param message
   *            - dashboard message
   * @param op
   *            - operation   
   * @param sendIdOnly
   *            - flag to indicate to send ID only                               
   */
  public void sendSseEvent(String companyId, DashboardMessage message, Operation op, boolean sendIdOnly) {
    try {
      Map<String, Object> messagePayLoad = new HashMap<String, Object>();
      messagePayLoad.put(Constants.PARAM_OPERATION, op);
      if (sendIdOnly) {
        messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE_ID, message.getRefId());
      } else {
        messagePayLoad.put(Constants.PARAM_DASHBOARD_MESSAGE, new DashboardMessageDTO(message, null));
      }
      
      if (message.isAllCompany()) {
        eventGateway.sendEvent(MessageEventRequest.newEvent(ActionType.DashboardMessage,
            companyId, messagePayLoad, message.getUnfollowMemberIds()));
      } else {
        eventGateway.sendEvent(MessageEventRequest.newEvent(ActionType.DashboardMessage,
            message.getMemberIds(), messagePayLoad, message.getUnfollowMemberIds(), companyId));
      }
    } catch (Exception e) {
      log.warn("Could not send SSE event.", e);
    }
  }

  /**
   * Get all the dashboard message post by the given user.
   * 
   * @param userId
   *          - user id
   * @return
   *    the list of dashboard posts
   */
  public List<DashboardMessage> getDashbaordMessageByOwner(String userId) {
    return dashboardMessageRepository.findByOwnerId(userId);
  }
  
}
