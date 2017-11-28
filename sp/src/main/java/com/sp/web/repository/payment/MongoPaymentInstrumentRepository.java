package com.sp.web.repository.payment;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.CreditNotePaymentInstrument;
import com.sp.web.model.PaymentInstrument;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Dax Abraham
 * 
 *         The mongo implementation of the payment instrument repository interface.
 */
@Repository
public class MongoPaymentInstrumentRepository implements PaymentInstrumentRepository {
  
  @Autowired
  MongoTemplate mongoTemplate;
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.repository.payment.PaymentInstrumentRepository#findById(java.lang.String)
   */
  @Override
  public PaymentInstrument findById(String paymentInstrumentId) {
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(PaymentInstrument.class));
    DBObject dbObject = collection.findOne(query(where("_id").is(paymentInstrumentId))
        .getQueryObject());
    MongoConverter converter = mongoTemplate.getConverter();
    if (dbObject != null) {
      String className = (String) dbObject.get("_class");
      switch (className) {
      case "com.sp.web.model.CreditNotePaymentInstrument":
        return converter.read(CreditNotePaymentInstrument.class, dbObject);
      default:
        return converter.read(PaymentInstrument.class, dbObject);
      }
    }
    return mongoTemplate.findById(paymentInstrumentId, PaymentInstrument.class);
  }
  
  @Override
  public PaymentInstrument findByIdValidated(String paymentInstrumentId) {
    return Optional.ofNullable(findById(paymentInstrumentId)).orElseThrow(
        () -> new InvalidRequestException("Payment instrument not found for id:"
            + paymentInstrumentId));
  }
  
  @Override
  public PaymentInstrument save(PaymentInstrument pi) {
    mongoTemplate.save(pi);
    return pi;
  }
  
  @Override
  public boolean remove(String paymentInstrumentId) {
    WriteResult result = mongoTemplate.remove(query(where("id").is(paymentInstrumentId)),
        PaymentInstrument.class);
    return (result.getN() > 0);
  }
  
}
