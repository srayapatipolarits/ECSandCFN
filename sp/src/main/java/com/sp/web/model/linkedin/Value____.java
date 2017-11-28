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
@JsonPropertyOrder({ "id", "organization", "role" })
public class Value____ implements Serializable {
  
  private static final long serialVersionUID = 6930656863336246612L;
  @JsonProperty("id")
  private Integer id;
  @JsonProperty("organization")
  private Organization organization;
  @JsonProperty("role")
  private String role;
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
   * @return The organization
   */
  @JsonProperty("organization")
  public Organization getOrganization() {
    return organization;
  }
  
  /**
   * 
   * @param organization
   *          The organization
   */
  @JsonProperty("organization")
  public void setOrganization(Organization organization) {
    this.organization = organization;
  }
  
  /**
   * 
   * @return The role
   */
  @JsonProperty("role")
  public String getRole() {
    return role;
  }
  
  /**
   * 
   * @param role
   *          The role
   */
  @JsonProperty("role")
  public void setRole(String role) {
    this.role = role;
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
