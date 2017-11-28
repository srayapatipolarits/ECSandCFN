package com.sp.web.model.hiring.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.sp.web.model.HiringUser;

import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

/**
 * @author Dax Abraham
 *
 *         The archived hiring user entity.
 */
public class HiringUserArchive extends HiringUser {

  /**
   * Serial version.
   */
  private static final long serialVersionUID = 1534066280349534181L;
  
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate archivedOn;
  
  public HiringUserArchive() {}
  
  /**
   * Constructor to copy the properties of the hiring user.
   * 
   * @param user
   *          - hiring user
   */
  public HiringUserArchive(HiringUser user) {
    BeanUtils.copyProperties(user, this);
    archivedOn = LocalDate.now();
  }

  public LocalDate getArchivedOn() {
    return archivedOn;
  }

  public void setArchivedOn(LocalDate archivedOn) {
    this.archivedOn = archivedOn;
  }

}
