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
@JsonPropertyOrder({ "name" })
public class Language implements Serializable {
  
  private static final long serialVersionUID = 7263776244144664589L;
  @JsonProperty("name")
  private String name;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();
  
  @JsonProperty("name")
  public String getName() {
    return name;
  }
  
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
