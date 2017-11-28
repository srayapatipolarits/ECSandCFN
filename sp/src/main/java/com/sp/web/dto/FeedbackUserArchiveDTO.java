package com.sp.web.dto;

import com.sp.web.model.FeedbackUserArchive;
import com.sp.web.utils.MessagesHelper;

import java.time.LocalDate;

/**
 * @author Dax Abraham
 * 
 *         The archived feedback user details.
 */
public class FeedbackUserArchiveDTO extends FeedbackUserDTO {

  private LocalDate archivedOn;
  
  public FeedbackUserArchiveDTO(FeedbackUserArchive referenceUser) {
    super(referenceUser);
  }

  public LocalDate getArchivedOn() {
    return archivedOn;
  }

  public String getArchivedOnFormatted() {
    return MessagesHelper.formatDate(archivedOn);
  }
  
  public void setArchivedOn(LocalDate archivedOn) {
    this.archivedOn = archivedOn;
  }

}
