package com.sp.web.service.external.rest;

import com.bullhornsdk.data.api.BullhornData;
import com.bullhornsdk.data.model.entity.core.standard.Candidate;
import com.bullhornsdk.data.model.entity.core.standard.Note;
import com.bullhornsdk.data.model.entity.core.standard.Person;
import com.bullhornsdk.data.model.entity.embedded.OneToMany;
import com.bullhornsdk.data.model.enums.FileContentType;
import com.bullhornsdk.data.model.parameter.standard.StandardFileParams;
import com.bullhornsdk.data.model.response.crud.CrudResponse;
import com.sp.web.Constants;
import com.sp.web.exception.FallbackFailException;
import com.sp.web.exception.SPException;
import com.sp.web.model.HiringUser;
import com.sp.web.model.ThirdPartyUser;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenType;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.external.rest.PartnerRequest;
import com.sp.web.model.external.rest.PartnerResponse;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.repository.external.ThirdPartyRepository;
import com.sp.web.service.fallback.FallbackFactory;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.service.pdf.PDFCreatorService;
import com.sp.web.service.pdf.PdfSourceType;
import com.sp.web.service.sse.ActionType;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.service.token.TokenRequest;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("medixActionProcessor")
public class MedixPartnerActionProcessor implements PartnerActionProcesssor {
  
  /** INitializing the logger. */
  
  private static final Logger log = Logger.getLogger(MedixPartnerActionProcessor.class);
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @Autowired
  private ThirdPartyRepository thirdPartyRepository;
  
  @Autowired
  @Qualifier("bullhornData")
  private BullhornData bullHornData;
  
  @Autowired
  LoginHelper loginHelper;
  
  @Autowired
  private SPTokenFactory tokenFactory;
  
  @Autowired
  @Qualifier("docraptorPdfService")
  private PDFCreatorService pdfCreatorService;
  
  @Autowired
  private FallbackFactory fallbackFactory;
  
  @Autowired
  private Environment environment;
  
  /**
   * processAction method will process the prism process action request.
   */
  @Override
  public PartnerResponse processAction(PartnerRequest partnerRequest) {
    PartnerResponse response = new PartnerResponse();
    try {
      String actionTypeString = (String) partnerRequest.getInputRequestMap().get("action");
      ActionType actionType = ActionType.valueOf(actionTypeString);
      switch (actionType) {
      case PartnerPrism:
        processPrism(partnerRequest, response);
        break;
      case PartnerPrismStatus:
        addNote(partnerRequest, response);
        break;
      
      case PartnerPrismResult:
        uploadPrismPortrait(partnerRequest, response);
        break;
      case DeletePartner:
        deleteTalent(partnerRequest, response);
      default:
        break;
      }
      //
    } catch (Exception ex) {
      log.error("Error occurred whle processing the request : " + partnerRequest, ex);
      /* check if the request is coming from a fallback dont create new */
      Boolean fallback = (Boolean) partnerRequest.getInputRequestMap().get("fallbackRequest");
      if (fallback == null || !fallback.booleanValue()) {
        fallbackFactory.createFallback(partnerRequest, "medixFallbackProcessor",
            partnerRequest.getCompanyId());
        
        /* send an email for the fallback failure */
        // Todo
      } else {
        throw new FallbackFailException("Unable to process Fallback", ex);
      }
      
    }
    return response;
  }
  
  private void deleteTalent(PartnerRequest partnerRequest, PartnerResponse response) {
    Map<String, Object> inputRequestMap = partnerRequest.getInputRequestMap();
    HiringUser user = (HiringUser) inputRequestMap.get("user");
    ThirdPartyUser thirdPartyUser = thirdPartyRepository.findBySpUserId(user.getId());
    
    List<Integer> notes = (List<Integer>) thirdPartyUser.getReferenceData().get("notes");
    if (notes != null) {
      
      for (Integer noteId : notes) {
        CrudResponse deleteEntity = bullHornData.deleteEntity(Note.class, noteId);
        if (deleteEntity.isError()) {
          log.error("Unable to delete the note from the bullhorn" + deleteEntity);
        }
      }
    }
    thirdPartyRepository.delete(thirdPartyUser);
    
  }
  
