package com.sp.web.model.newscred;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sp.web.utils.CustomJsonDateDeserializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @JsonPropertyOrder({ "category", "topic_set", "image_set", "description", "author_set", "source", "guid", "metadata",
// "created_at" })
public class Content {

  @JsonProperty("category")
  private Category category;
  @JsonProperty("topic_set")
  private List<TopicSet> topicSet = new ArrayList<TopicSet>();
  @JsonProperty("image_set")
  private List<ImageSet> imageSet = new ArrayList<ImageSet>();
  @JsonProperty("description")
  private String description;
  @JsonProperty("author_set")
  private List<AuthorSet> authorSet = new ArrayList<AuthorSet>();
  @JsonProperty("source")
  private Source_ source;
  @JsonProperty("guid")
  private String guid;
  @JsonProperty("metadata")
  private Metadata metadata;

  @JsonDeserialize(using=CustomJsonDateDeserializer.class)
  private Date created_at;

  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  /**
   * 
   * @return The category
   */
  @JsonProperty("category")
  public Category getCategory() {
    return category;
  }

  /**
   * 
   * @param category
   *          The category
   */
  @JsonProperty("category")
  public void setCategory(Category category) {
    this.category = category;
  }

  /**
   * 
   * @return The topicSet
   */
  @JsonProperty("topic_set")
  public List<TopicSet> getTopicSet() {
    return topicSet;
  }

  /**
   * 
   * @param topicSet
   *          The topic_set
   */
  @JsonProperty("topic_set")
  public void setTopicSet(List<TopicSet> topicSet) {
    this.topicSet = topicSet;
  }

  /**
   * 
   * @return The imageSet
   */
  @JsonProperty("image_set")
  public List<ImageSet> getImageSet() {
    return imageSet;
  }

  /**
   * 
   * @param imageSet
   *          The image_set
   */
  @JsonProperty("image_set")
  public void setImageSet(List<ImageSet> imageSet) {
    this.imageSet = imageSet;
  }

  /**
   * 
   * @return The description
   */
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  /**
   * 
   * @param description
   *          The description
   */
  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * 
   * @return The authorSet
   */
  @JsonProperty("author_set")
  public List<AuthorSet> getAuthorSet() {
    return authorSet;
  }

  /**
   * 
   * @param authorSet
   *          The author_set
   */
  @JsonProperty("author_set")
  public void setAuthorSet(List<AuthorSet> authorSet) {
    this.authorSet = authorSet;
  }

  /**
   * 
   * @return The source
   */
  @JsonProperty("source")
  public Source_ getSource() {
    return source;
  }

  /**
   * 
   * @param source
   *          The source
   */
  @JsonProperty("source")
  public void setSource(Source_ source) {
    this.source = source;
  }

  /**
   * 
   * @return The guid
   */
  @JsonProperty("guid")
  public String getGuid() {
    return guid;
  }

  /**
   * 
   * @param guid
   *          The guid
   */
  @JsonProperty("guid")
  public void setGuid(String guid) {
    this.guid = guid;
  }

  /**
   * @param created_at
   *          the created_at to set
   */
  public void setCreated_at(Date created_at) {
    this.created_at = created_at;
  }

  /**
   * @return the created_at
   */
  public Date getCreated_at() {
    return created_at;
  }

  /**
   * 
   * @return The metadata
   */
  @JsonProperty("metadata")
  public Metadata getMetadata() {
    return metadata;
  }

  /**
   * 
   * @param metadata
   *          The metadata
   */
  @JsonProperty("metadata")
  public void setMetadata(Metadata metadata) {
    this.metadata = metadata;
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
