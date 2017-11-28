package com.sp.web.service.image;

import com.sp.web.model.FileData;

/**
 * ImageService provides method to save the image
 * 
 * @author pruhil
 *
 */
public interface FileStorage {

	/**
	 * <code>storeFile</code> method will store the file in the underlying
	 * implemenation provider. (S3/Local File Store/ Google Data store/
	 * Database)
	 * 
	 * @param fileData
	 *            contains the metadata and file data information which to be
	 *            stored
	 * @return the absolute URL of the file where it is stored.
	 */
	String storeFile(FileData fileData);
	
	/**
   * <code>removeFile</code> method will remove the already stored file in the underlying
   * implementation provider. (S3/Local File Store/ Google Data store/
   * Database)
   * 
   * @param fileData
   *            contains the metadata and file data information which to be
   *            removed
   * @return the success/error string 
   */
  void removeFile(String fileName);
}
