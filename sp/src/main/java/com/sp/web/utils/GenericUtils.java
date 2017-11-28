package com.sp.web.utils;

import com.sp.web.model.FeedbackUser;
import com.sp.web.model.SPUserDetail;
import com.sp.web.model.User;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

/**
 * GenerciUtils contains method for the generic functionalities.
 * 
 * @author pradeepruhil
 *
 */
public class GenericUtils {
  
  /**
   * generatePDF will generate the PDF from the byte date.
   * 
   * @param response
   *          httpservlet response
   * @param bytes
   *          byte array
   */
  public static void generatePDF(HttpServletResponse response, byte[] bytes) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
    baos.write(bytes, 0, bytes.length);
    response.setContentLength(baos.size());
    response.setContentType("application/pdf");
    response.setHeader("Expires", "0");
    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
    response.setHeader("Pragma", "public");
    
    try {
      OutputStream os = response.getOutputStream();
      baos.writeTo(os);
      os.flush();
      os.close();
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
  
  /**
   * this method will check wither the node is job server node or not
   * 
   * @param environment
   *          enviornment value.
   * @return the value.
   */
  public static boolean isJobServerNode(Environment environment) {
    return Boolean.valueOf(environment.getProperty("sp.enable.schedulars.master"));
  }
  
  /**
   * getUserFromAuthentcation method will get the User from the authentication token either from
   * rememberMe token or ussernamePasswordtoken.
   * 
   * @param authentication
   *          Authentication object.
   * @return the User.
   */
  public static User getUserFromAuthentication(Authentication authentication) {
    Object principal = authentication.getPrincipal();
    User user = null;
    if (principal instanceof SPUserDetail) {
      user = ((SPUserDetail) principal).getUser();
    } else if (principal instanceof User) {
      user = (User) principal;
    }
    return user;
  }
  
  public static String getUserIdFromAuthentication(Authentication authentication) {
    Object principal = authentication.getPrincipal();
    User user = null;
    if (principal instanceof SPUserDetail) {
      user = ((SPUserDetail) principal).getUser();
    } else if (principal instanceof User) {
      user = (User) principal;
    }
    Assert.notNull(user, "Invalid authenticated user. User is not of typeUser or SpUser");
    return user != null ? user.getId() : "";
  }
  
  public static String normalize(String name) {
    return org.apache.commons.lang3.StringUtils.replacePattern(name, "[^a-zA-Z0-9]", "_");
  }
  
  /**
   * replaceSpecial Char method will replace the special characters from the value string passed.
   * 
   * @param value
   *          is the strnk whose value is to be replaced.
   * @return the replaced special char
   */
  public static String replaceSpecialChar(String value) {
    value = StringUtils.replacePattern(value, "\\r\\n", "<br/>");
    value = StringUtils.replacePattern(value, "\\n", "<br/>");
    // * Replacing the unicode characeter with Trade mark */
    value = StringUtils.replacePattern(value, "â¢", "\u2122");
    value = StringUtils.replacePattern(value, "ï¿½", "\u00AE");
    value = StringUtils.replacePattern(value, "Â®", "\u00AE");
    return value;
  }
  
  /**
   * @return - get a unique id.
   */
  public static String getId() {
    return UUID.randomUUID().toString();
  }
  
  /**
   * Truncate the given list to limit passed.
   * 
   * @param listToTruncate
   *          - list to truncate
   * @param limit
   *          of the list.
   * @return the truncated list
   */
  public static <T> List<T> truncateList(List<T> listToTruncate, int limit) {
    if (listToTruncate != null && listToTruncate.size() > limit) {
      return listToTruncate.subList(0, limit);
    }
    return listToTruncate;
  }
  
  /**
   * Truncate the given list to limit passed.
   * 
   * @param listToTruncate
   *          - list to truncate
   * @param limit
   *          of the list.
   * @return the truncated list
   */
  public static <T> List<T> truncateComments(List<T> listToTruncate, int limit) {
    final int size = (listToTruncate != null) ? listToTruncate.size() : 0;
    if (size > limit) {
      return listToTruncate.subList(size - limit, size);
    }
    return listToTruncate;
  }
  
  /**
   * Check if the currently logged in user is the feedback user if not then login the feedback user.
   * 
   * @param feedbackUser
   *          - feedback user
   * @return true if same else false
   */
  public static boolean isSameAsLoggedInUser(FeedbackUser feedbackUser) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      User userFromAuthentication = GenericUtils.getUserFromAuthentication(authentication);
      if (userFromAuthentication != null
          && userFromAuthentication.getEmail().equals(feedbackUser.getEmail())) {
        return true;
      }
    }
    return false;
  }
}
