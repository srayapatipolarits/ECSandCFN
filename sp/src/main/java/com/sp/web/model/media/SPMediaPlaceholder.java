package com.sp.web.model.media;

import com.sp.web.model.SPMediaType;

import java.io.Serializable;

/**
 * @author Dax Abraham
 * 
 *         The entity class for media placeholder.
 */
public class SPMediaPlaceholder implements Serializable {
  
  private static final long serialVersionUID = 7112059306815929740L;
  private String linkText;
  private String url;
  private String imageLink;
  private String altText;
  private SPMediaType type;
  
  public String getLinkText() {
    return linkText;
  }
  
  public void setLinkText(String linkText) {
    this.linkText = linkText;
  }
  
  public String getUrl() {
    return url;
  }
  
  public void setUrl(String url) {
    this.url = url;
  }
  
  public String getImageLink() {
    return imageLink;
  }
  
  public void setImageLink(String imageLink) {
    this.imageLink = imageLink;
  }
  
  public String getAltText() {
    return altText;
  }
  
  public void setAltText(String altText) {
    this.altText = altText;
  }
  
  public SPMediaType getType() {
    return type;
  }
  
  public void setType(SPMediaType type) {
    this.type = type;
  }
  
}
