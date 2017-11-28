package com.sp.web.model;


/**
 * @author pradeep
 * 
 *         The enumeration to store the various user states.
 */
public enum UserStatus {

  /** Incomplete profile status. */
  PROFILE_INCOMPLETE,

  /** Assessment pending. */
  ASSESSMENT_PENDING,

  /** Assessment progress. */
  ASSESSMENT_PROGRESS,

  /** Valid. */
  VALID,

  /** Suspended status. */
  SUSPENDED,

  /** Invitation sent. */
  INVITATION_SENT, 

  /** Invitation not sent. */
  INVITATION_NOT_SENT, INVITATION_EXPIRED, ALREADY_EXISTS, ALREADY_EXISTS_ARCHIVED, HIRED, ADD_REFERENCES, 
  HIRED_MOVED_TO_MEMBER;
}
