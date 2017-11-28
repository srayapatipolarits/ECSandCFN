package com.sp.web.model.assessment;

import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.assessment.questions.QuestionsBean;
import com.sp.web.assessment.questions.QuestionsFactory;
import com.sp.web.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The Prism Assessment class to store the currently executing prism assessment.
 */
public class PrismAssessment {
  
  private String id;
  private String userId;
  private boolean completed;
  private LocalDateTime createdOn;
  private LocalDateTime completedOn;
  private List<CategoryType> categories;
  private List<AssessmentResponse> assessmentData;
  /*
   * The parameters for the current assessment in progress
   */
  private CategoryType category;
  private int questionIndex;
  private int lastCategoryIndex;
  private int questionsAnsweredCount;
  
  /**
   * Default constructor.
   */
  public PrismAssessment() {
    super();
  }
  
  /**
   * Constructor from user.
   * 
   * @param user
   *          - user
   */
  public PrismAssessment(User user) {
    this.userId = user.getId();
    this.createdOn = LocalDateTime.now();
    this.lastCategoryIndex = -1;
    this.questionIndex = 0;
    this.questionsAnsweredCount = 0;
    this.assessmentData = new ArrayList<AssessmentResponse>();
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public boolean isCompleted() {
    return completed;
  }
  
  public void setCompleted(boolean completed) {
    this.completed = completed;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public LocalDateTime getCompletedOn() {
    return completedOn;
  }
  
  public void setCompletedOn(LocalDateTime completedOn) {
    this.completedOn = completedOn;
  }
  
  public List<CategoryType> getCategories() {
    return categories;
  }
  
  public void setCategories(List<CategoryType> categories) {
    this.categories = categories;
  }
  
  public List<AssessmentResponse> getAssessmentData() {
    return assessmentData;
  }
  
  public void setAssessmentData(List<AssessmentResponse> assessmentData) {
    this.assessmentData = assessmentData;
  }
  
  public CategoryType getCategory() {
    return category;
  }
  
  public void setCategory(CategoryType category) {
    this.category = category;
  }
  
  /**
   * Initializes the prism assessment with the categories to process.
   * 
   * @param categories
   *          - categories
   */
  public void initializeCategories(List<CategoryType> categories) {
    if (this.categories == null) {
      this.categories = categories;
      getNextCategory();
    }
  }
  
  /**
   * Gets the next category to process.
   * 
   * @return the next category
   */
  public CategoryType getNextCategory() {
    lastCategoryIndex++;
    category = (categories.size() > lastCategoryIndex) ? categories.get(lastCategoryIndex) : null;
    questionIndex = 0;
    return category;
  }
  
  /**
   * Go back one question.
   * 
   * @param questionsFactory
   *          - questions factory
   */
  public void processPrevious(QuestionsFactory questionsFactory) {
    questionIndex--;
    if (questionIndex == -1) {
      if (lastCategoryIndex > 0) {
        --lastCategoryIndex;
        category = categories.get(lastCategoryIndex);
        List<QuestionsBean> questions = questionsFactory.getQuestions(category);
        questionIndex = questions.size() - 1;
      } else {
        questionIndex = 0;
        throw new IllegalArgumentException("No questions answered yet.");
      }
    }
    questionsAnsweredCount--;
  }
  
  public List<String> getLastAnswer() {
    return assessmentData.get(assessmentData.size() - 1).getSelection();
  }

  /**
   * Process the current question.
   * 
   * @param questionNumber
   *          - question number
   * @param answer
   *          - answer
   */
  public void process(int questionNumber, List<String> answer) {
    assessmentData.add(new AssessmentResponse(questionNumber, answer));
    questionIndex++;
    questionsAnsweredCount++;
  }

  public Map<Integer, List<String>> getAssessmentDataMap() {
    return assessmentData.stream().collect(
        Collectors.toMap(AssessmentResponse::getQuestionNum, AssessmentResponse::getSelection, (v1,
            v2) -> v2));
  }

  public int getQuestionIndex() {
    return questionIndex;
  }

  public void setQuestionIndex(int questionIndex) {
    this.questionIndex = questionIndex;
  }

  public int getQuestionsAnsweredCount() {
    return questionsAnsweredCount;
  }

  public void setQuestionsAnsweredCount(int questionsAnsweredCount) {
    this.questionsAnsweredCount = questionsAnsweredCount;
  }

  public boolean allCategoriesAnswered() {
    return (lastCategoryIndex >= categories.size());
  }
  
}
