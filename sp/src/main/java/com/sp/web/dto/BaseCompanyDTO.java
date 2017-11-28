package com.sp.web.dto;

import com.sp.web.model.Address;
import com.sp.web.model.Company;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @author Dax Abraham
 *
 *         The DTO class to share the basic company information.
 */
public class BaseCompanyDTO implements Serializable {
  
  private static final long serialVersionUID = -6237279745582865117L;

  /** The company id. */
  private String id;
  
  /** company name. */
  private String name;
  
  /** company Address. */
  private Address address;
  
  public BaseCompanyDTO(Company company) {
    BeanUtils.copyProperties(company, this);
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public Address getAddress() {
    return address;
  }
  
  public void setAddress(Address address) {
    this.address = address;
  }
  
}
