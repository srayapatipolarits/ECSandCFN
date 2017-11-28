
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
    "collection_set"
})
public class NewsCredCollections {

    @JsonProperty("collection_set")
    private List<CollectionSet> collectionSet = new ArrayList<CollectionSet>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The collectionSet
     */
    @JsonProperty("collection_set")
    public List<CollectionSet> getCollectionSet() {
        return collectionSet;
    }

    /**
     * 
     * @param collectionSet
     *     The collection_set
     */
    @JsonProperty("collection_set")
    public void setCollectionSet(List<CollectionSet> collectionSet) {
        this.collectionSet = collectionSet;
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
