package com.sp.web.product;

import com.sp.web.model.Account;
import com.sp.web.model.Product;
import com.sp.web.model.Promotion;

import org.springframework.stereotype.Component;

/**
 * @author Dax Abraham
 * 
 *         The individual product helper implementation.
 */
@Component("individualProductHelper")
public class IndividualProductHelper extends ProductHelperCommon implements ProductHelper {

  @Override
  public void updateAccount(Account account, Product product, Promotion promotion,
      long purchaseUnits) {
    updateNextPayment(account, product, promotion, purchaseUnits);
  }

}
