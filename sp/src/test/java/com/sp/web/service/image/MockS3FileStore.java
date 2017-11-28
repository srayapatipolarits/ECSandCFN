/**
 * 
 */
package com.sp.web.service.image;

import com.sp.web.model.FileData;
import com.sp.web.service.image.FileStorage;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * MockS3FileStore
 * @author pradeepruhil
 *
 */
@Service("s3FileStore")
@Profile({ "Test" })
public class MockS3FileStore implements FileStorage {
  
  private static final Logger LOG = Logger.getLogger(MockS3FileStore.class);
  
  /**
   * @see com.sp.web.service.image.FileStorage#storeFile(com.sp.web.model.FileData)
   */
  @Override
  public String storeFile(FileData fileData) {
    LOG.info("File Stored scucessfully");
    return fileData.getName();
  }
  
  /**
   * @see com.sp.web.service.image.FileStorage#removeFile(java.lang.String)
   */
  @Override
  public void removeFile(String fileName) {
    LOG.info("File remooved sucessuflly");
    
  }
  
}
