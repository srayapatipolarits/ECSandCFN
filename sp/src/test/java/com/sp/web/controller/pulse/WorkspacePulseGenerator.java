package com.sp.web.controller.pulse;

import static org.junit.Assert.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.assessment.questions.QuestionType;
import com.sp.web.model.pulse.PulseQuestionBean;
import com.sp.web.model.pulse.PulseQuestionSet;
import com.sp.web.model.pulse.QuestionOptions;
import com.sp.web.mvc.test.setup.SPTestBase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkspacePulseGenerator extends SPTestBase {

  @Test 
  public void doNothing() {}
  
  //@Test
  public void test() {
    PulseQuestionSet pulseQuestionSet = new PulseQuestionSet();
    pulseQuestionSet.setName("defaultWorkspaceQuestionSet");
    final Map<String, List<PulseQuestionBean>> questions = new HashMap<String, List<PulseQuestionBean>>();
    List<QuestionOptions> defaultQuestionOptions = new ArrayList<QuestionOptions>();
    defaultQuestionOptions.add(new QuestionOptions("Strongly Disagree", 1.0));
    defaultQuestionOptions.add(new QuestionOptions("Disagree", 2.0));
    defaultQuestionOptions.add(new QuestionOptions("Agree", 4.0));
    defaultQuestionOptions.add(new QuestionOptions("Strongly Agree", 5.0));
    
    List<PulseQuestionBean> cultureQuestions = new ArrayList<PulseQuestionBean>();
    cultureQuestions.add(new PulseQuestionBean(0, QuestionType.MultipleChoice,
        "I believe in the mission and vision of  {0}", defaultQuestionOptions));
    cultureQuestions.add(new PulseQuestionBean(1, QuestionType.MultipleChoice,
        "I trust and respect my co-workers.", defaultQuestionOptions));
    questions.put("Culture", cultureQuestions);
    
    List<PulseQuestionBean> loyaltyQuestions = new ArrayList<PulseQuestionBean>();
    loyaltyQuestions.add(new PulseQuestionBean(0, QuestionType.MultipleChoice,
        "My company genuinely appreciates my efforts.", defaultQuestionOptions));
    loyaltyQuestions.add(new PulseQuestionBean(1, QuestionType.MultipleChoice,
        "I love my job.", defaultQuestionOptions));
    questions.put("Loyalty", loyaltyQuestions);
    
    List<PulseQuestionBean> leadershipQuestions = new ArrayList<PulseQuestionBean>();
    leadershipQuestions.add(new PulseQuestionBean(0, QuestionType.MultipleChoice,
        "I believe in the leadership team of my Company", defaultQuestionOptions));
    leadershipQuestions.add(new PulseQuestionBean(1, QuestionType.MultipleChoice,
        "My manager gives me clear direction", defaultQuestionOptions));
    questions.put("Leadership", leadershipQuestions);
    
    pulseQuestionSet.setQuestions(questions);
    
    // converting to json
    ObjectMapper om = new ObjectMapper();
    try {
      System.out.println(om.writeValueAsString(pulseQuestionSet));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      fail();
    }
  }

}
