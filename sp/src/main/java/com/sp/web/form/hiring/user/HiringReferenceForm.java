package com.sp.web.form.hiring.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sp.web.form.hiring.lens.HiringLensForm;
import com.sp.web.model.User;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 * 
 *         The form class for getting external references information.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HiringReferenceForm {
  
  private List<HiringLensForm> referenceList;
  
  public List<HiringLensForm> getReferenceList() {
    return referenceList;
  }
  
  public void setReferenceList(List<HiringLensForm> referenceList) {
    this.referenceList = referenceList;
  }
  
  /**
   * Validate the references request.
   * 
   * @param referenceTypes
   *          - reference types
   */
  public void validate(User user, final List<List<String>> referenceTypes) {
    Assert.notEmpty(referenceList, "References required.");
    Assert.isTrue(referenceList.size() == referenceTypes.size(), "Reference count mismatch.");
    int counter = 0;
    for (HiringLensForm lensRequest : referenceList) {
      lensRequest.validateReference(user);
      Assert.isTrue(referenceTypes.get(counter).contains(lensRequest.getReferenceType()),
          "Reference types mismatch.");
      counter++;
    }
  }

  /**
   * Sets the feedback for for the reference list.
   * 
   * @param feedbackFor
   *          - feedback for
   */
  public void updateFeedbackFor(String feedbackFor) {
    if (CollectionUtils.isNotEmpty(referenceList)) {
      referenceList.forEach(r -> r.setFeedbackFor(feedbackFor));
    }
  }
  
}
