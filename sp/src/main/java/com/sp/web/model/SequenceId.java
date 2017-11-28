package com.sp.web.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Dax Abraham The sequence entity.
 */
@Document(collection = "sequence")
public class SequenceId {
 
  @Id
  private String id;
 
  private long seq;
  
  private long certSeq;
  
  private long promoCodeSeq;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public long getSeq() {
    return seq;
  }

  public void setSeq(long seq) {
    this.seq = seq;
  }

  public long getCertSeq() {
    return certSeq;
  }

  public void setCertSeq(long certSeq) {
    this.certSeq = certSeq;
  }

  public long getPromoCodeSeq() {
    return promoCodeSeq;
  }

  public void setPromoCodeSeq(long promoCodeSeq) {
    this.promoCodeSeq = promoCodeSeq;
  }
}