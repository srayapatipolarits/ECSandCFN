package com.sp.web.service.pdf;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.sp.web.Constants;
import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestBase;
import com.sp.web.service.email.CommunicationGateway;
import com.sp.web.service.email.DataSourceAttahcment;
import com.sp.web.service.email.EmailParams;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ITextPDFCreatorServiceTest extends SPTestBase {

  @Autowired
  ITextPDFCreatorService pdfCreatorService;
  
  @Autowired
  CommunicationGateway gateway;
  
  @Before
  public void before() {
    testSmtp.start();
  }
  
  @After
  public void after() {
    testSmtp.stop();
  }
  
  @Test
  public void test() {
    assertNotNull(pdfCreatorService);
    final String htmlText = "<html><body><h1>Hi there</h1><table>"
              + "<tr><td>Test data</td></tr></table></body></html>";
    StringBuffer buf = new StringBuffer();
    buf.append("<html>");
    
    // put in some style
    buf.append("<head><style language='text/css'>");
    buf.append("h3 { border: 1px solid #aaaaff; background: #ccccff; ");
    buf.append("padding: 1em; text-transform: capitalize; font-family: sansserif; font-weight: normal;}");
    buf.append("p { margin: 1em 1em 4em 3em; } p:first-letter { color: red; font-size: 150%; }");
    buf.append("h2 { background: #5555ff; color: white; border: 10px solid black; padding: 3em; font-size: 200%; }");
    buf.append("</style></head>");
    
    // generate the body
    buf.append("<body>");
    buf.append("<p><img src='100bottles.jpg'/></p>");
    for (int i = 99; i > 0; i--) {
      buf.append("<h3>" + i + " bottles of beer on the wall, " + i + " bottles of beer!</h3>");
      buf.append("<p>Take one down and pass it around, " + (i - 1)
          + " bottles of beer on the wall</p>\n");
    }
    buf.append("<h2>No more bottles of beer on the wall, no more bottles of beer. ");
    buf.append("Go to the store and buy some more, 99 bottles of beer on the wall.</h2>");
    buf.append("</body>");
    buf.append("</html>");    
    //String htmlText = "This is test data.";
    ByteArrayOutputStream os = pdfCreatorService.createPDF(htmlText);
    try {
      File file = new File("test.pdf");
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(os.toByteArray());
      fos.close();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void test2() {
    assertNotNull(pdfCreatorService);
    final Map<String, Object> params = new HashMap<String, Object>();
    User user = new User();
    user.setFirstName("Dax");
    user.setLastName("Abraham");
    user.setEmail("dax@surepeople.com");
    params.put(Constants.PARAM_MEMBER, user);
    ByteArrayOutputStream bos = pdfCreatorService.createPDF("templates/pdf/spInvoice.stg", params, "1","Invoice");
    try {
      File file = new File("test.pdf");
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(bos.toByteArray());
      fos.close();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void test3() {
    assertNotNull(pdfCreatorService);
    assertNotNull(gateway);
    
    final Map<String, Object> params = new HashMap<String, Object>();
    User user = new User();
    user.setFirstName("Dax");
    user.setLastName("Abraham");
    user.setEmail("pradeep@surepeople.com");
    params.put(Constants.PARAM_MEMBER, user);
    ByteArrayOutputStream bos = pdfCreatorService.createPDF("templates/pdf/spInvoice.stg", params,"1","Invoice");
    try {
      EmailParams emailParams = new EmailParams();
      emailParams.setTos("dax@surepeople.com");
      emailParams.setSubject("Test attachment email !!!");
      emailParams.setEmailBody("Hi so and so. Test attachment email.");
      emailParams.addDataSourceAttachment(new DataSourceAttahcment("invoice.pdf", bos, "application/pdf"));
      gateway.sendMessage(emailParams);
      Thread.sleep(5000);
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
}
