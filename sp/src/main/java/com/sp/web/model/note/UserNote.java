package com.sp.web.model.note;

import com.sp.web.exception.InvalidRequestException;
import com.sp.web.utils.GenericUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * UserNote contains all the notes for the user.
 * 
 * @author pradeepruhil
 *
 */
public class UserNote implements Serializable {
  
  private static final long serialVersionUID = 4761240023068472663L;

  private String id;
  
  /** all the private notes created for the user. */
  private List<Note> notes;
  
  /** user for which the this user note belongs. */
  private String userId;
  
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * Returns the blank array list in case it is null.
   * 
   * @return notes.
   */
  public List<Note> getNotes() {
    if (notes == null) {
      notes = new ArrayList<>();
    }
    return notes;
  }
  
  public void setNotes(List<Note> notes) {
    this.notes = notes;
  }
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * Gets the next UID.
   * 
   * @return the next UID
   */
  public String getNextUID() {
    if (id == null) {
      throw new InvalidRequestException("UserNote is not initalized");
    }
    return GenericUtils.getId();
  }
}
