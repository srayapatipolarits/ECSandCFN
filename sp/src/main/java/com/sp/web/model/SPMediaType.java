package com.sp.web.model;

import com.sp.web.model.article.ArticleType;

/**
 * @author Prasanna Venkatesh
 * 
 *         Enum which holds the different media type.
 */
public enum SPMediaType {
  
  IMAGE, PDF, PPT, MSDOC, Video, VideoTab, Schedule, Web, SP, Audio, WebExternal, WebInternal, ActionPlan, MSEXCEL;

  /**
   * Map the article type to the media type.
   * 
   * @param articleType
   *            - article type
   * @return
   *    the SPMedia type
   */
  public static SPMediaType getType(ArticleType articleType) {
    SPMediaType type = Web;
    switch (articleType) {
    case AUDIO:
      type = Audio;
      break;
    case VIDEO:
      type = Video;
      break;
    case SLIDESHARE:
      type = PPT;
      break;
    default:
      break;
    }
    return type;
  }
  
}
