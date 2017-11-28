package com.sp.web.config;

import com.sp.web.service.image.FileStorage;
import com.sp.web.service.image.S3FileStore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.inject.Inject;

/**
 * SP file store configuration. Used to store user profile pictures. we use Amazon S3's file storage
 * service.
 * 
 * @author pruhil
 */
@Configuration
public class FileStoreConfig {
  
  /**
   * Environment variable
   */
  @Inject
  private Environment environment;
  
  /**
   * return the picture storage FileStore
   * 
   * @return
   */
  @Bean(name = "s3FileStore")
  @Profile("PROD")
  public FileStorage pictureStorage() {
    return new S3FileStore(environment.getProperty("s3.username.key"),
        environment.getProperty("s3.password.sceret"),
        environment.getProperty("s3.bucket.name.image"));
  }
  
}
