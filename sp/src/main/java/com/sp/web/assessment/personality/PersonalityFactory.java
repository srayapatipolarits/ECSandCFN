/**
 * 
 */
package com.sp.web.assessment.personality;

import static com.sp.web.Constants.KEY_DELIMITTER;

import com.sp.web.assessment.questions.TraitType;
import com.sp.web.exception.PersonalityNotFoundException;
import com.sp.web.exception.SPException;
import com.sp.web.model.User;
import com.sp.web.xml.personality.PatternDocument.Pattern;
import com.sp.web.xml.personality.PersonalityDocument;
import com.sp.web.xml.personality.PersonalityDocument.Personality;
import com.sp.web.xml.personality.RangeDocument.Range;
import com.sp.web.xml.personality.RangesDocument.Ranges;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author daxabraham
 * 
 *         This the factory class for the personality category type
 */
@Component
@Scope("singleton")
public class PersonalityFactory {

  private static final String PERSONALITY_XML = "personality.xml";

  private static final Logger LOG = Logger.getLogger(PersonalityFactory.class);

  private Personality personality;

  private PersonalityMatcher personalityMatcher;

  /**
   * Default constructor
   */
  public PersonalityFactory() {
    try {
      load();
    } catch (XmlException e) {
      LOG.fatal("Could not load personality xml from :" + PERSONALITY_XML, e);
      throw new SPException("Could not load personality xml from :" + PERSONALITY_XML, e);
    } catch (IOException e) {
      LOG.fatal("Could not load personality xml from :" + PERSONALITY_XML, e);
      throw new SPException("Could not load personality xml from :" + PERSONALITY_XML, e);
    }
  }

  /**
   * Load the personality profile from the xml file.
   * 
   * @throws IOException
   * @throws XmlException
   */
  private void load() throws XmlException, IOException {
    PersonalityDocument personalityDocument = PersonalityDocument.Factory.parse(this.getClass()
        .getClassLoader().getResourceAsStream(PERSONALITY_XML));
    personality = personalityDocument.getPersonality();

    // create the personality matcher
    loadPersonalityMatcher();

  }

  /**
   * Load the personality matcher.
   */
  private void loadPersonalityMatcher() {
    PersonalityMatcher tempMatcher = new PersonalityMatcher();
    Map<String, RangeBean> tempMatcherMap = new HashMap<String, RangeBean>();
    // iterate over all the ranges and create the matcher
    ArrayList<Integer> rangeValues;
    int i;
    int factor;
    String key;
    for (Ranges ranges : personality.getScoring().getRangesList()) {
      key = getKey(ranges);
      rangeValues = new ArrayList<Integer>();
      i = 0;
      factor = 0;
      for (Range range : ranges.getRangeList()) {
        int lowRange = range.getLow();
        int highRange = range.getHigh();
        if (i == 0) {
          factor = lowRange * (-1); // to make it a positive integer
        }
        for (int j = lowRange; j <= highRange; j++) {
          rangeValues.add(Integer.valueOf(range.getSegmentValue()));
          i++;
        }
        if ((i - 1) != (highRange + factor)) {
          LOG.fatal("Error loading the the personality factor mismatch :" + key + ": highRange:"
              + highRange + ": out of sync !!!");
          throw new SPException("Error loading the the personality factor mismatch :" + key
              + ": highRange:" + highRange + ": out of sync !!!");
        }
      }
      RangeBean rangeBean = new RangeBean(factor, rangeValues);
      tempMatcherMap.put(key, rangeBean);
    }
    tempMatcher.setMatcherMap(tempMatcherMap);
    if (LOG.isDebugEnabled()) {
      LOG.debug("Loaded matcher map :" + tempMatcher.getMatcherMap());
    }
    // load the patterns
    Map<String, PersonalityBean> tempPersonalityMap = new HashMap<String, PersonalityBean>();
    for (Pattern pattern : personality.getPatterns().getPatternList()) {
      int scoreLow = pattern.getScoreLow();
      int scoreHigh = pattern.getScoreHigh();
      PersonalityBean personalityBean = new PersonalityBean(pattern);
      for (i = scoreLow; i <= scoreHigh; i++) {
        key = i + "";
        if (tempPersonalityMap.get(key) == null) {
          tempPersonalityMap.put(key, personalityBean);
          if (LOG.isDebugEnabled()) {
            // LOG.debug("Added personality bean to map key:"+key+": Bean:"+personalityBean);
          }
        } else {
          LOG.fatal("Duplicate score value found :" + i + ": in pattern :" + pattern);
          throw new SPException("Duplicate score value found :" + i + ": in pattern :" + pattern);
        }
      }
    }
    tempMatcher.setPersonalityMap(tempPersonalityMap);

    this.personalityMatcher = tempMatcher;
  }

