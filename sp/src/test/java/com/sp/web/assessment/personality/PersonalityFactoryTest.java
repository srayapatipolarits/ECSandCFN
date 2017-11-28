/**
 * 
 */
package com.sp.web.assessment.personality;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.sp.web.mvc.test.setup.SPTestBase;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 * @author daxabraham
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class PersonalityFactoryTest extends SPTestBase {

	private static final Logger LOG = Logger.getLogger(PersonalityFactoryTest.class);
	
	@Autowired
	PersonalityFactory personalityFactory;
	
	@Test
	public void test() {
		assertNotNull("Personality Factory", personalityFactory);
		assertNotNull("Personality Object", personalityFactory.getPersonality());
		//getPersonalityTypes();
	}
 
	@Test
	public void testPersonality() {
		PersonalityMatcher personalityMatcher = personalityFactory.getPersonalityMatcher();
		Map<String, RangeBean> matcherMap = personalityMatcher.getMatcherMap();
		assertNotNull("Matcher Map", matcherMap);
		if(LOG.isDebugEnabled()) {
			// print all the indices 
			for(String key: matcherMap.keySet()) {
				RangeBean rangeBean = matcherMap.get(key);
				int[] tempList = rangeBean.getValueArray();
				LOG.debug("Printing for key:"+key+": length :"+tempList.length);
				for(int i=0; i<tempList.length; i++) {
					LOG.debug(tempList[i]);
				}
			}
		}
		
		// creating test data
		
		// primary personality type
		int dominanceMost = 0;
		int inducmentMost = 2;
		int submissionMost = 3;
		int complianceMost = 7;
		
		PersonalityBean personalityBean = personalityMatcher
				.getPrimaryPersonalityScore(dominanceMost, inducmentMost,
						submissionMost, complianceMost);
		
		assertNotNull("Primary personality Bean", personalityBean);
		assertTrue(
				"Personalty type primary expected :"
						+ PersonalityType.Actuary + ": got :"
						+ personalityBean.getPersonalityType(), personalityBean
						.getPersonalityType().equals(PersonalityType.Actuary));
		
		LOG.debug("Primary personaliyt bean :"+personalityBean);
		
		// under pressure personality type
		int dominanceLeast = 0;
		int inducmentLeast = 2;
		int submissionLeast = 3;
		int complianceLeast = 7;
		
		personalityBean = personalityMatcher
				.getUnderPressurePersonalityScore(dominanceLeast, inducmentLeast,
						submissionLeast, complianceLeast);
		
		assertNotNull("Under pressure personality Bean", personalityBean);
		assertTrue(
				"Personalty type under pressure expected :"
						+ PersonalityType.Innovator + ": got :"
						+ personalityBean.getPersonalityType(), personalityBean
						.getPersonalityType().equals(PersonalityType.Innovator));
		
		LOG.debug("Under pressure personaliyt bean :"+personalityBean);

		// under pressure personality type
		personalityBean = personalityMatcher
				.getUnderPressurePersonalityScore(dominanceMost - dominanceLeast, inducmentMost - inducmentLeast,
						submissionMost - submissionLeast, complianceMost - complianceLeast);
		
		assertNotNull("Perceived by others personality Bean", personalityBean);
		assertTrue(
				"Personalty type perceived by others expected :"
						+ PersonalityType.Overshift + ": got :"
						+ personalityBean.getPersonalityType(), personalityBean
						.getPersonalityType().equals(PersonalityType.Overshift));
		
		LOG.debug("Perceived by others personaliyt bean :"+personalityBean);

		
	}

	/*
	 * Only added to get the personality types from the XML
	 * 
	private void getPersonalityTypes() {
		Set<String> personalitySet = new HashSet<String>();
		for(Pattern pattern : personalityFactory.getPersonality().getPatterns().getPatternList()) {
			personalitySet.add(pattern.getName());
		}
		LOG.debug(personalitySet);
	}
	*/

}
