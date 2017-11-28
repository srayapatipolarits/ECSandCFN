package com.sp.web.service.email;

import com.sp.web.Constants;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.form.lndfeedback.DevelopmentForm;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.Token;
import com.sp.web.model.TokenStatus;
import com.sp.web.model.User;
import com.sp.web.model.lndfeedback.DevelopmentFeedback;
import com.sp.web.repository.lndfeedback.DevelopmentFeedbackRepository;
import com.sp.web.repository.token.TokenRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.lndfeedback.DevelopmentFeedbackFactory;
import com.sp.web.service.log.LogGateway;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * @author vikram
 *
 *         The service class implementation to read emails in notify@surepeople.com and update mongo
 *         with feedback response
 */

public class EmailReceiverService {
  
  private static final Logger log = Logger.getLogger(EmailReceiverService.class);
  
  @Autowired
  private DevelopmentFeedbackRepository developmentFeedbackRepository;
  
  @Autowired
  private DevelopmentFeedbackFactory developmentFactory;
  // @Autowired
  // private SPTokenFactory spTokenFactory;
  
  /** Token Repository to perform operation on the repository. */
  @Autowired
  private TokenRepository tokenRepository;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  CommunicationGateway gateway;
  
  @Autowired
  private Environment environment;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  private NotificationsProcessor notificationsProcessor;
  
  @Autowired
  @Qualifier("notificationLog")
  private LogGateway notificationGateway;
  
