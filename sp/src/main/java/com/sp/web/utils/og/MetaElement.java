package com.sp.web.utils.og;

import java.net.URL;

/**
 * Represents OpenGraph enabled meta data for a specific document.
 * 
 * @author Dax Abraham
 */
public class MetaElement {
  private OpenGraphNamespace namespace; // either "og" an NS specific
  private String property;
  private String content;
  
  /**
   * Construct the representation of an element.
   * 
   * @param namespace
   *          The namespace the element belongs to
   * @param property
   *          The property key
   * @param content
   *          The content or value of this element
   */
  public MetaElement(OpenGraphNamespace namespace, String property, String content) {
    this.namespace = namespace;
    this.property = property;
    this.content = content;
  }
  
  public String getContent() {
    return content;
  }
  
  public OpenGraphNamespace getNamespace() {
    return namespace;
  }
  
  public String getProperty() {
    return property;
  }
  
  /**
   * Fetch the OpenGraph data from the object.
   * 
   * @return If the content is a URL, then an attempted will be made to build OpenGraph data from
   *         the object
   */
  public OpenGraph getExtendedData() {
    // The Java language should know the best form of a URL
    try {
      URL url = new URL(getContent());
      
      // success
      return new OpenGraph(url.toString(), true);
    } catch (Exception e) {
      return null; // not a valid URL
    }
  }
}