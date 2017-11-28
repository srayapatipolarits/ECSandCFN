package com.sp.web.model.usertracking;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * UserActivityTracking will track the user activity in the system. It will track the user logged in
 * count, articles viewed, completd in the system.
 * 
 * @author pradeepruhil
 *
 */
public class UserActivityTracking implements Serializable {
  
  private static final long serialVersionUID = -2582679861075199399L;
  
  private String id;
  
  private String email;
  
  private String companyId;
  
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate date;
  
  private int loggedInCount;
  
  private long averageSession;
  
  private long totalSessionDuration;
  
  private int articleCompleted;
  
  private int articleViewed;
  
  private String userId;
  
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime lastAccessedTime;
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  public String getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
  
  public LocalDate getDate() {
    return date;
  }
  
  public void setDate(LocalDate date) {
    this.date = date;
  }
  
  public int getLoggedInCount() {
    return loggedInCount;
  }
  
  public void setLoggedInCount(int loggedInCount) {
    this.loggedInCount = loggedInCount;
  }
  
  public long getAverageSession() {
    return averageSession;
  }
  
  public void setAverageSession(long averageSession) {
    this.averageSession = averageSession;
  }
  
  public int getArticleCompleted() {
    return articleCompleted;
  }
  
  public void setArticleCompleted(int articleCompleted) {
    this.articleCompleted = articleCompleted;
  }
  
  public int getArticleViewed() {
    return articleViewed;
  }
  
  public void setArticleViewed(int articleViewed) {
    this.articleViewed = articleViewed;
  }
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public LocalDateTime getLastAccessedTime() {
    return lastAccessedTime;
  }
  
  public void setLastAccessedTime(LocalDateTime lastAccessedTime) {
    this.lastAccessedTime = lastAccessedTime;
  }
  
  public void inreaseLoggedInCount() {
    loggedInCount = loggedInCount + 1;
    
  }
  
  public void inreaseArticleCompletedCount() {
    articleCompleted = articleCompleted + 1;
    
  }
  
  public void increaseArticleView() {
    articleViewed = articleViewed + 1;
    
  }
  
  public void setTotalSessionDuration(long totalSessionDuration) {
    this.totalSessionDuration = totalSessionDuration;
  }
  
  public long getTotalSessionDuration() {
    return totalSessionDuration;
  }
  
  public void addSessionDuration(long seconds) {
    totalSessionDuration = totalSessionDuration + seconds;
    
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
  }
  
}
