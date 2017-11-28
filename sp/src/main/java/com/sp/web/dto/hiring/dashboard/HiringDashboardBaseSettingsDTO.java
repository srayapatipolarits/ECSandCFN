package com.sp.web.dto.hiring.dashboard;

import com.sp.web.dto.library.BaseArticleDto;
import com.sp.web.model.SPFeature;
import com.sp.web.model.hiring.dashboard.HiringDashboardSettings;
import com.sp.web.repository.library.ArticlesFactory;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HiringDashboardBaseSettingsDTO implements Serializable {
  
  private static final long serialVersionUID = -8132231197453552486L;
  
  private List<BaseArticleDto> articles;
  
  private Map<SPFeature, String> images;
  
  public HiringDashboardBaseSettingsDTO(HiringDashboardSettings hiringDashboardSetting,
      ArticlesFactory articlesFactory) {
    BeanUtils.copyProperties(hiringDashboardSetting, this);
    articles = hiringDashboardSetting.getArtilces().stream().map(articleId -> {
      return new BaseArticleDto(articlesFactory.getArticle(articleId));
    }).collect(Collectors.toList());
  }
  
  public void setArticles(List<BaseArticleDto> articles) {
    this.articles = articles;
  }
  
  public List<BaseArticleDto> getArticles() {
    return articles;
  }
  
  public void setImages(Map<SPFeature, String> images) {
    this.images = images;
  }
  
  public Map<SPFeature, String> getImages() {
    return images;
  }
}
