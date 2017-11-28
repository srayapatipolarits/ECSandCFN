package com.sp.web.repository.growth;

import com.sp.web.model.GrowthFeedbackQuestions;
import com.sp.web.model.GrowthRequest;
import com.sp.web.model.GrowthRequestArchived;

import java.util.List;

/**
 * <code>GrowthRespository</code> contains operation to get the growth information from the
 * database. It provides operation for the user to invite and remove growth team members in the user
 * 
 * @author pradeep
 *
 */
public interface GrowthRepository {
  
  /**
   * <code>createGrowthRequest</code> method will create a growth request which is a invitation to
   * the user.
   * 
   * @param growthRequest
   *          growth Request
   * @return the growth Request created
   */
  GrowthRequest createGrowthRequest(GrowthRequest growthRequest);
  
  /**
   * findGrowthRequest method will return the growth request for the user and with member email.
   * 
   * @param memberEmail
   *          to which growth request is sent
   * @param userEmail
   *          for which growth request is created.
   * @return the list of growth request between the member and user email
   */
  List<GrowthRequest> findGrowthRequest(String memberEmail, String userEmail);
  
  /**
   * <code>findGrowthRequest</code> methdo will return the growth request form growth id.
   * 
   * @param growthRequestId
   *          for the request.
   * @return the growth request for the requesetd id.
   */
  GrowthRequest findGrowthRequest(String growthRequestId);
  
  /**
   * <code>archiveGrowthRequest</code> method will archive the growth request by changing the status
   * from active to deactivate.
   * 
   * @param growthRequestID
   *          growth request id
   */
  void archiveGrowthRequest(String growthRequestID);
  
  /**
   * <code>archive</code> the growth request.
   * 
   * @param growthRequest
   *          gorwth request to be archived.
   */
  void archiveGrowthRequest(GrowthRequest growthRequest);
  
  /**
   * <code>getArchivedGrowthRequest</code> method will return the archvied growth request.
   * 
   * @param email
   *          the email params.
   */
  List<GrowthRequestArchived> getArchivedGrowthRequest(String email);
  
  /**
   * <code>findArchviedGrowhtRequets</code> method will return the archived id.
   * 
   * @param archivedId
   *          archived grwoth request
   * @param requestedByEmail
   *          of the user
   * @return the list of growth request archived.
   */
  GrowthRequestArchived findArchivedGrowthRequest(String archivedId, String requestedByEmail);
  
  /**
   * <code>allRecievedGrowthrequest</code> will return all the growht request sent to the logged in
   * user for feeback.
   * 
   * @param email
   *          of the logged in user
   * @return the growth request in which he has to give the feedback.
   */
  List<GrowthRequest> allRecievedGrowthRequest(String email);
  
  /**
   * <code>removeGrowthRequest</code> method will remove the growth request id.
   * 
   * @param growthRequestId
   *          request to be deleted.
   */
  void removeGrowthRequest(String growthRequestId);
  
  /**
   * <code>updateGrowthRequest</code> method will update the growth request.
   * 
   * @param growthRequest
   *          growth request to be udated.
   */
  void updateGrowthRequest(GrowthRequest growthRequest);
  
  /**
   * <code>getAllGrowthFeedbackQuestions</code> method will return all the growth feedback
   * questions.
   * 
   * @return the list of growth qusetions.
   */
  List<GrowthFeedbackQuestions> getAllGrowthFeedbackQuestions();
  
  /**
   * <code>getGrowthFeedbackQuestions</code> method will return the growth feedback questions.
   * 
   * @param questionId
   *          which is to be retrived.
   * @return the questions.
   */
  public GrowthFeedbackQuestions getGrowthFeedbackQuestion(String questionId);
  
  /**
   * <code>getGrowthFeedbackQuestionForGoals</code> method return the feedback associated witht he
   * the user.
   * 
   * @param goal
   *          goals of the user.
   * @return the growth feedback questions.
   */
  List<GrowthFeedbackQuestions> getGrowthFeedbackQuestionForGoals(List<String> goal);
  
  
  /**
   * <code>getAllGrowthRequest</code> method will return all the growthRequest sent by the current
   * user to other people.
   * 
   * @param requestedBy
   *          email for the current user who has sent the request.
   * @return all the growth request
   */
  List<GrowthRequest> getAllGrowthRequests(String requestedBy);
  
  /**
   * <code>findGrowthRequestByuser</code> will return the growth request by user.
   * 
   * @param email
   *          of the requested by user
   * @return the lsit of growth request.
   */
  List<GrowthRequest> findGrowthRequestByUser(String email);
  
  /**
   * Remov ethe archived growth request
   * 
   * @param id
   *          whcih is the removed.
   */
  void removeArchivedGrowthRequest(GrowthRequestArchived growthRequestArchived);
  
  
  int getPendingGrowthRequests(List<String> userEmails);
  
  List<GrowthRequest> getAllGrowthRequestsByCompany(String companyId);
  
  List<GrowthRequestArchived> getAllGrowthRequestArchivedsByCompany(String companyId);

  /**
   * GetAll Requests
   * @return
   */
  List<GrowthRequest> getAllRequests();
  
}
