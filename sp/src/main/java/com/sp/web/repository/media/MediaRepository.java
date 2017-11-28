package com.sp.web.repository.media;

import com.sp.web.model.SPMedia;

import java.util.List;

/**
 * @author Prasanna Venkatesh
 *
 *         The Media repository class.
 */
public interface MediaRepository {
  
  /**
   * Repository method to get the Media data by Id.
   * 
   * @param String
   *          Id
   * @return the Image Object
   */
  SPMedia getMedia(String id);
  
  /**
   * Save Media Data.
   * 
   * @param SPMedia
   *          - image
   */
  void saveMedia(SPMedia image);
  
  /**
   * Repository method to get All Media files uploaded through Admin Interface
   * 
   * @return the Image Object
   */
  
  List<SPMedia> getAllMedia();
  
  /**
   * Repository method to Delete an media from DB
   * 
   * @return void
   */

  void deleteMedia(String id);
  
  /**
   * Repository method to Update the media object with the url path
   * 
   * @return void
   */

  void updateMedia(String url, String id);

  /**
   * Repository method to Update the media object from edit page with more updated data passed
   * 
   * @return void
   */
  void updateMedia(SPMedia image);
  
}
