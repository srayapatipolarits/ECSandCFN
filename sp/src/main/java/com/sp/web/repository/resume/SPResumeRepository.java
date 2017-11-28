package com.sp.web.repository.resume;

import com.sp.web.model.resume.SPResume;

import java.util.List;

/**
 * SPResumeRepository will create the sp resume repository.
 * 
 * @author pradeepruhil
 *
 */
public interface SPResumeRepository {
  
  /**
   * <code>getAllResume</code> will return all the resume for the user.
   * 
   * @param userId
   *          for the user for which resume is to be returned.
   * @return the list of all the resumes.
   */
  List<SPResume> getAllResume(String userId);
  
  /**
   * <code>createResume</code> will create the resume for the user.
   * 
   * @param resume
   *          to be created.
   */
  void createResume(SPResume resume);
  
  /**
   * <code>getPdfDocument</code> will return the pdf document.
   * 
   * @param resumeId
   *          id.
   */
  SPResume getPdfDocument(String resumeId);

  /**
   * Delete the resume from the repository.
   * @param resumeId id to be deleted.
   */
  void deleteResume(String resumeId);
  
}
