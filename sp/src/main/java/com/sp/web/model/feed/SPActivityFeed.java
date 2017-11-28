package com.sp.web.model.feed;

import com.sp.web.model.SPFeature;
import com.sp.web.utils.GenericUtils;

/**
 * @author Dax Abraham
 * 
 *         The entity to store the activity feed.
 */
public class SPActivityFeed {
  
  private String id;
  private String text;
  private SPFeature feature;
  
  /**
   * Default Constructor.
   */
  public SPActivityFeed() { }
  
  /**
   * Constructor from feature type and text.
   * 
   * @param feature
   *            - feature
   * @param text
   *            - text
   */
  public SPActivityFeed(SPFeature feature, String text) {
    this.id = GenericUtils.getId();
    this.feature = feature;
    this.text = text;
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public SPFeature getFeature() {
    return feature;
  }
  
  public void setFeature(SPFeature feature) {
    this.feature = feature;
  }
  
}
