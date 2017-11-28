package com.sp.web.form.goal;

import com.sp.web.Constants;
import com.sp.web.service.email.EmailParamType;
import com.sp.web.service.email.EmailParams;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Dax Abraham
 *
 *         The form object for the meeting invites.
 */
public class MeetingInviteForm {
  
  private List<String> to;
  
  private String subject;
  
  private String agenda;
  
  private String location;
  
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime startDate;
  
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime endDate;
  
  public List<String> getTo() {
    return to;
  }
  
  public void setTo(List<String> to) {
    this.to = to;
  }
  
  public String getSubject() {
    return subject;
  }
  
  public void setSubject(String subject) {
    this.subject = subject;
  }
  
  public String getAgenda() {
    return agenda;
  }
  
  public void setAgenda(String agenda) {
    this.agenda = agenda;
  }
  
  public LocalDateTime getStartDate() {
    return startDate;
  }
  
  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }
  
  public LocalDateTime getEndDate() {
    return endDate;
  }
  
  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }

  /**
   * Validation method for the form.
   */
  public void validate() {
    Assert.notEmpty(to, "The recipient list is required and must contain at least one recipient.");
    Assert.hasText(subject, "Subject is required for the invite request.");
    Assert.notNull(startDate, "Start date is required.");
    Assert.notNull(endDate, "End date is required.");
  }

  /**
   * Get the email params from the given invite form.
   * 
   * @return
   *      the email params object
   */
  public EmailParams getEmailParam() {
    EmailParams emailParams = new EmailParams();
    emailParams.setTos(to);
    emailParams.setSubject(subject);
    emailParams.setEmailBody(" ");
    emailParams.addParam(Constants.PARAM_MEETING_INVITE_AGENDA, Optional.ofNullable(agenda).orElse(""));
    emailParams.addParam(Constants.PARAM_MEETING_INVITE_LOCATION, Optional.ofNullable(location).orElse(""));
    emailParams.addParam(Constants.PARAM_START_DATE, startDate);
    emailParams.addParam(Constants.PARAM_END_DATE, endDate);
    emailParams.setType(EmailParamType.Meeting);
    return emailParams;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
}
