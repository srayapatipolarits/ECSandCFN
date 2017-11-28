/**
 * 
 */
package com.sp.web.model.spectrum;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author pradeepruhil
 *
 */
public class LearnerStatusArticleGraph {
  
  private Map<LocalDateTime, Integer> graphs;
  
  /**
   * @param graphs
   *          the graphs to set
   */
  public void setGraphs(Map<LocalDateTime, Integer> graphs) {
    this.graphs = graphs;
  }
  
  /**
   * @return the graphs
   */
  public Map<LocalDateTime, Integer> getGraphs() {
    return graphs;
  }
}
