package com.sp.web.controller.pulse;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.assessment.questions.QuestionType;
import com.sp.web.model.pulse.PulseQuestionBean;
import com.sp.web.model.pulse.PulseQuestionSet;
import com.sp.web.model.pulse.QuestionOptions;
import com.sp.web.mvc.test.setup.SPTestBase;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PulseLoaderTest extends SPTestBase {

  @Value("classpath:PulseQuestions.tsv")
  private Resource pulseQuestionsFile;

  @Test
  public void testDoNothing() {
    assertTrue(true);
  }

  /**
   * Loader test.
   */
  @Test
  public void test() {
    try {
      List<String> pulseQuestionsContentList = IOUtils.readLines(pulseQuestionsFile
          .getInputStream());
      
      assertThat(pulseQuestionsContentList.size(), is(3));
      
      PulseQuestionSet pulseQuestionSet = new PulseQuestionSet();
      pulseQuestionSet.setId("defaultWorkspaceQuestionSet");
      pulseQuestionSet.setName("pulseQuestionSet.");
      List<QuestionOptions> optionList = new ArrayList<QuestionOptions>();
      optionList.add(new QuestionOptions("Strongly Disagree", 1));
      optionList.add(new QuestionOptions("Disagree", 2));
      optionList.add(new QuestionOptions("Agree", 4));
      optionList.add(new QuestionOptions("Strongly Agree", 5));
      Map<String, List<PulseQuestionBean>> questions = new HashMap<String, List<PulseQuestionBean>>();
      List<String> categoryKeys = new ArrayList<String>();
      for (String pulseCategoryQuestionStr : pulseQuestionsContentList) {
        List<PulseQuestionBean> categoryQuestionBeanList = new ArrayList<PulseQuestionBean>();
        String[] categoryQuestions = pulseCategoryQuestionStr.split("\t");
        String key = categoryQuestions[0];
        categoryKeys.add(key);
        for (int idx = 1; idx < categoryQuestions.length; idx++) {
          PulseQuestionBean questionBean = new PulseQuestionBean(idx - 1,
              QuestionType.MultipleChoice, categoryQuestions[idx], optionList);
          categoryQuestionBeanList.add(questionBean);
        }
        questions.put(key, categoryQuestionBeanList);
      }
      pulseQuestionSet.setCategoryKeys(categoryKeys);
      pulseQuestionSet.setQuestions(questions);
      
      ObjectMapper om = new ObjectMapper();
      System.out.println(om.writeValueAsString(pulseQuestionSet));
    } catch (Exception e1) {
      e1.printStackTrace();
      fail();
    }
  }
}
