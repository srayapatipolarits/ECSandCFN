package com.sp.web.controller.systemadmin.media;

import com.sp.web.Constants;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dto.SPMediaDTO;
import com.sp.web.exception.SPException;
import com.sp.web.form.SPMediaForm;
import com.sp.web.model.Company;
import com.sp.web.model.FileData;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPMedia;
import com.sp.web.model.SPMediaType;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.media.MediaRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.image.FileStorage;
import com.sp.web.theme.ThemeCacheableFactory;
import com.sp.web.utils.GenericUtils;
import com.sp.web.utils.ImageUtils;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * MediaHelper class provides helper methods for media related functionalities.
 * 
 * @author Prasanna Venkatesh
 */
@Component
public class MediaHelper {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(MediaHelper.class);
  
  /** Enviornment to fetch the max file size and allowed image extension. */
  @Inject
  private Environment environment;
  
  /** file Storage to upload the file in repository. */
  @Autowired
  @Qualifier("s3FileStore")
  private FileStorage fileStorage;
  
  /** Account Repository to update the user object with the profile image path. */
  @Autowired
  private UserRepository userRepository;
  
  /** Login Helper to update the user object in the security context. */
  @Autowired
  private LoginHelper loginHelper;
  
  /** Media Repository for all media related db mappings. */
  @Autowired
  private MediaRepository mediaRepository;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private ThemeCacheableFactory themeCacheableFactory;
  
  /**
   * <code>saveProfilePicture</code> method will save profile picture in the user profile as path
   * and upload the image in the S3.
   * 
   * @param user
   *          logged in user
   * @param params
   *          multi-part user
   * @return the SPResponse whether images is uploaded successfully or some error occurred
   */
  public SPResponse saveProfilePicture(User user, Object[] params) {
    
    String imageValue = (String) params[0];
    if (LOG.isDebugEnabled()) {
      LOG.debug("User for which saving profile pciture " + user.getEmail());
    }
    
    String profileImage = getProfileImage(
        "profile/" + user.getFirstName() + "_" + user.getLastName() + "/" + user.getId() + "/"
            + user.getImageCount() + "/" + user.getFirstName() + ".jpg", imageValue);
    
    user.setProfileImage("{0}/" + profileImage);
    user.incrementImageCount();
    User updateProfileImage = userRepository.updateProfileImage(user);
    loginHelper.updateUser(updateProfileImage);
    SPResponse spResponse = new SPResponse();
    spResponse.isSuccess();
    return spResponse;
  }
  
  /**
   * Helper method to store the image for the company.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - the image data
   * @return the response to the upload request
   */
  public SPResponse uploadCompanyImage(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String imageValue = (String) params[0];
    String companyId = (String) params[1];
    CompanyDao company = null;
    if (user.hasAnyRole(RoleType.SuperAdministrator, RoleType.SysThemes)
        && StringUtils.isNotBlank(companyId)) {
      
      Assert.hasText(companyId, "Company id is required.");
      company = companyFactory.getCompany(companyId);
    } else {
      company = companyFactory.getCompany(user.getCompanyId());
    }
    
    String profileImage = getProfileImage(getImagePath(company), imageValue);
    
    // delete any previous image if any existed
    deleteImage(company.getLogoImage());
    
    // setting the new logo image
    company.setLogoImage("{0}/" + profileImage);
    company.incrementImageCount();
    
    companyFactory.updateCompanyDao(company);
    
    themeCacheableFactory.clearCache(company.getId());
    return resp.isSuccess();
  }
  
  /**
   * Get the image path for the company.
   * 
   * @param company
   *          - company
   * @return the image path for the company
   */
  private String getImagePath(Company company) {
    final String companyName = GenericUtils.normalize(company.getName());
    return "profile/companyLogo/" + companyName + "/" + company.getId() + "/" + companyName
        + company.getImageCount() + ".jpg";
  }
  
  private String getProfileImage(String imagePathAndName, String imageValue) {
    byte[] bytes = null;
    
    /* parse the base64 string and take out the content type */
    String[] split = imageValue.split(",");
    if (split == null || split.length != 2) {
      throw new SPException("Invalid Base 64 string");
    }
    
    String contentTypeString = split[0];
    String contentType = "image/jpeg";
    
    /* get the content type from the base64 sting */
    if (org.apache.commons.lang3.StringUtils.isNotBlank(contentTypeString)) {
      contentType = contentTypeString.substring(contentTypeString.indexOf(":") + 1,
          contentTypeString.indexOf(";"));
    }
    
    String base64String = split[1];
    
    bytes = Base64.getDecoder().decode(base64String);
    
    /* check if file size is of zero size */
    
    assertBytesLength(bytes);
    assertImageContentType(contentType);
    // assertFileSize(multipartFile);
    
    String profileImage = fileStorage.storeFile(new FileData(imagePathAndName, bytes, contentType));
    
    return profileImage;
  }
  
