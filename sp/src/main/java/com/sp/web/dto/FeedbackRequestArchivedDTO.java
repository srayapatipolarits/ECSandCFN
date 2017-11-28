package com.sp.web.dto;

import com.sp.web.model.FeedbackArchiveRequest;
import com.sp.web.utils.MessagesHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Dax Abraham
 * 
 *         The DTO for the feedback archived requests.
 */
public class FeedbackRequestArchivedDTO extends FeedbackRequestDTO {

  /**
   * Feedback archived on date.
   */
  private LocalDate archivedOn;

  public FeedbackRequestArchivedDTO(FeedbackArchiveRequest fbArchvedRequest) {
    super(fbArchvedRequest);
  }

  public LocalDate getArchivedOn() {
    return archivedOn;
  }

  public void setArchivedOn(LocalDate archivedOn) {
    this.archivedOn = archivedOn;
  }

  public String getArchivedOnFormatted() {
    return MessagesHelper.formatDate(archivedOn, DateTimeFormatter.ISO_DATE);
  }
}
