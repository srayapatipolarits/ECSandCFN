package com.sp.web.service.hiring.match;

import com.sp.web.dao.hiring.match.HiringPortraitDao;
import com.sp.web.dto.hiring.match.HiringPortraitBaseDTO;
import com.sp.web.dto.hiring.match.HiringPortraitMatchDTO;
import com.sp.web.dto.hiring.match.HiringPortraitMatchListingDTO;
import com.sp.web.dto.hiring.match.HiringPortraitMatchResponseDTO;
import com.sp.web.dto.hiring.match.HiringPortraitMatchResultDTO;
import com.sp.web.dto.hiring.role.HiringRoleAndMatchDTO;
import com.sp.web.dto.hiring.user.HiringUserPortraitMatchDTO;
import com.sp.web.dto.hiring.user.HiringUserPrismAndMatchDTO;
import com.sp.web.form.hiring.match.HiringPortraitMatchForm;
import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.model.hiring.group.HiringGroup;
import com.sp.web.model.hiring.match.HiringPortrait;
import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.service.hiring.group.HiringGroupFactory;
import com.sp.web.service.hiring.match.processor.HiringPortraitMatchProcessorFactory;
import com.sp.web.service.hiring.role.HiringRoleFactory;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The factory class for the hiring portrait.
 */
@Component
public class HiringPortraitMatchFactory {
  
  @Autowired
  HiringPortraitMatchFactoryCache factoryCache;
  
  @Autowired
  HiringUserFactory userFactory;
  
  @Autowired
  HiringPortraitMatchProcessorFactory matchProcessor;
  
  @Autowired
  HiringRoleFactory rolesFactory;
  
  @Autowired
  HiringGroupFactory groupsFactory;
  
  /**
   * Get all the portraits along with the roles for a company.
   * 
   * @param user
   *          - user
   * @return the list of portraits
   */
  public List<HiringPortraitMatchListingDTO> getAll(User user) {
    final String companyId = user.getCompanyId();
    List<HiringPortrait> all = factoryCache.getAllByCompanyId(companyId);
    return all.stream().map(HiringPortraitMatchListingDTO::new)
        .map(p -> p.addRoles(rolesFactory.getRolesForPortrait(p.getId(), companyId)))
        .collect(Collectors.toList());
  }
  
  /**
   * Get all the portraits only basic information is returned.
   * 
   * @param user
   *          - user
   * @return the list of portraits for the company
   */
  public List<HiringPortraitBaseDTO> getAllBasicPortrait(User user) {
    return factoryCache.getAllByCompanyId(user.getCompanyId()).stream()
        .map(HiringPortraitBaseDTO::new).collect(Collectors.toList());
  }
  
  /**
   * Get the portrait for the given portrait id.
   * 
   * @param portraitId
   *          - portrait id
   * @return the portrait
   */
  public HiringPortraitDao getPortrait(String portraitId) {
    return factoryCache.get(portraitId);
  }
  
  /**
   * Get the hiring portrait match portrait.
   * 
   * @param user
   *          - user
   * @param portraitId
   *          - portrait id
   * @return the portrait
   */
  private HiringPortraitDao getPortrait(User user, String portraitId) {
    HiringPortraitDao hiringPortraitDao = factoryCache.get(portraitId);
    Assert.notNull(hiringPortraitDao,
        MessagesHelper.getMessage("error.hire.portrait.not.found", user.getLocale()));
    
    // check authorization for portrait
    Assert.isTrue(hiringPortraitDao.isCompanyAllowed(user.getCompanyId()), "Unauthorized access.");
    return hiringPortraitDao;
  }
  
  /**
   * Process the portrait match.
   * 
   * @param portrait
   *          - portrait
   * @param users
   *          - user list
   * @param addPrism
   *          - flag if PRISM portrait needs to be added to the result
   * @return the list of match results.
   */
  public List<HiringUserPrismAndMatchDTO> processMatch(HiringPortraitDao portrait,
      List<HiringUser> users, boolean addPrism) {
    List<HiringUserPrismAndMatchDTO> matchResults = new ArrayList<HiringUserPrismAndMatchDTO>();
    for (HiringUser user : users) {
      final HiringUserPrismAndMatchDTO userMatch = new HiringUserPrismAndMatchDTO(user, addPrism);
      userMatch.setMatchResult(processMatch(user, portrait));
      matchResults.add(userMatch);
    }
    return matchResults;
  }
  
