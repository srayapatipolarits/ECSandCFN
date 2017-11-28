package com.sp.web.relationship;

import com.sp.web.Constants;
import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.dto.PercentageMatchDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.User;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         This is the Relationship report manager class that generates the report for the relations
 *         between users.
 */
@Component
public class RelationshipReportManager {

  private static final Logger log = Logger.getLogger(RelationshipReportManager.class);
  
  private static final String PREFIX = "compare.";
  private static final String SEPARATOR = ".";
  private static final String TITLE = "title";
  
  /**
   * Method to get the relationship title for the given user.
   * 
   * @param user
   *          - user
   * @return
   *        the relationship title
   */
  public String getProfileStatement(User user) {
    // send the text back
    return MessagesHelper.genderNormalizeFromKey(PREFIX + getPersonalityType(user) + SEPARATOR
        + TITLE, user);
  }

  /**
   * Method to generate the comparison report between the given users.
   * 
   * @param user1
   *          - user1 to compare
   * @param user2
   *          - user2 to compare
   * @return - the comparison report
   */
//  public Map<String, CompareReport> getCompareReport(User user1, User user2) {
//    
//  }

  /**
   * Method to generate the comparison report between the given users.     
   *          - user1
   * @param user2
   *          - user2
   * @param user1RangeType
   *          - user1RangeType
   * @param user2RangeType
   *          - user2RangeType
   * @return
   *      the comparison report
   */
  public Map<String, CompareReport> getCompareReport(User user1, User user2,
      RangeType user1RangeType, RangeType user2RangeType, Locale locale) {
    // user1 personality type
    final PersonalityType user1PersonalityType = getPersonalityType(user1, user1RangeType);
    // user1 personality type
    final PersonalityType user2PersonalityType = getPersonalityType(user2, user2RangeType);

    final HashMap<String, CompareReport> respMap = new HashMap<String, CompareReport>();

    final String[] primaryGenderReplacementText = MessagesHelper.getGenderTextArray(locale,
        user1.getGender());
    
    final String[] secondaryGenderReplacementText = MessagesHelper.getGenderTextArray(locale,
        user2.getGender());
    
    final String primaryFirstName = user1.getFirstName();
    final String secondaryFirstName = user2.getFirstName();
    
    // primary compare report
    CompareReport reportPrimary = new CompareReport();
    reportPrimary.setTitle(replaceName(MessagesHelper.getRelationshipMessage(PREFIX + user1PersonalityType + SEPARATOR
        + TITLE, primaryGenderReplacementText, locale), primaryFirstName, secondaryFirstName));
    reportPrimary.setEffort(replaceName(MessagesHelper.getRelationshipMessage(
        getKey(user1PersonalityType, user2PersonalityType, ".primary.effort"),
        secondaryGenderReplacementText, locale), secondaryFirstName, primaryFirstName));
    reportPrimary.setAvoid(replaceName(MessagesHelper.getRelationshipMessage(
        getKey(user1PersonalityType, user2PersonalityType, ".primary.avoid"),
        secondaryGenderReplacementText, locale), secondaryFirstName, primaryFirstName));
    reportPrimary.setPersonalityPrimary(getMessage(user1, RangeType.Primary, locale));
    reportPrimary.setPersonalityUnderPressure(getMessage(user1, RangeType.UnderPressure, locale));
    
    respMap.put(Constants.PARAM_RELATIONSHIP_MANAGER_REPORT_PRIMARY, reportPrimary);
    
    CompareReport reportSecondary = new CompareReport();
    reportSecondary.setTitle(replaceName(MessagesHelper.getRelationshipMessage(PREFIX + user2PersonalityType + SEPARATOR
        + TITLE, secondaryGenderReplacementText, locale), secondaryFirstName, primaryFirstName));
    reportSecondary.setEffort(replaceName(MessagesHelper.getRelationshipMessage(
        getKey(user1PersonalityType, user2PersonalityType, ".secondary.effort"),
        primaryGenderReplacementText, locale), primaryFirstName, secondaryFirstName));
    reportSecondary.setAvoid(replaceName(MessagesHelper.getRelationshipMessage(
        getKey(user1PersonalityType, user2PersonalityType, ".secondary.avoid"),
        primaryGenderReplacementText, locale), primaryFirstName, secondaryFirstName));
    reportSecondary.setPersonalityPrimary(getMessage(user2, RangeType.Primary, locale));
    reportSecondary.setPersonalityUnderPressure(getMessage(user2, RangeType.UnderPressure, locale));

    respMap.put(Constants.PARAM_RELATIONSHIP_MANAGER_REPORT_SECONDARY, reportSecondary);

    return respMap;
  }

  private String getMessage(User user1, RangeType rangeType, Locale locale) {
    String personalityTypeStr = getPersonalityType(user1,
        rangeType) + "";
    try {
      return MessagesHelper.getMessage(personalityTypeStr, locale);
    } catch (Exception e) {
      log.warn("Error getting the profile message.", e);
    }
    return personalityTypeStr;
  }
  
  /**
   * Get the default primary personality type.
   * 
   * @param user
   *          - user
   * @return
   *      the primary personality type
   */
  public PersonalityType getPersonalityType(User user) {
    return getPersonalityType(user, RangeType.Primary);
  }

  /**
   * Gets the personality type for the given user.
   * 
   * @param user
   *          - user
   * @param rangeType
   *          - personality type
   * @return
   *        the personality type
   */
  public PersonalityType getPersonalityType(User user, RangeType rangeType) {
    return Optional
        .ofNullable(user.getAnalysis())
        .orElseThrow(
            () -> new InvalidRequestException("User :" + user.getEmail()
                + ": assessment not completed !!!")).getPersonality().get(rangeType)
        .getPersonalityType();
  }

