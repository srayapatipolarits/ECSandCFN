package com.sp.web.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.sp.web.mvc.signin.oauth.mongodb.OAuth2AuthenticationReadConverter;
import com.sp.web.user.UserFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SP DB access configuration.
 * 
 * @author Dax Abraham
 */
@Component
public class DataConfig {
  
  private static final Logger LOG = Logger.getLogger(DataConfig.class);
  
  @Value("${mongo.db.name}")
  private String dbName;
  
  @Value("${mongo.host}")
  private String host;
  
  @Value("${mongo.port}")
  private Integer port;
  
  @Value("${mongo.username}")
  private String userName;
  
  @Value("${mongo.password}")
  private String password;
  
  @Value("${mongo.archive.db.name}")
  private String archiveDb;
  
  @Value("${mongo.replicaset}")
  private String replicaSets;

  @Value("${mongo.deleted.db.name}")
  private String deletedDb;
  
  /**
   * Get the mongo factory.
   * 
   * @return - the reference to the mongo db factory
   * @throws Exception
   *           - exception while creating the mongo db factory
   */
  @Bean(name = "mongoDbFactory")
  @Profile("Cluster")
  public MongoDbFactory mongoDbProdFactory() throws Exception {
    LOG.debug("Mongo DB name:" + dbName);
    List<ServerAddress> serverAddress = Arrays.asList(replicaSets.split(",")).stream()
        .map(addres -> {
            String[] split = addres.split(":");
            ServerAddress address = null;
            try {
              address = new ServerAddress(split[0], Integer.valueOf(split[1]));
            } catch (Exception e) {
              LOG.error("Exception occured while creating address", e);
            }
            return address;
          }).collect(Collectors.toList());
    MongoCredential credential = MongoCredential.createCredential(userName, dbName, password.toCharArray());
    List<MongoCredential> credentials = new ArrayList<MongoCredential>();
    credentials.add(credential);
    MongoClient mongoClient = new MongoClient(serverAddress,credentials);
    return new SimpleMongoDbFactory(mongoClient, dbName);
  }
  
  /**
   * Get the mongo factory.
   * 
   * @return - the reference to the mongo db factory
   * @throws Exception
   *           - exception while creating the mongo db factory
   */
  @Bean(name = "mongoDbFactory")
  @Profile("NonCluster")
  public MongoDbFactory mongoDbDevFactory() throws Exception {
    LOG.debug("Mongo DB name:" + dbName);
    
    MongoCredential createCredential = MongoCredential.createCredential(userName, dbName,
        password.toCharArray());
    List<MongoCredential> mongoCredentail = new ArrayList<MongoCredential>();
    mongoCredentail.add(createCredential);
    MongoClient mongoClient = new MongoClient(new ServerAddress(host, port), mongoCredentail);
    return new SimpleMongoDbFactory(mongoClient, dbName);
  }
  
