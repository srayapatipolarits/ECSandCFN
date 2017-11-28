package com.sp.web.controller.image;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.dao.CompanyDao;
import com.sp.web.model.Company;
import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.product.CompanyFactory;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * @author pruhil
 *
 */
public class ProfileImageControllerTest extends SPTestLoggedInBase {

  @Autowired
  CompanyFactory companyFactory;
  
  @Before
  public void setUp() throws Exception {
    testSmtp.start();
    dbSetup.removeAllUsers();
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    dbSetup.createUsers();
    dbSetup.createCompanies();
    dbSetup.createAccounts();
  }

  @After
  public void after() throws Exception {
    testSmtp.stop();
  }
  /**
   * Test method for
   * {@link com.sp.web.controller.image.ProfileImageController#uploadImage(org.springframework.web.multipart.MultipartFile, org.springframework.security.authentication.Authentication)}
   * .
   * 
   * @throws Exception
   */
//  @Test
  public void testUploadImage() throws Exception {

    User user = new User();
    user.setEmail("dax@surepeople.com");
    user.setPassword("pwd123");
    authenticationHelper.doAuthenticate(session2, user);

    String base64String = "data:image/jpeg;base64,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";
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.post("/uploadImage").param("image", base64String).session(session2))
        // .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andExpect(jsonPath("$.success").exists())
        .andReturn();

  }

  @Test
  public void testLargeImage() throws Exception {

    File imgPath = new File("src/test/java/Invalid_large.jpg");
    ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
    BufferedImage img = ImageIO.read(imgPath);
    ImageIO.write(img, "jpg", baos);
    baos.flush();

    String base64String = Base64.getEncoder().encodeToString(baos.toByteArray());
    baos.close();
    byte[] bytearray = Base64.getDecoder().decode(base64String);

//    MockMultipartFile imageFile = new MockMultipartFile("image", "sample.jpg", "image/jpeg", bytearray);

    // mockMvc
    // .perform(MockMvcRequestBuilders.fileUpload("/uploadImage").file(imageFile).session(session))
    // .andExpect(jsonPath("$.error").exists())
    // .andExpect(
    // jsonPath("$.error.IllegalArgumentException").value(
    // "Image size is greater than max limit of 100,000")).andReturn();

  }

//  @Test
  public void testInvalidImageContent() throws Exception {

    File imgPath = new File("src/test/java/s3Image.jpg");
    ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
    BufferedImage img = ImageIO.read(imgPath);
    ImageIO.write(img, "jpg", baos);
    baos.flush();

    String base64String = Base64.getEncoder().encodeToString(baos.toByteArray());
    baos.close();
    byte[] bytearray = Base64.getDecoder().decode(base64String);

    MockMultipartFile imageFile = new MockMultipartFile("image", "s3Image.jpg", "image/tiff", bytearray);

    mockMvc
        .perform(MockMvcRequestBuilders.fileUpload("/uploadImage").file(imageFile).session(session))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(
            jsonPath("$.error.IllegalArgumentException").value(
                "Cannot accept content type image/tiff as a profile picture.")).andReturn();

  }
  
  //@Test
  public void testUpdateMetadata() {
    AWSCredentials awsCredentials = new AWSCredentials("AKIAJ5O3GNFCYEPK64BA",
        "XFoZ7DwCyNAxJArxxWgsGVjl9YYWKfpOoggx8aPS");
    S3Service s3 = new RestS3Service(awsCredentials);
    try {
      final String bucketName = "surepeople.com";
      S3Object[] listObjects = s3.listObjects(bucketName);
      for (S3Object s3Obj : listObjects) {
        final String objName = s3Obj.getName();
        log.debug(objName);
        if (objName.endsWith(".jpg")) {
          try {
            s3Obj.addMetadata("Cache-Control", "max-age=31536000");
            s3.updateObjectMetadata(bucketName, s3Obj);
          } catch (ServiceException e) {
            e.printStackTrace();
          }
        }
      }
    } catch (S3ServiceException e) {
      log.error("Unable to retreive the s3 bucket ", e);
      throw new IllegalStateException("Unable to retrieve S3 Bucket", e);
    }
  }
  
  @Test
  public void testUpdateCompanyLogoImage() {
    dbSetup.removeAllCompanies();
    dbSetup.createCompanies();
    
    CompanyDao company = companyFactory.getCompany("1");
    
    final String logoImage = "some logo url";
    company.setLogoImage(logoImage);
    companyFactory.updateCompany(company);
    
    List<Company> all = dbSetup.getAll(Company.class);
    assertTrue(all.size() > 0);
    
    Company company2 = all.get(0);
    
    assertThat(company2.getLogoImage(), is(logoImage));
    
  }
  
}
