package com.sp.web.account;

import static com.sp.web.utils.MessagesHelper.getMessage;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Dax Abraham
 * 
 *         The envelop to store the summary information.
 */
public class SummaryInfo {

  private static final String PROGRESS_MESSAGE_SUFFIX = ".progressMessage";

  private static final String PERCENT_MESSAGE_SUFFIX = ".percentMessage";

  private static final RoundingMode ROUNDING_MODE = RoundingMode.UP;

  private static final int PRECISION = 2;

  private static final String TITLE_SUFFIX = ".title";

  private String title;
  private int total;
  private int currentState;
  private BigDecimal percentComplete;
  private String progressMessage;
  private String percentageMessage;
  private String messageKeyPrefix;

  public SummaryInfo(String messageKeyPrefix) {
    this.messageKeyPrefix = messageKeyPrefix;
    setTitle(getMessage(messageKeyPrefix + TITLE_SUFFIX));
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getTotal() {
    return total;
  }

  public int getCurrentState() {
    return currentState;
  }

  public BigDecimal getPercentComplete() {
    return percentComplete;
  }

  public String getProgressMessage() {
    return progressMessage;
  }

  public String getPercentageMessage() {
    return percentageMessage;
  }

  /**
   * Sets the data for the Summary Info. Also adds the messages accordingly
   * 
   * @param currentStatus
   *          - the current status
   * @param total
   *          - the total count
   */
  public void setData(int currentStatus, int total) {
    this.total = total;
    this.currentState = currentStatus;
    process();
  }

  /**
   * Calculates the percentage and loads all the required messages.
   */
  private void process() {
    // ensure no divide by zero
    int tempTotal = (total == 0) ? 1 : total;

    // calculate the percentage
    percentComplete = new BigDecimal((currentState / tempTotal) * 100).setScale(PRECISION,
        ROUNDING_MODE);

    // load the percentage message
    percentageMessage = getMessage(messageKeyPrefix + PERCENT_MESSAGE_SUFFIX, percentComplete);

    // load the progress message
    progressMessage = getMessage(messageKeyPrefix + PROGRESS_MESSAGE_SUFFIX, currentState, total);
  }
}
