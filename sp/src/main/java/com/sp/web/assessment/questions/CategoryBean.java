package com.sp.web.assessment.questions;

import com.sp.web.model.User;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

/**
 * @author Dax Abraham
 * 
 *         The bean to store the sections info information for the json response.
 */
public class CategoryBean {

  private int number;
  private int totalQuestions;
  private String instruction;
  private String type;

  /**
   * Default constructor.
   */
  public CategoryBean() {}
  
  /**
   * Constructor to clone the data.
   * 
   * @param categoriesBean
   *            - the categories bean
   */
  public CategoryBean(CategoryBean categoryBean) {
    BeanUtils.copyProperties(categoryBean, this);
  }
  
  /**
   * Constructor from category bean and user 360.
   * 
   * @param categoryBean
   *            - category bean
   * @param user360
   *            - user 360
   */
  public CategoryBean(CategoryBean categoryBean, User user360) {
    this(categoryBean);
    updateInstruction(user360);
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public int getTotalQuestions() {
    return totalQuestions;
  }

  public void setTotalQuestions(int totalQuestions) {
    this.totalQuestions = totalQuestions;
  }

  public String getInstruction() {
    return instruction;
  }

  public void setInstruction(String instruction) {
    this.instruction = instruction;
  }

  public void updateInstruction(User user360) {
    this.instruction = MessagesHelper.genderNormalize(this.instruction, user360);
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
