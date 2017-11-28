
package com.sp.web.model.newscred;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "guid",
    "score",
    "topic_classification",
    "name",
    "topic_subclassification"
})
public class TopicSet {

    @JsonProperty("guid")
    private String guid;
    @JsonProperty("score")
    private String score;
    @JsonProperty("topic_classification")
    private TopicClassification topicClassification;
    @JsonProperty("name")
    private String name;
    @JsonProperty("topic_subclassification")
    private TopicSubclassification topicSubclassification;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The guid
     */
    @JsonProperty("guid")
    public String getGuid() {
        return guid;
    }

    /**
     * 
     * @param guid
     *     The guid
     */
    @JsonProperty("guid")
    public void setGuid(String guid) {
        this.guid = guid;
    }

    /**
     * 
     * @return
     *     The score
     */
    @JsonProperty("score")
    public String getScore() {
        return score;
    }

    /**
     * 
     * @param score
     *     The score
     */
    @JsonProperty("score")
    public void setScore(String score) {
        this.score = score;
    }

    /**
     * 
     * @return
     *     The topicClassification
     */
    @JsonProperty("topic_classification")
    public TopicClassification getTopicClassification() {
        return topicClassification;
    }

    /**
     * 
     * @param topicClassification
     *     The topic_classification
     */
    @JsonProperty("topic_classification")
    public void setTopicClassification(TopicClassification topicClassification) {
        this.topicClassification = topicClassification;
    }

    /**
     * 
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The topicSubclassification
     */
    @JsonProperty("topic_subclassification")
    public TopicSubclassification getTopicSubclassification() {
        return topicSubclassification;
    }

    /**
     * 
     * @param topicSubclassification
     *     The topic_subclassification
     */
    @JsonProperty("topic_subclassification")
    public void setTopicSubclassification(TopicSubclassification topicSubclassification) {
        this.topicSubclassification = topicSubclassification;
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
