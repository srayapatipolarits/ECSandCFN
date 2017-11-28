/**
 * 
 */
package com.sp.web.model;

import java.util.HashSet;
import java.util.Set;

/**
 * @author pradeepruhil
 *
 */
public class InternalMarketingAnalytics {
  
  private int allEmails;
  
  private int allIndividualEmails;
  
  private int allBusinessEmails;
  
  private Set<BusinessAnalyticsDetail> analyticsDetails;
  
  public int getAllEmails() {
    return allEmails;
  }
  
  public void setAllEmails(int allEmails) {
    this.allEmails = allEmails;
  }
  
  public int getAllIndividualEmails() {
    return allIndividualEmails;
  }
  
  public void setAllIndividualEmails(int allIndividualEmails) {
    this.allIndividualEmails = allIndividualEmails;
  }
  
  public int getAllBusinessEmails() {
    return allBusinessEmails;
  }
  
  public void setAllBusinessEmails(int allBusinessEmails) {
    this.allBusinessEmails = allBusinessEmails;
  }
  
  public void setAnalyticsDetails(Set<BusinessAnalyticsDetail> analyticsDetails) {
    this.analyticsDetails = analyticsDetails;
  }
  
  public Set<BusinessAnalyticsDetail> getAnalyticsDetails() {
    if (analyticsDetails == null) {
      analyticsDetails = new HashSet<InternalMarketingAnalytics.BusinessAnalyticsDetail>();
    }
    return analyticsDetails;
  }
  
  public static class BusinessAnalyticsDetail {
    private String name;
    
    private int emailSent;
    
    public String getName() {
      return name;
    }
    
    public void setName(String name) {
      this.name = name;
    }
    
    public int getEmailSent() {
      return emailSent;
    }
    
    public void setEmailSent(int emailSent) {
      this.emailSent = emailSent;
    }
    
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
    }
    
    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      BusinessAnalyticsDetail other = (BusinessAnalyticsDetail) obj;
      if (name == null) {
        if (other.name != null)
          return false;
      } else if (!name.equals(other.name))
        return false;
      return true;
    }
    
    @Override
    public String toString() {
      return "<li>" + name + ":" + emailSent + "</li>\n";
    }
    
  }
}