  /**
   * Helper method to delete the Company logo image.
   * 
   * @param user
   *          - logged in user
   * 
   * @return the response to the image delete request
   */
  public SPResponse deleteCompanyImage(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    CompanyDao company = null;
    if (user.hasRole(RoleType.SuperAdministrator)) {
      String companyId = (String) params[0];
      Assert.hasText(companyId, "Company id is required.");
      company = companyFactory.getCompany(companyId);
    } else {
      company = companyFactory.getCompany(user.getCompanyId());
    }
    
    // deleting the image from storage
    deleteImage(company.getLogoImage());
    company.setLogoImage(null);
    company.incrementImageCount();
    
    companyFactory.updateCompanyDao(company);
    themeCacheableFactory.clearCache(company.getId());
    return resp.isSuccess();
  }
  
  /**
   * Deleting the image.
   * 
   * @param imagePath
   *          - image path
   */
  private void deleteImage(String imagePath) {
    try {
      if (!org.apache.commons.lang3.StringUtils.isBlank(imagePath)) {
        fileStorage.removeFile(ImageUtils.stripDomainFromImagePath(imagePath));
      }
    } catch (Exception e) {
      // Just log the s3 Error. No need to throw it.
      LOG.error("Error occured while deleting the media from s3-->" + imagePath, e);
    }
  }
  
  /**
   * Helper method to delete the User profile image.
   * 
   * @param user
   *          - logged in user
   * 
   * @return the response to the image delete request
   */
  public SPResponse deleteProfileImage(User user) {
    final SPResponse resp = new SPResponse();
    
    try {
      fileStorage.removeFile(ImageUtils.stripDomainFromImagePath(user.getProfileImage()));
    } catch (Exception e) {
      // Just log the s3 Error. No need to throw it.
      LOG.error("Error occured while deleting the media from s3-->" + user.getProfileImage());
    }
    
    user.setProfileImage(null);
    user.incrementImageCount();
    
    User updateProfileImage = userRepository.updateProfileImage(user);
    loginHelper.updateUser(updateProfileImage);
    
    return resp.isSuccess();
  }
  
  /**
   * <code>saveMedia</code> method will save the media file uploaded through admin Interface. It
   * stores it under media/images/{company id}/{year}/{month}/{image id} path in amazon s3 The Image
   * data is stored in the DB
   * 
   * @param params
   *          multipart image data
   * @return the SPResponse whether images is uploaded successfully or some error occurred
   */
  public SPResponse saveMedia(User user, Object[] params) {
    
    // Update the Model with the Params
    SPMediaForm mediaForm = (SPMediaForm) params[0];
    SPMedia media = new SPMedia();
    SPResponse response = null;
    BeanUtils.copyProperties(mediaForm, media);
    try {
      response = saveMedia(mediaForm.getFile().getOriginalFilename(), mediaForm.getMediaType(),
          mediaForm.getCompanyId(), mediaForm.getFile().getBytes(), mediaForm.getFile()
              .getContentType(), media);
    } catch (Exception e) {
      LOG.error("Unable to put the object into s3", e);
      // Delete Image object in DB if there is any exception
      mediaRepository.deleteMedia(media.getId());
      throw new SPException("Unable to upload the media. Please try again later!!");
    }
    return response;
  }
  
  /**
   * save media method will save the meida in the s3 repository.
   * 
   * @param origianlFileName
   *          name of the file.
   * @param mediaType
   *          type of the file.
   * @param companyId
   *          company id of the user
   * @param file
   *          media file
   * @param contentType
   *          content type of media
   * @param media
   *          SP media.
   * @return the media dto.
   */
  public SPResponse saveMedia(String origianlFileName, SPMediaType mediaType, String companyId,
      byte[] file, String contentType, SPMedia media) {
    LocalDateTime now = LocalDateTime.now();
    media.setCreatedOn(now);
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Upload Image from Admin Interface for Company " + media.getCompanyId());
    }
    
    // Save the Media object to DB before storing the file in s3
    mediaRepository.saveMedia(media);
    
    String fileName = null;
    String extension = FilenameUtils.getExtension(origianlFileName);
    if (StringUtils.isBlank(extension)) {
      extension = contentType.split("/")[1];
    }
    
    fileName = fileStorage.storeFile(new FileData(mediaPath(mediaType) + "/" + companyId + "/"
        + now.getYear() + "/" + now.getMonthValue() + "/" + media.getId() + "."
        + extension, file, contentType));
    
    if (fileName != null) {
      media.setUrl("{0}/" + fileName);
      mediaRepository.saveMedia(media);
    }
    SPMediaDTO mediaDto = new SPMediaDTO(media);
    
