package com.sp.web.controller.systemadmin.library;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Dax Abraham
 *
 *         The Controller for the administrator interface for Learning Library.
 */
@Controller
@Scope("session")
public class TrainingLibraryAdminController {
  
  @Autowired
  TrainingLibraryAdminControllerHelper helper;
  
  /**
   * Controller method to get the list of all the companies for training library.
   * 
   * @param token
   *          - logged in user
   * @return the list of companies
   */
  @RequestMapping(value = "/sysAdmin/learning/library/getAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAll(Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::getAll, token);
  }
  
  /**
   * Controller method to get all the articles for the given company.
   * 
   * @param companyId
   *          - company id
   * @param token
   *          - logged in user
   * @return the response to the update status
   */
  @RequestMapping(value = "/sysAdmin/learning/library/articles", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getArticles(@RequestParam String companyId, Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::getArticles, token, companyId);
  }
  
  /**
   * The controller method to delete the articles for the given company id.
   * 
   * @param companyId
   *          - company
   * @param token
   *          - logged in user
   * @return the response for the delete operation
   */
  @RequestMapping(value = "/sysAdmin/learning/library/delete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse delete(@RequestParam String companyId, Authentication token) {
    // process the get hiring subscriptions request
    return process(helper::delete, token, companyId);
  }
  
  /**
   * View For Learning Library Home.
   * 
   */
  @RequestMapping(value = "/sysAdmin/learning/curriculam/libraryHome", method = RequestMethod.GET)
  public String libraryHome(Authentication token, @RequestParam(required = false) String theme) {
    return "libraryHome";
  }
  
  /**
   * View For Create Learning Library Home.
   * 
   */
  @RequestMapping(value = "/sysAdmin/learning/curriculam/createLibraryHome", method = RequestMethod.GET)
  public String createLibraryHome(Authentication token, @RequestParam(required = false) String theme) {
    return "createLibraryHome";
  }
  
  /**
   * View For Learning Library Home Details Page.
   * 
   */
  @RequestMapping(value = "/sysAdmin/learning/curriculam/libraryHomeDetails", method = RequestMethod.GET)
  public String libraryHomeDetails(Authentication token,
      @RequestParam(required = false) String theme) {
    return "libraryHomeDetails";
  }
  
  /**
   * Controller method to get all the articles present in the system.
   * 
   * @param token
   *          - logged in user
   * @return the response to the update status
   */
  @RequestMapping(value = "/sysAdmin/learning/getAllArticles", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllArticles(Authentication token) {
    
    // process the get hiring subscriptions request
    return process(helper::getAllArticles, token);
  }
  
}
