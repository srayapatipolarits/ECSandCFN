package com.sp.web.dto.note;

import com.sp.web.model.Comment;
import com.sp.web.model.note.Note;

import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * NoteDTO class holds the information for the note to be sent to the front end.
 * 
 * @author pradeepruhil
 *
 */
public class NoteDTO {
  
  private String title;
  
  private List<Comment> noteData;
  
  private String noteId;
  
  /**
   * Constructor for the note.
   * 
   * @param note
   *          is the note of the user.
   */
  public NoteDTO(Note note) {
    BeanUtils.copyProperties(note, this);
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public List<Comment> getNoteData() {
    return noteData;
  }
  
  public void setNoteData(List<Comment> noteData) {
    this.noteData = noteData;
  }
  
  public void setNoteId(String noteId) {
    this.noteId = noteId;
  }
  
  public String getNoteId() {
    return noteId;
  }
}
