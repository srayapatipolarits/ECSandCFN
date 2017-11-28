package com.sp.web.repository.archive.account;

import com.sp.web.model.Company;

/**
 * @author pruhil
 *
 *         The repository interface for archiving data.
 */
public interface ArchiveAccountRepository {
  
  /**
   * findArchive company by id method returns the id by company.
   * 
   * @param objectToArchive
   *          - object to archive
   */
  Company findArchiveCompanyById(String companyId);
  
  /**
   * find the company for the accountId.
   * 
   * @param accountId
   *          for which compnay need to be find.
   * @return the company.
   */
  Company getCompanyForAccount(String accountId);
  
}
