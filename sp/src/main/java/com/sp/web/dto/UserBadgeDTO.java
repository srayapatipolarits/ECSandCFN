package com.sp.web.dto;

import com.sp.web.model.User;
import com.sp.web.model.badge.UserBadge;
import com.sp.web.model.badge.UserBadgeActivity;
import com.sp.web.service.badge.BadgeFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserBadgeDTO is the handling the badge information for the user.
 * 
 * @author pradeepruhil
 *
 */
public class UserBadgeDTO extends UserDTO {
  
  private static final long serialVersionUID = 7615989915377230588L;
  private List<BadgeDTO> userBadges;
  private static final Comparator<UserBadge> awaredOn = (db1, db2) -> db2.getAwarededOn()
      .compareTo(db1.getAwarededOn());
  
  public UserBadgeDTO(User user) {
    super(user);
  }
  
  /**
   * getUserBadges method return the completed badges for the user.
   * 
   * @return the completed badges of the user.
   */
  public List<BadgeDTO> getUserBadges() {
    if (userBadges == null) {
      userBadges = new ArrayList<BadgeDTO>();
    }
    return userBadges;
  }
  
  public void setUserBadges(List<BadgeDTO> userBadges) {
    this.userBadges = userBadges;
  }
  
  /**
   * update the badge with the content to be shown to the user.
   * 
   * @param user
   *          for which badge to be updated.
   * @param badgeFactory
   *          containing the User badge.
   */
  public void updateBadge(User user, BadgeFactory badgeFactory) {
    UserBadgeActivity userBadge = badgeFactory.getUserBadge(user);
    Collection<UserBadge> values = userBadge.getCompletedBadges().values();
    this.userBadges = values.stream().sorted(awaredOn).map(BadgeDTO::new)
        .collect(Collectors.toList());
  }
}
