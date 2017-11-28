package com.sp.web.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({ "smartling", "countries" })
public class Countries {
  
  @JsonProperty("smartling")
  @JsonIgnore
  private Smartling smartling;
  @JsonProperty("countries")
  private List<Country> countries = new ArrayList<Country>();
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();
  
  /**
   * 
   * @return The smartling
   */
  @JsonProperty("smartling")
  @JsonIgnore
  public Smartling getSmartling() {
    return smartling;
  }
  
  /**
   * 
   * @param smartling
   *          The smartling
   */
  @JsonProperty("smartling")
  @JsonIgnore
  public void setSmartling(Smartling smartling) {
    this.smartling = smartling;
  }
  
  /**
   * 
   * @return The countries
   */
  @JsonProperty("countries")
  public List<Country> getCountries() {
    return countries;
  }
  
  /**
   * 
   * @param countries
   *          The countries
   */
  @JsonProperty("countries")
  public void setCountries(List<Country> countries) {
    this.countries = countries;
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