  /**
   * Performing the actual match logic.
   * 
   * @param user
   *          - hiring user
   * @param portrait
   *          - the portrait to match
   */
  private HiringPortraitMatchResultDTO processMatch(HiringUser user, HiringPortraitDao portrait) {
    return Optional.ofNullable(user.getAnalysis())
        .map(a -> matchProcessor.process(portrait, user.getAnalysis())).orElse(null);
  }
  
  /**
   * Process the portrait match for the given user and role.
   * 
   * @param user
   *          - user
   * @param role
   *          - role
   * @param hiringRoleAndMatchDTO
   *          - the hiring role
   */
  public void processMatch(HiringUser user, HiringRole role,
      HiringRoleAndMatchDTO hiringRoleAndMatchDTO) {
    Optional.ofNullable(role.getPortraitId()).map(this::getPortrait)
        .ifPresent(p -> processMatch(user, p, hiringRoleAndMatchDTO));
  }
  
  /**
   * Process the match result and update the hiring role and match DTO.
   * 
   * @param user
   *          - user
   * @param portrait
   *          - portrait
   * @param hiringRoleAndMatchDTO
   *          - hiring role and match DTO
   */
  private void processMatch(HiringUser user, HiringPortraitDao portrait,
      HiringRoleAndMatchDTO hiringRoleAndMatchDTO) {
    Optional.ofNullable(processMatch(user, portrait)).ifPresent(
        r -> hiringRoleAndMatchDTO.update(r, portrait));
  }
  
  /**
   * Perform the portrait match for the given data in the form.
   * 
   * @param user
   *          - user
   * @param form
   *          - the form data
   * @return the portrait match
   */
  public HiringPortraitMatchResponseDTO processMatch(User user, HiringPortraitMatchForm form) {
    
    // get the portrait
    final HiringPortraitDao hiringPortraitDao = getPortrait(user, form.getId());
    
    Set<String> userIds = new HashSet<String>();
    
    // getting the users from the groups
    final String companyId = user.getCompanyId();
    Optional.ofNullable(form.getGroupIds()).ifPresent(
        gids -> userIds.addAll(getGroupUserIds(gids, companyId)));

    // get the users from the user id list
    Optional.ofNullable(form.getUserIds()).ifPresent(userIds::addAll);
    
    final List<HiringUser> matchUsers = getMatchUsers(userIds, companyId);
    Assert.notEmpty(matchUsers, "User not found to process match.");
    
    // process the user matches and send the response
    HiringPortraitMatchResponseDTO response = new HiringPortraitMatchResponseDTO(hiringPortraitDao);
    response.setUsers(matchUsers.stream()
        .map(u -> new HiringUserPortraitMatchDTO(u, processMatch(u, hiringPortraitDao)))
        .collect(Collectors.toList()));
    return response;
  }
  
  private Set<String> getGroupUserIds(List<String> gids, String companyId) {
    return gids.stream().map(groupsFactory::get).filter(Objects::nonNull)
        .filter(g -> g.getCompanyId().equals(companyId)).map(HiringGroup::getUserIds)
        .filter(Objects::nonNull).flatMap(Set::stream).collect(Collectors.toSet());
  }
  
  /**
   * Get the list of users from the given user id list.
   * 
   * @param uids
   *          - user id's
   * @param companyId
   *          - company id
   * @return the list of users
   */
  private List<HiringUser> getMatchUsers(Set<String> uids, String companyId) {
    return uids.stream().map(userFactory::getUser).filter(Objects::nonNull)
        .filter(u -> u.getCompanyId().equals(companyId)).collect(Collectors.toList());
  }
  
  /**
   * Get the details of the given portrait for the company.
   * 
   * @param user
   *          - user
   * @param portraitId
   *          - portrait id
   * @return the details of the portrait
   */
  public HiringPortraitMatchDTO get(User user, String portraitId) {
    
    // get the portrait
    HiringPortraitDao hiringPortraitDao = getPortrait(user, portraitId);
    
    // returning the hiring portrait match details
    final String companyId = user.getCompanyId();
    return new HiringPortraitMatchDTO(hiringPortraitDao, rolesFactory.getRolesForPortrait(
        portraitId, companyId), companyId);
  }
  
}
