package com.sp.web.form;

import com.sp.web.model.Address;
import com.sp.web.model.Company;
import com.sp.web.model.User;

import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author daxabraham
 * 
 *         The form bean for address.
 */
public class AddressForm implements Serializable {
  
  /**
   * Serial version uid.
   */
  private static final long serialVersionUID = -831132374235659394L;
  
  private String company;
  
  private String industry;
  
  private int numEmp;
  
  @NotEmpty
  private String country;
  
  @NotEmpty
  private String address1;
  
  private String address2;
  
  @NotEmpty
  private String city;
  
  private String state;
  
  @NotEmpty
  private String zip;
  
  private String phone;
  
  public String getCountry() {
    return country;
  }
  
  public void setCountry(String country) {
    this.country = country;
  }
  
  public String getAddressLine1() {
    return address1;
  }
  
  public void setAddress1(String address1) {
    this.address1 = address1;
  }
  
  public String getAddress2() {
    return address2;
  }
  
  public void setAddress2(String addressLine2) {
    this.address2 = addressLine2;
  }
  
  public String getCity() {
    return city;
  }
  
  public void setCity(String city) {
    this.city = city;
  }
  
  public String getState() {
    return state;
  }
  
  public void setState(String state) {
    this.state = state;
  }
  
  public String getZip() {
    return zip;
  }
  
  public void setZip(String zipCode) {
    this.zip = zipCode;
  }
  
  /**
   * Updates the address for the given user.
   * 
   * @param userToUpdate
   *          - the user to update
   */
  public void update(User userToUpdate) {
    Address address = userToUpdate.getAddress();
    if (address == null) {
      address = new Address();
      userToUpdate.setAddress(address);
    }
    update(address);
  }
  
  /**
   * Updates the address object with the data in this form.
   * 
   * @param address
   *          - address to update
   */
  public void update(Address address) {
    address.setAddressLine1(address1);
    address.setAddressLine2(address2);
    address.setCity(city);
    address.setCountry(country);
    address.setState(state);
    address.setZipCode(zip);
  }
  
  /**
   * Updates the addrss for the company.
   * 
   * @param company
   *          - the company to update
   */
  public void update(Company company) {
    Address addressToUpdate = Optional.ofNullable(company.getAddress()).orElseGet(() -> {
      Address addr = new Address();
      company.setAddress(addr);
      return addr;
    });
    update(addressToUpdate);
  }
  
  /**
   * Get the address from the address form.
   * 
   * @return address
   */
  public Address getAddress() {
    Address addr = new Address();
    update(addr);
    return addr;
  }
  
  public void setCompany(String company) {
    this.company = company;
  }
  
  public String getIndustry() {
    return industry;
  }
  
  public int getNumEmp() {
    return numEmp;
  }
  
  public void setNumEmp(int numEmp) {
    this.numEmp = numEmp;
  }
  
  public String getCompany() {
    return company;
  }
  
  public void setIndustry(String industry) {
    this.industry = industry;
  }
  
  public String getAddress1() {
    return address1;
  }
  public void setPhone(String phone) {
    this.phone = phone;
  }
  
  public String getPhone() {
    return phone;
  }
  
  @Override
  public String toString() {
    return "AddressForm [company=" + company + ", industry=" + industry + ", numEmp=" + numEmp
        + ", country=" + country + ", address1=" + address1 + ", address2=" + address2 + ", city="
        + city + ", state=" + state + ", zip=" + zip + "]";
  }
  
}
