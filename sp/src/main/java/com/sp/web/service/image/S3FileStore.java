package com.sp.web.service.image;

import com.sp.web.model.FileData;

import org.apache.log4j.Logger;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

import java.io.ByteArrayInputStream;

/**
 * <code>S3FileStore</code> is a service for storing the file data in amazon s3
 * 
 * It uses jets3t api to connect and upload fine to amazon s3
 * 
 * @author pruhil
 *
 */
public class S3FileStore implements FileStorage {

  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(S3FileStore.class);

  /** AWS Credential wrapper to connect with s3 service. */
  private final AWSCredentials awsCredentials;

  /** bucket name where image is to be uploaded. */
  private final String bucketName;

  /**
   * Creates a S3-based file storage.
   * 
   * @param accessKey
   *          the S3 access key
   * @param secretAccessKey
   *          S3 the secret
   * @param bucketName
   *          the bucket in your account where files should be stored
   */
  public S3FileStore(String accessKey, String secretAccessKey, String bucketName) {
    awsCredentials = new AWSCredentials(accessKey, secretAccessKey);
    this.bucketName = bucketName;
  }

  /**
   * <code>storeFile</code> method will store the file in upload at the path specified.
   * 
   * @param file
   *          FileData
   */
  @Override
  public String storeFile(FileData file) {
    LOG.info("Enter storeFile(), fileData " + file);

    /* create the rest 3 service instance */
    S3Service s3 = createS3Service();

    /* get the bucket from s3 */
    S3Bucket bucket;
    try {
      bucket = s3.getBucket(bucketName);
    } catch (S3ServiceException e) {
      LOG.error("Unable to retreive the s3 bucket ", e);
      throw new IllegalStateException("Unable to retrieve S3 Bucket", e);
    }

    /* create s3 object */
    S3Object object = new S3Object(file.getName());
    object.setDataInputStream(new ByteArrayInputStream(file.getBytes()));
    object.setContentLength(file.getBytes().length);
    object.addMetadata("Cache-Control", "max-age=31536000");

    /* set the content type and permission */
    object.setContentType(file.getContentType());
    AccessControlList acl = new AccessControlList();
    acl.setOwner(bucket.getOwner());
    acl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
    object.setAcl(acl);
    if (LOG.isDebugEnabled()) {
      LOG.debug("Uploading the file in S3");
    }
    try {
      s3.putObject(bucket, object);
    } catch (S3ServiceException e) {
      LOG.error("Unable to put the object into s3", e);
      throw new RuntimeException("Unable to put object into S3", e);
    }
    if (LOG.isInfoEnabled()) {
      LOG.info("Exit, storeFile(), file Uploaded sucessfully, ");
    }
    return file.getName();
  }
  
  /**
   * <code>removeFile</code> method will remove the file from s3 bucket.
   * 
   * @param file
   *          String
   */
  @Override
  public void removeFile(String file) {
    LOG.info("Enter storeFile(), fileData " + file);

    /* create the rest 3 service instance */
    S3Service s3 = createS3Service();

    /* get the bucket from s3 */
    S3Bucket bucket;
    try {
      bucket = s3.getBucket(bucketName);
    } catch (S3ServiceException e) {
      LOG.error("Unable to retreive the s3 bucket ", e);
      throw new IllegalStateException("Unable to retrieve S3 Bucket", e);
    }
   
    try {
      s3.deleteObject(bucket, file);
    } catch (S3ServiceException e) {
      LOG.error("Unable to delete the object into s3", e);
      throw new RuntimeException("Unable to delete object into S3", e);
    }
    if (LOG.isInfoEnabled()) {
      LOG.info("Exit, removeFile(), file removed sucessfully, ");
    }
  }

  /**
   * @return 
   *      - the rest s3service to interact and call operation on aws.
   */
  private S3Service createS3Service() {
    return new RestS3Service(awsCredentials);
  }
}
