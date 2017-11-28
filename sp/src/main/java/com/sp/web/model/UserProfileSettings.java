package com.sp.web.model;

import com.sp.web.utils.LocaleHelper;

import java.io.Serializable;
import java.util.Locale;
import java.util.UUID;

/**
 * @author Dax Abraham
 * 
 *         The model bean to store the individual user profile settings.
 */
public class UserProfileSettings implements Serializable {
  
  private static final long serialVersionUID = 2793793041358211617L;
  
  private boolean isHiringAccessAllowed;
  private boolean is360ProfileAccessAllowed;
  private boolean isWorkspacePulseAllowed;
  private String token;
  private boolean certificateProfilePublicView;
  private boolean autoUpdateLearning;
  private Locale locale;
  private boolean peopleAnalyticsVisited;
  
  public boolean isHiringAccessAllowed() {
    return isHiringAccessAllowed;
  }
  
  public void setHiringAccessAllowed(boolean isHiringAccessAllowed) {
    this.isHiringAccessAllowed = isHiringAccessAllowed;
  }
  
  public boolean isIs360ProfileAccessAllowed() {
    return is360ProfileAccessAllowed;
  }
  
  public void setIs360ProfileAccessAllowed(boolean is360ProfileAccessAllowed) {
    this.is360ProfileAccessAllowed = is360ProfileAccessAllowed;
  }
  
  /**
   * This method generates and updates the token for the profile settings.
   */
  public void updateToken() {
    token = UUID.randomUUID().toString();
  }
  
  public String getToken() {
    return token;
  }
  
  public boolean isWorkspacePulseAllowed() {
    return isWorkspacePulseAllowed;
  }
  
  public void setWorkspacePulseAllowed(boolean isWorkspacePulseAllowed) {
    this.isWorkspacePulseAllowed = isWorkspacePulseAllowed;
  }
  
  public boolean isCertificateProfilePublicView() {
    return certificateProfilePublicView;
  }
  
  public void setCertificateProfilePublicView(boolean certificateProfilePublicView) {
    this.certificateProfilePublicView = certificateProfilePublicView;
  }
  
  public void setAutoUpdateLearning(boolean autoUpdateLearning) {
    this.autoUpdateLearning = autoUpdateLearning;
  }
  
  public boolean isAutoUpdateLearning() {
    return autoUpdateLearning;
  }
  
  public void setLocale(Locale locale) {
    this.locale = locale;
  }
  
  /**
   * Get the user's locale.
   * 
   * @return user locale
   */
  public Locale getLocale() {
    if (locale == null) {
      locale = LocaleHelper.locale();
    }
    return locale;
  }
  
  public void setPeopleAnalyticsVisited(boolean peopleAnalyticsVisited) {
    this.peopleAnalyticsVisited = peopleAnalyticsVisited;
  }
  
  public boolean isPeopleAnalyticsVisited() {
    return peopleAnalyticsVisited;
  }
}
