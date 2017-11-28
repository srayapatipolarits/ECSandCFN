package com.sp.web.mvc.assessment;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.sp.web.assessment.questions.AssessmentQuestionFactory;
import com.sp.web.assessment.questions.CategoriesBean;
import com.sp.web.assessment.questions.CategoryBean;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.assessment.questions.QuestionFactoryType;
import com.sp.web.assessment.questions.QuestionType;
import com.sp.web.assessment.questions.QuestionsFactory;
import com.sp.web.mvc.test.setup.SPTestBase;
import com.sp.web.xml.questions.CategoryDocument.Category;
import com.sp.web.xml.questions.MultipleChoiceResponseDocument.MultipleChoiceResponse;
import com.sp.web.xml.questions.QuestionDocument.Question;
import com.sp.web.xml.questions.QuestionsDocument.Questions;
import com.sp.web.xml.questions.RatingResponseDocument.RatingResponse;
import com.sp.web.xml.questions.VariantDocument.Variant;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;

/**
 * This is a test class for loading the questions as well as updating the questions.xml.
 * 
 * @author Dax Abraham
 *
 */
public class QuestionsLoaderTest extends SPTestBase {
  
  @Value("classpath:Assessment-Questions-updated.csv")
  private Resource modifiedQuestionsAns;
  
  @Value("classpath:3rdPartyAssessmentQuestions.tsv")
  private Resource thirdPartyQuestionsAns;
  
  private List<String> questionsAndAnsList;
  
  @Autowired
  AssessmentQuestionFactory assessmentquestionsFactory;
  
