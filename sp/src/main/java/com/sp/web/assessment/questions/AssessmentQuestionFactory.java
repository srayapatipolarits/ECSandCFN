package com.sp.web.assessment.questions;

/**
 * AssessmentQuestion factory is an abstract question factory which provides the question factory
 * for the requested locale and type.
 * 
 * @author pradeepruhil
 *
 */
public interface AssessmentQuestionFactory {
  
  /**
   * getQuestionFactory method will return the question factory for the requested locale and
   * questionFactoryType.
   * 
   * @param locale
   *          of the question factory
   * @param questionFactoryType
   *          type of the question factory
   * @return the question factory
   */
  QuestionsFactory getQuestionFactory(String locale, QuestionFactoryType questionFactoryType);
  
}
