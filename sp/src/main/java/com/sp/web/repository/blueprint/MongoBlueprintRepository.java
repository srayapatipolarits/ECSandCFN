package com.sp.web.repository.blueprint;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.Constants;
import com.sp.web.model.blueprint.BlueprintBackup;
import com.sp.web.model.blueprint.BlueprintSettings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The mongo implementation of the blueprint repository.
 */
@Repository
public class MongoBlueprintRepository implements BlueprintRepository {
  
  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public List<BlueprintSettings> findAllBlueprintSettings() {
    return mongoTemplate.find(
        Query.query(Criteria.where(Constants.ENTITY_COMPANY_ID).ne(
            Constants.BLUEPRINT_DEFAULT_COMPANY_ID)), BlueprintSettings.class);
  }


  @Override
  public void updateBlueprintSettings(BlueprintSettings blueprintSettings) {
    mongoTemplate.save(blueprintSettings);
    
  }

  @Override
  public BlueprintSettings findBlueprintSettingsById(String blueprintSettingsId) {
    return mongoTemplate.findById(blueprintSettingsId, BlueprintSettings.class);
  }

  @Override
  public BlueprintSettings findBlueprintSettingsByCompanyId(String companyId) {
    return mongoTemplate.findOne(
        Query.query(Criteria.where(Constants.ENTITY_COMPANY_ID).is(companyId)),
        BlueprintSettings.class);
  } 
  
  @Override
  public int deleteBlueprintSettings(String companyId) {
    return mongoTemplate.remove(query(where(Constants.ENTITY_COMPANY_ID).is(companyId)),
        BlueprintSettings.class).getN();
  }


  @Override
  public void updateBlueprintBackup(BlueprintBackup blueprintBackup) {
    mongoTemplate.save(blueprintBackup);
  }


  @Override
  public int removeBlueprintBackupByBlueprintId(String blueprintId) {
    return mongoTemplate.remove(query(where(Constants.ENTITY_BLUEPRINT_ID).is(blueprintId)),
        BlueprintBackup.class).getN();
  }


  @Override
  public BlueprintBackup getBlueprintBackupFromBlueprintId(String blueprintId) {
    return mongoTemplate.findOne(query(where(Constants.ENTITY_BLUEPRINT_ID).is(blueprintId)),
        BlueprintBackup.class);
  }


  @Override
  public void removeBlueprintBackup(BlueprintBackup blueprintBackup) {
    mongoTemplate.remove(blueprintBackup);
  }
}
