package com.sp.web.dto.usertracking;

import com.sp.web.dto.spectrum.SpectrumUserDTO;
import com.sp.web.model.User;
import com.sp.web.model.usertracking.UserActivityTracking;

import java.time.LocalDate;

public class UserActivityTrackingDTO extends SpectrumUserDTO {
  
  private static final long serialVersionUID = -5691152705429693952L;
  
  private String email;
  
  private String companyId;
  
  private LocalDate date;
  
  private int loggedInCount;
  
  private long totalSessionDuration;
  
  private int articleCompleted;
  
  private int articleViewed;
  
  public UserActivityTrackingDTO(User user, UserActivityTracking ua) {
    super(user);
    this.date = ua.getDate();
    this.articleCompleted = ua.getArticleCompleted();
    this.articleViewed = ua.getArticleViewed();
    this.totalSessionDuration = ua.getTotalSessionDuration();
    this.loggedInCount = ua.getLoggedInCount();
    
  }
  
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
  
  
  public long getTotalSessionDuration() {
    return totalSessionDuration;
  }
  
  public void setTotalSessionDuration(long totalSessionDuration) {
    this.totalSessionDuration = totalSessionDuration;
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
  
}