  @Test
  public void testDoNothing() {
    assertTrue(true);
  }
  
//  public void test() throws IOException {
//    questionsAndAnsList = IOUtils.readLines(modifiedQuestionsAns.getInputStream());
//    assertTrue("Read the lines from the file :" + questionsAndAnsList.size(),
//        questionsAndAnsList.size() == 4);
//    log.debug("Read the lines from the file :" + questionsAndAnsList.size());
//    ArrayList<String> o = new ArrayList<String>();
//    StringTokenizer st = new StringTokenizer(questionsAndAnsList.get(0));
//    while (st.hasMoreTokens()) {
//      o.add(st.nextToken());
//    }
//    
//    String[] originalQuestions = StringUtils.split(questionsAndAnsList.get(0), "\t");
//    String[] modifiedQuestions = StringUtils.split(questionsAndAnsList.get(1), "\t");
//    
//    String[] originalResponses = StringUtils.split(questionsAndAnsList.get(2), "\t");
//    String[] modifiedResponses = StringUtils.split(questionsAndAnsList.get(3), "\t");
//    
//    assertThat("The size of both the arrays match !!!", originalQuestions.length,
//        is(modifiedQuestions.length));
//    
//    QuestionsFactory questionFactory = assessmentquestionsFactory.getQuestionFactory("en_US",
//        QuestionFactoryType.SPAssessmentQuestion);
//    try {
//      log.debug("Starting work");
//      int i = 0;
//      Questions questions = questionFactory.getQuestions();
//      for (Category cat : questions.getCategoryList()) {
//        for (Question q : cat.getQuestionList()) {
//          Variant v = q.getVariant();
//          if (v.getText() != null) {
//            if (v.getText().equals(originalQuestions[i])) {
//              v.setText(modifiedQuestions[i]);
//            } else {
//              log.debug("Original:" + originalQuestions[i] + ":Existing:" + v.getText());
//              throw new Exception("Something happened !!!");
//            }
//          }
//          // update the responses
//          String[] oRespArr = originalResponses[i].split(",");
//          String[] mRespArr = modifiedResponses[i].split(",");
//          assertThat(originalResponses[i] + ":" + modifiedResponses[i], oRespArr.length,
//              is(mRespArr.length));
//          int j = 0;
//          if (QuestionType.MultipleChoice == QuestionType.valueOf(v.getType())) {
//            for (MultipleChoiceResponse mcr : v.getMultipleChoiceResponses()
//                .getMultipleChoiceResponseList()) {
//              String oStr = oRespArr[j].trim();
//              String mStr = mRespArr[j].trim();
//              if (mcr.getText().contains(",")) {
//                oStr += "," + oRespArr[++j];
//                mStr += "," + mRespArr[j];
//              }
//              if (mcr.getText().equals(oStr)) {
//                mcr.setText(mStr.replace(";", ","));
//              } else {
//                log.debug("Original:" + oStr + ":Existing:" + mcr.getText() + ":" + mcr.getId());
//                throw new Exception("Something happened !!!");
//              }
//              j++;
//            }
//          } else if (QuestionType.Rating == QuestionType.valueOf(v.getType())) {
//            for (RatingResponse rr : v.getRatingResponses().getRatingResponseList()) {
//              
//              String oStr = oRespArr[j].trim();
//              String mStr = mRespArr[j].trim();
//              if (rr.getText().contains(",")) {
//                oStr += "," + oRespArr[++j];
//                mStr += "," + mRespArr[j];
//              }
//              
//              if (rr.getText().equals(oStr)) {
//                rr.setText(mStr.replace(";", ","));
//              } else {
//                log.debug("Original:" + oStr + ":Existing:" + rr.getText());
//                throw new Exception("Something happened !!!");
//              }
//              j++;
//            }
//          } else {
//            fail("Don't know how to process !!!");
//          }
//          
//          i++;
//        }
//      }
//      questions.save(new File("updatedQuestions.xml"));
//    } catch (Exception e) {
//      e.printStackTrace();
//      fail();
//    }
//    
//  }
//  
//  @Test
//  public void testThirdParty() throws IOException {
//    questionsAndAnsList = IOUtils.readLines(thirdPartyQuestionsAns.getInputStream());
//    
//    assertTrue("Read the lines from the file :" + questionsAndAnsList.size(),
//        questionsAndAnsList.size() == 3);
//    log.debug("Read the lines from the file :" + questionsAndAnsList.size());
//    
//    String[] modifiedQuestions = StringUtils.split(questionsAndAnsList.get(1), "\t");
//    
//    String[] modifiedResponses = StringUtils.split(questionsAndAnsList.get(2), "\t");
//    
//    assertThat("The size of both the arrays match !!!", modifiedQuestions.length,
//        is(modifiedResponses.length));
//    
//    QuestionsFactory questionFactory = assessmentquestionsFactory.getQuestionFactory("en_US",
//        QuestionFactoryType.SPAssessmentQuestion);
//    
//    try {
//      log.debug("Starting work");
//      int i = 1;
//      Questions questions = questionFactory.getQuestions();
//      for (Category cat : questions.getCategoryList()) {
//        for (Question q : cat.getQuestionList()) {
//          Variant v = q.getVariant();
//          if (v.getText() != null) {
//            v.setText(normalizeText(modifiedQuestions[i]));
//          }
//          // update the responses
//          String[] mRespArr = modifiedResponses[i].split("\\\\");
//          int j = 0;
//          if (QuestionType.MultipleChoice == QuestionType.valueOf(v.getType())) {
//            assertThat(v.getMultipleChoiceResponses().getMultipleChoiceResponseList().size(),
//                is(mRespArr.length));
//            for (MultipleChoiceResponse mcr : v.getMultipleChoiceResponses()
//                .getMultipleChoiceResponseList()) {
//              String mStr = mRespArr[j].trim();
//              mcr.setText(normalizeText(mStr));
//              j++;
//            }
//          } else if (QuestionType.Rating == QuestionType.valueOf(v.getType())) {
//            assertThat(v.getRatingResponses().getRatingResponseList().size(), is(mRespArr.length));
//            for (RatingResponse rr : v.getRatingResponses().getRatingResponseList()) {
//              String mStr = mRespArr[j].trim();
//              rr.setText(normalizeText(mStr));
//              j++;
//            }
//          } else {
//            fail("Don't know how to process !!!");
//          }
//          i++;
//        }
//      }
//      questions.save(new File("updatedThridPartyQuestions.xml"));
//    } catch (Exception e) {
//      e.printStackTrace();
//      fail();
//    }
//    
//  }
  
  @Test
  public void testPrintCategoryQNos() {
    try {
      QuestionsFactory questionFactory = assessmentquestionsFactory.getQuestionFactory("en_US",
          QuestionFactoryType.SPAssessmentQuestion);
      CategoriesBean categoriesSummary = questionFactory.getCategoriesSummary();
      for (Entry<CategoryType, CategoryBean> entry : categoriesSummary.getCategoryMap().entrySet()) {
        CategoryBean categoryInfo = entry.getValue();
        System.out.println(entry.getKey() + ":" + categoryInfo.getTotalQuestions());
//        System.out.println(categoryInfo.getTotalQuestions());
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  private String normalizeText(String str) {
    str = str.replaceAll("\\[Name\\]", "{0}").replaceAll("\\[he/she\\]", "{1}")
        .replaceAll("\\[He/She\\]", "{2}").replaceAll("\\[him/her\\]", "{3}")
        .replaceAll("\\[himself/herself\\]", "{4}").replaceAll("\\[his/her\\]", "{5}")
        .replaceAll("\\[his/hers\\]", "{6}").replaceAll("\\[His/Her\\]", "{7}")
        .replaceAll("'", "''");
    if (str.contains("[")) {
      throw new RuntimeException(str);
    }
    return str;
  }
}
