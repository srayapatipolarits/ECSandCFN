/**
 * 
 */
package com.sp.web.model;

import java.util.Date;

/**
 * @author daxabraham
 * 
 * This is the entity class for storing
 * assisted account details
 */
public class AssistedAccount {

	private String id;
	private String name;
	private String email;
	private String phone;
	private int numEmployees;
	private String company;
	private Date createdOn;
	private AssistedAccountStatus status;
	private String supportUserId;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * @return the numEmployees
	 */
	public int getNumEmployees() {
		return numEmployees;
	}
	/**
	 * @param numEmployees the numEmployees to set
	 */
	public void setNumEmployees(int numEmployees) {
		this.numEmployees = numEmployees;
	}
	/**
	 * @return the createdOn
	 */
	public Date getCreatedOn() {
		return createdOn;
	}
	/**
	 * @param createdOn the createdOn to set
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	/**
	 * @return the status
	 */
	public AssistedAccountStatus getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(AssistedAccountStatus status) {
		this.status = status;
	}
	/**
	 * @return the supportUserId
	 */
	public String getSupportUserId() {
		return supportUserId;
	}
	/**
	 * @param supportUserId the supportUserId to set
	 */
	public void setSupportUserId(String supportUserId) {
		this.supportUserId = supportUserId;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}
	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}
	
}
