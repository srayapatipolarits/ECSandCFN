package com.sp.web.assessment.questions;

import com.sp.web.Constants;
import com.sp.web.utils.LocaleHelper;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * AssessmentQuestionFactoryImpl is the implementation class. It provides the implementation for the
 * assessment question factoy.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class AssessmentQuestionFactoryImpl implements AssessmentQuestionFactory {
  
  /**
   * QuestionFacotry map for each type and localized.
   */
  private Map<String, QuestionsFactory> questionFactoryMap = new HashMap<String, QuestionsFactory>();
  
  /**
   * getQuestionFactory method will return the quesiton factory for the requested locale.
   */
  @Override
  public QuestionsFactory getQuestionFactory(String locale, QuestionFactoryType questionFactoryType) {
    QuestionsFactory questionsFactory = null;
    if (StringUtils.isEmpty(locale)) {
      locale = Constants.DEFAULT_LOCALE;
    }

    locale = LocaleHelper.isSupported(locale);
    switch (questionFactoryType) {
    case SPAssessmentQuestion:
      
      questionsFactory = questionFactoryMap.get(locale + questionFactoryType);
      if (questionsFactory == null) {
        questionsFactory = new QuestionsFactory(locale);
        questionFactoryMap.put(locale + questionFactoryType, questionsFactory);
      }
      break;
    case ThirdPartyQuestion:
      questionsFactory = questionFactoryMap.get(locale + questionFactoryType);
      if (questionsFactory == null) {
        questionsFactory = new ThirdPartyQuestionsFactory(locale);
        questionFactoryMap.put(locale + questionFactoryType, questionsFactory);
      }
      
      break;
    default:
      break;
    }
    return questionsFactory;
  }
}
