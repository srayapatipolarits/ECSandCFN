package com.sp.web.service.translation;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sp.web.model.goal.DevelopmentStrategy;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.translation.TranslationData;
import com.sp.web.utils.JsonView;
import com.sp.web.utils.JsonViewSerializer;
import com.sp.web.utils.Match;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * TranslableHandlerService class will map the tranlsable data to the parent object.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class TranslationHandlerService {
  
  private static final Logger log = Logger.getLogger(TranslationHandlerService.class);
  private ObjectMapper objectMapper = null;
  
  public TranslationHandlerService() {
    
    objectMapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addSerializer(JsonView.class, new JsonViewSerializer());
    objectMapper.registerModule(module);
    
  }
  
  /**
   * injectTranslabelContent will inject the transalbel content in to the source data.
   * 
   * @param <T>
   * 
   * @param translationData
   *          data containing the translated content.
   * @param sourceData
   *          source object
   * @param sourceClass
   *          source class.
   * @throws JsonParseException
   *           in case exception occurred while parsing the data.
   * @throws JsonMappingException
   *           json mapping.
   * @throws IOException
   *           io exception.
   */
  public <T> void injectTranslableContent(TranslationData translationData, T sourceData,
      Class<T> sourceClass) throws JsonParseException, JsonMappingException, IOException {
    
    /* Get the json data */
    String jsonData = translationData.getJsonData();
    /* conver the json data into the object */
    ObjectMapper objectMapper = new ObjectMapper();
    Object readValue = objectMapper.readValue(jsonData, sourceClass);
    
    Field[] declaredFields = sourceClass.getDeclaredFields();
    
    String[] ignoreProperties = ArrayUtils.EMPTY_STRING_ARRAY;
    for (Field field : declaredFields) {
      if (!field.isAnnotationPresent(Translable.class)) {
        
        /*
         * get the same field from the source data and get the translable content from the
         * transalble object and set it in the source dat
         */
        ignoreProperties = ArrayUtils.add(ignoreProperties, field.getName());
        
      }
    }
    BeanUtils.copyProperties(readValue, sourceData, ignoreProperties);
  }
  
  public TranslationData getEnglishTranslationData(Object sourceObject) {
    TranslationData translationData = null;
    
    String[] includeProperties = ArrayUtils.EMPTY_STRING_ARRAY;
    for (Field field : sourceObject.getClass().getDeclaredFields()) {
      if (field.isAnnotationPresent(Translable.class)) {
        
        /*
         * get the same field from the source data and get the translable content from the
         * transalble object and set it in the source dat
         */
        includeProperties = ArrayUtils.add(includeProperties, field.getName());
        
      }
    }
    includeProperties = ArrayUtils.add(includeProperties, "dsDescription");
    
    try {
      String serialized = objectMapper.writeValueAsString(JsonView.with(sourceObject)
          .onClass(sourceObject.getClass(), Match.match().exclude("*").include(includeProperties))
          .onClass(DevelopmentStrategy.class, Match.match().exclude("videoUrl", "imageUrl")));
      translationData = new TranslationData();
      translationData.setJsonData(serialized);
      translationData.setKey(((SPGoal) sourceObject).getId());
      translationData.setLocale("en_US");
      translationData.setTranslated(false);
      translationData.setClassType(sourceObject.getClass().getName());
    } catch (JsonProcessingException e) {
      log.error("error occurred while getting the content.", e);
    }
    return translationData;
  }
  
  /**
   * getTranslatedData method will take the translatedContent object and its class and will convert
   * the data into the serialized json string.
   * 
   * @param translatedContent
   *          transalated Contet.
   * @param dataType
   *          class type
   * @return the json data string.
   */
  public String getTranslatedData(Object translatedContent, Class<?> dataType) {
    
    String[] includeProperties = ArrayUtils.EMPTY_STRING_ARRAY;
    String[] excludeProperties = ArrayUtils.EMPTY_STRING_ARRAY;
    for (Field field : translatedContent.getClass().getDeclaredFields()) {
      if (field.isAnnotationPresent(Translable.class)) {
        
        /*
         * get the same field from the source data and get the translable content from the
         * transalble object and set it in the source dat
         */
        includeProperties = ArrayUtils.add(includeProperties, field.getName());
        
      }else{
        excludeProperties= ArrayUtils.add(excludeProperties, field.getName());
      }
    }
    includeProperties = ArrayUtils.add(includeProperties, "dsDescription");
    includeProperties = ArrayUtils.add(includeProperties, "videoUrl");
    includeProperties = ArrayUtils.add(includeProperties, "imageUrl");
//    
    try {
      String serialized = objectMapper.writeValueAsString(JsonView.with(translatedContent).onClass(
          translatedContent.getClass(), Match.match().exclude(excludeProperties).include(includeProperties)));
      return serialized;
    } catch (JsonProcessingException e) {
      log.error("error occurred while getting the content.", e);
    }
    return null;
    
  }
}
