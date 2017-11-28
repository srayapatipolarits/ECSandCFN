package com.sp.web.promotions;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.Account;
import com.sp.web.model.Promotion;
import com.sp.web.utils.RandomGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Dax Abraham
 * 
 *         The mongo implementation of the repositories class.
 */
@Repository
public class MongoPromotionsRepository implements PromotionsRepository {

  @Autowired
  MongoTemplate mongoTemplate;
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.promotions.PromotionsRepository#findByCode(java.lang.String)
   */
  @Override
  public Promotion findByCode(String code) {
    return mongoTemplate.findOne(query(where("code").is(code)), Promotion.class);
  }

  @Override
  public Promotion findById(String promotionId) {
    return mongoTemplate.findById(promotionId, Promotion.class);
  }

  @Override
  public List<Promotion> getAllPromotionsById(List<String> promotions) {
    if (promotions == null ) {
      return new ArrayList<Promotion>();
    }
    return mongoTemplate.find(query(where("id").in(promotions)), Promotion.class);
  }

  @Override
  public Promotion findByIdValidated(String promotionId) {
    return Optional.ofNullable(findById(promotionId)).orElseThrow(
        () -> new InvalidRequestException("Promotion not found for id:" + promotionId));
  }

  @Override
  public List<Promotion> getAllPromotions() {
    return mongoTemplate.findAll(Promotion.class);
  }

  @Override
  public void update(Promotion promotion) {
    mongoTemplate.save(promotion);
  }

  @Override
  public String getNextCode() {
// Dax Abraham : Removing the logic for generating the code number 
// from DB sequence.
//    
//    String key = "orderNo";
//    
//    //get sequence id
//    Query query = new Query(Criteria.where("_id").is(key));
// 
//    //increase sequence id by 1
//    Update update = new Update();
//    update.inc("promoCodeSeq", 1);
// 
//    //return new increased id
//    FindAndModifyOptions options = new FindAndModifyOptions();
//    options.returnNew(true);
// 
//    //this is the magic happened.
//    SequenceId seqId = 
//            mongoTemplate.findAndModify(query, update, options, SequenceId.class);
// 
//    //if no id, throws SequenceException
//          //optional, just a way to tell user when the sequence id is failed to generate.
//    if (seqId == null) {
//      throw new InvalidRequestException("Unable to get sequence id for key : " + key);
//    }
// 
//    return "SP" + Constants.promoFormatter.format(seqId.getPromoCodeSeq());
    String randomStr = null;
    while (randomStr == null) {
      randomStr = RandomGenerator.randomString(5);
      Promotion findById = mongoTemplate.findById(randomStr, Promotion.class);
      if (findById != null) {
        randomStr = null;
      }
    }
    return randomStr;
  }


  @Override
  public void create(Promotion promotion) {
    mongoTemplate.save(promotion);
  }

  @Override
  public List<Account> getAccountsWithPromotion(String promotionId) {
    return mongoTemplate.find(query(where("promotions").in(promotionId)), Account.class);
  }
}
