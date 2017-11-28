package com.sp.web.service.pdf;

import com.docraptor.ApiClient;
import com.docraptor.ApiException;
import com.docraptor.Doc;
import com.docraptor.DocApi;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Service("docraptorPdfService")
@Profile("PROD")
public class DocratorPdfCreatorService implements PDFCreatorService {
  
  private static final Logger log = Logger.getLogger(DocratorPdfCreatorService.class);
  @Autowired
  private Environment enviornment;
  
  @Override
  public ByteArrayOutputStream createPDF(String templateName, Map<String, Object> params,
      String companyId, String notificationType) {
    return null;
  }
  
  @Override
  public ByteArrayOutputStream createPDF(String pdfTextUrl) {
    if (log.isDebugEnabled()) {
      log.debug("Url for which pdf to be created is: " + pdfTextUrl);
    }
    /* check if test env or not */
    String env = enviornment.getProperty("docraptor.enviornment");
    String apiKey = enviornment.getProperty("docraptor.apiKey");
    if (env == null) {
      env = "Test";
    }
    DocApi docraptor = new DocApi();
    ApiClient client = docraptor.getApiClient();
    client.setUsername(apiKey);
    
    Doc doc = new Doc();
    if (env.equalsIgnoreCase("Test")) {
      doc.setTest(true);
    } else {
      doc.setTest(false);
    }
    
    doc.setDocumentUrl(pdfTextUrl);
    doc.setJavascript(true);
    doc.setDocumentType(Doc.DocumentTypeEnum.PDF);
    doc.setName("docraptor-java.pdf");
    byte[] createDoc = null;
    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
    try {
      createDoc = docraptor.createDoc(doc);
      arrayOutputStream.write(createDoc);
    } catch (ApiException e) {
      log.error("Error occureing while creating the pdf doc", e);
    } catch (IOException e) {
      log.error("Error occureing while creating the pdf doc", e);
    }
    return arrayOutputStream;
  }
}
