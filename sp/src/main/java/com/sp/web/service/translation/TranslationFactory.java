package com.sp.web.service.translation;

import com.sp.web.model.goal.PersonalityPracticeArea;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.translation.TranslationData;
import com.sp.web.model.tutorial.SPTutorial;
import com.sp.web.repository.translation.TranslationRepository;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.function.Function;

/**
 * TranslationFactory provides translation for the passed collection and the locale.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class TranslationFactory {
  
  /** Initializing the logger. */
  private static Logger log = Logger.getLogger(TranslationFactory.class);
  
  @Autowired
  private TranslationRepository translationRepository;
  
  @Autowired
  private TranslationHandlerService handlerService;
  
  /**
   * getTranslationMethod will return the translation for the specific class type and the id passed
   * for the given collection in english format.
   * 
   * @param <T>
   * 
   * @param id
   *          is the mongoId of the collections
   * @param locale
   *          is the locale for which data is to be fetched.
   * @param classType
   *          is the class of the collection.
   * @return the translated data.
   */
  public <T> T getTranslation(String id, String locale, Class<T> classType,
      Function<String, T> sourceSuppplier) {
    
    TranslationData translationData = translationRepository.getTranslationData(id, locale,
        classType);
    if (log.isDebugEnabled()) {
      log.debug("Translation data returned is " + translationData + ",locae " + locale
          + ", classTYpe" + classType);
    }
    T sourceData = sourceSuppplier.apply(id);
    if (translationData == null) {
      return sourceData;
    }
    
    try {
      handlerService.injectTranslableContent(translationData, sourceData, classType);
    } catch (Exception ex) {
      log.error("Error occurred while injecting the translation content into the source object "
          + id, ex);
    }
    return sourceData;
    
  }
  
  /**
   * getTranslationMethod will return the translation for the specific class type and the id passed
   * for the given collection in english format.
   * 
   * @param <T>
   * 
   * @param id
   *          is the mongoId of the collections
   * @param locale
   *          is the locale for which data is to be fetched.
   * @param classType
   *          is the class of the collection.
   * @return the translated data.
   */
  public <T> T getTranslationWithSource(String id, String locale, Class<T> classType, T sourceData) {
    
    TranslationData translationData = translationRepository.getTranslationData(id, locale,
        classType);
    if (log.isDebugEnabled()) {
      log.debug("Translation data returned is " + translationData + ",locae " + locale
          + ", classTYpe" + classType);
    }
    
    if (translationData == null) {
      return sourceData;
    }

    try {
      handlerService.injectTranslableContent(translationData, sourceData, classType);
    } catch (Exception ex) {
      log.error("Error occurred while injecting the translation content into the source object "
          + id, ex);
    }
    return sourceData;
    
  }
  
  /**
   * updateTranslationData method will save the translation back into the database
   * 
   * @param translableData
   *          translation data.
   * @param locale
   *          for which translation is to be updated.
   * @param dataType
   *          of the the class.
   */
  public void updateTranslationData(Object translableData, String locale, String dataType) {
    
    if (StringUtils.isEmpty(dataType)) {
      return;
    }
    
    switch (dataType) {
    case "goals":
      
      @SuppressWarnings("unchecked")
      Collection<SPGoal> spGoals = (Collection<SPGoal>) translableData;
      for (SPGoal goal : spGoals) {
        
        /* fetch the translation data if present in the db */
        TranslationData translationData = translationRepository.getTranslationData(goal.getId(),
            locale, SPGoal.class);
        if (translationData == null) {
          translationData = new TranslationData();
          translationData.setClassType(SPGoal.class.getName());
          translationData.setKey(goal.getId());
          translationData.setLocale(locale);
          translationData.setTranslated(true);
        }
        
        /* get the json string */
        String translatedData = handlerService.getTranslatedData(goal, SPGoal.class);
        translationData.setJsonData(translatedData);
        translationRepository.save(translationData);
      }
      break;
    case "PersonalityPracticeArea":
      @SuppressWarnings("unchecked")
      Collection<PersonalityPracticeArea> personalityPracticeArea = (Collection<PersonalityPracticeArea>) translableData;
      for (PersonalityPracticeArea personality : personalityPracticeArea) {
        
        /* fetch the translation data if present in the db */
        TranslationData translationData = translationRepository.getTranslationData(
            personality.getId(), locale, PersonalityPracticeArea.class);
        if (translationData == null) {
          translationData = new TranslationData();
          translationData.setClassType(PersonalityPracticeArea.class.getName());
          translationData.setKey(personality.getPersonalityType().toString());
          translationData.setLocale(locale);
          translationData.setTranslated(true);
        }
        
        /* get the json string */
        String translatedData = handlerService.getTranslatedData(personality,
            PersonalityPracticeArea.class);
        translationData.setJsonData(translatedData);
        translationRepository.save(translationData);
      }
      break;
    case "tutorial":
      @SuppressWarnings("unchecked")
      Collection<SPTutorial> tutorialList = (Collection<SPTutorial>) translableData;
      for (SPTutorial tutorial : tutorialList) {
        
        /* fetch the translation data if present in the db */
        TranslationData translationData = translationRepository.getTranslationData(
            tutorial.getId(), locale, SPGoal.class);
        if (translationData == null) {
          translationData = new TranslationData();
          translationData.setClassType(SPTutorial.class.getName());
          translationData.setKey(tutorial.getId());
          translationData.setLocale(locale);
          translationData.setTranslated(true);
        }
        
        /* get the json string */
        String translatedData = handlerService.getTranslatedData(tutorial, SPTutorial.class);
        translationData.setJsonData(translatedData);
        translationRepository.save(translationData);
      }
      break;
    default:
      break;
    }
  }
}
