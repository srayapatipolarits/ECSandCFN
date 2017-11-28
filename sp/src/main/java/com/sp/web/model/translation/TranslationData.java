package com.sp.web.model.translation;

/**
 * Model for holding the translation data for the collection.
 * 
 * @author pradeepruhil
 *
 */
public class TranslationData {
  
  private String id;
  
  private String key;
  
  private String locale;
  
  private String jsonData;
  
  private String classType;
  
  private boolean translated;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getKey() {
    return key;
  }
  
  public void setKey(String key) {
    this.key = key;
  }
  
  public String getLocale() {
    return locale;
  }
  
  public void setLocale(String locale) {
    this.locale = locale;
  }
  
  public String getJsonData() {
    return jsonData;
  }
  
  public void setJsonData(String jsonData) {
    this.jsonData = jsonData;
  }
  
  public void setClassType(String classType) {
    this.classType = classType;
  }
  
  public String getClassType() {
    return classType;
  }
  
  public boolean isTranslated() {
    return translated;
  }
  
  public void setTranslated(boolean translated) {
    this.translated = translated;
  }
  
}
