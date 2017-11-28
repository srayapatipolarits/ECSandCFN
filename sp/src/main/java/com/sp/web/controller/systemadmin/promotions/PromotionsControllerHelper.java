package com.sp.web.controller.systemadmin.promotions;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.dto.ProductDTO;
import com.sp.web.dto.promotions.PromotionAccountDTO;
import com.sp.web.dto.promotions.PromotionsDTO;
import com.sp.web.dto.promotions.PromotionsSummaryDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.form.PromotionForm;
import com.sp.web.model.Account;
import com.sp.web.model.AccountType;
import com.sp.web.model.Company;
import com.sp.web.model.Product;
import com.sp.web.model.Promotion;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.ProductRepository;
import com.sp.web.promotions.PromotionStatus;
import com.sp.web.promotions.PromotionsRepository;
import com.sp.web.repository.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The helper class for the promotions helper.
 */
@Component
public class PromotionsControllerHelper {
  
  @Autowired
  PromotionsRepository promotionsRepository;
  
  @Autowired
  ProductRepository productRepository;
  
  @Autowired
  AccountRepository accountRepository;
  
  @Autowired
  UserRepository userRepository;
  
  /**
   * Helper method to get all the promotions configured in the system.
   * 
   * @param user
   *          - logged in user
   * @return
   *    the response to the get request
   */
  public SPResponse getAllPromotions(User user) {
    final SPResponse resp = new SPResponse();
    List<Promotion> allPromotions = promotionsRepository.getAllPromotions();
    final Map<String, ProductDTO> collect = getProductMap();
    return resp.add(Constants.PARAM_PROMOTION, allPromotions.stream().map(p -> {
        return new PromotionsSummaryDTO(p, collect);
      }).collect(Collectors.toList()));
  }
  
  /**
   * Helper method to get the details of the promotion.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return
   *    the response to the get request
   */
  public SPResponse getPromotionDetails(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String promotionId = (String) params[0];
    Assert.hasText(promotionId, "Promotion id cannot be blank or emtpy.");
    
    Promotion promotion = promotionsRepository.findByIdValidated(promotionId);
    final Map<String, ProductDTO> collect = getProductMap();

    return resp.add(Constants.PARAM_PROMOTION, new PromotionsDTO(promotion, collect));
  }

  private Map<String, ProductDTO> getProductMap() {
    List<Product> allProducts = productRepository.getAllProducts();
    final Map<String, ProductDTO> collect = allProducts.stream().collect(
        Collectors.toMap(Product::getId, ProductDTO::new));
    return collect;
  }
  
  /**
   * Get all the products.
   * 
   * @param user
   *          - user
   * @return
   *    all the products
   */
  public SPResponse getAllProducts(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    Comparator<Product> comparator = (bd1, bd2) -> bd1.getTitleKey().compareTo(
        bd2.getTitleKey());
    return resp.add(
        Constants.PARAM_PRODUCT,
        productRepository.getAllProducts().stream().sorted(comparator.reversed()).map(ProductDTO::new)
            .collect(Collectors.toList()));
  }
  
  /**
   * Update the promotion status for the given promotion id.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return
   *      the response to the update request
   */
  public SPResponse updatePromotionStatus(User user, Object[] params) {
    final SPResponse resp = new SPResponse();

    String promotionId = (String) params[0];
    Assert.hasText(promotionId, "Promotion id cannot be blank or emtpy.");
    PromotionStatus promotionStatus = (PromotionStatus) params[1];
    
    Promotion promotion = promotionsRepository.findByIdValidated(promotionId);
    promotion.setStatus(promotionStatus);

    promotionsRepository.update(promotion);
    return resp.isSuccess();
  }

  /**
   * Get the next promotion code.
   * 
   * @param user
   *        - user
   * @return
   *      the next promo code sequence
   */
  public SPResponse getCode(User user) {
    return new SPResponse().add(Constants.PARAM_PROMOTION, promotionsRepository.getNextCode());
  }
  
  /**
   * Method to create a promotion.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *      the response to the create request
   */
  @SuppressWarnings("unchecked")
  public SPResponse createPromotion(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    PromotionForm promotionForm = (PromotionForm) params[0];
    List<String> productList = (List<String>) params[1];
    Assert.notEmpty(productList, "At least one product required.");
    
    Promotion promotion = promotionForm.getPromotion();
    
    final Map<String, ProductDTO> collect = getProductMap();
    if (!productList.stream().allMatch(collect::containsKey)) {
      throw new InvalidRequestException("Invalid product list.");
    }
    
    promotion.setProductIdList(productList);
    try {
      promotionsRepository.create(promotion);
    } catch (Exception e) {
      throw new InvalidRequestException("Could not create promotion.", e);
    }
    return resp.isSuccess();
  }
  
  /**
   * Method to update a promotion.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return
   *      the response to the create request
   */
  public SPResponse updatePromotion(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    PromotionForm promotionForm = (PromotionForm) params[0];
    
    String promotionId = (String) params[1];
    Assert.hasText(promotionId, "Promotion id cannot be blank or emtpy.");
    Promotion promotion = promotionsRepository.findByIdValidated(promotionId);
    
    promotionForm.update(promotion);
    promotionsRepository.update(promotion);    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to get the list of accounts where promotion is applied.
   * 
   * @param user
   *        - logged in user
   * @param params
   *        - params
   * @return
   *      the list of accounts
   */
  public SPResponse getAccounts(User user, Object[] params) {
    final SPResponse resp = new SPResponse();

    String promotionId = (String) params[0];
    Assert.hasText(promotionId, "Promotion id cannot be blank or emtpy.");
    promotionsRepository.findByIdValidated(promotionId);
    
    
    // get the list of accounts using the given promotion
    List<Account> accountList = promotionsRepository.getAccountsWithPromotion(promotionId);
    Map<String, Account> collect = accountList.stream().collect(Collectors.toMap(Account::getId, a -> a));
    
    // break them into the list of user accounts and company accounts
    ArrayList<String> companyAccountList = new ArrayList<String>();
    ArrayList<String> userAccountList = new ArrayList<String>();
    for (Account account : accountList) {
      if (account.getType() == AccountType.Business) {
        companyAccountList.add(account.getId());
      } else {
        userAccountList.add(account.getId());
      }
    }
    
    // process the list of company accounts
    ArrayList<PromotionAccountDTO> promotionAccountList = new ArrayList<PromotionAccountDTO>();
    List<Company> findCompanyByAccountId = accountRepository.findCompanyByAccountId(companyAccountList);
    for (Company company : findCompanyByAccountId) {
      Account account = collect.get(company.getAccountId());
      promotionAccountList.add(new PromotionAccountDTO(account, company));
    }
    
    // process the list of user accounts
    List<User> userList = userRepository.findByAccountId(userAccountList);
    for (User usr : userList) {
      Account account = collect.get(usr.getAccountId());
      promotionAccountList.add(new PromotionAccountDTO(account, usr));
    }
    
    // sending back response
    return resp.add(Constants.PARAM_ACCOUNT, promotionAccountList);
  }
  
}
