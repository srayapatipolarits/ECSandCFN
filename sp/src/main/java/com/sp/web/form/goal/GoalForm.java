package com.sp.web.form.goal;

import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * GoalForm is the form for creating a new Goal.
 * 
 * @author pradeepruhil
 *
 */
public class GoalForm {
  
  private String name;
  
  private String description;
  
  private GoalCategory category;
  
  private String isMandatory;
  
  private GoalStatus status;
  
  private String allAccounts;
  
  private List<String> accounts;

  /**
   * Default constructor.
   */
  public GoalForm() {
  }
  
  /**
   * Constructor.
   * 
   * @param goal
   *         - goal
   */
  public GoalForm(SPGoal goal) {
    BeanUtils.copyProperties(goal, this);
    this.allAccounts = goal.isAllAccounts() + "";
    this.isMandatory = goal.isMandatory() + "";
  }

  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public GoalCategory getCategory() {
    return category;
  }
  
  public void setCategory(GoalCategory category) {
    this.category = category;
  }
  
  public void setIsMandatory(String isMandatory) {
    this.isMandatory = isMandatory;
  }
  
  public String getIsMandatory() {
    return isMandatory;
  }
  
  public void setStatus(GoalStatus status) {
    this.status = status;
  }
  
  public GoalStatus getStatus() {
    return status;
  }
  
  public String getAllAccounts() {
    return allAccounts;
  }
  
  public void setAllAccounts(String allAccounts) {
    this.allAccounts = allAccounts;
  }
  
 
  @Override
  public String toString() {
    return "GoalForm [name=" + name + ", description=" + description + ", category=" + category
        + ", isMandatory=" + isMandatory + ", status=" + status + ", allAccounts=" + allAccounts
        + ", accounts=" + accounts + "]";
  }
  
  /**
   * method will create a new spGoal out of the form submitted by the admisntrator for creating a
   * new goal.
   */
  public SPGoal createSPGoal() {
    SPGoal spGoal = new SPGoal();
    BeanUtils.copyProperties(this, spGoal);
    spGoal.setMandatory(Boolean.valueOf(this.getIsMandatory()));
    spGoal.setAllAccounts(Boolean.valueOf(this.getAllAccounts()));
    return spGoal;
  }

  /**
   * Get the accounts list.
   * 
   * @return
   *      - the account list
   */
  public List<String> getAccounts() {
    if (accounts == null) {
      accounts = new ArrayList<String>();
    }
    return accounts;
  }

  public void setAccounts(List<String> accounts) {
    this.accounts = accounts;
  }  
  
}
