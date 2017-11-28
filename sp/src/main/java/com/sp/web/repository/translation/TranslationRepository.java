package com.sp.web.repository.translation;

import com.sp.web.model.translation.TranslationData;
import com.sp.web.repository.generic.GenericMongoRepository;

/**
 * TranslationRepository interface provides the sevice for fetching the transaltion data.
 * 
 * @author pradeepruhil
 *
 */
public interface TranslationRepository extends GenericMongoRepository<TranslationData> {
  
  /**
   * getTranslationData for the passed locale and the id of the tranlsation data
   * 
   * @param id
   *          of the collection id in english format.
   * @param locale
   *          in which transaltion data is to be retrieved.
   * @param classType
   *          of the collection for which data is to be retreived.
   * @return the locale.
   */
  public TranslationData getTranslationData(String id, String locale, Class<?> classType);
}
