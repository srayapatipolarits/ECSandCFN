package com.sp.web.dto.blueprint;

import com.sp.web.dto.CommentsDTO;
import com.sp.web.model.blueprint.BlueprintMissionStatement;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for blueprint.
 */
public class BlueprintMissionStatementDTO {
  
  private String uid;
  private String text;
  private List<CommentsDTO> commentsList;

  /**
   * Constructor.
   * 
   * @param missionStatement
   *            - the mission statement
   * @param commentsFactory
   *            - the comments factory
   * @param commentsDTOMap
   *            - the user cache 
   */
  public BlueprintMissionStatementDTO(BlueprintMissionStatement missionStatement,
      Map<String, List<CommentsDTO>> commentsDTOMap) {
    BeanUtils.copyProperties(missionStatement, this);
    commentsList = commentsDTOMap.remove(getUid());
  }

  public String getUid() {
    return uid;
  }
  
  public void setUid(String uid) {
    this.uid = uid;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }

  public List<CommentsDTO> getCommentsList() {
    return commentsList;
  }

  public void setCommentsList(List<CommentsDTO> commentsList) {
    this.commentsList = commentsList;
  }

  /**
   * Filters all the comments for only the given user.
   * 
   * @param email
   *          - email to filter for
   */
  public void filterComments(String email) {
    if (commentsList != null) {
      commentsList.removeIf(c -> !c.getBy().getEmail().equals(email));
    }
  }
  
}