  /**
   * @param range
   *          - @see{@link Ranges} to derive the key from
   * @return the key from @see{@link Ranges}
   */
  private String getKey(Ranges range) {
    StringBuffer key = new StringBuffer();
    key.append(TraitType.valueOf(range.getTrait())).append(KEY_DELIMITTER)
        .append(RangeType.valueOf(range.getType()));
    return key.toString();
  }

  /**
   * @return the personality
   */
  public Personality getPersonality() {
    return personality;
  }

  /**
   * @param personality
   *          the personality to set
   */
  public void setPersonality(Personality personality) {
    this.personality = personality;
  }

  /**
   * @return the reference to the PersonalityMatcher
   */
  public PersonalityMatcher getPersonalityMatcher() {
    return personalityMatcher;
  }

  /**
   * @param segmentScore
   *          - the segment score
   * @return the personality bean for the given segment score
   * 
   * @throws PersonalityNotFoundException
   *           if the personality is not found for given segment score
   */
  public PersonalityBeanResponse getPersonalityFromSegmentScore(String segmentScore)
      throws PersonalityNotFoundException {
    return personalityMatcher.getPersonalityFromSegmentScore(segmentScore);
  }
  
  /**
   * Fixes the user personality.
   * 
   * @param user
   *          - use personality to fix
   *          
   * @return 
   *        - true if the personality was updated         
   */
  public boolean fixPersonality(User user) {
    if (user.getAnalysis() == null || user.getAnalysis().getPersonality() == null) {
      return false;
    }
    return fixPerosnality(user.getAnalysis().getPersonality());
  }

  /**
   * Fixes the user personality.
   * 
   * @param personality
   *          - use personality to fix
   *          
   * @return 
   *        - true if the personality was updated         
   */
  public boolean fixPerosnality(HashMap<RangeType, PersonalityBeanResponse> personality) {
    
    PersonalityBeanResponse primaryPersonalityBeanResponse = personality.get(RangeType.Primary);
    PersonalityBeanResponse underPressurePersonalityBeanResponse = personality
        .get(RangeType.UnderPressure);
    PersonalityBeanResponse othersPersonalityBeanResponse = personality
        .get(RangeType.PerceivedByOthers);
    
    boolean isPersonalityChagned = false;
    
    PersonalityType personalityType = primaryPersonalityBeanResponse.getPersonalityType();
    if (personalityType == PersonalityType.Undershift
        || personalityType == PersonalityType.Overshift || personalityType == PersonalityType.Tight) {
      personality.put(RangeType.Primary, underPressurePersonalityBeanResponse);
      personality.put(RangeType.UnderPressure, othersPersonalityBeanResponse);
      isPersonalityChagned = true;
    }
    
    // checking the under pressure
    personalityType = underPressurePersonalityBeanResponse.getPersonalityType();
    if (personalityType == PersonalityType.Undershift
        || personalityType == PersonalityType.Overshift || personalityType == PersonalityType.Tight) {
      personality.put(RangeType.UnderPressure, othersPersonalityBeanResponse);
      isPersonalityChagned = true;
    }
    return isPersonalityChagned;
  }
}