  /**
   * processPrism method will process the prism request for the Medix users.
   * 
   * @param partnerRequest
   *          partner request contains the partmertes.
   * @param response
   *          is the response returned.
   */
  public void processPrism(PartnerRequest partnerRequest, PartnerResponse response) {
    
    SPPlanType planType = (SPPlanType) partnerRequest.getInputRequestMap().get("planType");
    
    String uid = (String) partnerRequest.getInputRequestMap().get("uid");
    
    switch (planType) {
    case IntelligentHiring:
      ThirdPartyUser findByUser = thirdPartyRepository.findByExternalUid(uid);
      if (findByUser == null) {
        response.setModelView("errorUserNotFound");
        return;
      }
      
      HiringUser user = hiringUserFactory.getUser(findByUser.getSpUserId());
      
      loginHelper.authenticateUserAndSetSession(user);
      switch (user.getUserStatus()) {
      case ASSESSMENT_PENDING:
        // send them to the welcome page
        response.setModelView("partnerWelcome");
        break;
      case ASSESSMENT_PROGRESS:
        // take the candidates to the assessment screens
        response.setModelView("partnerAssessment");
        break;
      default:
        // take them to the thank you/error page
        response.setModelView("partnerThankYou");
      }
      break;
    
    default:
      break;
    }
  }
  
  /**
   * Add the note to the user.
   * 
   * @param partnerRequest
   *          is the PartnerRequest.
   * @param response
   *          is the PartnerResponse.
   */
  private void addNote(PartnerRequest partnerRequest, PartnerResponse response) {
    Map<String, Object> inputRequestMap = partnerRequest.getInputRequestMap();
    HiringUser user = (HiringUser) inputRequestMap.get("user");
    ThirdPartyUser thirdPartyUser = thirdPartyRepository.findBySpUserId(user.getId());
    
    Note note = new Note();
    String message = MessagesHelper.getMessage("medix.userStatus."
        + user.getUserStatus().toString());
    note.setAction(message);
    note.setComments(message);
    
    note.setPersonReference(new Person(Integer.valueOf(thirdPartyUser.getUid())));
    List<Candidate> candidateList = new ArrayList<>();
    Candidate candidate1 = new Candidate(Integer.valueOf(thirdPartyUser.getUid()));
    candidateList.add(candidate1);
    
    OneToMany<Candidate> candidateOneToMany = new OneToMany<>();
    candidateOneToMany.setData(candidateList);
    note.setCandidates(candidateOneToMany);
    if (log.isDebugEnabled()) {
      log.debug("Creating note in BH: " + note);
    }
    CrudResponse insertEntity = bullHornData.insertEntity(note);
    if (insertEntity.isError()) {
      log.error("Error occurred while creating a note in BH ");
      throw new SPException("Unable to create note in BH");
    } else {
      
      List<Integer> notesId = (List<Integer>) thirdPartyUser.getReferenceData().get("notes");
      if (notesId == null) {
        notesId = new ArrayList<>();
        thirdPartyUser.getReferenceData().put("notes", notesId);
      }
      notesId.add(insertEntity.getChangedEntityId());
      thirdPartyRepository.save(thirdPartyUser);
    }
  }
  
  /**
   * uploadPrismPortrait method will generate the pdf and upload the same to the Bull horn
   * interface.
   * 
   * @param request
   *          is the partner request
   * @param response
   *          the partner repsonse.
   */
  private void uploadPrismPortrait(PartnerRequest request, PartnerResponse response) {
    
    HiringUser hiringUser = (HiringUser) request.getInputRequestMap().get("user");
    TokenRequest tokenRequest = new TokenRequest(TokenType.PERPETUAL);
    tokenRequest.addParam("hiringUserId", hiringUser.getId());
    tokenRequest.addParam("type", PdfSourceType.MedixRecruiter);
    Token token = tokenFactory.getToken(tokenRequest, TokenProcessorType.DOCRAPTOR_PDF);
    String tokenUrl = token.getTokenUrl();
    String path = environment.getProperty(Constants.PARAM_SHARED_PATH);
    path = path.concat("/pdf/");
    
    /* Generate the Pdf */
    ByteArrayOutputStream createPDF = pdfCreatorService.createPDF(tokenUrl);
    
    String fileName = path + hiringUser.getId() + hiringUser.getFirstName() + ".pdf";
    try {
      
      IOUtils.write(createPDF.toByteArray(), FileUtils.openOutputStream(new File(fileName)));
    } catch (IOException e) {
      log.error("Error occurred while  creating pdf", e);
    } finally {
      try {
        createPDF.close();
      } catch (IOException e) {
        log.error("Error occurred while  closing pdf", e);
      }
    }
    File createTempFile = new File(fileName);
    StandardFileParams fileParams = StandardFileParams.getInstance();
    fileParams.setContentType(FileContentType.PDF);
    fileParams.setType("assessment");
    fileParams.setDescription("Candidate SurePeople Assessment");
    ThirdPartyUser thirdPartyUser = thirdPartyRepository.findBySpUserId(hiringUser.getId());
    bullHornData.addFile(Candidate.class, Integer.valueOf(thirdPartyUser.getUid()), createTempFile,
        "spassessment", fileParams);
    
    createTempFile.delete();
    
  }
}
