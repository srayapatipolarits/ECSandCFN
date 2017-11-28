package com.sp.web.form.note;

import com.sp.web.Constants;
import com.sp.web.form.CommentForm;
import com.sp.web.model.Comment;
import com.sp.web.model.User;
import com.sp.web.model.note.Note;
import com.sp.web.model.note.UserNoteDao;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Optional;

/**
 * NoteForm class contains the form for the adding a private note to the user.
 * 
 * @author pradeepruhil
 *
 */
public class NoteForm {
  
  private String title;
  
  private CommentForm commentForm;
  
  private String noteId;
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public CommentForm getCommentForm() {
    return commentForm;
  }
  
  public void setCommentForm(CommentForm commentForm) {
    this.commentForm = commentForm;
  }
  
  public String getNoteId() {
    return noteId;
  }
  
  public void setNoteId(String noteId) {
    this.noteId = noteId;
  }
  
  public void validate() {
    Assert.notNull(commentForm, "No comment is present. Cannot create the comment.");
    commentForm.validate();
  }
  
  /**
   * Update the user note for the new comment or updated comment.
   * 
   * @param userNote
   *          to be updated.
   * @param user
   *          logged in user profile.
   */
  public void updateNote(UserNoteDao userNote, User user) {
    Note note = null;
    if (this.noteId != null) {
      note = userNote.getNotesMap().get(this.noteId);
    }
    
    if (note == null) {
      /* request to add new note to user note. */
      note = new Note();
      note.setNoteId(userNote.getNextUID());
      userNote.getNotes().add(note);
    }
    
    /* update the note title. */
    if (StringUtils.isNotBlank(getTitle())) {
      note.setTitle(getTitle());
    } else {
      note.setTitle(MessagesHelper.getMessage(Constants.PARAM_NOTE_DEFAULT_MESSAGE));
    }
    
    /* check if existing comment is updated or a new comment. */
    Optional<Comment> existingComment = note.getNoteData().stream()
        .filter(comment -> comment.getCid() == commentForm.getCid()).findFirst();
    
    if (existingComment.isPresent()) {
      commentForm.updateComment(existingComment.get());
    } else {
      /* Add the new comment. */
      int cidCounter = note.getNoteData().size();
      
      Comment newComment = Comment.newCommment(user, commentForm.getComment());
      newComment.setCid(++cidCounter);
      newComment.setContentReference(commentForm.getContentReference());
      newComment.setText(commentForm.getComment());
      ((LinkedList<Comment>) note.getNoteData()).addFirst(newComment);
    }
    /* update the note, to bring it to the top */
    note.setUpdatedOn(LocalDateTime.now());
    
  }
  
}
