package com.sp.web.repository.payment;

import com.sp.web.model.PaymentInstrument;

/**
 * @author Dax Abraham
 * 
 *         The repository interface to store and retrieve the payment instruments.
 */
public interface PaymentInstrumentRepository {

  /**
   * Gets the payment instrument for the given payment instrument id.
   * 
   * @param paymentInstrumentId
   *            - the payment instrument id
   * @return
   *    the payment instrument 
   */
  PaymentInstrument findById(String paymentInstrumentId);

  /**
   * Gets the payment instrument for the given payment instrument id.
   * Also validates if the response is present.
   * 
   * @param paymentInstrumentId
   *            - the payment instrument id
   * @return
   *    the payment instrument 
   */
  PaymentInstrument findByIdValidated(String paymentInstrumentId);

  /**
   * Saves the payment instrument to the database.
   * 
   * @param pi
   *        - payment instrument
   * @return
   *      the updated payment instrument
   */
  PaymentInstrument save(PaymentInstrument pi);

  /**
   * Removes the given payment instrument from the database.
   * 
   * @param paymentInstrumentId
   *          - payment instrument id
   * @return
   *      true if remove was successful
   */
  boolean remove(String paymentInstrumentId);

}
