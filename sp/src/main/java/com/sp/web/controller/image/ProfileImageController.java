package com.sp.web.controller.image;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.controller.systemadmin.media.MediaHelper;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <code>ImageController</code> is used to upload user images.
 * 
 * @author pruhil
 *
 */
@Controller
public class ProfileImageController {
  
  /** Media helper class for all media related functionalities */
  @Autowired
  private MediaHelper mediaHelper;
  
  /**
   * <code>uploadImage</code> method will upload the image and return the SP response stating
   * success or failure message.
   * 
   * @param image
   *          to be uploaded
   * @param token
   *          User authentication token
   * @return the SP Response
   */
  @RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse uploadImage(@RequestParam String image,
      UsernamePasswordAuthenticationToken token) {
    return ControllerHelper.process(mediaHelper::saveProfilePicture, token, image);
  }
  
  /**
   * Controller method to upload the company image.
   * 
   * @param image
   *          - company image
   * @param token
   *          - logged in user
   * @return
   *     the response to the upload request
   */
  @RequestMapping(value = "/media/company/uploadImage", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse uploadCompanyImage(@RequestParam String image,
      @RequestParam(required = false) String companyId, UsernamePasswordAuthenticationToken token) {
    return ControllerHelper.process(mediaHelper::uploadCompanyImage, token, image, companyId);
  }
  
  /**
   * Controller method to delete the user profile image.
   * 
   * @param token
   *          - logged in user
   * @return the response to the delete request
   */
  @RequestMapping(value = "/admin/user/deleteImage", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteProfileImage(UsernamePasswordAuthenticationToken token) {
    return ControllerHelper.process(mediaHelper::deleteProfileImage, token);
  }
  
  /**
   * Controller method to delete the company logo image.
   * 
   * @param token
   *          - logged in user
   * @return the response to the delete request
   */
  @RequestMapping(value = "/media/company/deleteImage", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteCompanyImage(@RequestParam(required = false) String companyId,
      UsernamePasswordAuthenticationToken token) {
    return ControllerHelper.process(mediaHelper::deleteCompanyImage, token, companyId);
  }
  
}
