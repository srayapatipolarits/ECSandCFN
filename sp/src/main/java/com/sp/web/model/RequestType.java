package com.sp.web.model;

/**
 * Request Type for the grwoth. Whether to external or internal .
 * 
 * @author pradeep
 */
public enum RequestType {

  /** Growth request to internal employee or company employee. */
  INTERNAL,

  /** Grwoth request to external users which can be his friend/family. */
  EXTERNAL;
}