  /**
   * Replace the first name in the text.
   * 
   * @param message
   *            - message
   * @param firstName
   *            - first name
   * @return
   *      the text with name replaced
   */
  private String replaceName(String message, String firstName, String firstName2) {
    return message.replaceAll("\\[Name\\]", firstName).replaceAll("\\[Name2\\]", firstName2);
  }

  /**
   * Gets the key in the relationship messages properties for the given personality types.
   * 
   * @param user1PersonalityType
   *          - user1 personality
   * @param user2PersonalityType
   *          - user2 personality
   * @param suffix
   *          - key suffix 
   * @return - key for relationship message properties
   */
  private String getKey(PersonalityType user1PersonalityType, PersonalityType user2PersonalityType, String suffix) {
    return PREFIX + user1PersonalityType + SEPARATOR + user2PersonalityType + suffix;
  }

  /**
   * Factory method to get the comparison chart information.
   * 
   * @param user1
   *          - user 1
   * @param user2
   *          - user 2
   * @return
   *      the response to the get compare request
   */
  public Map<String, ? extends Object> getChartCompareReport(User user1, User user2) {
    
    // get the user analysis
    AnalysisBean analysis1 = user1.getAnalysis();
    Assert.notNull(analysis1, "User analysis not found for first user.");

    // get the second users analysis
    AnalysisBean analysis2 = user2.getAnalysis();
    Assert.notNull(analysis2, "User analysis not found for second user.");
    
    HashMap<String, Object> responseMap = new HashMap<String, Object>();
    final PercentageMatchDTO matchDTO = new PercentageMatchDTO();

    // adding the analysis to the response
    responseMap.put(Constants.PARAM_ANALYSIS + "1", analysis1);
    responseMap.put(Constants.PARAM_ANALYSIS + "2", analysis2);
    
    // calculate the percentages
    // primary personality percentages
    matchDTO.setPrimaryPersonality(calculatePersonalityMatch(analysis1, analysis2,
        RangeType.Primary));
    
    // under pressure personality percentages
    matchDTO.setUnderPressurePersonality(calculatePersonalityMatch(analysis1, analysis2,
        RangeType.UnderPressure));
    
    // processing blue print percentages
    matchDTO.setProcessingBluePrint(calculateTraitsMatch(analysis1.getProcessing(), analysis2.getProcessing()));
    
    // conflict management percentages
    matchDTO.setConflictManagement(calculateTraitsMatch(analysis1.getConflictManagement(),
        analysis2.getConflictManagement()));
    
    // fundamental needs
    matchDTO.setFundamentalNeeds(calculateTraitsMatch(analysis1.getFundamentalNeeds(),
        analysis2.getFundamentalNeeds()));
    
    // learning style
    matchDTO.setLeanringStyle(calculateTraitsMatch(analysis1.getLearningStyle(),
        analysis2.getLearningStyle()));
    
    // decision making
    matchDTO.setDecisionMaking(calculateTraitsMatch(analysis1.getDecisionMaking(),
        analysis2.getDecisionMaking()));
    
    // Motivation 
    BigDecimal total = calculateTraitsMatch(analysis1.getMotivationHow(), analysis2.getMotivationHow());
    total = total.add(calculateTraitsMatch(analysis1.getMotivationWhat(), analysis2.getMotivationWhat()));
    total = total.add(calculateTraitsMatch(analysis1.getMotivationWhy(), analysis2.getMotivationWhy()));
    total = total.divide(new BigDecimal(3), 0, Constants.ROUNDING_MODE);
    matchDTO.setMotivation(total);
    
    
    responseMap.put(Constants.PARAM_MATCH_PERCENT, matchDTO);
    return responseMap;
  }

  /**
   * Calculate the percentages for the given trait types.
   * 
   * @param traitTypeMap1
   *            - trait type map 1
   * @param traitTypeMap2
   *            - trait type map 2
   * @return
   *      the percentage match
   */
  private BigDecimal calculateTraitsMatch(Map<TraitType, BigDecimal> traitTypeMap1,
      Map<TraitType, BigDecimal> traitTypeMap2) {
    double total = 0;
    Set<TraitType> keySet = traitTypeMap1.keySet();
    for (TraitType traitType : keySet) {
      BigDecimal value1 = traitTypeMap1.get(traitType);
      BigDecimal value2 = traitTypeMap2.get(traitType);
      total += Math.abs(value1.doubleValue() - value2.doubleValue());
    }
    BigDecimal totalPercentage = new BigDecimal((100 - (total / keySet.size()))).setScale(0,
        Constants.ROUNDING_MODE);
    return totalPercentage;
  }

  /**
   * Calculate the personality match.
   * 
   * @param analysis1
   *          - analysis 1
   * @param analysis2
   *          - analysis 2
   * @param rangeType
   *          - the range type
   * @return
   *          the personality percentage match
   */
  private BigDecimal calculatePersonalityMatch(AnalysisBean analysis1, AnalysisBean analysis2,
      RangeType rangeType) {
    PersonalityBeanResponse personality1 = analysis1.getPersonality().get(rangeType);
    PersonalityBeanResponse personality2 = analysis2.getPersonality().get(rangeType);
    
    BigDecimal[] segmentGraph1 = personality1.getSegmentGraph();
    BigDecimal[] segmentGraph2 = personality2.getSegmentGraph();
    
    double total = 0;
    int length = segmentGraph1.length;
    for (int i = 0; i < length; i++) {
      total += Math.abs((segmentGraph1[i].doubleValue() - segmentGraph2[i].doubleValue()));
    }
    BigDecimal totalPercentage = new BigDecimal(1 - (total / length)).movePointRight(2).setScale(0,
        Constants.ROUNDING_MODE);
    return totalPercentage;
  }
}
