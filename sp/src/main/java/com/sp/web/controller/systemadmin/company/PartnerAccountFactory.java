package com.sp.web.controller.systemadmin.company;

import com.sp.web.controller.generic.GenericFactory;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dto.partner.PartnerAccountDTO;
import com.sp.web.dto.partner.PartnerAccountListingDTO;
import com.sp.web.form.PartnerAccountForm;
import com.sp.web.model.Company;
import com.sp.web.model.User;
import com.sp.web.model.partner.account.PartnerAccount;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.partneraccount.PartnerAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * PartnerAccountFactory is the account factory for the partner.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class PartnerAccountFactory implements
    GenericFactory<PartnerAccountListingDTO, PartnerAccountDTO, PartnerAccountForm> {
  
  @Autowired
  private PartnerAccountRepository paRepoistory;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Override
  public List<PartnerAccountListingDTO> getAll(User user) {
    List<PartnerAccount> findAll = paRepoistory.findAll();
    List<PartnerAccountListingDTO> collect = findAll.stream().map(pa -> {
      Company company = companyFactory.getCompany(pa.getCompanyId());
      PartnerAccountListingDTO list = new PartnerAccountListingDTO();
      PartnerAccountDTO accountDTO = new PartnerAccountDTO(pa, company);
      list.setPartnerAccount(accountDTO);
      return list;
    }).collect(Collectors.toList());
    return collect;
  }
  
  @Override
  public PartnerAccountDTO get(User user, PartnerAccountForm form) {
    PartnerAccount partnerAccount = paRepoistory.findById(form.getId());
    Assert.notNull(partnerAccount, "Partner Account not found");
    CompanyDao company = companyFactory.getCompany(partnerAccount.getCompanyId());
    
    return new PartnerAccountDTO(partnerAccount, company);
  }
  
  @Override
  public PartnerAccountDTO create(User user, PartnerAccountForm form) {
    
    PartnerAccount create = form.create(user);
    paRepoistory.save(create);
    CompanyDao company = companyFactory.getCompany(create.getCompanyId());
    
    return new PartnerAccountDTO(create, company);
  }
  
  @Override
  public PartnerAccountDTO update(User user, PartnerAccountForm form) {
    PartnerAccount toBeUpdated = paRepoistory.findById(form.getId());
    Assert.notNull(toBeUpdated, "Partner Account not found");
    form.update(user, toBeUpdated);
    paRepoistory.save(toBeUpdated);
    CompanyDao company = companyFactory.getCompany(toBeUpdated.getCompanyId());
    
    return new PartnerAccountDTO(toBeUpdated, company);
  }
  
  @Override
  public void delete(User user, PartnerAccountForm form) {
    PartnerAccount pa = paRepoistory.findById(form.getId());
    Assert.notNull(pa, "Partner Account not found");
    paRepoistory.delete(pa);
    
  }
  
  /**
   * findByPartnerId method will find the partner by the partner id.
   * 
   * @param partnerId
   *          is the partner id.
   * @return the the partner account.
   */
  public PartnerAccount findByPartnerId(String partnerId) {
    return paRepoistory.findByPartnerId(partnerId);
  }
  
  /**
   * findPartnerAccountByCompanyId method will return the partner account on company id.
   * 
   * @param companyId
   *          is the company.
   * @return Partner Account.
   */
  public PartnerAccount findPartnerAccountByCompany(String companyId) {
    // TODO Auto-generated method stub
    return paRepoistory.findByComanyId(companyId);
  }
  
  /**
   * updateActionProcessor method will update the action process to the companyId.
   * 
   * @param actionProcessorType
   *          is the action processor type
   * @param companyId
   *          is the company id for which action processor type is to be udpated.
   */
  public void updateActionProcessor(String actionProcessorType, String companyId) {
    
    PartnerAccount partnerAccount = findPartnerAccountByCompany(companyId);
    partnerAccount.setActionProcessor(actionProcessorType);
    paRepoistory.save(partnerAccount);
    
  }
  
}
