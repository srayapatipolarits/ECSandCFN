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
@JsonPropertyOrder({ "courses", "honorsAwards", "languages", "publications", "volunteer" })
public class LinkedInOtherProfile implements Serializable {
  
  private static final long serialVersionUID = 217315746338495094L;
  @JsonProperty("courses")
  private Courses courses;
  @JsonProperty("honorsAwards")
  private HonorsAwards honorsAwards;
  @JsonProperty("languages")
  private Languages languages;
  @JsonProperty("publications")
  private Publications publications;
  @JsonProperty("volunteer")
  private Volunteer volunteer;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();
  
  /**
   * 
   * @return The courses.
   */
  @JsonProperty("courses")
  public Courses getCourses() {
    return courses;
  }
  
  /**
   * 
   * @param courses
   *          The courses.
   */
  @JsonProperty("courses")
  public void setCourses(Courses courses) {
    this.courses = courses;
  }
  
  /**
   * 
   * @return The honorsAwards
   */
  @JsonProperty("honorsAwards")
  public HonorsAwards getHonorsAwards() {
    return honorsAwards;
  }
  
  /**
   * 
   * @param honorsAwards
   *          The honorsAwards
   */
  @JsonProperty("honorsAwards")
  public void setHonorsAwards(HonorsAwards honorsAwards) {
    this.honorsAwards = honorsAwards;
  }
  
  /**
   * 
   * @return The languages
   */
  @JsonProperty("languages")
  public Languages getLanguages() {
    return languages;
  }
  
  /**
   * 
   * @param languages
   *          The languages
   */
  @JsonProperty("languages")
  public void setLanguages(Languages languages) {
    this.languages = languages;
  }
  
  /**
   * 
   * @return The publications
   */
  @JsonProperty("publications")
  public Publications getPublications() {
    return publications;
  }
  
  /**
   * 
   * @param publications
   *          The publications
   */
  @JsonProperty("publications")
  public void setPublications(Publications publications) {
    this.publications = publications;
  }
  
  /**
   * 
   * @return The volunteer
   */
  @JsonProperty("volunteer")
  public Volunteer getVolunteer() {
    return volunteer;
  }
  
  /**
   * 
   * @param volunteer
   *          The volunteer
   */
  @JsonProperty("volunteer")
  public void setVolunteer(Volunteer volunteer) {
    this.volunteer = volunteer;
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
