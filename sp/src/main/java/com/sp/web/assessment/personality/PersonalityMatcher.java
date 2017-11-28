package com.sp.web.assessment.personality;

import static com.sp.web.Constants.KEY_DELIMITTER;

import com.sp.web.assessment.questions.TraitType;
import com.sp.web.exception.PersonalityNotFoundException;

import org.apache.log4j.Logger;

import java.util.Map;

/**
 * The reference to the Personality Matcher.
 * 
 * @author daxabraham
 * 
 */
public class PersonalityMatcher {
  
  private static final Logger LOG = Logger.getLogger(PersonalityMatcher.class);
  
  // Primary
  private static final String DOMINANCE_HIGH_KEY = TraitType.Dominance + KEY_DELIMITTER
      + RangeType.Primary;
  private static final String INDUCEMENT_HIGH_KEY = TraitType.Inducement + KEY_DELIMITTER
      + RangeType.Primary;
  private static final String SUBMISSION_HIGH_KEY = TraitType.Submission + KEY_DELIMITTER
      + RangeType.Primary;
  private static final String COMPLIANCE_HIGH_KEY = TraitType.Compliance + KEY_DELIMITTER
      + RangeType.Primary;
  
  // under pressure
  private static final String DOMINANCE_LOW_KEY = TraitType.Dominance + KEY_DELIMITTER
      + RangeType.UnderPressure;
  private static final String INDUCEMENT_LOW_KEY = TraitType.Inducement + KEY_DELIMITTER
      + RangeType.UnderPressure;
  private static final String SUBMISSION_LOW_KEY = TraitType.Submission + KEY_DELIMITTER
      + RangeType.UnderPressure;
  private static final String COMPLIANCE_LOW_KEY = TraitType.Compliance + KEY_DELIMITTER
      + RangeType.UnderPressure;
  
  // perceived by others
  private static final String DOMINANCE_OTHER_KEY = TraitType.Dominance + KEY_DELIMITTER
      + RangeType.PerceivedByOthers;
  private static final String INDUCEMENT_OTHER_KEY = TraitType.Inducement + KEY_DELIMITTER
      + RangeType.PerceivedByOthers;
  private static final String SUBMISSION_OTHER_KEY = TraitType.Submission + KEY_DELIMITTER
      + RangeType.PerceivedByOthers;
  private static final String COMPLIANCE_OTHER_KEY = TraitType.Compliance + KEY_DELIMITTER
      + RangeType.PerceivedByOthers;
  
  private Map<String, RangeBean> matcherMap;
  private Map<String, PersonalityBean> personalityMap;
  
  public Map<String, RangeBean> getMatcherMap() {
    return matcherMap;
  }
  
  public void setMatcherMap(Map<String, RangeBean> matcherMap) {
    this.matcherMap = matcherMap;
  }
  
  public Map<String, PersonalityBean> getPersonalityMap() {
    return personalityMap;
  }
  
  public void setPersonalityMap(Map<String, PersonalityBean> personalityMap) {
    this.personalityMap = personalityMap;
  }
  
