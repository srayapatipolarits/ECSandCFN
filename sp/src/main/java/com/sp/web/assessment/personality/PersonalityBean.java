/**
 * 
 */
package com.sp.web.assessment.personality;

import com.sp.web.xml.personality.PatternDocument.Pattern;

import java.io.Serializable;

/**
 * @author daxabraham
 * 
 *         The bean class for the personality type
 */
public class PersonalityBean implements Serializable {

  /**
   * Serial version UID.
   */
  private static final long serialVersionUID = 6364961997709886165L;

  private boolean isValid;
  private PersonalityType personalityType;

  /**
   * @param pattern
   *          - pattern to use for creating the personality bean
   */
  public PersonalityBean(Pattern pattern) {
    isValid = Boolean.parseBoolean(pattern.getValid());
    personalityType = PersonalityType.valueOf(pattern.getName());
  }

  /**
   * Default constructor
   */
  public PersonalityBean() {
  }

  /**
   * @return the isValid
   */
  public boolean isValid() {
    return isValid;
  }

  /**
   * @param isValid
   *          the isValid to set
   */
  public void setValid(boolean isValid) {
    this.isValid = isValid;
  }

  /**
   * @return the personalityType
   */
  public PersonalityType getPersonalityType() {
    return personalityType;
  }

  /**
   * @param personalityType
   *          the personalityType to set
   */
  public void setPersonalityType(PersonalityType personalityType) {
    this.personalityType = personalityType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return isValid + ":" + personalityType;
  }

}
