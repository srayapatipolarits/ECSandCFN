package com.sp.web.controller.note;

import com.sp.web.Constants;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dto.note.NoteDTO;
import com.sp.web.dto.note.NoteListingDTO;
import com.sp.web.form.note.NoteForm;
import com.sp.web.model.User;
import com.sp.web.model.note.Note;
import com.sp.web.model.note.UserNoteDao;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.note.NoteFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <code>NoteControllerHelper</code> class is the helper class for {@link NotesController}.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class NotesControllerHelper {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(NotesControllerHelper.class);
  
  @Autowired
  private NoteFactory noteFactory;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  private NotificationsProcessor notificationsProcessor;
  
  /**
   * addNote method will add the note to the user.
   * 
   * @param user
   *          is the logged in user.
   * @param params
   *          contains the note form.
   * @return the response.
   */
  public SPResponse addNote(User user, Object[] params) {
    
    NoteForm noteForm = (NoteForm) params[0];
    
    /* check if request is for adding a existing note or new note. */
    noteForm.validate();
    UserNoteDao userNote = noteFactory.getUserNote(user);
    noteForm.updateNote(userNote, user);
    noteFactory.saveNote(userNote, user);
    
    boolean isNewNote = (boolean) params[1];
    if (isNewNote) {
      SPResponse spResponse = new SPResponse();
      /* get the lates note */
      Note note = userNote.getNotes().get(userNote.getNotes().size() - 1);
      spResponse.add(Constants.PARAM_NOTE_DETAIL, new NoteDTO(note));
      return spResponse;
    }
    return new SPResponse().isSuccess();
  }
  
  /**
   * <code>getNotes</code> method will return all the notes depending upon the request whether from
   * dashboard or from notes section.
   * 
   * @param user
   *          logged in user.
   * @param params
   *          contains flag for dashboard request or not.
   * @return the resposne.
   */
  public SPResponse getNotes(User user, Object[] params) {
    
    boolean dashboardRequest = (boolean) params[0];
    SPResponse response = new SPResponse();
    UserNoteDao userNote = noteFactory.getUserNote(user);
    List<NoteListingDTO> noteListing = userNote.getNotes().stream().map(NoteListingDTO::new)
        .collect(Collectors.toList());
    response.add(Constants.PARAM_NOTELISTING, noteListing);
    
    /* if dashboard request fetch the detail of the first note. */
    if (dashboardRequest) {
      if (!CollectionUtils.isEmpty(noteListing)) {
        NoteListingDTO noteListingDTO = noteListing.get(0);
        Note note = userNote.getNotesMap().get(noteListingDTO.getNid());
        response.add(Constants.PARAM_NOTE_DETAIL, new NoteDTO(note));
      }
    }
    
    return response;
  }
  
  /**
   * <code>getDetail</code> request method will return the detailed information of a single note.
   * 
   * @param user
   *          for which the details to be fetched
   * @param params
   *          constains the paramter helper
   * @return the the note response.
   */
  public SPResponse getDetail(User user, Object[] params) {
    
    String noteId = (String) params[0];
    // Get User Notes
    UserNoteDao userNote = noteFactory.getUserNote(user);
    // Get Single Note Detail from userNotes map
    Note note = userNote.getNotesMap().get(noteId);
    Assert.notNull(note, "Note not found");
    
    return new SPResponse().add(Constants.PARAM_NOTE_DETAIL, new NoteDTO(note));
  }
  
  /**
   * <code>deleteNotes</code> method will delete the note or the comment in the note.
   * 
   * @param user
   *          logged in user.
   * @param params
   *          contains flag for dashboard request or not.
   * @return the resposne.
   */
  public SPResponse deleteNotes(User user, Object[] params) {
    
    String noteId = (String) params[0];
    
    Assert.hasText(noteId, "NoteId is not present");
    if (LOG.isDebugEnabled()) {
      LOG.debug("NoteId to be deleted is " + noteId);
    }
    UserNoteDao userNote = noteFactory.getUserNote(user);
    Note note = userNote.getNotesMap().get(noteId);
    
    Assert.notNull(note, "Note not found for id : " + noteId);
    
    /* check if only comment is deleted on complete note is to be deleted. */
    int cid = (Integer) params[1];
    
    if (cid != 0) {
      /* find the comment and deleting comment */
      note.getNoteData().removeIf(c -> c.getCid() == cid);
      /* check if not comment is present then remove the note as well. */
      if (note.getNoteData().size() == 0) {
        userNote.getNotes().remove(note);
      }
    } else {
      userNote.getNotes().removeIf(nt -> nt.getNoteId().equalsIgnoreCase(noteId));
    }
    noteFactory.saveNote(userNote, user);
    SPResponse response = new SPResponse();
    
    return response.isSuccess();
  }
  
  /**
   * <code>emailNotes</code> method will send the private notes email to the person.
   * 
   * @param user
   *          logged user.
   * @param params
   *          contains the noteId which is to be sent in the email.
   * @return the response.
   */
  public SPResponse emailNotes(User user, Object[] params) {
    
    String noteId = (String) params[0];
    
    UserNoteDao userNote = noteFactory.getUserNote(user);
    Note note = userNote.getNotesMap().get(noteId);
    
    Assert.notNull(note, "Invalid noteId");
    Map<String, Object> param = new HashMap<>();
    param.put(Constants.PARAM_NOTE, note);
    param.put(Constants.PARAM_SUBJECT,
        MessagesHelper.getMessage(Constants.PARAM_EMAIL_NOTES_SUB,user.getLocale(), note.getTitle()));
    notificationsProcessor.process(NotificationType.EmailNotes, user, user, param);
    return new SPResponse().isSuccess();
  }
  
  /**
   * <code>updateTitle</code> method will update the title of the note.
   * 
   * @param user
   *          logged user.
   * @param params
   *          contains the noteId which is to be sent in the email.
   * @return the response.
   */
  public SPResponse updateTitle(User user, Object[] params) {
    
    String noteId = (String) params[0];
    
    String title = (String) params[1];
    
    Assert.hasText(title, "Title cannot be blank or null");
    UserNoteDao userNote = noteFactory.getUserNote(user);
    Note note = userNote.getNotesMap().get(noteId);
    
    Assert.notNull(note, "Invalid noteId");
    note.setTitle(title);
    noteFactory.saveNote(userNote, user);
    
    return new SPResponse().isSuccess();
  }
  
}
