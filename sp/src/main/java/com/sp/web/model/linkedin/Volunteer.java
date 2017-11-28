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
@JsonPropertyOrder({ "volunteerExperiences" })
public class Volunteer implements Serializable {
  
  private static final long serialVersionUID = 1233653707608104041L;
  @JsonProperty("volunteerExperiences")
  private VolunteerExperiences volunteerExperiences;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();
  
  /**
   * 
   * @return The volunteerExperiences
   */
  @JsonProperty("volunteerExperiences")
  public VolunteerExperiences getVolunteerExperiences() {
    return volunteerExperiences;
  }
  
  /**
   * 
   * @param volunteerExperiences
   *          The volunteerExperiences
   */
  @JsonProperty("volunteerExperiences")
  public void setVolunteerExperiences(VolunteerExperiences volunteerExperiences) {
    this.volunteerExperiences = volunteerExperiences;
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
