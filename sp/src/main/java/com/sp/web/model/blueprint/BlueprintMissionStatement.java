package com.sp.web.model.blueprint;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * @author Dax Abraham
 *
 *         The entity to store the blueprint mission statement.
 */
public class BlueprintMissionStatement implements Serializable{
  
  private static final long serialVersionUID = 7444867463918493099L;
  
  private String uid;
  private String text;
  
  public String getUid() {
    return uid;
  }
  
  public void setUid(String uid) {
    this.uid = uid;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  /**
   * This method sets a unique id for the given category.
   *  
   * @param uidGenerator
   *            - the UID Generator
   * 
   */
  public void addUID(Supplier<String> uidGenerator) {
    if (uid == null) {
      uid = uidGenerator.get();
    }
  }
  
}
