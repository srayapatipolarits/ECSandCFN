package com.sp.web.promotions;

import com.sp.web.model.Account;
import com.sp.web.model.Promotion;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The repository class for Promotions.
 */
@Deprecated
public interface PromotionsRepository {

  /**
   * Find the promotion for the given promotion code.
   * 
   * @param code
   *          - promotion code
   * @return
   *        - the promotion for the code
   */
  @Deprecated
  Promotion findByCode(String code);

  /**
   * Gets the promotion for the given promotion id.
   * 
   * @param promotionId
   *          - the promotion id
   * @return
   *        the promotion for the given promotion id
   */
  @Deprecated
  Promotion findById(String promotionId);

  /**
   * Gets the promotion for the given promotion id. Also validate.
   * 
   * @param promotionId
   *          - the promotion id
   * @return
   *        the promotion for the given promotion id
   */
  Promotion findByIdValidated(String promotionId);
  /**
   * Gets all the promotions for the given promotions id.
   * 
   * @param promotions
   *          - the promotion list
   * @return
   *      - the list of promotions for the given promotions id's
   */
  List<Promotion> getAllPromotionsById(List<String> promotions);

  /**
   * Get a list of all the promotions.
   * 
   * @return
   *      list of all the promotions
   */
  @Deprecated
  List<Promotion> getAllPromotions();

  /**
   * Update the given promotion to the database.
   * 
   * @param promotion
   *          - promotion to update
   */
  @Deprecated
  void update(Promotion promotion);

  /**
   * Get the next available code.
   * 
   * @return
   *      the next code
   */
  @Deprecated
  String getNextCode();

  /**
   * Create the given promotion.
   * 
   * @param promotion
   *            - promotion to create
   */
  @Deprecated
  void create(Promotion promotion);

  /**
   * Get the list of accounts using the given promotion id.
   * 
   * @param promotionId
   *          - promotion id
   * @return
   *      the list of accounts
   */
  @Deprecated
  List<Account> getAccountsWithPromotion(String promotionId);
}
