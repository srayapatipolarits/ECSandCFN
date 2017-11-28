package com.sp.web.product;

import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.Account;
import com.sp.web.model.Product;
import com.sp.web.model.Promotion;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Dax Abraham
 * 
 *         The Hiring product helper.
 */
@Component("hiringProductHelper")
public class HiringProductHelper implements ProductHelper {

  @Override
  public DateTime getExpiresTime(Product product, Account account) {
    throw new InvalidRequestException("Not supported !!!");
  }

  @Override
  public Double getChargeAmount(Account account, Product product, Optional<Promotion> promotion,
      long purchaseUnits) {
    double unitPrice = getUnitPrice(account, product, promotion);
    return unitPrice * purchaseUnits;
  }

  @Override
  public Double getUnitPrice(Account account, Product product, Optional<Promotion> promotion) {
    return promotion.map(Promotion::getUnitPrice).orElse(product.getUnitPrice());
  }

  @Override
  public void updateAccount(Account account, Product product, Promotion promotion,
      long purchaseUnits) {
    account.setHiringSubscription(account.getHiringSubscription()
        + Long.valueOf((purchaseUnits * product.getMinEmployee())).intValue());
  }

}
