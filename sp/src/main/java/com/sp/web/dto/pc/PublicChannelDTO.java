package com.sp.web.dto.pc;

import com.sp.web.model.Comment;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.pubchannel.PublicChannel;
import com.sp.web.utils.GenericUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * PubliChannelDTO is the dto for the public channel.
 * 
 * @author pradeepruhil
 *
 */
public class PublicChannelDTO implements Serializable {
  
  private static final long serialVersionUID = -4372574766282584062L;
  
  private String id;
  
  private LinkedList<Comment> comments = null;
  
  private String title;
  
  private String pcRefId;
  
  private SPFeature spFeature;
  
  private String companyId;
  
  private String parentRefId;
  
  private int commentCount;
  
  private boolean follow;
  
  /**
   * Constructor for the DTO.
   * 
   * @param publicChannel
   *          public channel.
   * @param user
   *          for the public channel.
   */
  public PublicChannelDTO(PublicChannel publicChannel, int limit, User user) {
    this(publicChannel, limit);
    this.follow = publicChannel.followStatus(user.getId());
  }
  
  /**
   * Constructor for the DTO.
   * 
   * @param publicChannel
   *          public channel.
   */
  public PublicChannelDTO(PublicChannel publicChannel, int limit) {
    BeanUtils.copyProperties(publicChannel, this);
    if (!CollectionUtils.isEmpty(comments)) {
      this.commentCount = comments.size();
      this.comments = new LinkedList<>(GenericUtils.truncateList(this.comments, limit));
    }
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public LinkedList<Comment> getComments() {
    return comments;
  }
  
  public void setComments(LinkedList<Comment> comments) {
    this.comments = comments;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getPcRefId() {
    return pcRefId;
  }
  
  public void setPcRefId(String pcRefId) {
    this.pcRefId = pcRefId;
  }
  
  public SPFeature getSpFeature() {
    return spFeature;
  }
  
  public void setSpFeature(SPFeature spFeature) {
    this.spFeature = spFeature;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getParentRefId() {
    return parentRefId;
  }
  
  public void setParentRefId(String parentRefId) {
    this.parentRefId = parentRefId;
  }
  
  public void setCommentCount(int commentCount) {
    this.commentCount = commentCount;
  }
  
  public int getCommentCount() {
    return commentCount;
  }
  
  @Override
  public String toString() {
    return "PublicChannelDTO [id=" + id + ", comments=" + comments + ", title=" + title
        + ", pcRefId=" + pcRefId + ", spFeature=" + spFeature + ", companyId=" + companyId
        + ", parentRefId=" + parentRefId + ", commentCount=" + commentCount + "]";
  }
  
  public void setFollow(boolean follow) {
    this.follow = follow;
  }
  
  public boolean isFollow() {
    return follow;
  }
  
}
