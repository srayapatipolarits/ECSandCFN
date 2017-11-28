package com.sp.web.service.smartling;

import com.smartling.api.sdk.dto.file.FileLastModified;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.dto.file.UploadFileData;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.FileApiClient;
import com.smartling.api.sdk.file.FileApiClientImpl;
import com.smartling.api.sdk.file.FileType;
import com.smartling.api.sdk.file.parameters.FileLastModifiedParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileListSearchParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetFileParameterBuilder;
import com.smartling.api.sdk.file.parameters.RetrievalType;
import com.smartling.api.sdk.file.response.EmptyResponse;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.FileStatus;
import com.sp.web.mvc.test.setup.SPTestBase;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SmartlingIntegrationTest extends SPTestBase {
  
  private static final String USER_ID = "jyuxomvtsngtnjdndahyorhymdzzkd";
  private static final String USER_SECRET = "b8l4vdk3hopdiv8t6g8e988cn0CH(fo2u5h1gmjgb8s9q1ce5j03s8i";
  private static final String PROJECT_ID = "ad0fbbbdb";
  private static final String LOCALE = "es_LA";
  
  private static final String PATH_TO_FILE = "feedbackMessages.xml";
  private static final String FILE_ENCODING = "UTF-8";
  private static final FileType FILE_TYPE = FileType.XML;
  private static final String CALLBACK_URL = null;
  
  @Test
  public void testSmartlingIntegration() throws SmartlingApiException {
    FileApiClient smartlingFAPI = new FileApiClientImpl.Builder(PROJECT_ID)
        .authWithUserIdAndSecret(USER_ID, USER_SECRET).build();
    
    // upload the file
    File file = new File(PATH_TO_FILE);
    FileUploadParameterBuilder fileUploadParameterBuilder = new FileUploadParameterBuilder(
        FILE_TYPE, getFileUri(file));
    List<String> localesToAprrove = new ArrayList<String>();
    localesToAprrove.add("es_LA");
//    fileUploadParameterBuilder.localeIdsToAuthorize(localesToAprrove);
    fileUploadParameterBuilder.charset(FILE_ENCODING).approveContent(true)
        .callbackUrl(CALLBACK_URL);
    UploadFileData uploadFileResponse = smartlingFAPI.uploadFile(file, fileUploadParameterBuilder);
    System.out.println(uploadFileResponse);
    
    // get last modified date
    FileLastModified lastModifiedResponse = smartlingFAPI
        .getLastModified(new FileLastModifiedParameterBuilder(getFileUri(file)).locale(LOCALE));
    System.out.println(lastModifiedResponse);
    
    // rename the file
    final String fileIdentifier = "feedbackMessagesXML";
    EmptyResponse renameFileResponse = smartlingFAPI.renameFile(getFileUri(file), fileIdentifier);
    System.out.println(renameFileResponse);
    
    // run a search for files
    FileList filesListResponse = smartlingFAPI.getFilesList(new FileListSearchParameterBuilder()
        .withUriMask(fileIdentifier));
    System.out.println(filesListResponse);
    
    // check the file status
    FileStatus fileStatusResponse = smartlingFAPI.getFileStatus(fileIdentifier);
    System.out.println(fileStatusResponse.getItems());
    
    if(fileStatusResponse.getTotalWordCount() == uploadFileResponse.getWordCount()){
      // get the file back, including any translations that have been published.
      StringResponse translatedContent = smartlingFAPI.getFile(new GetFileParameterBuilder(
          fileIdentifier, LOCALE).retrievalType(RetrievalType.PUBLISHED));
      System.out.println(translatedContent.getContents());
      
      // delete the file
      EmptyResponse deleteFileResponse = smartlingFAPI.deleteFile(fileIdentifier);
      System.out.println(deleteFileResponse);      
    }

  }
  
  private static String getFileUri(File file) {
    return file.getName();
  }
}
