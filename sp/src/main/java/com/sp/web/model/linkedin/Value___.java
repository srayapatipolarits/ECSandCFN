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
@JsonPropertyOrder({ "date", "id", "title" })
public class Value___ implements Serializable {
  
  private static final long serialVersionUID = -1574488415657948587L;
  @JsonProperty("date")
  private Date date;
  @JsonProperty("id")
  private Integer id;
  @JsonProperty("title")
  private String title;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();
  
  /**
   * 
   * @return The date
   */
  @JsonProperty("date")
  public Date getDate() {
    return date;
  }
  
  /**
   * 
   * @param date
   *          The date
   */
  @JsonProperty("date")
  public void setDate(Date date) {
    this.date = date;
  }
  
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
   * @return The title
   */
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }
  
  /**
   * 
   * @param title
   *          The title
   */
  @JsonProperty("title")
  public void setTitle(String title) {
    this.title = title;
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
