package com.sp.web.model;

import java.io.Serializable;

/**
 * @author Dax Abraham
 * 
 *         The entity to store the rating score information.
 */
public class SPRatingScore implements Serializable {
  
  private static final long serialVersionUID = -4704963914091234928L;
  private String title;
  private String description;
  private int score;
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public int getScore() {
    return score;
  }
  
  public void setScore(int score) {
    this.score = score;
  }
  
}
