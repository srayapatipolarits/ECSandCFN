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
@JsonPropertyOrder({ "id", "name" })
public class Organization implements Serializable {
  
  private static final long serialVersionUID = -2993297825153038445L;
  @JsonProperty("id")
  private Integer id;
  @JsonProperty("name")
  private String name;
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
   * @return The name
   */
  @JsonProperty("name")
  public String getName() {
    return name;
  }
  
  /**
   * 
   * @param name
   *          The name
   */
  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
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
