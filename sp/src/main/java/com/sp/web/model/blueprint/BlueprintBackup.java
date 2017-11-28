package com.sp.web.model.blueprint;

import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Dax Abraham
 * 
 *         The entity to store the blueprint backup when blueprint is in edit state.
 */
@Document(collection = "blueprintBackup")
public class BlueprintBackup extends Blueprint {

  private static final long serialVersionUID = -3774825047675082717L;

  private String blueprintId;
  
  /**
   * Default Constructor.
   */
  public BlueprintBackup() {
    // default constructor
  }
  
  /**
   * Constructor from blueprint.
   * 
   * @param blueprint
   *          - blueprint
   */
  public BlueprintBackup(Blueprint blueprint) {
    BeanUtils.copyProperties(blueprint, this, "id");
    blueprintId = blueprint.getId();
  }

  public String getBlueprintId() {
    return blueprintId;
  }

  public void setBlueprintId(String blueprintId) {
    this.blueprintId = blueprintId;
  }

  /**
   * Helper method to copy the blueprint data back into blueprint.
   * 
   * @param blueprint
   *          - blueprint to update
   */
  public void updateBlueprint(Blueprint blueprint) {
    BeanUtils.copyProperties(this, blueprint, "id");
  }
}
