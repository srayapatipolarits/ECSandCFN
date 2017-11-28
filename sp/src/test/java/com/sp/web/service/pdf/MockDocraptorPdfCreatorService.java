package com.sp.web.service.pdf;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

@Profile("Test")
@Service("docraptorPdfService")
public class MockDocraptorPdfCreatorService implements PDFCreatorService {
  
  @Override
  public ByteArrayOutputStream createPDF(String templateName, Map<String, Object> params,
      String companyId, String notificationType) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public ByteArrayOutputStream createPDF(String pdfTextUrl) {
    
    File file = new File("/Users/pradeepruhil/git/surepeoplellc/sp-app/sp/src/test/resources/SP_resume_rd5.pdf");
    ByteArrayOutputStream arrayOutputStream = null;
    try {
      FileInputStream fileInputStream = new FileInputStream(file);
      byte[] byteArray = IOUtils.toByteArray(fileInputStream);
      
      fileInputStream.close();
      arrayOutputStream = new ByteArrayOutputStream();
      arrayOutputStream.write(byteArray);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return arrayOutputStream;
  }
  
}
