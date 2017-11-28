/**
 * 
 */
package com.sp.web.model.spectrum;

import java.io.Serializable;
import java.util.SortedMap;

/**
 * @author pradeepruhil
 *
 */
public class LearnerStausTracker implements Serializable {
  
  private static final long serialVersionUID = -8024260346212498952L;
  
  private int articlesCompleted;
  
  private int trainingLibraryVisits;
  
  private int articlesRecommened;
  
  private int artclesBookmarked;
  
  private SP360LearnerStatusTracker sp360LearnerTracker;
  
  private GrowthLearnerTracker growthLearnerTracker;
  
  private PracticeNoteFeedbackTracker prNoteFeedbackTracker;
  
  private SortedMap<String, Integer> graphs;
  
  private TimeFilter filter;
  
  /**
   * @param graphs
   *          the graphs to set
   */
  public void setGraphs(SortedMap<String, Integer> graphs) {
    this.graphs = graphs;
  }
  
  /**
   * @return the graphs
   */
  public SortedMap<String, Integer> getGraphs() {
    return graphs;
  }
  
  public int getArticlesCompleted() {
    return articlesCompleted;
  }
  
  public void setArticlesCompleted(int articlesCompleted) {
    this.articlesCompleted = articlesCompleted;
  }
  
  public int getTrainingLibraryVisits() {
    return trainingLibraryVisits;
  }
  
  public void setTrainingLibraryVisits(int trainingLibraryVisits) {
    this.trainingLibraryVisits = trainingLibraryVisits;
  }
  
  public int getArticlesRecommened() {
    return articlesRecommened;
  }
  
  public void setArticlesRecommened(int articlesRecommened) {
    this.articlesRecommened = articlesRecommened;
  }
  
  public int getArtclesBookmarked() {
    return artclesBookmarked;
  }
  
  public void setArtclesBookmarked(int artclesBookmarked) {
    this.artclesBookmarked = artclesBookmarked;
  }
  
  public SP360LearnerStatusTracker getSp360LearnerTracker() {
    if (sp360LearnerTracker == null) {
      sp360LearnerTracker = new SP360LearnerStatusTracker();
    }
    return sp360LearnerTracker;
  }
  
  public void setSp360LearnerTracker(SP360LearnerStatusTracker sp360LearnerTracker) {
    this.sp360LearnerTracker = sp360LearnerTracker;
  }
  
  public GrowthLearnerTracker getGrowthLearnerTracker() {
    if (growthLearnerTracker == null) {
      growthLearnerTracker = new GrowthLearnerTracker();
    }
    return growthLearnerTracker;
  }
  
  /**
   * @param prNoteFeedbackTracker
   *          the prNoteFeedbackTracker to set
   */
  public void setPrNoteFeedbackTracker(PracticeNoteFeedbackTracker prNoteFeedbackTracker) {
    this.prNoteFeedbackTracker = prNoteFeedbackTracker;
  }
  
  /**
   * @return the prNoteFeedbackTracker
   */
  public PracticeNoteFeedbackTracker getPrNoteFeedbackTracker() {
    if(prNoteFeedbackTracker == null){
      prNoteFeedbackTracker = new PracticeNoteFeedbackTracker();
    }
    return prNoteFeedbackTracker;
  }
  
  public void setGrowthLearnerTracker(GrowthLearnerTracker growthLearnerTracker) {
    this.growthLearnerTracker = growthLearnerTracker;
  }
  
  /**
   * @param filter
   *          the filter to set
   */
  public void setFilter(TimeFilter filter) {
    this.filter = filter;
  }
  
  /**
   * @return the filter
   */
  public TimeFilter getFilter() {
    return filter;
  }
}
