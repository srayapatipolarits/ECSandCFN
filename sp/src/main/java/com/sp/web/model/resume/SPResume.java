package com.sp.web.model.resume;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;

/**
 * SPResume is the model class for the holding the resume information.
 * 
 * @author pradeepruhil
 *
 */
public class SPResume {
  
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate dateCreated;
  
  private String userRole;
  
  private String fileName;
  
  private byte[] bytes;
  
  private String id;
  
  private String resumeForId;
  
  private String providerName;
  
  public LocalDate getDateCreated() {
    return dateCreated;
  }
  
  public void setDateCreated(LocalDate dateCreated) {
    this.dateCreated = dateCreated;
  }
  
  public String getUserRole() {
    return userRole;
  }
  
  public void setUserRole(String userRole) {
    this.userRole = userRole;
  }
  
  public String getFileName() {
    return fileName;
  }
  
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
  
  public byte[] getBytes() {
    return bytes;
  }
  
  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public void setResumeForId(String resumeForId) {
    this.resumeForId = resumeForId;
  }
  
  public String getResumeForId() {
    return resumeForId;
  }
  
  public void setProviderName(String providerName) {
    this.providerName = providerName;
  }
  
  public String getProviderName() {
    return providerName;
  }
}
