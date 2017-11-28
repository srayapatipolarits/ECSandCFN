package com.sp.web.model.audit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDateTime;

/**
 * @author pradeepruhil
 * 
 *         The entity class for the audit log bean.
 */
public class AuditLogBean {
  
  private String id;
  
  private String logMessage;
  
  private String email;
  
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDateTime createdOn;
  
  private ServiceType serviceType;
  
  private String companyId;
  
  public void setLogMessage(String logMessage) {
    this.logMessage = logMessage;
  }
  
  public String getLogMessage() {
    return logMessage;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setServiceType(ServiceType serviceType) {
    this.serviceType = serviceType;
  }
  
  public ServiceType getServiceType() {
    return serviceType;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public String getCompanyId() {
    return companyId;
  }
}