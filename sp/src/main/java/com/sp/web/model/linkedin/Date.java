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
@JsonPropertyOrder({ "day", "month", "year" })
public class Date implements Serializable {
  
  private static final long serialVersionUID = -5566569880318467878L;
  @JsonProperty("day")
  private Integer day;
  @JsonProperty("month")
  private Integer month;
  @JsonProperty("year")
  private Integer year;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();
  
  @JsonProperty("day")
  public Integer getDay() {
    return day;
  }
  
  @JsonProperty("day")
  public void setDay(Integer day) {
    this.day = day;
  }
  
  @JsonProperty("month")
  public Integer getMonth() {
    return month;
  }
  
  @JsonProperty("month")
  public void setMonth(Integer month) {
    this.month = month;
  }
  
  @JsonProperty("year")
  public Integer getYear() {
    return year;
  }
  
  @JsonProperty("year")
  public void setYear(Integer year) {
    this.year = year;
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
