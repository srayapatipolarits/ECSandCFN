package com.sp.web.model.library;

import java.util.HashSet;
import java.util.Set;

/**
 * @author pradeepruhil
 *
 */
public class ArticleMetaData {

  String id = "1";
  private Set<String> authorNames;

  private Set<String> themes;

  private Set<String> sources;

  /**
   * @param authorNames
   *          the authorNames to set
   */
  public void setAuthorNames(Set<String> authorNames) {
    this.authorNames = authorNames;
  }

  /**
   * @param sources
   *          the sources to set
   */
  public void setSources(Set<String> sources) {
    this.sources = sources;
  }

  /**
   * @param themes
   *          the themes to set
   */
  public void setThemes(Set<String> themes) {
    this.themes = themes;
  }

  /**
   * @return the authorNames
   */
  public Set<String> getAuthorNames() {
    if (authorNames == null) {
      authorNames = new HashSet<>();
    }
    return authorNames;
  }

  /**
   * @return the sources
   */
  public Set<String> getSources() {
    if (sources == null) {
      sources = new HashSet<>();
    }
    return sources;
  }

  /**
   * @return the themes
   */
  public Set<String> getThemes() {
    if (themes == null) {
      themes = new HashSet<>();
    }
    return themes;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

}
