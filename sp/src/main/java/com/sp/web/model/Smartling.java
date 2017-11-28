
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
@JsonPropertyOrder({
    "translate_paths"
})
public class Smartling {

    @JsonProperty("translate_paths")
    private List<TranslatePath> translatePaths = new ArrayList<TranslatePath>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The translatePaths
     */
    @JsonProperty("translate_paths")
    public List<TranslatePath> getTranslatePaths() {
        return translatePaths;
    }

    /**
     * 
     * @param translatePaths
     *     The translate_paths
     */
    @JsonProperty("translate_paths")
    public void setTranslatePaths(List<TranslatePath> translatePaths) {
        this.translatePaths = translatePaths;
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