  /**
   * method to read feedback response and invoke helper method update mongo db with feedback
   * response. method gets invoked from the inbound mail channel config present in integration xml
   * 
   * @param MimeMessage
   *          - message
   */
  public void processEmail(MimeMessage mimeMessage) {
    
    log.debug("EmailReceiverService processEmail called !! ");
    
    String feedbackUserEmail = null;
    String feedbackResponse = "";
    FeedbackUser feedbackUser = null;
    User memberUser = null;
    String tokenId = null;
    Token feedbackToken = null;
    
    try {
      
      // Get email address of the person who has sent feedback to notify@surepeople.com
      Address[] fromAddress = mimeMessage.getFrom();
      if (fromAddress != null && fromAddress[0] != null)
        feedbackUserEmail = fromAddress[0].toString();
      
      String subject = mimeMessage.getSubject();
      if (subject != null && subject.contains("Feedback Request")) {
        
        int startInd = subject.indexOf("[");
        int endInd = subject.indexOf("]");
        
        if (startInd != -1 && endInd != -1) {
          tokenId = subject.substring(startInd + 1, endInd);
        }
        
        if (tokenId != null) {
          
          /* fetch the token from the repository */
          feedbackToken = tokenRepository.findTokenById(tokenId);
          
          if (feedbackToken == null) {
            log.debug("Token not found" + feedbackToken);
            notifyFeedbackProvider(
                feedbackUserEmail,
                null,
                "Your feedback could not be received due to an error. Try again by finding the original email from us, then click the Provide Feedback link.<br/><br/>If you are still having trouble, contact our Client Solutions Team: 1-855-755-7873 or support@surepeople.com.");
            
          } else if (feedbackToken.getTokenStatus() == TokenStatus.INVALID) {
            log.debug("Invalid Token" + tokenId);
            notifyFeedbackProvider(
                feedbackUserEmail,
                null,
                "Your feedback could not be received due to an error. Try again by finding the original email from us, then click the Provide Feedback link.<br/><br/>If you are still having trouble, contact our Client Solutions Team: 1-855-755-7873 or support@surepeople.com.");
            
          } else {
            
            feedbackResponse = getFeedbackReplyTextFromMessage(mimeMessage);
            
            if (feedbackResponse == null) {
              log.debug("Unable to extract email body");
              throw new MessagingException("Unable to extract email body");
            }
            
            // feedback provider can respond without actually entering any feedback
            if (feedbackResponse.isEmpty()) {
              log.debug("Feedback Email Body Empty");
            }
            
            // feedback response is mime type not supported, log and notify feedback provider that
            // he needs to use the link to provide feedback
            if (feedbackResponse.equals("MIMETYPE_NOT_SUPPORTED")) {
              log.debug("Mimetype not supported " + mimeMessage.getContentType());
              throw new MessagingException("MIMETYPE_NOT_SUPPORTED");
            }
            
            Object[] params = new Object[2];
            params[0] = feedbackToken.getParamAsString("feedbackId");
            params[1] = feedbackResponse;
            
            log.debug("feedbackUserEmail:" + feedbackUserEmail);
            log.debug("feedbackResponse:" + feedbackResponse);
            log.debug("tokenId:" + tokenId);
            
            DevelopmentFeedback feedback = developmentFeedbackRepository
                .findById((String) params[0]);
            
            if (feedback != null) {
              
              feedbackUser = userRepository.findFeedbackUser(feedback.getFeedbackUserId());
              memberUser = userRepository.findUserById(feedbackUser.getFeedbackFor());
              
              if (memberUser != null) {
                
                if (feedback.getRequestStatus() == RequestStatus.COMPLETED) {
                  log.debug("Feedback is already provided for feedbackId: " + feedback.getId());
                  
                  // invalidating token after successful feedback response
                  feedbackToken.setTokenStatus(TokenStatus.INVALID);
                  feedbackToken.invalidate("Token already used.");
                  tokenRepository.updateToken(feedbackToken);
                  log.debug("Token invalidated successfully " + tokenId);
                  
                  notifyFeedbackProvider(
                      feedbackUserEmail,
                      null,
                      "Thanks for your support; it looks like we already have your "
                          + "feedback on file. If this is not the case or you are still having trouble,"
                          + " contact our Client Solutions Team: 1-855-755-7873 or support@surepeople.com.");
                  
                } else if (feedback.getRequestStatus() == RequestStatus.DECLINED) {
                  
                  log.debug("Feedback is already provided for feedbackId: " + feedback.getId());
                  notifyFeedbackProvider(
                      feedbackUserEmail,
                      null,
                      "Your attempt to provide feedback has failed, records show that you have already declined this option. If you would like to speak with a representative, please contact our Client Solutions Team: 1-855-755-7873 or support@surepeople.com.");
                  
                } else {
                  
                  DevelopmentForm feedbackForm = new DevelopmentForm();
                  feedbackForm.setDevFeedRefId(feedback.getDevFeedRefId());
                  feedbackForm.setResponse(feedbackResponse);
                  feedbackForm.setId(feedback.getId());
                  feedbackForm.setSpFeature(feedback.getSpFeature());
                  developmentFactory.update(feedbackUser, feedbackForm);
                  log.debug("spNoteFeedbackHelper.giveRequestFeedback method completed successfully for feedbackId"
                      + params[0]);
                }
                
              }// end of memberuser condition
              else {
                log.debug("MemberUser not found for Feedback ID:" + params[0]);
                notifyFeedbackProvider(
                    feedbackUserEmail,
                    null,
                    "Hello,<br/><br/>Unfortunately, the user you submitted feedback for is no longer in the system."
                        + " If you would like to speak with a representative, please contact our Client Solutions Team: 1-855-755-7873 or support@surepeople.com.");
                
              }
              
            }// end of feedback condition
            else {
              log.debug("Feedback Id not found:" + params[0]);
              notifyFeedbackProvider(
                  feedbackUserEmail,
                  tokenId,
                  "Your feedback could not be received due to an error. Please click on Provide Feedback link below to provide the feedback");
            }
          }
        } else {
          
          log.debug("Token not found in Email Reply subject");
          notifyFeedbackProvider(
              feedbackUserEmail,
              null,
              "Your feedback could not be received due to an error. Try again by finding the original email from us, then click the Provide Feedback link.<br/><br/>If you are still having trouble, contact our Client Solutions Team: 1-855-755-7873 or support@surepeople.com.");
          
        }
      }// end of subject condition.
    } catch (Exception ex) {
      
      notifyFeedbackProvider(
          feedbackUserEmail,
          null,
          "Your feedback could not be received due to an error. Please contact our Client Solutions Team: 1-855-755-7873 or support@surepeople.com.");
      log.debug("EmailReceiverService processEmail Failed" + ex);
      ex.printStackTrace();
      
    }
    
  }
  
  /**
   * method to parse email content, check for mimeType and return body without any html tags.
   * 
   * @param MimeMessage
   *          - message
   * 
   * @return the String result
   */
  
