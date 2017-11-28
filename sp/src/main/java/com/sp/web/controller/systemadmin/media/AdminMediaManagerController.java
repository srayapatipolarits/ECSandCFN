package com.sp.web.controller.systemadmin.media;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.form.SPMediaForm;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;


/**
 * <code>AdminImageManagerController</code> define the controller Mappings for Media Management actions.
 * 
 * @author Prasanna Venkatesh
 *
 */
@Controller
public class AdminMediaManagerController {
  
  @Autowired
  private MediaHelper mediaHelper;
  
 
  @RequestMapping(value = "/sysAdmin/media/uploadPage", method = RequestMethod.GET)
  public String uploadPage(Authentication token) {
    return "uploadPage";
  }
  
  @RequestMapping(value = "/sysAdmin/media/listPage", method = RequestMethod.GET)
  public String listingPage(Authentication token) {
    return "listingPage";
  }
  
  @RequestMapping(value = "/sysAdmin/media/editPage", method = RequestMethod.GET)
  public String editPage(Authentication token) {
    return "editPage";
  }
  
  /**
   * Controller method to upload the media file uploaded through admin interface.
   * 
   * @param mediaForm
   *          - SPMediaForm 
   * @param token
   *          - logged in user
   * @param image
   *          - String imageDataurl        
   * @return
   *     the response to the upload request
   */
  @RequestMapping(value = "/media/save", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse saveMedia(Authentication token, @Valid SPMediaForm mediaForm) {
    return ControllerHelper.process(mediaHelper::saveMedia, token, mediaForm);
  }
  
  /**
   * Controller method to get all the media files 
   * 
   * @param image
   *          - company image
   * @param token
   *          - logged in user
   * @return
   *     the response to the upload request
   */
  @RequestMapping(value = "/media/getAll", method = RequestMethod.GET)
  @ResponseBody 
  public SPResponse getAllMediaFiles(Authentication token) {
    return ControllerHelper.process(mediaHelper::getAllMediaFiles, token);
  }
  
  /**
   * Controller method to update the media file uploaded through admin interface.
   * 
   * @param mediaForm
   *          - SPMediaForm 
   * @param token
   *          - logged in user
   * @return
   *     the response to the upload request
   */
  @RequestMapping(value = "/sysAdmin/media/update", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse updateMedia(Authentication token, @Valid SPMediaForm mediaForm) {
    return ControllerHelper.process(mediaHelper::updateMedia, token, mediaForm);
  }
  
  /**
   * Controller method to get a media file
   * 
   * @param id
   *          - String 
   * @param token
   *          - logged in user
   * @return
   *     the response to the upload request
   */
  @RequestMapping(value = "/sysAdmin/media/get", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse getMedia(Authentication token, String id) {
    return ControllerHelper.process(mediaHelper::getMedia, token, id);
  }
  
  /**
   * Controller method to delete the media file.
   * 
   * @param imageId
   *          - String
   * @param token
   *          - logged in user
   * @return
   *     the response to the upload request
   */
  @RequestMapping(value = "/sysAdmin/media/delete", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse deleteMedia(Authentication token, @RequestParam String imageId,
      @RequestParam String imagePath) {
    return ControllerHelper.process(mediaHelper::deleteMedia, token, imageId, imagePath);
  }
}
