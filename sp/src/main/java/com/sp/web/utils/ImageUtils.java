package com.sp.web.utils;

import com.sp.web.Constants;
import com.sp.web.exception.SPException;
import com.sp.web.model.Company;
import com.sp.web.model.User;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.imageio.ImageIO;

public class ImageUtils {

  private static final Logger LOG = Logger.getLogger(ImageUtils.class);

  /**
   * Scale the image stored in the byte array to a specific width.
   */
  public static byte[] scaleImageToWidth(byte[] originalBytes, int scaledWidth) throws IOException {
    BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalBytes));
    int originalWidth = originalImage.getWidth();
    if (originalWidth <= scaledWidth) {
      return originalBytes;
    }
    int scaledHeight = (int) (originalImage.getHeight() * ((float) scaledWidth / (float) originalWidth));
    BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics2D = scaledImage.createGraphics();
    graphics2D.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
    graphics2D.dispose();
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    ImageIO.write(scaledImage, "jpeg", byteStream);
    return byteStream.toByteArray();
  }

  /**
   * <code>getUserImageSize</code> method will return the userImage size as
   * requested.
   * 
   * @param user
   *          containing list of user images
   * @return the updated profile image or the default profile image
   */
  public static String getUserImage(User user) {
    Properties environment = getEnvironmentProps();

    // if the profile image is not set send the default profile image.
    if (user == null || StringUtils.isBlank(user.getProfileImage())) {
      return MessageFormat.format(environment.getProperty(Constants.DEFAULT_IMAGE_PATH_KEY),
          environment.getProperty(Constants.S3_FILE_PREFIX));
    }

    // return the updated profile image
    return MessageFormat.format(user.getProfileImage(), environment.getProperty(Constants.S3_FILE_PREFIX));
  }

  private static Properties getEnvironmentProps() {
    String filePath = System.getProperty("appPropsFile");
    Resource resource = new FileSystemResource(filePath + "/application.properties");
    Properties environment = null;
    try {
      environment = PropertiesLoaderUtils.loadProperties(resource);
    } catch (IOException e) {
      LOG.error("Application.proprerties file not found");
      throw new SPException("Application.properties file not present, can't upload the user image");
    }
    return environment;
  }

  /**
   * Get the logo image for the given company.
   * 
   * @param company
   *          - company
   * @return
   *    logo image
   */
  public static String getCssUrl(String themeUrl) {
    Properties environment = getEnvironmentProps();

    if (StringUtils.isBlank(themeUrl)) {
      return null;
    }

    // return the updated profile image
    return MessageFormat.format(themeUrl, environment.getProperty(Constants.S3_FILE_PREFIX));
  }
  
  /**
   * Get the logo image for the given company.
   * 
   * @param company
   *          - company
   * @return
   *    logo image
   */
  public static String getCompanyImage(Company company) {
    Properties environment = getEnvironmentProps();

    final String logoImage = company.getLogoImage();
    // if the profile image is not set send the default profile image.
    if (StringUtils.isBlank(logoImage)) {
      return null;
    }

    // return the updated profile image
    return MessageFormat.format(logoImage, environment.getProperty(Constants.S3_FILE_PREFIX));
  }
  
  
  /**
   * <code>getMediaFullPath</code> method will return the media with full path for Media Admin Screen.
   * 
   * @param imageUrl
   *          String
   * @return  the updated media with actual URL
   */
  public static String getMediaFullPath(String imageUrl) {
    Properties environment = getEnvironmentProps();
    
    return MessageFormat.format(imageUrl, environment.getProperty(Constants.S3_FILE_PREFIX));
  }
  /**
   * Helper method to strip the domain part from Image url path. 
   *   This will give us the exact name, the way we stored the object in s3
   * @param imageUrl
   * @return String image object name
   */
  public static String stripDomainFromImagePath(String imageUrl) {
    Properties environment = getEnvironmentProps();
    
    return imageUrl.replace(environment.getProperty(Constants.S3_FILE_PREFIX) +"/", ""); 
  }
}
