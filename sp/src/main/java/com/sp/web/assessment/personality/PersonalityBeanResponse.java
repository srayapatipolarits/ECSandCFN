package com.sp.web.assessment.personality;

import com.sp.web.Constants;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @author Dax Abraham
 * 
 *         This class is to send the response back for the personality bean but also stores the
 *         segment score.
 */
public class PersonalityBeanResponse extends PersonalityBean implements Serializable {

  /**
   * Serial version UID.
   */
  private static final long serialVersionUID = -7459223164654810433L;
  int segmentScore;
  BigDecimal[] segmentGraph;

  /**
   * Default Constructor.
   */
  public PersonalityBeanResponse() {
  }

  /**
   * Constructor.
   * 
   * @param personalityType
   *          - personality type
   * @param isValid
   *          - is valid flag
   * @param segmentScore
   *          - segment score
   */
  public PersonalityBeanResponse(PersonalityType personalityType, boolean isValid,
                                                                   int segmentScore) {
    setPersonalityType(personalityType);
    setValid(isValid);
    setSegmentScore(segmentScore);
  }

  /**
   * Constructor, isValid set to true by default.
   * 
   * @param personalityType
   *          - personality type
   * @param segmentScore
   *          - segment score
   */
  public PersonalityBeanResponse(PersonalityType personalityType, int segmentScore) {
    this(personalityType, true, segmentScore);
  }

  /**
   * Constructor to clone the personality response.
   * 
   * @param pb
   *          - the personality bean to clone
   */
  public PersonalityBeanResponse(PersonalityBean pb) {
    setPersonalityType(pb.getPersonalityType());
    setValid(pb.isValid());
  }

  public int getSegmentScore() {
    return segmentScore;
  }

  public void setSegmentScore(int segmentScore) {
    this.segmentScore = segmentScore;
    processScore();
  }

  /**
   * Processing to be done for generating
   * graph values for front end.
   */
  private void processScore() {
    segmentGraph = new BigDecimal[4];
    int tempSegmentScore = segmentScore;
    int index = 3;
    while (tempSegmentScore > 0) {
      segmentGraph[index] = BigDecimal.valueOf(
          (tempSegmentScore % 10) * Constants.PERSONALITY_MULTIPLIER).setScale(
          Constants.PERCENT_PRECISION, Constants.ROUNDING_MODE);
      tempSegmentScore /= 10;
      index--;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return super.toString() + ":" + segmentScore;
  }

  public BigDecimal[] getSegmentGraph() {
    return segmentGraph;
  }

  public void setSegmentGraph(BigDecimal[] segmentGraph) {
    this.segmentGraph = segmentGraph;
  }
}
