package com.sp.web.controller.systemadmin.pulse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.Constants;
import com.sp.web.dto.pulse.PulseQuestionSetDTO;
import com.sp.web.dto.pulse.PulseQuestionSetDownloadDTO;
import com.sp.web.dto.pulse.PulseQuestionSetSummaryDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.form.pulse.CreatePulseForm;
import com.sp.web.model.User;
import com.sp.web.model.pulse.PulseQuestionBean;
import com.sp.web.model.pulse.PulseQuestionSet;
import com.sp.web.model.pulse.PulseQuestionSetStatus;
import com.sp.web.model.pulse.QuestionOptions;
import com.sp.web.model.pulse.QuestionSetType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.pulse.WorkspacePulseRepository;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Dax Abraham
 *
 *         The helper method for the pulse administration screens.
 */
@Component
@Scope("prototype")
public class PulseAdminControllerHelper {
  
  private static final Logger log = Logger.getLogger(PulseAdminControllerHelper.class);
  
  @Autowired
  WorkspacePulseRepository pulseRepository;
  
  private PulseQuestionSet tempPulseQuetionSet;
  
  /**
   * Controller helper to get all the pulse questions.
   * 
   * @param user
   *          - logged in user
   * @return the response to the get all request
   */
  public SPResponse getAll(User user) {
    final SPResponse resp = new SPResponse();
    List<PulseQuestionSet> pulseQuestionSetList = pulseRepository.getAllPulseQuestionSets();
    return resp.add(
        Constants.PARAM_PULSE_QUESTION,
        pulseQuestionSetList.stream().map(PulseQuestionSetSummaryDTO::new)
            .collect(Collectors.toList()));
  }
  
