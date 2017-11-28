package com.sp.web.model.competency;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sp.web.Constants;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.User;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 
 * @author Dax Abraham
 *
 *         The entity class to store the peer competency evaluation scores along with average scores
 *         etc.
 */
public class PeerCompetencyEvaluationScore extends BaseCompetencyEvaluationScore {
  
  private static final long serialVersionUID = -7441872304037724659L;
  @JsonIgnore
  private List<UserCompetencyEvaluationScore> peers;
  private int count;
  private int completedCount;
  
  public List<UserCompetencyEvaluationScore> getPeers() {
    return peers;
  }
  
  public void setPeers(List<UserCompetencyEvaluationScore> peers) {
    this.peers = peers;
  }
  
  public int getCompletedCount() {
    return completedCount;
  }
  
  public void setCompletedCount(int completedCount) {
    this.completedCount = completedCount;
  }
  
  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  /**
   * Get a new instance of the peer competency evaluation score also initialize any initial data.
   * 
   * @return new instance of peer competency evaluation score
   */
  public static PeerCompetencyEvaluationScore newInstance() {
    PeerCompetencyEvaluationScore score = new PeerCompetencyEvaluationScore();
    score.setPeers(new ArrayList<UserCompetencyEvaluationScore>());
    return score;
  }
  
  /**
   * Add the given competency evaluation score to the peer score.
   * 
   * @param score
   *          - score to add
   * @return
   *    true if added else false 
   */
  public boolean add(UserCompetencyEvaluationScore score) {
    
    // check if the user request already exists
    BaseUserDTO reviewer = score.getReviewer();
    if (!peers.isEmpty()) {
      Optional<UserCompetencyEvaluationScore> findFirst = peers.stream()
          .filter(p -> p.checkIfSameUser(reviewer)).findFirst();
      if (findFirst.isPresent()) {
        return false;
      }
    }
    
    peers.add(score);
    count++;
    if (score.getScore() > 0) {
      completedCount++;
    }
    return true;
  }
  
  /**
   * Update the competency evaluation score for the given peer request.
   * 
   * @param reviewer
   *          - peer
   * @param evaluationDetails
   *          - score details
   * @return true if updated
   */
  public boolean updateScore(User reviewer, UserCompetencyEvaluationDetails evaluationDetails) {
    // getting the score to update
    Optional<UserCompetencyEvaluationScore> findFirst = peers.stream()
        .filter(peer -> peer.checkIfSameUser(reviewer)).findFirst();
    // if the score request found then update the score
    Assert.isTrue(findFirst.isPresent(), "Peer not found in request list.");
    findFirst.get().updateFrom(evaluationDetails);
    completedCount++;
    updateAverageScore();
    return true;
  }
  
  /**
   * Do average of all the peer scores.
   */
  public void updateAverageScore() {
    double total = 0d;
    double[] averageScore = null;
    for (UserCompetencyEvaluationScore peerScore : peers) {
      if (peerScore.getScore() > 0) {
        total += peerScore.getScore();
        final double[] scoreArray = peerScore.getScoreArray();
        if (averageScore == null) {
          averageScore = new double[scoreArray.length];
          Arrays.fill(averageScore, 0d);
        }
        for (int i = 0; i < scoreArray.length; i++) {
          averageScore[i] += scoreArray[i];
        }
      }
    }
    if (completedCount > 1) {
      total = doAverage(total);
      for (int i = 0; i < averageScore.length; i++) {
        averageScore[i] = doAverage(averageScore[i]);
      }
    }
    setScore(total);
    setScoreArray(averageScore);
  }
  
  private double doAverage(double scoreToAverage) {
    return (completedCount == 0) ? 0 : BigDecimal.valueOf(scoreToAverage / completedCount)
        .setScale(Constants.PRECISION, Constants.ROUNDING_MODE).doubleValue();
  }

  /**
   * Get the peer reviewer.
   * 
   * @param reviewerId
   *          - reviewer id.
   * @return
   *    the reviewer found 
   */
  public BaseUserDTO getPeer(String reviewerId) {
    return Optional.ofNullable(getPeerRequest(reviewerId))
        .map(UserCompetencyEvaluationScore::getReviewer).orElse(null);
  }

  /**
   * Get the peer evaluation score for the given reviewer id else null.
   * 
   * @param reviewerId
   *            - Reviewer id
   * @return
   *    the peer request found else null
   */
  public UserCompetencyEvaluationScore getPeerRequest(String reviewerId) {
    if (!CollectionUtils.isEmpty(getPeers())) {
      Optional<UserCompetencyEvaluationScore> findFirst = getPeers().stream()
          .filter(peer -> peer.getReviewer().getId().equals(reviewerId)).findFirst();
      if (findFirst.isPresent()) {
        return findFirst.get();
      }
    }
    return null;
  }

  /**
   * Remove the given peer request.
   * 
   * @param peerRequest
   *          - peer request
   * @return
   *    true if removed else false
   */
  public boolean removePeer(UserCompetencyEvaluationScore peerRequest) {
    final boolean isRemoved = peers.remove(peerRequest);
    if (isRemoved) {
      if (peerRequest.isCompleted()) {
        completedCount--;
      }
      count--;
    }
    return isRemoved;
  }
}
