package com.sp.web.repository.translation;

import com.sp.web.model.translation.TranslationData;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * MongoTranslationRepository fetch the translation object from the mongo for the collection.
 * 
 * @author pradeepruhil
 *
 */
@Repository
public class MongoTranslationRepository extends GenericMongoRepositoryImpl<TranslationData>
    implements TranslationRepository {
  
  @Override
  public TranslationData getTranslationData(String id, String locale, Class<?> classType) {
    return mongoTemplate.findOne(
        Query.query(Criteria.where("key").is(id).and("locale").is(locale).and("classType")
            .is(classType.getName())), TranslationData.class);
  }
  
}
