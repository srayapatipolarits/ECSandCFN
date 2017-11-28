package com.sp.web.controller.systemadmin.smartling;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.translation.SmartlingJsonGoals;
import com.sp.web.service.translation.SmartlingJsonPersonalityPractice;
import com.sp.web.service.translation.SmartlingJsonTutorial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * SPSmartlingController class will upload/download the xml files for transaltion from smartling
 * language translation system.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class SPSmartlingController {
  
  @Autowired
  private SPTranslationControllerHelper helper;
  
  @RequestMapping(value = "/translation/upload", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse uploadFiles() {
    return ControllerHelper.doProcess(helper::uploadTranslations, "smartling");
  }
  
  @RequestMapping(value = "/translation/upload/{fileName}", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse uploadFile(@PathVariable String fileName) {
    return ControllerHelper.doProcess(helper::uploadTranslation, "smartling", fileName);
  }
  
  @RequestMapping(value = "/translation/download/{fileName}", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse downloadFiles(@PathVariable String fileName,
      @RequestParam(required = false, defaultValue = "es-LA") String locale) {
    return ControllerHelper.doProcess(helper::downloadFiles, fileName, locale, "smartling");
  }
  
  @RequestMapping(value = "/sysAdmin/createJsonDataTranslableSource", method = RequestMethod.GET)
  @ResponseBody
  public SmartlingJsonGoals createDataBaseTranslationData(Authentication token) {
    return helper.createJsonDataTranslableSource();
  }

  @RequestMapping(value = "/sysAdmin/createTutorialJson", method = RequestMethod.GET)
  @ResponseBody
  public SmartlingJsonTutorial createTutorialJson(Authentication token) {
    return helper.createTutorialJson();
  }
  
  @RequestMapping(value = "/sysAdmin/createJsonDataTranslableSourcePersonality", method = RequestMethod.GET)
  @ResponseBody
  public SmartlingJsonPersonalityPractice createJsonDataTranslableSourcePersonality(Authentication token) {
    return helper.createJsonDataTranslableSourcePersonality();
  }
  
  @RequestMapping(value = "/translation/updatePracticeAreaJson", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse uploadPracticeAreaJson(@RequestParam MultipartFile practiceAreaJson,
      @RequestParam MultipartFile spanishPracticeAreaJson, @RequestParam String locale,
      Authentication token) {
    return ControllerHelper.process(helper::uploadPracticeAreaJson, token, practiceAreaJson,
        spanishPracticeAreaJson, locale);
  }
  
  @RequestMapping(value = "/translation/updatePracticeAreaJsonMongo", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updatePracticeAreaJsonMongo(Authentication token) {
    return ControllerHelper.doProcess(helper::updatePracticeAreaJsonMongo,"es_LA");
  }
  
  @RequestMapping(value = "/translation/updateTutoriaToMongo", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateTutoriaToMongo(Authentication token) {
    return ControllerHelper.doProcess(helper::updateTutoriaToMongo,"es_LA");
  }
  
  @RequestMapping(value = "/translation/updatePersonalityPracticeAreaJsonMongo", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updatePersonalityPracticeAreaJsonMongo() {
    return ControllerHelper.doProcess(helper::updatePersonalityPracticeAreaJsonMongo,"es_LA");
  }
  
  @RequestMapping(value = "/translation/updateTutorial", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse updateTutorial(Authentication token) {
    return ControllerHelper.process(helper::updateTutorial, token, "es_LA");
  }
  
}
