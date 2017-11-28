package com.sp.web.controller.systemadmin.smartling;

import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.dto.file.UploadFileData;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.FileApiClient;
import com.smartling.api.sdk.file.FileApiClientImpl;
import com.smartling.api.sdk.file.FileType;
import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetFileParameterBuilder;
import com.smartling.api.sdk.file.parameters.RetrievalType;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;

@Service
public class SmartlingSystemConnector implements TranslationSystemConnector {
  
  private static final Logger LOG = Logger.getLogger(SmartlingSystemConnector.class);
  
  private static final String USER_ID = "smartling.webapp.userid";
  private static final String USER_SECRET = "smartling.webapp.tokensecret";
  private static final String PROJECT_ID = "smartling.webapp.projectid";
  
  private static final String FILE_ENCODING = "UTF-8";
  private static final FileType FILE_TYPE = FileType.XML;
  private static final String CALLBACK_URL = "http://preprod.surepeople.com/translation/download/";
  
  @Autowired
  private Environment enviornment;
  
  @Override
  public boolean uploadFiles(Collection<File> files) {
    
    String userId = enviornment.getProperty(USER_ID);
    String userSecret = enviornment.getProperty(USER_SECRET);
    String projectId = enviornment.getProperty(PROJECT_ID);
    FileApiClient smartlingFapi = new FileApiClientImpl.Builder(projectId).authWithUserIdAndSecret(
        userId, userSecret).build();
    
    // upload the file
    for (File file : files) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Upload file to smartling" + getFileUri(file));
      }
      FileUploadParameterBuilder fileUploadParameterBuilder = new FileUploadParameterBuilder(
          FILE_TYPE, getFileUri(file));
      String callbackUrl = CALLBACK_URL + FilenameUtils.removeExtension(getFileUri(file));
      fileUploadParameterBuilder.charset(FILE_ENCODING).approveContent(false)
          .callbackUrl(callbackUrl);
      UploadFileData uploadFileResponse = null;
      try {
        uploadFileResponse = smartlingFapi.uploadFile(file, fileUploadParameterBuilder);
      } catch (SmartlingApiException e) {
        LOG.error("Error occurred while uploading file to smartling :" + getFileUri(file), e);
      }
      LOG.debug(uploadFileResponse);
    }
    
    return true;
  }
  
  @Override
  public String downloadFiles(String filesName, String locale, String fileExtension) {
    
    String userId = enviornment.getProperty(USER_ID);
    String userSecret = enviornment.getProperty(USER_SECRET);
    String projectId = enviornment.getProperty(PROJECT_ID);
    
    FileApiClient smartlingFapi = new FileApiClientImpl.Builder(projectId).authWithUserIdAndSecret(
        userId, userSecret).build();
    
    final String fileIdentifier = filesName + "." + fileExtension;
    
    StringResponse translatedContent = null;
    try {
      translatedContent = smartlingFapi.getFile(new GetFileParameterBuilder(fileIdentifier, locale)
          .retrievalType(RetrievalType.PUBLISHED));
    } catch (SmartlingApiException e) {
      LOG.error("Error occurred while download file " + filesName, e);
    }
    return translatedContent.getContents();
    
  }
  
  private static String getFileUri(File file) {
    return file.getName();
  }
  
}
