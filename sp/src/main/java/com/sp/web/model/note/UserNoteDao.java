package com.sp.web.model.note;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * UserNoteDao class holds the note with note Id in the map in the cache.
 * 
 * @author pradeepruhil
 *
 */
@Document(collection = "userNote")
public class UserNoteDao extends UserNote {
  
  private static final long serialVersionUID = 7318784256744986780L;
  /**
   * Notes Map containing the note data against the note id.
   */
  @Transient
  private Map<String, Note> notesMap;
  
  /**
   * Initializing the user note dao.
   * @param userNote userNote. 
   */
  public UserNoteDao(UserNote userNote) {
    BeanUtils.copyProperties(userNote, this);
    
    Comparator<Note> comparator = (bd1, bd2) -> bd1.getUpdatedOn().compareTo(bd2.getUpdatedOn());
    setNotes(getNotes().stream().sorted(comparator.reversed()).collect(Collectors.toList()));
    this.notesMap = getNotes().stream().collect(
        Collectors.toMap(Note::getNoteId, Function.identity()));
  }
  
  public Map<String, Note> getNotesMap() {
    return notesMap;
  }
  
  /**
   * Get the original user note from the dao.
   * 
   * @return
   *      the company object
   */
  public UserNote getUserNote() {
    UserNote userNote = new UserNote();
    BeanUtils.copyProperties(this, userNote);
    return userNote;
  }
}
