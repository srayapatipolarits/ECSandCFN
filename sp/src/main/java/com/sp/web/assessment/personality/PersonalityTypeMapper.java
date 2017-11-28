package com.sp.web.assessment.personality;

/**
 * @author Dax Abraham
 * 
 *         Personality type Enumerator.
 */
public enum PersonalityTypeMapper {

  Influencer("Examiner"), 
  Pioneer("Designer"), 
  Tight("Tight"), 
  Connector("Promoter"), 
  Pragmatist("Pragmatist"), 
  Peacemaker("Encourager"), 
  Visionary("Visionary"), 
  Strategist("Innovator"), 
  Instructor("Instructor"), 
  Undershift("Undershift"), 
  Ambassador("Ambassador"), 
  Scientist("Actuary"), 
  Catalyst("Motivator"), 
  Ally("Supporter"), 
  Navigator("Navigator"), 
  Tough_N_Tender("Tough_N_Tender"), 
  Invalid("Invalid"), 
  Architect("Refiner"), 
  Overshift("Overshift"), 
  Researcher("Researcher");
  
  private PersonalityType mapsTo;

  private PersonalityTypeMapper(String oldType) {
    this.mapsTo = PersonalityType.valueOf(oldType);
  }
  
  /**
   * <code>getPersonalitiyType</code> will return the mapped personality type
   * @param oldType personality for which new is to be returned
   * @return the personality type.
   */
  public static PersonalityTypeMapper getPersonalityTypeMapper(PersonalityType personalityType) {
    return getPersonalityTypeMapper(personalityType.toString());
  }
  
  /**
   * <code>getPersonalitiyType</code> will return the mapped personality type
   * @param oldType personality for which new is to be returned
   * @return the personality type.
   */
  public static PersonalityTypeMapper getPersonalityTypeMapper(String oldType) {
    for (PersonalityTypeMapper typeMapper : PersonalityTypeMapper.values()) {
      if (oldType.equalsIgnoreCase(typeMapper.getMapsTo().toString())) {
        return typeMapper;
      }
    }
    return null;

  }

  public PersonalityType getMapsTo() {
    return mapsTo;
  }
}
