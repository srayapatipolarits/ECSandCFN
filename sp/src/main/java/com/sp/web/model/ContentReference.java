package com.sp.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sp.web.controller.library.TrainingLibraryController;
import com.sp.web.model.article.Article;
import com.sp.web.model.library.NewsCredVideoArticle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The entity to store the reference to a piece of content.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentReference implements Serializable {
  
  private static final long serialVersionUID = 7950652045725388481L;
  private String title;
  private String description;
  private String authorSource;
  private String imageUrl;
  private List<SPMedia> media;
  private SPFeature spFeature;
  private String iconReference;
  
  /** Added field for OG , to open url in same tab or new tab for SP internal URL and external URL. */
  private boolean newTab;
  
  /**
   * Url for the content, specially in case of Video Article or video Source, where is the source
   * URl and URL in media is video URL.
   */
  private String url;
  
  /** Added for showing subtitle for Include Reference for all modules of notes panel. */
  private String subTitle;
  
  /** Added to identify whether the reference was from fileStack. */
  private boolean fileStack;
  
  /** Source of filestack uploads. */
  private String client;
  
  /** Flag to identify whether the reference was through OG tags */
  private boolean ogRef;
  
  /**
   * Default constructor.
   */
  public ContentReference() {
  }
  
  /**
   * Constructor.
   * 
   * @param article
   *          - article
   */
  public ContentReference(Article article) {
    // adding name and title
    title = article.getArticleLinkLabel();
    // adding description
    final List<String> content = article.getContent();
    if (!CollectionUtils.isEmpty(content)) {
      description = content.get(0);
    }
    
    StringBuffer sb = new StringBuffer();
    // adding author
    final List<String> authorList = article.getAuthor();
    if (!authorList.isEmpty()) {
      sb.append(String.join(",", authorList)).append(" - ");
    }
    // adding source
    final String articleSource = article.getArticleSource();
    if (articleSource != null) {
      sb.append(articleSource);
    }
    // adding the author source
    authorSource = sb.length() > 0 ? sb.toString() : null;
    
    // setting the image URL
    imageUrl = article.getImageUrl();
    if (imageUrl != null) {
      imageUrl += "?width=200";
    }
    
    setMedia(new ArrayList<SPMedia>());
    String url = "/sp" + TrainingLibraryController.TRAINING_LIBRARY_ARTICLE_URL + "?"
        + article.getId();
    setUrl(url);
    String mediaUrl = null;
    /* get the media URL, in case video, we need to get the video URL */
    switch (article.getArticleType()) {
    case VIDEO:
      mediaUrl = ((NewsCredVideoArticle) article).getVideoUrl();
      break;
    default:
      mediaUrl = url;
      break;
    }
    getMedia().add(new SPMedia(title, mediaUrl, SPMediaType.getType(article.getArticleType())));
    
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public String getAuthorSource() {
    return authorSource;
  }
  
  public void setAuthorSource(String authorSource) {
    this.authorSource = authorSource;
  }
  
  public String getImageUrl() {
    return imageUrl;
  }
  
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
  
  public List<SPMedia> getMedia() {
    return media;
  }
  
  public void setMedia(List<SPMedia> media) {
    this.media = media;
  }
  
  /**
   * @param mediaToAdd
   *          - add to the media.
   */
  public void addMedia(SPMedia mediaToAdd) {
    if (media == null) {
      media = new ArrayList<SPMedia>();
    }
    media.add(mediaToAdd);
  }
  
  public SPFeature getSpFeature() {
    return spFeature;
  }
  
  public void setSpFeature(SPFeature spFeature) {
    this.spFeature = spFeature;
  }
  
  public boolean isNewTab() {
    return newTab;
  }
  
  public void setNewTab(boolean newTab) {
    this.newTab = newTab;
  }
  
  public String getUrl() {
    return url;
  }
  
  public void setUrl(String url) {
    this.url = url;
  }
  
  public String getSubTitle() {
    return subTitle;
  }
  
  public void setSubTitle(String subTitle) {
    this.subTitle = subTitle;
  }
  
  public boolean isFileStack() {
    return fileStack;
  }
  
  public void setFileStack(boolean fileStack) {
    this.fileStack = fileStack;
  }
  
  public String getClient() {
    return client;
  }
  
  public void setClient(String client) {
    this.client = client;
  }
  
  public boolean isValid() {
    return !StringUtils.isBlank(imageUrl) || !StringUtils.isBlank(url);
  }
  
  public boolean isOgRef() {
    return ogRef;
  }
  
  public void setOgRef(boolean ogRef) {
    this.ogRef = ogRef;
  }
  
  public void setIconReference(String iconReference) {
    this.iconReference = iconReference;
  }
  
  public String getIconReference() {
    return iconReference;
  }
  
}
