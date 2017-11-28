package com.sp.web.form;

import com.sp.web.model.SPMediaType;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Set;

/**
 * The form to capture the data and update the details for the Image object from admin interface.
 * 
 * @author Prasanna Venkatesh
 *
 */

public class SPMediaForm implements Serializable {
  
  
  private static final long serialVersionUID = 1L;
  
  private String id;
  
  private String name;
  
  private String altText;
  
  private Set<String> tags;
  
  private int height;
  
  private int width;
  
  private String status;
  
  private SPMediaType mediaType;
  
  private MultipartFile file;
  
  private String companyId;
  
  private String companyName;
  
  
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

  public MultipartFile getFile() {
    return file;
  }

  public void setFile(MultipartFile file) {
    this.file = file;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
}