  /**
   * Helper method to update the pulse question set status.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the update status
   */
  public SPResponse updateStatus(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    String pulseQuestionSetId = (String) params[0];
    PulseQuestionSetStatus status = (PulseQuestionSetStatus) params[1];
    
    PulseQuestionSet findPulseQuestionSetById = pulseRepository
        .findPulseQuestionSetById(pulseQuestionSetId);
    Assert.notNull(findPulseQuestionSetById, "Pulse questoin set not found for given id.");
    
    // update the pulse question set status if not same
    if (findPulseQuestionSetById.getStatus() != status) {
      findPulseQuestionSetById.setStatus(status);
      pulseRepository.savePulseQuestionSet(findPulseQuestionSetById);
    }
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to store the pulse form information.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the create request
   */
  public SPResponse createPulse(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    CreatePulseForm pulseForm = (CreatePulseForm) params[0];
    
    // create a new pulse question set
    PulseQuestionSet pulseQuestionSet = pulseForm.generatePulseQuestionSet();
    pulseQuestionSet.setCategoryKeys(tempPulseQuetionSet.getCategoryKeys());
    pulseQuestionSet.setQuestions(tempPulseQuetionSet.getQuestions());
    
    // save it to the repository
    pulseRepository.savePulseQuestionSet(pulseQuestionSet);
    
    return resp.isSuccess();
  }
  
  /**
   * The helper method to create the pulse file.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - parameters
   * @return the pulse file success
   */
  public SPResponse createPulseFile(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    MultipartFile pulseQuestionSet = (MultipartFile) params[0];
    ObjectMapper om = new ObjectMapper();
    try {
      tempPulseQuetionSet = om.readValue(pulseQuestionSet.getBytes(), PulseQuestionSet.class);
    } catch (IOException e) {
      log.fatal("Could not load the default pulse question set from file !!!", e);
      throw new InvalidRequestException("Could not load default pulse question set from file.");
    }
    validatePulseFile(tempPulseQuetionSet);
    return resp.add(Constants.PARAM_PULSE_QUESTION, tempPulseQuetionSet);
  }
  
  /**
   * Method to validate the pulse file.
   * 
   * @param pulseQuetionSet
   *          - pulse question set
   */
  private void validatePulseFile(PulseQuestionSet pulseQuetionSet) {
    // validate if the category names are non empty
    List<String> categoryKeys = pulseQuetionSet.getCategoryKeys();
    Assert.notEmpty(categoryKeys, "Invalid File Data:  Category keys not found in file.");
    
    // validate the questions
    Map<String, List<PulseQuestionBean>> questions = pulseQuetionSet.getQuestions();
    Assert.notEmpty(questions, "Invalid File Data:  Questions not found in file.");
    
    for (String categoryKey : categoryKeys) {
      List<PulseQuestionBean> questionList = questions.get(categoryKey);
      Assert.notEmpty(questionList, "Invalid File Data:  Questions not found for category key :"
          + categoryKey);
      PulseQuestionBean pulseQuestionBean = questionList.get(0);
      List<QuestionOptions> optionsList = pulseQuestionBean.getOptionsList();
      Assert.notEmpty(optionsList,
          "Invalid File Data:  Options not found for first question in category :" + categoryKey);
      int optionsListSize = optionsList.size();
      for (PulseQuestionBean pqb : questionList) {
        Assert.hasText(pqb.getDescription(),
            "Invalid File Data:  Description not found in question under category :" + categoryKey);
        List<QuestionOptions> optionsListTemp = pqb.getOptionsList();
        Assert.notEmpty(optionsListTemp,
            "Invalid File Data:  Options not found for first question in category :" + categoryKey);
        Assert.isTrue(optionsListTemp.size() == optionsListSize,
            "Invalid File Data:  All questions in category must have same options :" + categoryKey);
      }
    }
  }
  
  /**
   * Helper method to get the pulse question set details for the given pulse question set id.
   * 
   * @param user
   *          - the logged in user
   * @param params
   *          - parameters
   * @return the response to the get request
   */
  public SPResponse getDetails(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    String pulseQuestionSetId = (String) params[0];
    
    PulseQuestionSet pulseQuestionSet = pulseRepository
        .findPulseQuestionSetById(pulseQuestionSetId);
    resp.add(Constants.PARAM_PULSE_QUESTION, new PulseQuestionSetDTO(pulseQuestionSet));
    return resp;
  }
  
  /**
   * Helper method to download the pulse question set as a json file.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the success flag
   */
  public SPResponse downloadJson(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    String pulseQuestionSetId = (String) params[0];
    final HttpServletResponse response = (HttpServletResponse) params[1];
    boolean doDownload = (boolean) params[2];
    PulseQuestionSet pulseQuestionSet = pulseRepository
        .findPulseQuestionSetById(pulseQuestionSetId);
    
    if(doDownload){
      ObjectMapper om = new ObjectMapper();
      byte[] bytes;
      try {
        bytes = om.writeValueAsBytes(new PulseQuestionSetDownloadDTO(pulseQuestionSet));
      } catch (JsonProcessingException e1) {
        log.warn("Error writing the pulse question set.", e1);
        throw new InvalidRequestException("Unable to process the request.");
      }
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
      baos.write(bytes, 0, bytes.length);
      response.setContentLength(baos.size());
      response.setContentType("application/json");
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
    }else{
      PulseQuestionSetDownloadDTO questionSetDownload = new PulseQuestionSetDownloadDTO(pulseQuestionSet);
      resp.add("pulseQuestionSetPreview",questionSetDownload);
    }
    
    
    return resp.isSuccess();
  }
  
  /**
   * Update the pulse question sets.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the update request
   */
  @SuppressWarnings("unchecked")
  public SPResponse update(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    String pulseQuestionSetId = (String) params[0];
    QuestionSetType type = (QuestionSetType) params[1];
    List<String> companyIds = (List<String>) params[2];
    String name = (String) params[3];
    final boolean isForAll = (boolean) params[4];
    
    PulseQuestionSet pulseQuestionSet = pulseRepository
        .findPulseQuestionSetById(pulseQuestionSetId);
    
    if (StringUtils.isNotBlank(name)) {
      pulseQuestionSet.setName(name.trim());
    }
    pulseQuestionSet.setQuestionSetType(type);
    pulseQuestionSet.setCompanyId(companyIds);
    if (isForAll) {
      pulseQuestionSet.setForAll(true);
      pulseQuestionSet.setCompanyId(null);
    } else {
      pulseQuestionSet.setForAll(false);
    }
    pulseRepository.savePulseQuestionSet(pulseQuestionSet);
    
    return resp.isSuccess();
  }
  
}
