package com.sp.web.controller.systemadmin.smartling;

import java.io.File;
import java.util.Collection;

/**
 * TranslationSystemConnnector provides integration with external transaltion system.
 * 
 * @author pradeepruhil
 *
 */
public interface TranslationSystemConnector {
  
  /**
   * uploadFiles method will upload the files to the transaltion system.
   * 
   * @param xmlFiles
   *          to be upload to translation system.
   * @return the true/false whether the file was uploaded successfully or not.
   */
  public boolean uploadFiles(Collection<File> xmlFiles);
  
  /**
   * downloadFiles method will download all the transalted files from the translation system.
   * 
   * @return the downloaded file content.
   */
  
  String downloadFiles(String filesName, String locale, String fileExtension);
}
