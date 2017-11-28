
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
    "provider_id",
    "guid",
    "num_items"
})
public class CollectionSet {

    @JsonProperty("provider_id")
    private String providerId;
    @JsonProperty("guid")
    private String guid;
    @JsonProperty("num_items")
    private String numItems;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The providerId
     */
    @JsonProperty("provider_id")
    public String getProviderId() {
        return providerId;
    }

    /**
     * 
     * @param providerId
     *     The provider_id
     */
    @JsonProperty("provider_id")
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

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
     *     The numItems
     */
    @JsonProperty("num_items")
    public String getNumItems() {
        return numItems;
    }

    /**
     * 
     * @param numItems
     *     The num_items
     */
    @JsonProperty("num_items")
    public void setNumItems(String numItems) {
        this.numItems = numItems;
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
