package com.sp.web.service.email;

import org.springframework.integration.mail.SearchTermStrategy;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.search.SearchTerm;

/**
 * Search term strategy to enable imap protocol to pick all messages
 * @author vikram
 *
 */
public class AcceptAllSearchTermStrategy implements SearchTermStrategy {
  
  @Override
  public SearchTerm generateSearchTerm(Flags supportedFlags, Folder folder) {
    return new AcceptAllSearchTerm();
  }
  
  private class AcceptAllSearchTerm extends SearchTerm{

    private static final long serialVersionUID = 7006570822758782412L;

    public boolean match(Message mesg){
      return true;
    }
  }
  
}
