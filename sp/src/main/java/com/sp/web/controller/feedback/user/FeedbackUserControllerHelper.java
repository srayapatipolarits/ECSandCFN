/**
 * 
 */
package com.sp.web.controller.feedback.user;

import com.sp.web.Constants;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.UserStatus;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.FeedbackRepository;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import javax.servlet.http.HttpSession;

/**
 * @author pradeep
 *
 */
@Component
public class FeedbackUserControllerHelper {

  /** Initializing the logger */
  private static final Logger LOG = Logger.getLogger(FeedbackUserControllerHelper.class);

  @Autowired
  private FeedbackRepository feedbackRepository;

  /**
   * <code>confirmProfileDetailsExternalUser</code> methdo will confirm the user details and will
   * set the flag that user has confirmed his profile details.
   * 
   * @param param
   *          contains the http session to fetch the profile details
   * @return the profile details.
   */
  public SPResponse confirmProfileDetailsExternalUser(Object[] param) {
    SPResponse response = new SPResponse();

    String accepted = (String) param[0];
    HttpSession httpSession = (HttpSession) param[1];
    String firstName = (String) param[2];
    String lastName = (String) param[3];
    String linkedin = (String) param[4];
    /* Get the feedback request id ID and external user email to fetch the details */

    Optional.ofNullable(accepted).orElseThrow(
        () -> new InvalidRequestException(MessagesHelper
            .getMessage("feedback.error.invalid.request")));
    String feedbackUserId = (String) httpSession.getAttribute(Constants.PARAM_FEEDBACK_USERID);
    LOG.info("Confirming user the profile detail for feedback user id " + feedbackUserId);

    Optional.ofNullable(feedbackUserId).orElseThrow(
        () -> new InvalidRequestException(MessagesHelper
            .getMessage("feedback.error.invalid.request")));

    /* fetch the external user details */
    if (accepted.equalsIgnoreCase("true")) {
      LOG.info("Update the feedback request id, as user has verfied his confirm details");

      FeedbackUser user = feedbackRepository.findByIdValidated(feedbackUserId);
      user.setFirstName(firstName);
      user.setLastName(lastName);
      user.setLinkedInUrl(linkedin);
      user.setUserStatus(UserStatus.ASSESSMENT_PENDING);
      feedbackRepository.updateFeedbackUser(user);
      response.add("url", "/sp/assessment360/" + user.getId());

      response.isSuccess();
    } else {
      response.addError("notAccepted", "Profile Details not confirmed");
    }

    LOG.info("External user resposne" + response);
    return response;
  }
}
