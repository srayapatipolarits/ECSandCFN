package com.sp.web.dto.goal;

import com.sp.web.dto.PracticeFeedbackRequestDTO;
import com.sp.web.dto.SPNoteFeedbackPracticeAreaDTO;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.SPNoteFeedbackType;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The envelop class to hold all the notes and feedback listing.
 */
public class NotesAndFeedbackListingDTO {
  
  private String id;
  private String name;
  private SPNoteFeedbackType type;
  private String createdOn;
  private GoalCategory goalCategory;
  List<PracticeFeedbackRequestDTO> notesFeedbackList;
  
  /**
   * Create from the practice area DTO and feedback request DTO.
   * 
   * @param practiceAreaDTO
   *            - Practice area DTO
   * @param dto
   *            - notes and feedback DTO
   */
  public NotesAndFeedbackListingDTO(SPNoteFeedbackPracticeAreaDTO practiceAreaDTO,
      PracticeFeedbackRequestDTO dto) {
    BeanUtils.copyProperties(practiceAreaDTO, this);
    this.type = dto.getType();
    this.createdOn = dto.getFormattedDate();
    if (dto.getPracticeAreaDTO() != null) {
      this.goalCategory = dto.getPracticeAreaDTO().getCategory();
    } else {
      this.goalCategory = null;
    }
    notesFeedbackList = new ArrayList<PracticeFeedbackRequestDTO>();
    notesFeedbackList.add(dto);
  }

  public SPNoteFeedbackType getType() {
    return type;
  }
  
  public void setType(SPNoteFeedbackType type) {
    this.type = type;
  }
    
  public List<PracticeFeedbackRequestDTO> getNotesFeedbackList() {
    return notesFeedbackList;
  }
  
  public void setNotesFeedbackList(List<PracticeFeedbackRequestDTO> notesFeedbackList) {
    this.notesFeedbackList = notesFeedbackList;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public GoalCategory getGoalCategory() {
    return goalCategory;
  }

  public void setGoalCategory(GoalCategory goalCategory) {
    this.goalCategory = goalCategory;
  }
}
