/**
 * 
 */
package com.sp.web.test.setup;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.sp.web.config.DataConfig;
import com.sp.web.config.Jsr310Converters;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 *
 */
@Configuration
@Component
@Profile("Test")
public class DateConfigTestSP {

  private static final Logger LOG = Logger.getLogger(DataConfig.class);

  /**
   * @return - the reference to the mongo db factory
   * @throws Exception
   *           - exception while creating the mongo db factory
   */
  @Bean
  public MongoDbFactory mongoDbFactory() throws Exception {
    LOG.debug("Mongo DB name:sptest" );
    
    MongoCredential createCredential = MongoCredential.createCredential("spmongouser", "sptest",
        "surepeople".toCharArray());
    List<MongoCredential> mongoCredentail = new ArrayList<MongoCredential>();
    mongoCredentail.add(createCredential);
    MongoClient mongoClient = new MongoClient(new ServerAddress(), mongoCredentail);
    return new SimpleMongoDbFactory(mongoClient, "sptest");
    
  }

  /**
   * @return - the mongo db template to use
   * @throws Exception
   *           - exception while getting the template
   */
  @Bean
  public MongoTemplate mongoTemplate() throws Exception {
    DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory());
    MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver,
        new MongoMappingContext());

    converter.setCustomConversions(new CustomConversions((List<?>) Jsr310Converters
        .getConvertersToRegister()));
    converter.afterPropertiesSet();
    MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(), converter);

    return mongoTemplate;
  }

  /**
   * The factory for the mongo archive.
   * 
   * @return
   *      - the db factory
   * @throws UnknownHostException
   *      - if the host is unknown 
   */
  @Bean
  public MongoDbFactory mongoArchiveDbFactory() throws UnknownHostException {
    
    String dbName = "sptestarchive";
    if (LOG.isDebugEnabled()) {
      LOG.debug("Mongo DB name:" + dbName);
    }

    MongoCredential createCredential = MongoCredential.createCredential("spmongouser", "sptestarchive",
        "surepeople".toCharArray());
    List<MongoCredential> mongoCredentail = new ArrayList<MongoCredential>();
    mongoCredentail.add(createCredential);
    MongoClient mongoClient = new MongoClient(new ServerAddress(), mongoCredentail);
    return new SimpleMongoDbFactory(mongoClient, "sptestarchive");
  }
  
  /**
   * Get the db template for the archive database.
   * 
   * @return - the mongo db template to use
   * @throws Exception
   *           - exception while getting the template
   */
  @Bean(name = "archiveTemplate")
  public MongoTemplate mongoArchiveTemplate() throws Exception {
    MongoTemplate mongoTemplate = new MongoTemplate(mongoArchiveDbFactory());
    return mongoTemplate;
  }
  
  /**
   * The factory for the mongo archive.
   * 
   * @return
   *      - the db factory
   * @throws UnknownHostException
   *      - if the host is unknown 
   */
  @Bean
  public MongoDbFactory mongoDeletedDbFactory() throws UnknownHostException {
    
    String dbName = "sptestdeleted";
    if (LOG.isDebugEnabled()) {
      LOG.debug("Mongo DB name:" + dbName);
    }
    MongoCredential createCredential = MongoCredential.createCredential("spmongouser", "sptestdeleted",
        "surepeople".toCharArray());
    List<MongoCredential> mongoCredentail = new ArrayList<MongoCredential>();
    mongoCredentail.add(createCredential);
    MongoClient mongoClient = new MongoClient(new ServerAddress(), mongoCredentail);
    return new SimpleMongoDbFactory(mongoClient, "sptestdeleted");
    
  }
  
  /**
   * Get the db template for the archive database.
   * 
   * @return - the mongo db template to use
   * @throws Exception
   *           - exception while getting the template
   */
  @Bean(name = "deletedTemplate")
  public MongoTemplate mongoDeletedTemplate() throws Exception {
    MongoTemplate mongoTemplate = new MongoTemplate(mongoDeletedDbFactory());
    return mongoTemplate;
  }


}
