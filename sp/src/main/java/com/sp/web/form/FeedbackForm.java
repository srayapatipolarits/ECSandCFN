package com.sp.web.form;

import com.sp.web.model.RequestType;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

/**
 * @author pradeep
 * 
 *         The feedback form.
 */
public class FeedbackForm implements Serializable {

  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = 6281022995405246119L;

  private String comment;

  @NotNull
  private String endDate;

  @NotNull
  private RequestType requestType;

  /**
   * return comment.
   * 
   * @return the comment
   */
  public String getComment() {
    if (comment != null) {
      comment = StringUtils.replacePattern(comment, "\\\\r\\\\n", "<br/>");
      comment = StringUtils.replacePattern(comment, "\\\\n", "<br/>");
    }
    return comment;
  }

  /**
   * set comment.
   * 
   * @param comment
   *          the comment to set
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * return the enddate.
   * 
   * @return the endDate
   */
  public String getEndDate() {
    return endDate;
  }

  /**
   * set the endDate.
   * 
   * @param endDate
   *          the endDate to set
   */
  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  /**
   * RequestType to be set.
   * 
   * @param requestType
   *          the requestType to set
   */
  public void setRequestType(RequestType requestType) {
    this.requestType = requestType;
  }

  /**
   * return the request type.
   * 
   * @return the requestType
   */
  public RequestType getRequestType() {
    return requestType;
  }

}
