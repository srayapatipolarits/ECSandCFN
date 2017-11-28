package com.sp.web.dto.hiring.group;

import com.sp.web.dto.hiring.user.HiringUserListingDTO;
import com.sp.web.model.hiring.group.HiringGroup;
import com.sp.web.service.hiring.user.HiringUserFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for people analytics groups details.
 */
public class HiringGroupDTO extends HiringGroupBaseDTO {
  
  private static final long serialVersionUID = -7213366589810519989L;
  private List<HiringUserListingDTO> users;

  /**
   * Constructor where the users are not sent back.
   * 
   * @param group
   *          - group
   */
  public HiringGroupDTO(HiringGroup group) {
    super(group);
  }
  
  /**
   * Constructor.
   * 
   * @param group
   *          - group
   * @param userFactory
   *          - user factory
   */
  public HiringGroupDTO(HiringGroup group, HiringUserFactory userFactory) {
    this(group);
    users = group.getUserIds().stream().map(userFactory::getUser).filter(Objects::nonNull)
        .map(HiringUserListingDTO::new).collect(Collectors.toList());
  }

  public List<HiringUserListingDTO> getUsers() {
    return users;
  }
  
  public void setUsers(List<HiringUserListingDTO> users) {
    this.users = users;
  }
}
