package com.sp.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * SPMedia class which holds all the Media related details.
 * 
 * @author Prasanna Venkatesh
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SPMedia implements Serializable {
  
  private static final long serialVersionUID = 8029105011049584470L;

  private String id;
  
  private String name;
  
  private String altText;
  
  private Set<String> tags;
  
  private String url;
  
  private int height;
  
  private int width;
  
  private String companyId;
  
  private LocalDateTime createdOn;
  
  private String status;
  
  private SPMediaType mediaType;
  
  private String companyName;
  
  private String linkText;
  
  /**
   * Default constructor.
   */
  public SPMedia() {
    
    if (mediaType == null) {
      this.mediaType = SPMediaType.IMAGE;
    }
  }
  
  /**
   * Constructor.
   * 
   * @param name
   *          - name
   * @param url
   *          - url
   * @param type
   *          - media type
   */
  public SPMedia(String name, String url, SPMediaType type) {
    this.name = name;
    this.url = url;
    this.mediaType = type;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getAltText() {
    return altText;
  }
  
  public void setAltText(String altText) {
    this.altText = altText;
  }
  
  public Set<String> getTags() {
    return tags;
  }
  
  public void setTags(Set<String> tags) {
    this.tags = tags;
  }
  
  public String getUrl() {
    return url;
  }
  
  public void setUrl(String url) {
    this.url = url;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public int getHeight() {
    return height;
  }
  
  public void setHeight(int height) {
    this.height = height;
  }
  
  public int getWidth() {
    return width;
  }
  
  public void setWidth(int width) {
    this.width = width;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public String getStatus() {
    return status;
  }
  
  public void setStatus(String status) {
    this.status = status;
  }
  
  public SPMediaType getMediaType() {
    return mediaType;
  }
  
  public void setMediaType(SPMediaType mediaType) {
    this.mediaType = mediaType;
  }
  
  public String getCompanyName() {
    return companyName;
  }
  
  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getLinkText() {
    return linkText;
  }

  public void setLinkText(String linkText) {
    this.linkText = linkText;
  }
  
}
