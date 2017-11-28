package com.sp.web.dto;

import com.sp.web.model.SPMedia;
import com.sp.web.model.SPMediaType;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * This is the DTO class for the SPImage bean to be consumed by FE
 * 
 * @author Prasanna Venkatesh
 *
 */

public class SPMediaDTO implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String id;
  
  private String name;
  
  private String altText;
  
  private Set<String> tags;
  
  private String url;
  
  private int height;
  
  private int width;
  
  private String companyId;
  
  private LocalDateTime createdOn = null;
  
  private String formattedDate;
  
  private String status;
  
  private SPMediaType mediaType;
  
  private String companyName;
  
  /**
   * Default constructor.
   * 
   * @param spImage
   *          - the SPImage object
   */
  public SPMediaDTO(SPMedia spImage) {
    BeanUtils.copyProperties(spImage, this);
    
    // The date has been formatted for FE
    if (createdOn != null) {
      formattedDate = MessagesHelper.formatDate(LocalDate.from(createdOn), "MMM dd, yyyy");
    }
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
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public String getFormattedDate() {
    return formattedDate;
  }
  
  public void setFormattedDate(String formattedDate) {
    this.formattedDate = formattedDate;
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
  
}
