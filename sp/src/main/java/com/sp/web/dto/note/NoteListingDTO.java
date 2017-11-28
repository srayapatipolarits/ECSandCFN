package com.sp.web.dto.note;

import com.sp.web.model.Comment;
import com.sp.web.model.note.Note;

import java.time.LocalDateTime;

/**
 * NoteListingDTO holds the information for the DTO.
 * 
 * @author pradeepruhil
 *
 */
public class NoteListingDTO {
  
  private String title;
  
  private String comment;
  
  private LocalDateTime updatedOn;
  
  private String nid;
  
  /**
   * Constructor for the note listing
   */
  public NoteListingDTO(Note note) {
    this.title = note.getTitle();
    if (note.getNoteData().size() > 0) {
      Comment latestComment = note.getNoteData().peekFirst();
      this.comment = latestComment.getText();
      this.updatedOn = latestComment.getCreatedOn();
    }
    this.nid = note.getNoteId();
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getComment() {
    return comment;
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  public String getNid() {
    return nid;
  }
  
  public void setNid(String nid) {
    this.nid = nid;
  }
  
}
