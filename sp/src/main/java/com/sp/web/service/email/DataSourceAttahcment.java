package com.sp.web.service.email;

import java.io.ByteArrayOutputStream;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

/**
 * @author Dax Abraham
 *
 *         This is the data source attachment class.
 */
public class DataSourceAttahcment {

  private String fileName;
  private DataSource dataSource;

  /**
   * Default Constructor.
   */
  public DataSourceAttahcment() { }
  
  /**
   * Constructor.
   * 
   * @param fileName
   *          - file name
   * @param dataSource
   *          - data source
   */
  public DataSourceAttahcment(String fileName, DataSource dataSource) {
    this.fileName = fileName;
    this.dataSource = dataSource;
  }
  
  /**
   * Constructor to create the attachment using the byte array output stream.
   * 
   * @param fileName
   *          - file name
   * @param byteArrayOutputStream
   *          - output stream
   * @param fileType
   *          - type of content
   */
  public DataSourceAttahcment(String fileName, ByteArrayOutputStream byteArrayOutputStream, String fileType) {
    this.fileName = fileName;
    this.dataSource = new ByteArrayDataSource(byteArrayOutputStream.toByteArray(), fileType);
  }
  
  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }
}