    mediaDto.setUrl(ImageUtils.getMediaFullPath("{0}/" + fileName));
    SPResponse spResponse = new SPResponse();
    spResponse.isSuccess();
    
    spResponse.add("mediaInfo", mediaDto);
    return spResponse;
  }
  
  /**
   * <code>updateMedia</code> method will update the media object through admin Interface.
   * 
   * @param params
   *          multipart image data
   * @return the SPResponse whether images is uploaded successfully or some error occurred
   */
  public SPResponse updateMedia(User user, Object[] params) {
    
    // Update the Model with the Params
    SPMediaForm spMediaForm = (SPMediaForm) params[0];
    SPMedia spMedia = new SPMedia();
    
    BeanUtils.copyProperties(spMediaForm, spMedia);
    
    mediaRepository.updateMedia(spMedia);
    
    SPResponse spResponse = new SPResponse();
    spResponse.isSuccess();
    return spResponse;
  }
  
  /**
   * getAllMedia method will return all the sp media supported by surepeople plateform.
   * @param user logged in user.
   * @return response.
   */
  public SPResponse getAllMediaFiles(User user) {
    SPResponse response = new SPResponse();
    
    List<SPMedia> mediaList = mediaRepository.getAllMedia();
    
    List<SPMediaDTO> mediaDtoList = null;
    
    if (user.hasRole(RoleType.SuperAdministrator) || user.hasRole(RoleType.SysMedia)) {
      mediaDtoList = mediaList.stream().map(SPMediaDTO::new).collect(Collectors.toList());
    } else {
      // Filter by company id for roles other than superadmin
      mediaDtoList = mediaList
          .stream()
          .map(SPMediaDTO::new)
          .filter(
              media -> media.getCompanyId() != null
                  && media.getCompanyId().equalsIgnoreCase(user.getCompanyId()))
          .collect(Collectors.toList());
    }
    for (SPMediaDTO media : mediaDtoList) {
      if (media.getUrl() != null) {
        media.setUrl(ImageUtils.getMediaFullPath(media.getUrl()));
      }
    }
    
    return response.add("mediaList", mediaDtoList);
  }
  
  public SPResponse getMedia(User user, Object[] params) {
    SPResponse response = new SPResponse();
    String mediaId = (String) params[0];
    
    SPMedia media = mediaRepository.getMedia(mediaId);
    
    SPMediaDTO mediaDto = new SPMediaDTO(media);
    mediaDto.setUrl(ImageUtils.getMediaFullPath(mediaDto.getUrl()));
    
    return response.add("mediaInfo", mediaDto);
  }
  
  public SPResponse deleteMedia(User user, Object[] params) {
    
    String mediaId = (String) params[0];
    boolean mediaDeleted = true;
    
    // Delete the Image from DB
    try {
      mediaRepository.deleteMedia(mediaId);
    } catch (Exception e) {
      mediaDeleted = false;
      LOG.error("Error occured while deleting the media-->" + (String) params[1]);
    }
    
    // On Successful deletion from DB - delete the actual media from s3 bucket.
    if (mediaDeleted) {
      try {
        fileStorage.removeFile(ImageUtils.stripDomainFromImagePath((String) params[1]));
      } catch (Exception e) {
        // Just log the s3 Error. No need to throw it.
        LOG.error("Error occured while deleting the media from s3-->" + (String) params[1]);
      }
    }
    // TODO: Error handling for s3 delete
    return new SPResponse().isSuccess();
  }
  
  /**
   * <code>assertBytesLength</code> method check if file size length is zero or not. In case it is
   * zero, throws an illegal argument exception
   * 
   * @param imageBytes
   *          array of image data in byte
   */
  private void assertBytesLength(byte[] imageBytes) {
    if (imageBytes.length == 0) {
      throw new IllegalArgumentException("Cannot accept empty picture byte[] as a profile picture");
    }
  }
  
  private void assertImageContentType(String imageContentType) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Image contentType which is to be uploaded : " + imageContentType);
    }
    List<String> extensionTypes = Arrays.asList(org.springframework.util.StringUtils
        .commaDelimitedListToStringArray(environment
            .getProperty("s3.profile.image.allowedExtension")));
    if (!extensionTypes.contains(imageContentType)) {
      throw new IllegalArgumentException(MessagesHelper.getMessage(
          "profile.image.invalid.contentType", imageContentType));
    }
  }
  
  private String mediaPath(SPMediaType mediaType) {
    String type = "";
    switch (mediaType) {
    case IMAGE:
      type = Constants.MEDIA_IMAGE;
      break;
    case PDF:
      type = Constants.MEDIA_PDF;
      break;
    case PPT:
      type = Constants.MEDIA_PPT;
      break;
    case MSDOC:
      type = Constants.MEDIA_MSDOC;
      break;
    default:
      break;
    }
    return type;
  }
}
