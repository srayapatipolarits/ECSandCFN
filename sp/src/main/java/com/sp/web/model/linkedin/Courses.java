
package com.sp.web.model.linkedin;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "_total",
    "values"
})
public class Courses implements Serializable{

  private static final long serialVersionUID = 6871650789540664374L;
    @JsonProperty("_total")
    private Integer Total;
    @JsonProperty("values")
    private List<Value> values = new ArrayList<Value>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The Total
     */
    @JsonProperty("_total")
    public Integer getTotal() {
        return Total;
    }

    /**
     * 
     * @param Total
     *     The _total
     */
    @JsonProperty("_total")
    public void setTotal(Integer Total) {
        this.Total = Total;
    }

    /**
     * 
     * @return
     *     The values
     */
    @JsonProperty("values")
    public List<Value> getValues() {
        return values;
    }

    /**
     * 
     * @param values
     *     The values
     */
    @JsonProperty("values")
    public void setValues(List<Value> values) {
        this.values = values;
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
