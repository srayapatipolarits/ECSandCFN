package com.sp.web.dto.feed;

import com.sp.web.dto.BaseCompanyDTO;
import com.sp.web.model.Comment;
import com.sp.web.model.feed.SPMessagePost;
import com.sp.web.product.CompanyFactory;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for the SP message post listing.
 */
public class SPMessagePostListingDTO {
  
  private String id;
  private Comment message;
  private boolean published = false;
  private List<BaseCompanyDTO> companList;
  
  /**
   * Constructor.
   * 
   * @param message
   *            - message
   * @param companyFactory
   *            - company factory
   */
  public SPMessagePostListingDTO(SPMessagePost message, CompanyFactory companyFactory) {
    BeanUtils.copyProperties(message, this);
    if (published) {
      companList = message.getCompanyIds().stream().map(companyFactory::getCompany)
          .filter(c -> c != null).map(BaseCompanyDTO::new).collect(Collectors.toList());
    }
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public Comment getMessage() {
    return message;
  }
  
  public void setMessage(Comment message) {
    this.message = message;
  }
  
  public boolean isPublished() {
    return published;
  }
  
  public void setPublished(boolean published) {
    this.published = published;
  }
  
  public List<BaseCompanyDTO> getCompanList() {
    return companList;
  }
  
  public void setCompanList(List<BaseCompanyDTO> companList) {
    this.companList = companList;
  }
}
