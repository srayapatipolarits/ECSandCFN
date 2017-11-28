package com.sp.web.service.hiring.lens;

import com.sp.web.model.FeedbackUser;
import com.sp.web.repository.hiring.lens.HiringLensRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The factory cache interface to implement cache in case it is necessary.
 */
@Component
public class HiringLensFactoryCache {
  
  @Autowired
  HiringLensRepository repo;

  public List<FeedbackUser> getByUserFor(String feedbackFor) {
    return repo.findByUserFor(feedbackFor);
  }

  public FeedbackUser getById(String id) {
    return repo.findById(id);
  }

  public void save(FeedbackUser feedbackUser) {
    repo.save(feedbackUser);
  }

  public void delete(FeedbackUser feedbackUser) {
    repo.delete(feedbackUser);
  }
}
