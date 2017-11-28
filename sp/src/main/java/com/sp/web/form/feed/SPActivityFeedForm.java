package com.sp.web.form.feed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sp.web.model.SPFeature;
import com.sp.web.model.feed.SPActivityFeed;

import org.springframework.util.Assert;

import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The form class for the SP Activity Feed.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SPActivityFeedForm {
  
  private String id;
  private String text;
  private SPFeature feature;
  private int position;
  
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

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  /**
   * Validate the form data for add.
   */
  public void validate() {
    Assert.notNull(text, "Activity text is required.");
    Assert.notNull(feature, "Activity feature is required.");
  }
  
  /**
   * Validate update request.
   */
  public void validateUpdate() {
    validate();
    Assert.notNull(id, "Id is required.");
    Assert.isTrue(position >= 0, "Position is required.");
  }
  

  /**
   * Add the new activity feed to the activity feeds list.
   * 
   * @param activityFeeds
   *          - activity feed list to update
   * @return 
   *    the new activity feed
   */
  public SPActivityFeed addActivityFeed(List<SPActivityFeed> activityFeeds) {
    // normalize the position
    normalizePosition(activityFeeds.size());
    // add the activity feed
    final SPActivityFeed activityFeed = new SPActivityFeed(feature, text);
    activityFeeds.add(position, activityFeed);
    return activityFeed;
  }

  /**
   * Normalize the position for insert.
   * 
   * @param size
   *          - size of activity feed
   */
  private void normalizePosition(int size) {
    if (position > size || position < 0) {
      position = size;
    }
  }

  /**
   * UPdating the data of the activity feed.
   * 
   * @param activityFeed
   *            - activity feed to update
   */
  public void updateData(SPActivityFeed activityFeed) {
    activityFeed.setText(text);
    activityFeed.setFeature(feature);
  }
  
}
