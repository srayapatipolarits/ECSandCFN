package com.sp.web.repository.goal;

import com.sp.web.model.goal.PracticeFeedback;
import com.sp.web.model.goal.SPNote;

import java.util.List;

/**
 * <code>SPNoteFeedbackRepository</code> interface will load the note, feedback for user, goal, dev
 * strategy.
 * 
 * @author vikram
 *
 */
public interface SPNoteFeedbackRepository {
  
  public SPNote addNote(SPNote spnote);
  
  public SPNote updateNote(String noteId, String desc);
  
  public void deleteNote(String noteId);
  
  /**
   * Delete all the notes for the given practice area.
   * 
   * @param goalId
   *          - practice area id
   * 
   * @return the number of records that were affected
   */
  public int deleteAllNote(String goalId);
  
  public PracticeFeedback createRequestFeedback(PracticeFeedback feedback);
  
  public PracticeFeedback updateRequestFeedback(PracticeFeedback feedback);
  
  public void deleteRequestFeedback(String feedbackId);
  
  /**
   * Delete all the feedbacks for the given goal id.
   * 
   * @param goalId
   *          - practice area id
   * @return the number of records affected
   */
  public int deleteAllFeedback(String goalId);
  
  public PracticeFeedback giveRequestFeedback(String feedbackId, String feebackResponse);
  
  public List<PracticeFeedback> findRequestFeedbackReceived(String email);
  
  public List<SPNote> findAllNotes(String userId);
  
  public List<SPNote> findNotesForGoal(String userId, String goalId);
  
  public List<PracticeFeedback> findAllFeedback(String userId);
  
  public List<PracticeFeedback> findFeedbackForGoal(String userId, String goalId);
  
  public List<SPNote> findAllNotesFeedback(String userId);
  
  public List<SPNote> findAllNotesFeedbackForGoal(String userId, String goalId);
  
  public SPNote findNoteById(String noteId);
  
  public PracticeFeedback findFeedbackById(String feedbackId);
  
  public SPNote findNoteFeedbackById(String noteId);
  
  public int getAllFeedbackCount(String userId);
  
  public int getAllNotesCount(String userId);
  
  /**
   * This method gets the top notes and feedback to display on the dashboard.
   * 
   * @param userId
   *          - the user id
   * @return the list of notes and feedback
   */
  public List<SPNote> findTopNotesFeedback(String userId);
  
  /**
   * This method gets the feedback based on token id. Used in Tokenprocessor to send feedbackid to
   * jsp.
   * 
   * @param userId
   *          - the token id
   * @return Practice feedback
   */
  public PracticeFeedback findFeedbackbyTokenId(String tokenId);
  
  /**
   * findAllPracticeFeedbackNoteByCompany method will return all the notes which will belong to list
   * of users and to the same company.
   * 
   * @param usersIds
   *          list of userIds.
   * @param companyId
   *          for which users belong
   * @return the list of SPNote
   */
  public List<SPNote> findAllPracticeFeedbackNoteByCompany(List<String> usersIds, String companyId);
  
  /**
   * Deleting the practice request.
   * 
   * @param spNoteFeedback
   *          - note or feedback to delete
   */
  public void deleteRequest(SPNote spNoteFeedback);
  
  /**
   * RemoveAllNotes member will remove all the notes and feedback for the userId passed.
   * 
   * @param forUserId
   *          for whose notes and feedback are to be removed.
   */
  public void removeAllNotesAndFeeback(String forUserId);
  
}
