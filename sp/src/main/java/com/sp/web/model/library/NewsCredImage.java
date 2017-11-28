/**
 * 
 */
package com.sp.web.model.library;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author pradeep
 *
 */
public class NewsCredImage implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -2909524592698055348L;

  private String guid;

  private String orientation;

  private String width;

  private String caption;

  private Date createdOn;

  private Set<Source> source;

  private String url;

  /**
   * @return the guid
   */
  public String getGuid() {
    return guid;
  }

  /**
   * @param guid
   *          the guid to set
   */
  public void setGuid(String guid) {
    this.guid = guid;
  }

  /**
   * @return the orientation
   */
  public String getOrientation() {
    return orientation;
  }

  /**
   * @param orientation
   *          the orientation to set
   */
  public void setOrientation(String orientation) {
    this.orientation = orientation;
  }

  /**
   * @return the width
   */
  public String getWidth() {
    return width;
  }

  /**
   * @param width
   *          the width to set
   */
  public void setWidth(String width) {
    this.width = width;
  }

  /**
   * @return the caption
   */
  public String getCaption() {
    return caption;
  }

  /**
   * @param caption
   *          the caption to set
   */
  public void setCaption(String caption) {
    this.caption = caption;
  }

  /**
   * @return the createdOn
   */
  public Date getCreatedOn() {
    return createdOn;
  }

  /**
   * @param createdOn
   *          the createdOn to set
   */
  public void setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
  }

  /**
   * @return the source
   */
  public Set<Source> getSource() {
    if (source == null) {
      source = new HashSet<Source>();
    }
    return source;
  }

  /**
   * @param source
   *          the source to set
   */
  public void setSource(Set<Source> source) {
    this.source = source;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
