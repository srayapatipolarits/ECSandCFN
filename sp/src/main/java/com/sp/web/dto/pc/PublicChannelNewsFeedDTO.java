package com.sp.web.dto.pc;

import com.sp.web.Constants;
import com.sp.web.model.User;
import com.sp.web.model.feed.DashboardMessageType;
import com.sp.web.model.pubchannel.PublicChannel;

/**
 * PublicChannelNewsFeedDTO news feed dto.
 * @author pradeepruhil
 *
 */
public class PublicChannelNewsFeedDTO extends PublicChannelDTO {
  
  private static final long serialVersionUID = -4841154109826593069L;

  private DashboardMessageType type;
  private String text;
  private String name;
  
  /**
   * PubliChannelNewsFeeDTO constructor.
   * 
   * @param publicChannel public channel.
   * @param user  who is fetching the public channel details.
   */
  public PublicChannelNewsFeedDTO(PublicChannel publicChannel, User user) {
    super(publicChannel, Constants.NEWS_FEED_COMMENT_LIMIT, user);
    this.type = DashboardMessageType.PublicChannel;
  }
  
  /**
   * PubliChannelNewsFeeDTO constructor.
   * 
   * @param publicChannel public channel.
   * @param user  who is fetching the public channel details.
   */
  public PublicChannelNewsFeedDTO(PublicChannel publicChannel, User user, int limit) {
    super(publicChannel, limit, user);
    this.type = DashboardMessageType.PublicChannel;
  }
  
  public DashboardMessageType getType() {
    return type;
  }
  
  public void setType(DashboardMessageType type) {
    this.type = type;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
}
