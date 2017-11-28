package com.sp.web.model.note;

import com.sp.web.model.Comment;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Note class is the entity class for holding the notes for the user. These will be the private
 * notes for the user.
 * 
 * @author pradeepruhil
 *
 */
public class Note implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 4008732591911126195L;
  
  private String noteId;
  
  /** note title. */
  private String title;
  
  private LinkedList<Comment> noteData;
  
  private LocalDateTime updatedOn;
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  /**
   * get the noteData.
   * 
   * @return the noteData.
   */
  public LinkedList<Comment> getNoteData() {
    if (noteData == null) {
      noteData = new LinkedList<>();
    }
    return noteData;
  }
  
  protected void setNoteData(LinkedList<Comment> noteData) {
    this.noteData = noteData;
  }
  
  public void setNoteId(String noteId) {
    this.noteId = noteId;
  }
  
  public String getNoteId() {
    return noteId;
  }
  
  @Override
  public int hashCode() {
    return Objects.hashCode(noteId);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Note) {
      Note note = (Note) obj;
      return Objects.equals(this.noteId, note.getNoteId());
    }
    return false;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  /**
   * returns the last updated notes.
   * 
   * @return updated on date time.
   */
  public LocalDateTime getUpdatedOn() {
    if (updatedOn == null) {
      Comment first = getNoteData().getFirst();
      updatedOn = first.getUpdatedOn() != null ? first.getUpdatedOn() : first.getCreatedOn();
    }
    return updatedOn;
  }
  
}
