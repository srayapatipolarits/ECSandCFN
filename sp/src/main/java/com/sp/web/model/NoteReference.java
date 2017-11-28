package com.sp.web.model;


/**
 * NoteReference class holds the reference data which is included in a note.
 * 
 * @author pradeepruhil
 *
 */
public class NoteReference {
  
  private String title;
  
  private String description;
  
  private SPMedia mediaRef;
  
  private String name;
  
  private String id;
  
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
  
  public SPMedia getMediaRef() {
    return mediaRef;
  }
  
  public void setMediaRef(SPMedia mediaRef) {
    this.mediaRef = mediaRef;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  @Override
  public String toString() {
    return "NoteReference [title=" + title + ", description=" + description + ", name=" + name
        + ", id=" + id + "]";
  }
  
}