  /**
   * Get the mongo template.
   * 
   * @return - the mongo db template to use
   * @throws Exception
   *           - exception while getting the template
   */
  @Bean(name = "mongoTemplate")
  @Profile("NonCluster")
  public MongoTemplate mongoDevTemplate() throws Exception {
    DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbDevFactory());
    MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver,
        new MongoMappingContext());
    
    converter.setCustomConversions(new CustomConversions((List<?>) Jsr310Converters
        .getConvertersToRegister()));
    converter.afterPropertiesSet();
    MongoTemplate mongoTemplate = new MongoTemplate(mongoDbDevFactory(), converter);
    
    return mongoTemplate;
  }
  
  /**
   * Get the mongo template.
   * 
   * @return - the mongo db template to use
   * @throws Exception
   *           - exception while getting the template
   */
  @Bean(name = "mongoTemplate")
  @Profile("Cluster")
  public MongoTemplate mongoProdTemplate() throws Exception {
    DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbProdFactory());
    MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver,
        new MongoMappingContext());
    
    List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
    converterList.addAll(Jsr310Converters.getConvertersToRegister());
    OAuth2AuthenticationReadConverter oAuth2AuthenticationReadConverter = new OAuth2AuthenticationReadConverter();
    converterList.add(oAuth2AuthenticationReadConverter);
    converter.setCustomConversions(new CustomConversions(converterList));
    converter.afterPropertiesSet();
    MongoTemplate mongoTemplate = new MongoTemplate(mongoDbProdFactory(), converter);
    
    return mongoTemplate;
  }
  
  /**
   * The factory for the mongo archive.
   * 
   * @return - the db factory
   * @throws UnknownHostException
   *           - if the host is unknown
   */
  @Bean(name = "mongoArchiveDbFactory")
  @Profile("Cluster")
  public MongoDbFactory mongoArchiveDbProdFactory() throws UnknownHostException {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Mongo DB name:" + archiveDb);
    }
    List<ServerAddress> serverAddress = Arrays.asList(replicaSets.split(",")).stream()
        .map(addres -> {
            String[] split = addres.split(":");
            ServerAddress address = null;
            try {
              address = new ServerAddress(split[0], Integer.valueOf(split[1]));
            } catch (Exception e) {
              LOG.error("Exception occured while creating address", e);
            }
            return address;
          }).collect(Collectors.toList());
    MongoCredential credential = MongoCredential.createCredential(userName, archiveDb, password.toCharArray());
    List<MongoCredential> credentials = new ArrayList<MongoCredential>();
    credentials.add(credential);
    MongoClient mongoClient = new MongoClient(serverAddress,credentials);
    return new SimpleMongoDbFactory(mongoClient, archiveDb);
  }
  
  /**
   * The factory for the mongo archive.
   * 
   * @return - the db factory
   * @throws UnknownHostException
   *           - if the host is unknown
   */
  @Bean(name = "mongoArchiveDbFactory")
  @Profile("NonCluster")
  public MongoDbFactory mongoArchiveDbDevFactory() throws UnknownHostException {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Mongo DB name:" + archiveDb);
    }
    MongoCredential createCredential = MongoCredential.createCredential(userName, archiveDb,
        password.toCharArray());
    List<MongoCredential> mongoCredentail = new ArrayList<MongoCredential>();
    mongoCredentail.add(createCredential);
    MongoClient mongoClient = new MongoClient(new ServerAddress(host, port), mongoCredentail);
    return new SimpleMongoDbFactory(mongoClient, archiveDb);
  }
  
  /**
   * The factory for the mongo archive.
   * 
   * @return - the db factory
   * @throws UnknownHostException
   *           - if the host is unknown
   */
  @Bean(name = "mongoDeletedDbFactory")
  @Profile("NonCluster")
  public MongoDbFactory mongoDeletedDbDevFactory() throws UnknownHostException {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Mongo DB name:" + deletedDb);
    }
    MongoCredential createCredential = MongoCredential.createCredential(userName, deletedDb,
        password.toCharArray());
    List<MongoCredential> mongoCredentail = new ArrayList<MongoCredential>();
    mongoCredentail.add(createCredential);
    MongoClient mongoClient = new MongoClient(new ServerAddress(host, port), mongoCredentail);
    return new SimpleMongoDbFactory(mongoClient, deletedDb);
  }
  
  /**
   * The factory for the mongo archive.
   * 
   * @return - the db factory
   * @throws UnknownHostException
   *           - if the host is unknown
   */
  @Bean(name = "mongoDeletedDbFactory")
  @Profile("Cluster")
  public MongoDbFactory mongoDeletedDbProdFactory() throws UnknownHostException {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Mongo DB name:" + deletedDb);
    }
    List<ServerAddress> serverAddress = Arrays.asList(replicaSets.split(",")).stream()
        .map(addres -> {
            String[] split = addres.split(":");
            ServerAddress address = null;
            try {
              address = new ServerAddress(split[0], Integer.valueOf(split[1]));
            } catch (Exception e) {
              LOG.error("Exception occured while creating address", e);
            }
            return address;
          }).collect(Collectors.toList());
    MongoCredential credential = MongoCredential.createCredential(userName, deletedDb, password.toCharArray());
    List<MongoCredential> credentials = new ArrayList<MongoCredential>();
    credentials.add(credential);
    MongoClient mongoClient = new MongoClient(serverAddress,credentials);
    return new SimpleMongoDbFactory(mongoClient, deletedDb);
  }
  
  /**
   * Get the db template for the archive database.
   * 
   * @return - the mongo db template to use
   * @throws Exception
   *           - exception while getting the template
   */
  @Bean(name = "archiveTemplate")
  @Profile("NonCluster")
  public MongoTemplate mongoArchiveDevTemplate() throws Exception {
    MongoTemplate mongoTemplate = new MongoTemplate(mongoArchiveDbDevFactory());
    return mongoTemplate;
  }
  
  /**
   * Get the db template for the archive database.
   * 
   * @return - the mongo db template to use
   * @throws Exception
   *           - exception while getting the template
   */
  @Bean(name = "archiveTemplate")
  @Profile("Cluster")
  public MongoTemplate mongoArchiveProdTemplate() throws Exception {
    MongoTemplate mongoTemplate = new MongoTemplate(mongoArchiveDbProdFactory());
    return mongoTemplate;
  }
  
  /**
   * Get the db template for the archive database.
   * 
   * @return - the mongo db template to use
   * @throws Exception
   *           - exception while getting the template
   */
  @Bean(name = "deletedTemplate")
  @Profile("Cluster")
  public MongoTemplate mongoDeletedProdTemplate() throws Exception {
    MongoTemplate mongoTemplate = new MongoTemplate(mongoDeletedDbProdFactory());
    return mongoTemplate;
  }
  
  /**
   * Get the db template for the archive database.
   * 
   * @return - the mongo db template to use
   * @throws Exception
   *           - exception while getting the template
   */
  @Bean(name = "deletedTemplate")
  @Profile("NonCluster")
  public MongoTemplate mongoDeletedDevTemplate() throws Exception {
    MongoTemplate mongoTemplate = new MongoTemplate(mongoDeletedDbDevFactory());
    return mongoTemplate;
  }
  
}