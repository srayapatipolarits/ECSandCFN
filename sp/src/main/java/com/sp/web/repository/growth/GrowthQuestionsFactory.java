package com.sp.web.repository.growth;

import com.sp.web.model.GrowthFeedbackQuestions;
import com.sp.web.model.User;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GrowthQuestionsFactory will return the growth feedback question factory.
 * 
 * @author pradeep
 */
@Component
public class GrowthQuestionsFactory {

  /**
   * <code>getGrowthFeedbackQuestions</code> method will fetch the goals of the
   * user.
   * 
   * @param goals
   *          growth Question to be returned
   * @return the growth feedback questions.
   */
  public List<GrowthFeedbackQuestions> getGrowthFeedbackQuestions(List<String> goals, User user) {

    List<String> optionsList = new ArrayList<String>();
    optionsList.add(MessagesHelper.getMessage("growth.assessment.question.option.yes"));
    optionsList.add(MessagesHelper.getMessage("growth.assessment.question.option.wip"));
    optionsList.add(MessagesHelper.getMessage("growth.assessment.question.option.no"));

    return goals
        .stream()
        .map(
            goal -> {
            GrowthFeedbackQuestions feedbackQuestions = new GrowthFeedbackQuestions();
            String deleteWhitespace = StringUtils.deleteWhitespace(goal);
            String question = MessagesHelper.genderNormalizeFromKey(
                "growth.assessment.questions.".concat(deleteWhitespace), user);
            String optionalText;
            try {
              optionalText = MessagesHelper.genderNormalizeFromKey("growth.assessment.questions." + deleteWhitespace
                  + ".optionalText", user);
            } catch (NoSuchMessageException ex) {
              optionalText = MessagesHelper.getMessage("growth.requestDetailEditExternal.providefeedback");
            }

            feedbackQuestions.setQuestion(question);
            feedbackQuestions.setOptionalText(optionalText);
            feedbackQuestions.setOptionsList(optionsList);
            return feedbackQuestions;
          }).collect(Collectors.toList());
  }
}
