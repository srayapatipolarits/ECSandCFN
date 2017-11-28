package com.sp.web.service.note;

import com.sp.web.model.User;
import com.sp.web.model.note.UserNote;
import com.sp.web.model.note.UserNoteDao;
import com.sp.web.repository.note.NoteRepository;
import com.sp.web.user.UserFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * NoteFactory is the factory class for the notes.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class NoteFactory {
  
  @Autowired
  private NoteRepository noteRepository;
  
  @Autowired
  private UserFactory userFactory;
  
  /**
   * Save note method will update the private note for the user.
   * 
   * @param userNote
   *          of the user.
   */
  @CacheEvict(value = "notes", key = "#user.id")
  public void saveNote(UserNoteDao userNote, User user) {
    noteRepository.save(userNote.getUserNote());
  }
  
  /**
   * getUserNote method will return the usernote dao for the note.
   * 
   * @param user
   *          logged in user.
   * @return the user note dao for the user.
   */
  @Cacheable(value = "notes", key = "#user.id")
  public UserNoteDao getUserNote(User user) {
    UserNote userNote = noteRepository.findById(user.getUserNoteId());
    
    /* if no user note exist create a new user note for the user. */
    if (userNote == null) {
      userNote = new UserNote();
      userNote.setUserId(user.getId());
      noteRepository.save(userNote);
      
      /* update the note in the user. */
      user.setUserNoteId(userNote.getId());
      
      /* update the user in database. */
      userFactory.updateUser(user);
    }
    return new UserNoteDao(userNote);
  }
  
  /**
   * delete note method will delete all the private notes for the user.
   * 
   * @param user
   *          of the user.
   */
  @CacheEvict(value = "notes", key = "#user.id")
  public void deleteNotes(User user) {
    UserNote userNote = noteRepository.findById(user.getUserNoteId());
    if (userNote != null) {
      noteRepository.delete(userNote);
    }
  }

  /**
   * @param userNote
   *          - delete the user note.
   */
  @CacheEvict(value = "notes", key = "#userNote.userId")
  public void deleteNotes(UserNote userNote) {
    noteRepository.delete(userNote);
  }
}
