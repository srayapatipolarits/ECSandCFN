package com.sp.web.xml.questions.impl;

import static org.junit.Assert.assertTrue;

//import com.sp.web.xml.questions.CategoryDocument.Category;
//import com.sp.web.xml.questions.MultipleChoiceResponseDocument.MultipleChoiceResponse;
//import com.sp.web.xml.questions.QuestionDocument.Question;
//import com.sp.web.xml.questions.QuestionsDocument;
//import com.sp.web.xml.questions.RatingResponseDocument.RatingResponse;
//import com.sp.web.xml.questions.TraitTransformDocument.TraitTransform;

import org.apache.xmlbeans.XmlException;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;

/**
 * @author daxabraham
 *
 */
public class QuestionsDocumentImplTest {

	@Test
	public void test() {
//		try {
//			QuestionsDocument qDocument = QuestionsDocument.Factory.parse(this.getClass().getClassLoader().getResourceAsStream("questions.xml"));
//			System.out.println(qDocument.getQuestions().getCategoryList().size());
//			HashSet<String> categoriesSet = new HashSet<String>();
//			HashSet<String> traitsSet = new HashSet<String>();
//			for(Category c : qDocument.getQuestions().getCategoryList() ) {
//				for(Question q : c.getQuestionList() ) {
//					if( q.getVariant().getMultipleChoiceResponses() != null ) {
//						for(MultipleChoiceResponse multiResponse : q.getVariant().getMultipleChoiceResponses().getMultipleChoiceResponseList() ) {
//							for(TraitTransform transform : multiResponse.getTraitTransforms().getTraitTransformList()) {
//								traitsSet.add(transform.getTrait());
//								categoriesSet.add(transform.getCategory());
//							}
//						}
//					} else {
//						for(RatingResponse ratingResponse : q.getVariant().getRatingResponses().getRatingResponseList() ) {
//							for(TraitTransform transform : ratingResponse.getTraitTransforms().getTraitTransformList()) {
//								traitsSet.add(transform.getTrait());
//								categoriesSet.add(transform.getCategory());
//							}
//						}
//					}
//				}
//			}
//			// printing the traits and categories
//			StringBuffer sb = new StringBuffer();
//			for(String s : traitsSet) {
//				sb.append(s).append(",");
//			}
//			System.out.println(sb.toString());
//			sb = new StringBuffer();
//			for(String s : categoriesSet) {
//				sb.append(s).append(",");
//			}
//			System.out.println(sb.toString());
//			assertTrue(true);
//			
//		} catch (XmlException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
	}

}
