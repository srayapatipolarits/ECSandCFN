package com.sp.web.assessment.questions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The bean to store the sections information for the json response.
 */
public class CategoriesBean {
  
  private int totalCategories;
  private Map<CategoryType, CategoryBean> categoryMap;
  
  public CategoriesBean() {
    this.setCategoryMap(new HashMap<CategoryType, CategoryBean>());
  }
  
  /**
   * Required for 360 summary copy of the passed categories.
   * 
   * @param categoriesSummary
   *          - the categories summary
   */
  public CategoriesBean(CategoriesBean categoriesSummary) {
    this.totalCategories = categoriesSummary.getTotalCategories();
    categoryMap = new HashMap<CategoryType, CategoryBean>();
    categoriesSummary.getCategoryMap().forEach(
        (key, value) -> categoryMap.put(key, new CategoryBean(value)));
  }
  
  public int getTotalCategories() {
    return totalCategories;
  }
  
  public void setTotalCategories(int totalCategories) {
    this.totalCategories = totalCategories;
  }
  
  public Map<CategoryType, CategoryBean> getCategoryMap() {
    return categoryMap;
  }
  
  public void setCategoryMap(Map<CategoryType, CategoryBean> categoryMap) {
    this.categoryMap = categoryMap;
  }
  
  public void add(CategoryType categoryType, CategoryBean sectionInfo) {
    getCategoryMap().put(categoryType, sectionInfo);
  }

  public List<CategoryBean> getCategoryInfo(List<CategoryType> categories) {
    return categories.stream().map(categoryMap::get).collect(Collectors.toList());
  }
  
}
