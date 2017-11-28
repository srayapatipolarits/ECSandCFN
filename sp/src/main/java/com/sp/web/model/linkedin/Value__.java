package com.sp.web.model.linkedin;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({ "id", "language" })
public class Value__ implements Serializable {
  
  private static final long serialVersionUID = -5881221782305770043L;
  @JsonProperty("id")
  private Integer id;
  @JsonProperty("language")
  private Language language;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();
  
  /**
   * 
   * @return The id
   */
  @JsonProperty("id")
  public Integer getId() {
    return id;
  }
  
  /**
   * 
   * @param id
   *          The id
   */
  @JsonProperty("id")
  public void setId(Integer id) {
    this.id = id;
  }
  
  /**
   * 
   * @return The language
   */
  @JsonProperty("language")
  public Language getLanguage() {
    return language;
  }
  
  /**
   * 
   * @param language
   *          The language
   */
  @JsonProperty("language")
  public void setLanguage(Language language) {
    this.language = language;
  }
  
  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }
  
  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
  
}
