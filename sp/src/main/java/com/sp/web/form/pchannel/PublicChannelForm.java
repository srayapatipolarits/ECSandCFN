package com.sp.web.form.pchannel;

import com.sp.web.form.CommentForm;
import com.sp.web.model.SPFeature;

import org.springframework.util.Assert;

import java.util.List;

/**
 * PublicChannel form is the form for creation of public channel.
 * 
 * @author pradeepruhil
 *
 */
public class PublicChannelForm {
  
  private CommentForm comment;
  
  private SPFeature spFeature;
  
  private String pcRefId;
  
  private String parentRefId;
  
  private String title;
  
  private String id;
  
  private String subModuleFeature;
  
  private List<String> taggedMemberIds;
  
  public CommentForm getComment() {
    return comment;
  }
  
  public void setComment(CommentForm commentForm) {
    this.comment = commentForm;
  }
  
  public SPFeature getSpFeature() {
    return spFeature;
  }
  
  public void setSpFeature(SPFeature spFeature) {
    this.spFeature = spFeature;
  }
  
  public String getPcRefId() {
    return pcRefId;
  }
  
  public void setPcRefId(String pcRefId) {
    this.pcRefId = pcRefId;
  }
  
  public String getParentRefId() {
    return parentRefId;
  }
  
  public void setParentRefId(String parentRefId) {
    this.parentRefId = parentRefId;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  /**
   * Validate method will validate the form.
   */
  public void validate() {
    Assert.notNull(comment, "No comment present, cannot create a public channel with no comment.");
    
    Assert.notNull(comment.getComment(),
        "No comment present, cannot create a public channel with no comment.");
    Assert.hasText(pcRefId, "No reference present to create a public channel ");
    Assert.notNull(spFeature, "Feature not present to create public channel.");
    
  }
  
  /**
   * Validate method will validate the form.
   */
  public void validateUpdate() {
    Assert.notNull(comment, "No comment present, cannot update a public channel with no comment.");
    
    Assert.notNull(comment.getComment(),
        "No comment present, cannot add comment to public channel.");
    Assert.hasText(pcRefId, "No reference present to update public channel ");
    Assert.notNull(spFeature, "Feature not present to update public channel.");
    
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
  }
  
  public void setSubModuleFeature(String subModuleFeature) {
    this.subModuleFeature = subModuleFeature;
  }
  
  public String getSubModuleFeature() {
    return subModuleFeature;
  }
  
  public void setTaggedMemberIds(List<String> taggedMemberIds) {
    this.taggedMemberIds = taggedMemberIds;
  }
  
  public List<String> getTaggedMemberIds() {
    return taggedMemberIds;
  }
}
