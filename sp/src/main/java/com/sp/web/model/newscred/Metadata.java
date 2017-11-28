
package com.sp.web.model.newscred;

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
@JsonPropertyOrder({
    "edited",
    "cmc_published_at",
    "cmc_campaign__ss_set",
    "original_guid",
    "custom_fields_set",
    "type",
    "fulltext__b"
})
public class Metadata {

    @JsonProperty("edited")
    private String edited;
    @JsonProperty("cmc_published_at")
    private String cmcPublishedAt;
    @JsonProperty("cmc_campaign__ss_set")
    private List<String> cmcCampaignSsSet = new ArrayList<String>();
    @JsonProperty("original_guid__s")
    private String originalGuid;
    @JsonProperty("custom_fields_set")
    private List<CustomFieldsSet> customFieldsSet = new ArrayList<CustomFieldsSet>();
    @JsonProperty("type")
    private String type;
    @JsonProperty("fulltext__b")
    private String fulltextB;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The edited
     */
    @JsonProperty("edited")
    public String getEdited() {
        return edited;
    }

    /**
     * 
     * @param edited
     *     The edited
     */
    @JsonProperty("edited")
    public void setEdited(String edited) {
        this.edited = edited;
    }

    /**
     * 
     * @return
     *     The cmcPublishedAt
     */
    @JsonProperty("cmc_published_at")
    public String getCmcPublishedAt() {
        return cmcPublishedAt;
    }

    /**
     * 
     * @param cmcPublishedAt
     *     The cmc_published_at
     */
    @JsonProperty("cmc_published_at")
    public void setCmcPublishedAt(String cmcPublishedAt) {
        this.cmcPublishedAt = cmcPublishedAt;
    }

    /**
     * 
     * @return
     *     The cmcCampaignSsSet
     */
    @JsonProperty("cmc_campaign__ss_set")
    public List<String> getCmcCampaignSsSet() {
        return cmcCampaignSsSet;
    }

    /**
     * 
     * @param cmcCampaignSsSet
     *     The cmc_campaign__ss_set
     */
    @JsonProperty("cmc_campaign__ss_set")
    public void setCmcCampaignSsSet(List<String> cmcCampaignSsSet) {
        this.cmcCampaignSsSet = cmcCampaignSsSet;
    }

    /**
     * 
     * @return
     *     The originalGuid
     */
    @JsonProperty("original_guid")
    public String getOriginalGuid() {
        return originalGuid;
    }

    /**
     * 
     * @param originalGuid
     *     The original_guid
     */
    @JsonProperty("original_guid")
    public void setOriginalGuid(String originalGuid) {
        this.originalGuid = originalGuid;
    }

    /**
     * 
     * @return
     *     The customFieldsSet
     */
    @JsonProperty("custom_fields_set")
    public List<CustomFieldsSet> getCustomFieldsSet() {
        return customFieldsSet;
    }

    /**
     * 
     * @param customFieldsSet
     *     The custom_fields_set
     */
    @JsonProperty("custom_fields_set")
    public void setCustomFieldsSet(List<CustomFieldsSet> customFieldsSet) {
        this.customFieldsSet = customFieldsSet;
    }

    /**
     * 
     * @return
     *     The type
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The fulltextB
     */
    @JsonProperty("fulltext__b")
    public String getFulltextB() {
        return fulltextB;
    }

    /**
     * 
     * @param fulltextB
     *     The fulltext__b
     */
    @JsonProperty("fulltext__b")
    public void setFulltextB(String fulltextB) {
        this.fulltextB = fulltextB;
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
