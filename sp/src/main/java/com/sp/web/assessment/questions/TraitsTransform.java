package com.sp.web.assessment.questions;

import com.sp.web.xml.questions.TraitTransformDocument.TraitTransform;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;


/**
 * @author Dax Abraham
 * 
 *         The holding class for the traits transformers.
 */
public class TraitsTransform {
  
  private CategoryType category;
  private TraitType trait;
  private float incrementAmount = 1f;
  private String requiredRating;
  
  /**
   * Default constructor.
   */
  public TraitsTransform() {
    // do nothing
  }
  
  /**
   * Constructor from trait transform.
   * 
   * @param transform
   *          - trait transform
   */
  public TraitsTransform(TraitTransform transform) {
    category = CategoryType.valueOf(transform.getCategory());
    trait = TraitType.valueOf(transform.getTrait());
    incrementAmount = transform.getAmount();
    if (transform.isSetRequiredRating()) {
      requiredRating = transform.getRequiredRating();
    }
  }
  
  /**
   * Constructor from DOM Element.
   * 
   * @param traitTransformNode
   *            - trait transform node
   */
  public TraitsTransform(Element traitTransformNode) {
    category = CategoryType.valueOf(traitTransformNode.getAttribute("Category"));
    trait = TraitType.valueOf(traitTransformNode.getAttribute("Trait"));
    incrementAmount = Float.valueOf(traitTransformNode.getAttribute("Amount"));
    String tempReqRating = traitTransformNode.getAttribute("RequiredRating");
    if (!StringUtils.isBlank(tempReqRating)) {
      requiredRating = tempReqRating;
    }
  }

  public CategoryType getCategory() {
    return category;
  }
  
  public void setCategory(CategoryType category) {
    this.category = category;
  }
  
  public TraitType getTrait() {
    return trait;
  }
  
  public void setTrait(TraitType trait) {
    this.trait = trait;
  }
  
  public void setIncrementAmount(float amount) {
    this.incrementAmount = amount;
  }
  
  public float getIncrementAmount() {
    return incrementAmount;
  }
  
  public void setRequiredRating(String requiredRating) {
    this.requiredRating = requiredRating;
  }
  
  public String getRequiredRating() {
    return requiredRating;
  }
  
  @Override
  public String toString() {
    return "TraitsTransform [category=" + category + ", trait=" + trait + ", incrementAmount="
        + incrementAmount + ", requiredRating=" + requiredRating + "]";
  }
}