  /**
   * Gets the Personality for the given keys.
   * 
   * @param dominanceKey
   *          - dominance key
   * @param dominanceIndex
   *          - dominance index
   * @param inducementKey
   *          - inducement key
   * @param inducementIndex
   *          - inducement index
   * @param submissionKey
   *          - submission key
   * @param submissionIndex
   *          - submission index
   * @param complianceKey
   *          - compliance key
   * @param complianceIndex
   *          - compliance index
   * @return - the personality bean
   */
  private PersonalityBeanResponse getPersonality(String dominanceKey, int dominanceIndex,
      String inducementKey, int inducementIndex, String submissionKey, int submissionIndex,
      String complianceKey, int complianceIndex) {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Processing Personality request  Dominance:" + dominanceIndex + ":Inducement:"
          + inducementIndex + ":Submission:" + submissionIndex + ":Compliance:" + complianceIndex);
    }
    
    RangeBean dominanceRangeBean = matcherMap.get(dominanceKey);
    RangeBean inducementRangeBean = matcherMap.get(inducementKey);
    RangeBean submissionRangeBean = matcherMap.get(submissionKey);
    RangeBean complianceRangeBean = matcherMap.get(complianceKey);
    
    int segmentScore = 0;
    
    segmentScore = dominanceRangeBean.getSegmentValue(dominanceIndex)
        + inducementRangeBean.getSegmentValue(inducementIndex)
        + submissionRangeBean.getSegmentValue(submissionIndex)
        + complianceRangeBean.getSegmentValue(complianceIndex);
    
    PersonalityBean personalityBean = personalityMap.get(segmentScore + "");
    PersonalityBeanResponse personalityBeanResponse = new PersonalityBeanResponse(personalityBean);
    personalityBeanResponse.setSegmentScore(segmentScore);
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Personality Response Bean:" + personalityBeanResponse);
    }
    
    return personalityBeanResponse;
  }
  
  /**
   * Method to get the primary personality type.
   * 
   * @param dominanceMost
   *          - dominance most score
   * @param inducementMost
   *          - Inducement most score
   * @param submissionMost
   *          - submission most score
   * @param complianceMost
   *          - compliance most score
   * @return the personality type
   */
  public PersonalityBeanResponse getPrimaryPersonalityScore(int dominanceMost, int inducementMost,
      int submissionMost, int complianceMost) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Primary called !!!");
    }
    
    return getPersonality(
        DOMINANCE_HIGH_KEY, dominanceMost,
        INDUCEMENT_HIGH_KEY, inducementMost,
        SUBMISSION_HIGH_KEY, submissionMost,
        COMPLIANCE_HIGH_KEY, complianceMost);
  }
  
  /**
   * Method to get the under pressure personality type.
   * 
   * @param dominanceLeast
   *          - dominance least score
   * @param inducementLeast
   *          - Inducement least score
   * @param submissionLeast
   *          - submission least score
   * @param complianceLeast
   *          - compliance least score
   * @return the personality type
   */
  public PersonalityBeanResponse getUnderPressurePersonalityScore(int dominanceLeast,
      int inducementLeast, int submissionLeast, int complianceLeast) {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Under Pressure called !!!");
    }
    
    return getPersonality(
        DOMINANCE_LOW_KEY, dominanceLeast,
        INDUCEMENT_LOW_KEY, inducementLeast,
        SUBMISSION_LOW_KEY, submissionLeast,
        COMPLIANCE_LOW_KEY, complianceLeast);
  }
  
  /**
   * Method to get the perceived by others personality type.
   * 
   * @param dominancePerceivedByOthers
   *          - dominance perceived by others score
   * @param inducementPerceivedByOthers
   *          - Inducement perceived by others score
   * @param submissionPerceivedByOthers
   *          - submission perceived by others score
   * @param compliancePerceivedByOthers
   *          - compliance perceived by others score
   * @return the personality type
   */
  public PersonalityBeanResponse getPerceievedByOthersPersonalityScore(
      int dominancePerceivedByOthers, int inducementPerceivedByOthers,
      int submissionPerceivedByOthers, int compliancePerceivedByOthers) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Perceived by others called !!!");
    }
    return getPersonality(DOMINANCE_OTHER_KEY, dominancePerceivedByOthers, INDUCEMENT_OTHER_KEY,
        inducementPerceivedByOthers, SUBMISSION_OTHER_KEY, submissionPerceivedByOthers,
        COMPLIANCE_OTHER_KEY, compliancePerceivedByOthers);
  }
  
  /**
   * Gets the personality bean.
   * 
   * @param segmentScore
   *          - the segment score
   * @return the personality bean for the given segment score
   * 
   * @throws PersonalityNotFoundException
   *           if the personality is not found for given segment score
   */
  public PersonalityBeanResponse getPersonalityFromSegmentScore(String segmentScore)
      throws PersonalityNotFoundException {
    PersonalityBean pb = personalityMap.get(segmentScore);
    if (pb == null) {
      throw new PersonalityNotFoundException("No personality found for given segment score :"
          + segmentScore);
    }
    PersonalityBeanResponse personalityBeanResponse = new PersonalityBeanResponse(pb);
    personalityBeanResponse.setSegmentScore(Integer.parseInt(segmentScore));
    return personalityBeanResponse;
  }
  
}
