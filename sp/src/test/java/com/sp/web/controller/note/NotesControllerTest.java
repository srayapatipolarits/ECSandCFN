package com.sp.web.controller.note;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.form.CommentForm;
import com.sp.web.form.note.NoteForm;
import com.sp.web.model.User;
import com.sp.web.model.note.Note;
import com.sp.web.model.note.UserNoteDao;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.note.NoteFactory;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * NotesControllerTest contains the test method for the note controller.
 * 
 * @author pradeepruhil
 *
 */
public class NotesControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  private NoteFactory noteFactory;
  
  @Test
  public void addNoteTest() throws Exception {
    
    dbSetup.removeAll("userNote");
    NoteForm noteForm = new NoteForm();
    ObjectMapper om = new ObjectMapper();
    String request = om.writeValueAsString(noteForm);
    
    /* invalid request */
    MvcResult result = this.mockMvc
        .perform(
            post("/notes/addNote").param("newNote", "true").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(
            jsonPath("$.error.IllegalArgumentException").value(
                "No comment is present. Cannot create the comment.")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    /* valid comment with no title */
    CommentForm commentForm = new CommentForm();
    commentForm.setComment("Test comment");
    commentForm.setContentReference(null);
    noteForm.setCommentForm(commentForm);
    request = om.writeValueAsString(noteForm);
    log.debug("Request is " + request);
    result = this.mockMvc
        .perform(
            post("/notes/addNote").param("newNote", "true").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    User defaultUser = dbSetup.getUser("admin@admin.com");
    UserNoteDao userNote = noteFactory.getUserNote(defaultUser);
    Note notes = userNote.getNotes().get(0);
    Assert.assertTrue(notes.getNoteData().get(0).getText().equalsIgnoreCase("Test comment"));
    
    /* update comment with no title */
    commentForm.setComment("Second comment");
    commentForm.setContentReference(null);
    noteForm.setCommentForm(commentForm);
    noteForm.setNoteId(notes.getNoteId());
    request = om.writeValueAsString(noteForm);
    result = this.mockMvc
        .perform(
            post("/notes/addNote").param("newNote", "false").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    defaultUser = dbSetup.getUser("admin@admin.com");
    userNote = noteFactory.getUserNote(defaultUser);
    notes = userNote.getNotes().get(0);
    Assert.assertTrue(notes.getNoteData().get(0).getText().equalsIgnoreCase("Second comment"));
    
  }
  
  @Test
  public void getNoteDetailsList() throws Exception {
    
    dbSetup.removeAll("userNote");
    
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/notes/getAllNotes").contentType(MediaType.TEXT_PLAIN)
                .session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.noteListing", hasSize(0))).andReturn();
    log.info("The MVC Response:" + result.getResponse().getContentAsString());
    
    NoteForm noteForm = new NoteForm();
    ObjectMapper om = new ObjectMapper();
    String request = om.writeValueAsString(noteForm);
    
    /* valid comment with no title */
    CommentForm commentForm = new CommentForm();
    commentForm.setComment("Test comment");
    commentForm.setContentReference(null);
    noteForm.setCommentForm(commentForm);
    request = om.writeValueAsString(noteForm);
    result = this.mockMvc
        .perform(
            post("/notes/addNote").content(request).contentType(MediaType.APPLICATION_JSON)
                .session(session)).andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    User defaultUser = dbSetup.getUser("admin@admin.com");
    UserNoteDao userNote = noteFactory.getUserNote(defaultUser);
    Note notes = userNote.getNotes().get(0);
    Assert.assertTrue(notes.getNoteData().get(0).getText().equalsIgnoreCase("Test comment"));
    
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/notes/getAllNotes").contentType(MediaType.TEXT_PLAIN)
                .session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.noteListing", hasSize(1))).andReturn();
    log.info("The MVC Response:" + result.getResponse().getContentAsString());
    
  }
  
  @Test
  public void deleteNotes() throws Exception {
    
    dbSetup.removeAll("userNote");
    
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/notes/deleteNotes").contentType(MediaType.TEXT_PLAIN)
                .param("noteId", "1").session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error.IllegalArgumentException").value("Note not found for id : 1"))
        .andReturn();
    log.info("The MVC Response:" + result.getResponse().getContentAsString());
    
    NoteForm noteForm = new NoteForm();
    ObjectMapper om = new ObjectMapper();
    
    String request = om.writeValueAsString(noteForm);
    
    /* valid comment with no title */
    CommentForm commentForm = new CommentForm();
    commentForm.setComment("Test comment");
    commentForm.setContentReference(null);
    noteForm.setCommentForm(commentForm);
    request = om.writeValueAsString(noteForm);
    result = this.mockMvc
        .perform(
            post("/notes/addNote").content(request).contentType(MediaType.APPLICATION_JSON)
                .session(session)).andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    User defaultUser = dbSetup.getUser("admin@admin.com");
    UserNoteDao userNote = noteFactory.getUserNote(defaultUser);
    Note notes = userNote.getNotes().get(0);
    Assert.assertTrue(notes.getNoteData().get(0).getText().equalsIgnoreCase("Test comment"));
    
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/notes/deleteNotes").contentType(MediaType.TEXT_PLAIN)
                .param("noteId", notes.getNoteId()).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.info("The MVC Response:" + result.getResponse().getContentAsString());
  }
  
  @Test
  public void eamilNotes() throws Exception {
    
    dbSetup.removeAll("userNote");
    
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/notes/emailNotes").contentType(MediaType.TEXT_PLAIN)
                .param("noteId", "1").session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error.IllegalArgumentException").value("Invalid noteId"))
        .andReturn();
    log.info("The MVC Response:" + result.getResponse().getContentAsString());
    
    NoteForm noteForm = new NoteForm();
    ObjectMapper om = new ObjectMapper();
    
    String request = om.writeValueAsString(noteForm);
    
    /* valid comment with no title */
    CommentForm commentForm = new CommentForm();
    commentForm.setComment("Test comment");
    commentForm.setContentReference(null);
    noteForm.setCommentForm(commentForm);
    request = om.writeValueAsString(noteForm);
    result = this.mockMvc
        .perform(
            post("/notes/addNote").content(request).contentType(MediaType.APPLICATION_JSON)
                .session(session)).andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    User defaultUser = dbSetup.getUser("admin@admin.com");
    UserNoteDao userNote = noteFactory.getUserNote(defaultUser);
    Note notes = userNote.getNotes().get(0);
    Assert.assertTrue(notes.getNoteData().get(0).getText().equalsIgnoreCase("Test comment"));
    
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/notes/emailNotes").contentType(MediaType.TEXT_PLAIN)
                .param("noteId", notes.getNoteId()).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.info("The MVC Response:" + result.getResponse().getContentAsString());
  }
  
  @Test
  public void updateTitle() throws Exception {
    
    dbSetup.removeAll("userNote");
    
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/notes/updateTitle").contentType(MediaType.TEXT_PLAIN)
                .param("noteId", "1").param("title", "Good").session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error.IllegalArgumentException").value("Invalid noteId"))
        .andReturn();
    log.info("The MVC Response:" + result.getResponse().getContentAsString());
    
    NoteForm noteForm = new NoteForm();
    ObjectMapper om = new ObjectMapper();
    
    String request = om.writeValueAsString(noteForm);
    
    /* valid comment with no title */
    CommentForm commentForm = new CommentForm();
    commentForm.setComment("Test comment");
    commentForm.setContentReference(null);
    noteForm.setCommentForm(commentForm);
    request = om.writeValueAsString(noteForm);
    result = this.mockMvc
        .perform(
            post("/notes/addNote").content(request).contentType(MediaType.APPLICATION_JSON)
                .session(session)).andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    User defaultUser = dbSetup.getUser("admin@admin.com");
    UserNoteDao userNote = noteFactory.getUserNote(defaultUser);
    Note notes = userNote.getNotes().get(0);
    Assert.assertTrue(notes.getNoteData().get(0).getText().equalsIgnoreCase("Test comment"));
    
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/notes/updateTitle").contentType(MediaType.TEXT_PLAIN)
                .param("noteId", notes.getNoteId()).param("title", "Good").session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.info("The MVC Response:" + result.getResponse().getContentAsString());
    
    UserNoteDao userNote2 = noteFactory.getUserNote(defaultUser);
    Note note = userNote2.getNotesMap().get(notes.getNoteId());
    Assert.assertNotSame("Title is not updated", "Good", note.getTitle());
  }
  
}
