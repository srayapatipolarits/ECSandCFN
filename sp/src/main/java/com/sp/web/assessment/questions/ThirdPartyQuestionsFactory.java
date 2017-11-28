package com.sp.web.assessment.questions;

import com.sp.web.Constants;

/**
 * @author Dax Abraham
 * 
 *         This is the questions factory for the third party questions.
 */
public class ThirdPartyQuestionsFactory extends QuestionsFactory {
  
  private static final String QUESTIONS_XML = "thirdPartyQuestions";
  
  /**
   * This would initialize the factory and load the Questions from the file.
   */
  public ThirdPartyQuestionsFactory(String locale) {
    super(locale);
  }
  
  @Override
  protected String getQuestionsFile(String locale) {
    if (locale.equalsIgnoreCase(Constants.DEFAULT_LOCALE)) {
      return QUESTIONS_XML.concat(".xml");
    } else {
      return QUESTIONS_XML.concat("_" + locale + ".xml");
    }
  }
  
  
}
