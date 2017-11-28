package com.sp.web.controller.note;

import com.sp.web.audit.Audit;
import com.sp.web.controller.ControllerHelper;
import com.sp.web.form.note.NoteForm;
import com.sp.web.model.audit.ServiceType;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller class for the Notes function in spotlight.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class NotesController {
  
  /** helper class for notes controller. */
  @Autowired
  private NotesControllerHelper notesControllerHelper;
  
  /**
   * <code>addNote</code> request will add a new note to the user.
   * 
   * @param noteForm
   *          contains the new note information.
   * @param authentication
   *          logged in user.
   * @return the response whether note got creted or not.
   */
  @RequestMapping(value = "/notes/addNote", method = RequestMethod.POST)
  @ResponseBody
  @Audit(type = ServiceType.NOTES)
  public SPResponse addNote(@RequestBody NoteForm noteForm,
      @RequestParam(defaultValue = "false") boolean newNote, Authentication authentication) {
    return ControllerHelper.process(notesControllerHelper::addNote, authentication, noteForm,
        newNote);
  }
  
  /**
   * <code>getNotes</code> request method will return the private notes details.
   * 
   * @param dashboardRequest
   *          whether to return the dashboard request notes with detial of the first note.
   * @param authentication
   *          is the logged in user
   * @return the the notes response.
   */
  @RequestMapping(value = "/notes/getAllNotes", method = RequestMethod.POST)
  @ResponseBody
  @Audit(type = ServiceType.NOTES)
  public SPResponse getNotes(
      @RequestParam(required = false, defaultValue = "false") boolean dashboardRequest,
      Authentication authentication) {
    return ControllerHelper.process(notesControllerHelper::getNotes, authentication,
        dashboardRequest);
  }
  
  /**
   * <code>getDetail</code> request method will return the detailed information of a single note.
   * 
   * @param noteId
   *          for which the details to be fetched
   * @param authentication
   *          is the logged in user
   * @return the the note response.
   */
  @RequestMapping(value = "/notes/getDetail", method = RequestMethod.POST)
  @ResponseBody
  @Audit(type = ServiceType.NOTES)
  public SPResponse getDetail(@RequestParam String noteId, Authentication authentication) {
    return ControllerHelper.process(notesControllerHelper::getDetail, authentication, noteId);
  }
  
  /**
   * <code>deleteNote</code> request method will delete the complete note or the comment of the
   * note.
   * 
   * @param authentication
   *          is the logged in user
   * @param noteId
   *          which is to be deleted
   * @param cid
   *          comment which is to be deleted.
   * @return the the notes response.
   */
  @RequestMapping(value = "/notes/deleteNotes", method = RequestMethod.POST)
  @ResponseBody
  @Audit(type = ServiceType.NOTES)
  public SPResponse deleteNotes(@RequestParam String noteId,
      @RequestParam(required = false, defaultValue = "0") int cid, Authentication authentication) {
    return ControllerHelper
        .process(notesControllerHelper::deleteNotes, authentication, noteId, cid);
  }
  
  /**
   * <code>emailNotes</code> request method will email the private note. note.
   * 
   * @param authentication
   *          is the logged in user
   * @param noteId
   *          which is to be deleted
   * @return the the notes response.
   */
  @RequestMapping(value = "/notes/emailNotes", method = RequestMethod.POST)
  @ResponseBody
  @Audit(type = ServiceType.NOTES)
  public SPResponse emailNotes(@RequestParam String noteId, Authentication authentication) {
    return ControllerHelper.process(notesControllerHelper::emailNotes, authentication, noteId);
  }
  
  /**
   * <code>updateTitle</code> request method will update the title of the notes.
   * 
   * @param authentication
   *          is the logged in user
   * @param noteId
   *          which is to be deleted
   * @return the the notes response.
   */
  @RequestMapping(value = "/notes/updateTitle", method = RequestMethod.POST)
  @ResponseBody
  @Audit(type = ServiceType.NOTES)
  public SPResponse updateTitle(@RequestParam String noteId,@RequestParam String title, Authentication authentication) {
    return ControllerHelper.process(notesControllerHelper::updateTitle, authentication, noteId, title);
  }
}