  private String getFeedbackReplyTextFromMessage(MimeMessage message) throws Exception {
    
    log.debug("getBodyFromMessage invoked !! ");
    
    String result = null;
    
    if (message.isMimeType("text/plain")) {
      
      String body = (String) message.getContent();
      result = removePreviousEmailData(body);
      
      return result;
      
    } else if (message.isMimeType("multipart/*")) {
      
      MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
      int count = mimeMultipart.getCount();
      
      for (int i = 0; i < count; i++) {
        
        BodyPart bodyPart = mimeMultipart.getBodyPart(i);
        // there is no From:" in body
        // gmail reply with text executes this flow. It returns reply text along with complete email
        // content from previous email
        if (bodyPart.isMimeType("text/plain")) {
          
          // Get all lines from Email Reply. Remove all lines with char ">". It is added by email
          // clients for date, time, previous email messages.
          // We only need feedback reply in text.
          String body = (String) bodyPart.getContent();
          result = removePreviousEmailData(body);
          break;
          
        } else if (bodyPart.isMimeType("text/html")) {
          String body = (String) bodyPart.getContent();
          result = removePreviousEmailData(body);
          break;
        }
      }
      
      return result;
      
    } else if (message.isMimeType("text/html")) {
      
      String html = (String) message.getContent();
      String text = Jsoup.parse(html).text();
      
      // Document doc = Jsoup.parse(html);
      // String text = Jsoup.clean(html, "", Whitelist.none(),
      // doc.outputSettings().prettyPrint(false));
      
      int index = text.indexOf("From:");
      if (index != -1) {
        result = text.substring(0, index);
      }
      return result;
      
    } else {
      return "MIMETYPE_NOT_SUPPORTED";
    }
    
  }
  
  /**
   * method to remove previous data from email body. It gets added by email clients
   * 
   * @param MimeMessage
   *          - message
   * 
   * @return the String result
   */
  
  private String removePreviousEmailData(String body) {
    
    String result = "";
    
    int ind = body.indexOf("Reply above this line to comment");
    if (ind != -1)
      body = body.substring(0, ind);
    
    // split string into lines based on new line char
    String lines[] = body.split("\\r?\\n");
    
    // loop thru the lines to remove earlier email reply, date time and email id added by email
    // clients
    // on 1st occurence of earlier email and surepeople.com domain, it means u have reached end of
    // feedback response, so break the loop
    for (String line : lines) {
      if (line.contains(">")) {
        line = "";
        break;
      } else {
        result = result + line + "\r\n";
      }
    }
    
    return result;
    
  }
  
  /**
   * method to delete the messages if successful response is posted
   * 
   * @param MimeMessage
   *          - message
   * 
   * @return the String result
   */
  
  public void deleteMessage(MimeMessage message) throws MessagingException {
    
    log.debug("EmailReceiverService deleteMessage called !! ");
    
    Folder folder = message.getFolder();
    folder.open(Folder.READ_WRITE);
    String messageId = message.getMessageID();
    Message[] messages = folder.getMessages();
    
    FetchProfile contentsProfile = new FetchProfile();
    contentsProfile.add(FetchProfile.Item.ENVELOPE);
    contentsProfile.add(FetchProfile.Item.CONTENT_INFO);
    contentsProfile.add(FetchProfile.Item.FLAGS);
    
    folder.fetch(messages, contentsProfile);
    
    // find this message and mark for deletion
    for (int i = 0; i < messages.length; i++) {
      if (((MimeMessage) messages[i]).getMessageID().equals(messageId)) {
        messages[i].setFlag(Flags.Flag.DELETED, true);
        break;
      }
      
    }
    folder.close(true);
    
    // Get email address of the person who has sent feedback to notify@surepeople.com
    String feedbackUserEmail = null;
    Address[] fromAddress = message.getFrom();
    if (fromAddress != null && fromAddress[0] != null)
      feedbackUserEmail = fromAddress[0].toString();
    
    log.debug("Email deleted succesfully. Feedback Provider Email ID: " + feedbackUserEmail);
    
  }
  
  /**
   * method to notify feedback provider if token is invalid/not found.
   * 
   * @param MimeMessage
   *          - message
   * 
   * @return the String result
   */
  
  private void notifyFeedbackProvider(String feedbackUserEmail, String tokenId, String message) {
    
    // create the email params to send the notification for
    EmailParams emailParams = new EmailParams();
    emailParams.setTemplateName(NotificationType.FeedbackEmailReplyFailure.getTemplateName());
    emailParams.setFrom(environment.getProperty("default.fromAddress"));
    String subject = MessagesHelper.getMessage(Constants.NOTIFICATION_SUBJECT_PREFIX
        + NotificationType.FeedbackEmailReplyFailure);
    
    if (tokenId == null) {
      emailParams.setSubject(subject);
    } else {
      emailParams.setSubject(subject + " [" + tokenId + "]");
    }
    emailParams.setTos(feedbackUserEmail);
    emailParams.addParam(Constants.PARAM_MESSAGE, message);
    emailParams.addParam(Constants.PARAM_NOTIFICATION_TYPE, NotificationType.FeedbackEmailReplyFailure);
    
    String baseUrl = environment.getProperty("base.serverUrl", "http://www.surepeople.com/");
    
    emailParams.addParam(Constants.PARAM_BASE_SERVER_URL, baseUrl);
    emailParams.addParam(Constants.PARAM_TOKEN, tokenId);
    
    gateway.sendMessage(emailParams);
    
  }
  
}
