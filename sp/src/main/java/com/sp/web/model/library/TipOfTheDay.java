package com.sp.web.model.library;

/**
 * @author Dax Abraham
 * 
 *         The entity for the tip of the day.
 */
public class TipOfTheDay {
  
  String id;
  String title;
  String description;
  String linkText;
  String linkUrl;
  String iconImage;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public String getLinkText() {
    return linkText;
  }
  
  public void setLinkText(String linkText) {
    this.linkText = linkText;
  }
  
  public String getLinkUrl() {
    return linkUrl;
  }
  
  public void setLinkUrl(String linkUrl) {
    this.linkUrl = linkUrl;
  }
  
  public String getIconImage() {
    return iconImage;
  }
  
  public void setIconImage(String iconImage) {
    this.iconImage = iconImage;
  }
  
}
