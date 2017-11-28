
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
    "num_found",
    "collection_item_set"
})
public class NewsCred {

    @JsonProperty("num_found")
    private Integer numFound;
    @JsonProperty("collection_item_set")
    private List<CollectionItemSet> collectionItemSet = new ArrayList<CollectionItemSet>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The numFound
     */
    @JsonProperty("num_found")
    public Integer getNumFound() {
        return numFound;
    }

    /**
     * 
     * @param numFound
     *     The num_found
     */
    @JsonProperty("num_found")
    public void setNumFound(Integer numFound) {
        this.numFound = numFound;
    }

    /**
     * 
     * @return
     *     The collectionItemSet
     */
    @JsonProperty("collection_item_set")
    public List<CollectionItemSet> getCollectionItemSet() {
        return collectionItemSet;
    }

    /**
     * 
     * @param collectionItemSet
     *     The collection_item_set
     */
    @JsonProperty("collection_item_set")
    public void setCollectionItemSet(List<CollectionItemSet> collectionItemSet) {
        this.collectionItemSet = collectionItemSet;
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
