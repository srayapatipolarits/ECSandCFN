package com.sp.web.assessment.questions;

import com.sp.web.xml.questions.MultipleChoiceResponseDocument.MultipleChoiceResponse;
import com.sp.web.xml.questions.RatingResponseDocument.RatingResponse;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

/**
 * 
 * @author Dax Abraham
 * 
 *         This is the bean to store the options information.
 */
public class OptionsBean {
  
  private String id;
  private String text;
  private String key;
  
  
  /**
   * Default Constructor.
   */
  public OptionsBean() { }

  /**
   * Creating the options from the given multiple choice response.
   * 
   * @param response
   *          - response
   */
  public OptionsBean(MultipleChoiceResponse response) {
    this.id = response.getId();
    this.text = response.getText();
  }

  /**
   * Constructor from rating response.
   * 
   * @param response
   *          - response
   */
  public OptionsBean(RatingResponse response) {
    this.id = response.getId();
    this.text = response.getText();
    final String tempKey = response.getKey();
    this.key = (StringUtils.isBlank(tempKey)) ? null : tempKey;
  }

  /**
   * Constructor form DOM element.
   * 
   * @param mcrNode
   *          - DOM element
   */
  public OptionsBean(Element mcrNode) {
    this.id = mcrNode.getAttribute("Id");
    this.text = mcrNode.getElementsByTagName("Text").item(0).getTextContent();
    this.key = mcrNode.getAttribute("key");
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
  
  public String getKey() {
    return key;
  }
  
  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public String toString() {
    return "[" + id + ":" + text + "]";
  }
}
