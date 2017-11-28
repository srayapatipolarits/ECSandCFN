/**
 * 
 */
package com.sp.web.mvc.home;

import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;

import org.springframework.stereotype.Component;

/**
 * <code>HomeHelper</code> class contains functionality for the for the user who lands on the .
 * logged home page
 * 
 * @author pradeep
 *
 */
@Component
public class HomeHelper {

  /**
   * <code>sendInCompleteProfile</code> method will send the incompete profile for the user in the
   * response
   * 
   * @param user
   *          logged in user details
   * @return the SP Response
   */
  public SPResponse sendInCompleteProfile(User user) {

    SPResponse response = new SPResponse();
    addIfNotNull("firstName", user.getFirstName(), response);
    addIfNotNull("lastName", user.getLastName(), response);
    addIfNotNull("title", user.getTitle(), response);
    addIfNotNull("email", user.getEmail(), response);
    addIfNotNull("dob", user.getDob(), response);
    if (user.getAddress() != null) {
      addIfNotNull("country", user.getAddress().getCountry(), response);
      addIfNotNull("address1", user.getAddress().getAddressLine1(), response);
      addIfNotNull("address2", user.getAddress().getAddressLine2(), response);
      addIfNotNull("zipCode", user.getAddress().getZipCode(), response);
      addIfNotNull("city", user.getAddress().getCity(), response);
      addIfNotNull("phoneNumber", user.getPhoneNumber(), response);

    }
    response.isSuccess();
    return response;

  }

  /**
   * Check if object getting added in null or not.
   * 
   * @param key
   *          to be added in the json
   * @param value
   *          to be added
   * @param response
   */
  private void addIfNotNull(String key, Object value, SPResponse response) {

    if (value == null) {
      return;
    }
    response.add(key, value);

  }

}
