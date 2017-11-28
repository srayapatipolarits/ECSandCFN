package com.sp.web.service.feed;

import com.sp.web.dto.pc.PublicChannelNewsFeedDTO;
import com.sp.web.model.User;
import com.sp.web.model.feed.NewsFeed;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.pubchannel.PublicChannel;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.pc.PublicChannelFactory;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <code>PubliChannelNewsFeedProcessor</code> will get the news feed for the public channel.
 * 
 * @author pradeepruhil
 *
 */
@Component("publiChannelNewsFeedProcessor")
public class PublicChannelNewsFeedProcessor implements NewsFeedProcessor {

  
  @Autowired
  private PublicChannelFactory publicChannelFactory;
  
  @Autowired
  private SPGoalFactory goalsFactory;
  /**
   * @see com.sp.web.service.feed.NewsFeedProcessor#process(com.sp.web.model.User,
   * com.sp.web.model.feed.NewsFeed)
   */
  @Override
  public Object process(User user, NewsFeed newsFeed, boolean filterByUser) {
    
    // check if this is a filter by user request
    if (filterByUser) {
      return null;
    }
    
    PublicChannel publicChannel = publicChannelFactory.getPublicChannel(newsFeed.getFeedRefId(),user.getCompanyId());
    /* check if public channel applicable for user */
    if (publicChannel != null && publicChannel.isApplicable(user.getId())) {
      PublicChannelNewsFeedDTO publicChannelNewsFeedDTO = new PublicChannelNewsFeedDTO(
          publicChannel, user);
      switch (publicChannel.getSpFeature()) {
      case Erti:
        SPGoal goal = goalsFactory.getGoal(publicChannel.getPcRefId());
        publicChannelNewsFeedDTO.setText(goal.getDescription());
        break;
      case PrismLens :
        publicChannelNewsFeedDTO.setText(MessagesHelper.getMessage("dashboard.publicchannel.description."
            + publicChannel.getSpFeature()));
        break;
      case Prism: 
        String pcRefId = publicChannel.getPcRefId();
        publicChannelNewsFeedDTO.setText(MessagesHelper.getMessage("dashboard.publicchannel.description."
              + pcRefId));
        publicChannelNewsFeedDTO.setTitle(MessagesHelper.getMessage("dashboard.publicchannel.name."
              + pcRefId));
        break;
      case RelationShipAdvisor:
        publicChannelNewsFeedDTO.setText(MessagesHelper.getMessage("dashboard.publicchannel.description."
              + publicChannel.getSpFeature()));
        publicChannelNewsFeedDTO.setTitle(MessagesHelper.getMessage("dashboard.publicchannel.name."
            + publicChannel.getSpFeature()));
        break;
      default:
        break;
      }
      if (!publicChannel.getUnfollowMemberIds().contains(user.getId())) {
        publicChannelNewsFeedDTO.setFollow(true);
      }
      return publicChannelNewsFeedDTO;
    }
    return null;
  }
  
}
