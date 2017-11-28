package com.sp.web.test.setup;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;
import com.sp.web.Constants;
import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.model.Account;
import com.sp.web.model.Company;
import com.sp.web.model.CompanyTheme;
import com.sp.web.model.CreditNotePaymentInstrument;
import com.sp.web.model.ExternalUser;
import com.sp.web.model.FeedbackRequest;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.GrowthFeedbackQuestions;
import com.sp.web.model.GrowthRequest;
import com.sp.web.model.GrowthRequestArchived;
import com.sp.web.model.HiringUser;
import com.sp.web.model.PaymentInstrument;
import com.sp.web.model.PaymentRecord;
import com.sp.web.model.Product;
import com.sp.web.model.Promotion;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenStatus;
import com.sp.web.model.TokenType;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.model.assessment.AssessmentProgressTracker;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.PersonalityPracticeArea;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.UserArticleProgress;
import com.sp.web.model.goal.UserGoalProgress;
import com.sp.web.model.hiring.user.HiringUserArchive;
import com.sp.web.model.library.NewsCredTextArticle;
import com.sp.web.model.library.NewsCredVideoArticle;
import com.sp.web.utils.ApplicationContextUtils;
import com.sp.web.utils.DateTimeUtil;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author daxabraham This is the db setup class
 */
public class DBSetup {
  
  private static final Logger LOG = Logger.getLogger(DBSetup.class);
  
  private String[] productsArray = {
      "{\"id\":\"1\",\"name\":\"Business Annual\",\"unitPrice\":14.0,\"productType\":\"BUSINESS\",\"minEmployee\":20,\"minHiringEmployee\":10,\"titleKey\":\"Pay Annually <span class='offers'>and save 20%</span>\",\"validityKey\":\"12 month contract Expires on {0}\",\"unitPriceKey\":\"per Employee per Month\",\"status\":\"Active\",\"validityType\":\"YEARLY\"}",
      "{\"id\":\"2\",\"name\":\"Business Monthly\",\"unitPrice\":16.0,\"productType\":\"BUSINESS\",\"minEmployee\":20,\"titleKey\":\"Pay Monthly <span class='tc'>A month is a 30 day period</span>\",\"validityKey\":\"1 month contract Expires on {0}\",\"unitPriceKey\":\"per Employee per Month\",\"status\":\"Active\",\"validityType\":\"MONTHLY\"}",
      "{\"id\":\"3\",\"name\":\"Business Monthly INV\",\"unitPrice\":16.0,\"productType\":\"BUSINESS\",\"minEmployee\":20,\"titleKey\":\"Pay Monthly <span class='tc'>A month is a 30 day period</span>\",\"validityKey\":\"1 month contract Expires on {0}\",\"unitPriceKey\":\"per Employee per Month\",\"status\":\"InActive\",\"validityType\":\"MONTHLY\"}",
      "{\"id\":\"4\",\"name\":\"Hiring\",\"unitPrice\":499.0,\"productType\":\"HIRING\",\"minEmployee\":25,\"titleKey\":\"Hiring Product\",\"validityKey\":\"Hiring product\",\"unitPriceKey\":\"for 25 evaluations\",\"status\":\"Active\"}",
      "{\"id\":\"5\",\"name\":\"Individual Annual\",\"unitPrice\":8.25,\"productType\":\"INDIVIDUAL\",\"minEmployee\":1,\"titleKey\":\"Pay Annually <span class='offers'>and save 20%</span>\",\"validityKey\":\"12 month contract Expires on {0}\",\"unitPriceKey\":\"\",\"status\":\"Active\",\"validityType\":\"YEARLY\"}",
      "{\"id\":\"6\",\"name\":\"Individual Monthly\",\"unitPrice\":12.0,\"productType\":\"INDIVIDUAL\",\"minEmployee\":1,\"titleKey\":\"Pay Monthly <span class='tc'>A month is a 30 day period</span>\",\"validityKey\":\"1 month contract Expires on {0}\",\"unitPriceKey\":\"\",\"status\":\"Active\",\"validityType\":\"MONTHLY\"}",
      "{\"id\":\"7\",\"name\":\"Individual Monthly INV\",\"unitPrice\":12.0,\"productType\":\"INDIVIDUAL\",\"minEmployee\":1,\"titleKey\":\"Pay Monthly <span class='tc'>A month is a 30 day period</span>\",\"validityKey\":\"1 month contract Expires on {0}\",\"unitPriceKey\":\"\",\"status\":\"InActive\",\"validityType\":\"MONTHLY\"}", };
  
  private String[] promotionsArray = {
      "{\"id\":\"1\",\"code\":\"testCode\",\"name\":\"test\",\"promotionType\":\"TIME_BASED\",\"productIdList\":[\"1\",\"3\",\"4\",\"5\"],\"unitPrice\":5.5,\"status\":\"Active\",\"message\":\"<strong>Additional 5% discount</strong> per employee per month.\"}",
      "{\"id\":\"2\",\"code\":\"testCodeInvalid\",\"name\":\"testInvalid\",\"promotionType\":\"TIME_BASED\",\"productIdList\":[\"1\"],\"unitPrice\":5.5,\"status\":\"Active\",\"message\":\"<strong>Additional 5% discount</strong> per employee per month.\"}" };
  
  private String[] usersArray = {
      "{\"companyId\":\"1\",\"firstName\":\"Pradeep\",\"lastName\":\"Ruhil\",\"email\":\"admin@admin.com\",\"password\":\"admin\",\"roles\":[\"SuperAdministrator\",\"AccountAdministrator\",\"User\",\"Hiring\",\"BillingAdmin\",\"Blueprint\",\"Erti\"],\"userStatus\":\"VALID\",\"profileImage\":\"1\",\"analysis\" : {\"accuracy\" : \"74\", \"processing\" : { \"External\" : \"80\", \"Concrete\" : \"75\", \"Intuitive\" : \"25\", \"Cognitive\" : \"75\", \"Internal\" : \"20\", \"Affective\" : \"25\", \"Orderly\" : \"80\", \"Spontaneous\" : \"20\" }, \"conflictManagement\" : { \"Avoid\" : \"20\", \"Accommodate\" : \"10\", \"Compete\" : \"20\", \"Compromise\" : \"20\", \"Collaborate\" : \"30\" }, \"learningStyle\" : { \"Global\" : \"73\", \"Analytical\" : \"27\" }, \"motivationWhy\" : { \"Compliance\" : \"10\", \"AttainmentOfGoals\" : \"90\", \"RecognitionForEffort\" : \"10\", \"Power\" : \"90\", \"Affiliation\" : \"90\", \"Activity\" : \"10\" }, \"motivationHow\" : { \"TaskCompletion\" : \"25\", \"PrefersProcess\" : \"75\", \"ExchangeOfIdeas\" : \"58\", \"ReceiveDirection\" : \"42\", \"Freedom\" : \"42\", \"Consistency\" : \"58\", \"AffirmedByOthers\" : \"58\", \"SelfAffirmed\" : \"42\" }, \"motivationWhat\" : { \"Hygiene\" : \"50\", \"Accomplishment\" : \"50\" }, \"personality\" : { \"UnderPressure\" : { \"segmentScore\" : 7314, \"segmentGraph\" : [ \"1.00\", \"0.43\", \"0.14\", \"0.57\" ], \"personalityType\" : \"Visionary\" }, \"PerceivedByOthers\" : { \"segmentScore\" : 7314, \"segmentGraph\" : [ \"1.00\", \"0.43\", \"0.14\", \"0.57\" ],\"personalityType\" : \"Visionary\" }, \"Primary\" : { \"segmentScore\" : 6425, \"segmentGraph\" : [ \"0.86\", \"0.57\", \"0.29\", \"0.71\" ], \"personalityType\" : \"Navigator\" } }, \"fundamentalNeeds\" : { \"Security\" : \"23\", \"Control\" : \"35\", \"Significance\" : \"43\" }, \"decisionMaking\" : { \"Outward\" : \"67\", \"Rapid\" : \"33\", \"Inward\" : \"33\", \"Careful\" : \"67\" } }}",
      "{\"companyId\":\"1\",\"firstName\":\"Dax\",\"lastName\":\"Abraham\",\"email\":\"dax@surepeople.com\",\"password\":\"pwd123\",\"roles\":[\"User\",\"Spectrum\"],\"userStatus\":\"VALID\",\"profileImage\":\"{0}/profile/Dax_Abraham/54b99545d4c6dc504623a957/profile.jpg\",\"analysis\": {\"accuracy\": \"0\",\"processing\": {\"External\": \"60\",\"Internal\": \"40\",\"Cognitive\": \"40\",\"Spontaneous\": \"50\",\"Intuitive\": \"50\",\"Orderly\": \"50\",\"Concrete\": \"50\",\"Affective\": \"60\"},\"conflictManagement\": {\"Compromise\": \"17\",\"Accommodate\": \"20\",\"Compete\": \"20\",\"Collaborate\": \"20\",\"Avoid\": \"23\"},\"learningStyle\": {\"Analytical\": \"60\",\"Global\": \"40\"},\"motivationWhy\": {\"Affiliation\": \"70\",\"Compliance\": \"10\",\"AttainmentOfGoals\": \"70\",\"Power\": \"90\",\"RecognitionForEffort\": \"30\",\"Activity\": \"30\"},\"motivationHow\": {\"Freedom\": \"75\",\"AffirmedByOthers\": \"88\",\"ExchangeOfIdeas\": \"58\",\"TaskCompletion\": \"29\",\"Consistency\": \"25\",\"PrefersProcess\": \"71\",\"SelfAffirmed\": \"13\",\"ReceiveDirection\": \"42\"},\"motivationWhat\": {\"Accomplishment\": \"50\",\"Hygiene\": \"50\"},\"personality\": {\"UnderPressure\": {\"segmentScore\": 5334,\"segmentGraph\": [\"0.71\",\"0.43\",\"0.43\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Visionary\"},\"Primary\": {\"segmentScore\": 4644,\"segmentGraph\": [\"0.57\",\"0.86\",\"0.57\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Promoter\"},\"PerceivedByOthers\": {\"segmentScore\": 5543,\"segmentGraph\": [\"0.71\",\"0.71\",\"0.57\",\"0.43\"],\"isValid\": true,\"personalityType\": \"Instructor\"}},\"fundamentalNeeds\": {\"Security\": \"33\",\"Control\": \"33\",\"Significance\": \"35\"},\"decisionMaking\": {\"Rapid\": \"58\",\"Careful\": \"42\",\"Inward\": \"33\",\"Outward\": \"67\"}}}",
      "{\"id\":\"1\",\"companyId\":\"1\",\"blueprintId\":\"1000\",\"firstName\": \"Pradeep\",\"lastName\": \"Ruhil\",\"title\": \"\",\"email\": \"pradeep1@surepeople.com\",\"password\": \"password\",\"profileImage\":\"1\",\"address\": {\"country\": \"USA\",\"addressLine1\": \"12313\",\"city\": \"123123\",\"state\": \"AR\",\"zipCode\": \"12312\"},\"phoneNumber\": \"1231231\",\"userStatus\": \"VALID\",\"roles\": [\"User\",\"AccountAdministrator\",\"Spectrum\",\"Prism\",\"Blueprint\",\"Erti\",\"Hiring\"],\"gender\": \"M\",\"analysis\": {\"accuracy\": \"0\",\"processing\": {\"External\": \"60\",\"Internal\": \"40\",\"Cognitive\": \"40\",\"Spontaneous\": \"50\",\"Intuitive\": \"50\",\"Orderly\": \"50\",\"Concrete\": \"50\",\"Affective\": \"60\"},\"conflictManagement\": {\"Compromise\": \"17\",\"Accommodate\": \"20\",\"Compete\": \"20\",\"Collaborate\": \"20\",\"Avoid\": \"23\"},\"learningStyle\": {\"Analytical\": \"60\",\"Global\": \"40\"},\"motivationWhy\": {\"Affiliation\": \"70\",\"Compliance\": \"10\",\"AttainmentOfGoals\": \"70\",\"Power\": \"90\",\"RecognitionForEffort\": \"30\",\"Activity\": \"30\"},\"motivationHow\": {\"Freedom\": \"75\",\"AffirmedByOthers\": \"88\",\"ExchangeOfIdeas\": \"58\",\"TaskCompletion\": \"29\",\"Consistency\": \"25\",\"PrefersProcess\": \"71\",\"SelfAffirmed\": \"13\",\"ReceiveDirection\": \"42\"},\"motivationWhat\": {\"Accomplishment\": \"50\",\"Hygiene\": \"50\"},\"personality\": {\"UnderPressure\": {\"segmentScore\": 5334,\"segmentGraph\": [\"0.71\",\"0.43\",\"0.43\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Visionary\"},\"Primary\": {\"segmentScore\": 4644,\"segmentGraph\": [\"0.57\",\"0.86\",\"0.57\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Promoter\"},\"PerceivedByOthers\": {\"segmentScore\": 5543,\"segmentGraph\": [\"0.71\",\"0.71\",\"0.57\",\"0.43\"],\"isValid\": true,\"personalityType\": \"Instructor\"}},\"fundamentalNeeds\": {\"Security\": \"33\",\"Control\": \"33\",\"Significance\": \"35\"},\"decisionMaking\": {\"Rapid\": \"58\",\"Careful\": \"42\",\"Inward\": \"33\",\"Outward\": \"67\"}}}",
      "{\"id\":\"6\",\"companyId\":\"1\",\"firstName\": \"Pradeep\",\"lastName\": \"Ruhil\",\"title\": \"\",\"email\": \"pradeep3@surepeople.com\",\"password\": \"password\",\"profileImage\":\"1\",\"address\": {\"country\": \"USA\",\"addressLine1\": \"12313\",\"city\": \"123123\",\"state\": \"AR\",\"zipCode\": \"12312\"},\"phoneNumber\": \"1231231\",\"userStatus\": \"VALID\",\"roles\": [\"User\",\"AccountAdministrator\"],\"gender\": \"M\",\"analysis\": {\"accuracy\": \"0\",\"processing\": {\"External\": \"30\",\"Internal\": \"70\",\"Cognitive\": \"40\",\"Spontaneous\": \"50\",\"Intuitive\": \"50\",\"Orderly\": \"50\",\"Concrete\": \"50\",\"Affective\": \"60\"},\"conflictManagement\": {\"Compromise\": \"17\",\"Accommodate\": \"20\",\"Compete\": \"20\",\"Collaborate\": \"20\",\"Avoid\": \"23\"},\"learningStyle\": {\"Analytical\": \"60\",\"Global\": \"40\"},\"motivationWhy\": {\"Affiliation\": \"70\",\"Compliance\": \"10\",\"AttainmentOfGoals\": \"70\",\"Power\": \"90\",\"RecognitionForEffort\": \"30\",\"Activity\": \"30\"},\"motivationHow\": {\"Freedom\": \"75\",\"AffirmedByOthers\": \"88\",\"ExchangeOfIdeas\": \"58\",\"TaskCompletion\": \"29\",\"Consistency\": \"25\",\"PrefersProcess\": \"71\",\"SelfAffirmed\": \"13\",\"ReceiveDirection\": \"42\"},\"motivationWhat\": {\"Accomplishment\": \"50\",\"Hygiene\": \"50\"},\"personality\": {\"UnderPressure\": {\"segmentScore\": 5334,\"segmentGraph\": [\"0.71\",\"0.43\",\"0.43\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Visionary\"},\"Primary\": {\"segmentScore\": 4644,\"segmentGraph\": [\"0.57\",\"0.86\",\"0.57\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Innovator\"},\"PerceivedByOthers\": {\"segmentScore\": 5543,\"segmentGraph\": [\"0.71\",\"0.71\",\"0.57\",\"0.43\"],\"isValid\": true,\"personalityType\": \"Instructor\"}},\"fundamentalNeeds\": {\"Security\": \"33\",\"Control\": \"33\",\"Significance\": \"35\"},\"decisionMaking\": {\"Rapid\": \"58\",\"Careful\": \"42\",\"Inward\": \"33\",\"Outward\": \"67\"}}}",
      "{\"id\":\"2\",\"companyId\":\"2\",\"firstName\": \"Pradeep\",\"lastName\": \"Ruhil\",\"title\": \"\",\"email\": \"pradeep2@surepeople.com\",\"password\": \"password\",\"address\": {\"country\": \"USA\",\"addressLine1\": \"12313\",\"city\": \"123123\",\"state\": \"AR\",\"zipCode\": \"12312\"},\"phoneNumber\": \"1231231\",\"userStatus\": \"VALID\",\"roles\": [\"User\",\"AccountAdministrator\"],\"gender\": \"M\",\"analysis\": {\"accuracy\": \"0\",\"processing\": {\"External\": \"50\",\"Internal\": \"50\",\"Cognitive\": \"40\",\"Spontaneous\": \"50\",\"Intuitive\": \"50\",\"Orderly\": \"50\",\"Concrete\": \"50\",\"Affective\": \"60\"},\"conflictManagement\": {\"Compromise\": \"17\",\"Accommodate\": \"20\",\"Compete\": \"20\",\"Collaborate\": \"20\",\"Avoid\": \"23\"},\"learningStyle\": {\"Analytical\": \"60\",\"Global\": \"40\"},\"motivationWhy\": {\"Affiliation\": \"70\",\"Compliance\": \"10\",\"AttainmentOfGoals\": \"70\",\"Power\": \"90\",\"RecognitionForEffort\": \"30\",\"Activity\": \"30\"},\"motivationHow\": {\"Freedom\": \"75\",\"AffirmedByOthers\": \"88\",\"ExchangeOfIdeas\": \"58\",\"TaskCompletion\": \"29\",\"Consistency\": \"25\",\"PrefersProcess\": \"71\",\"SelfAffirmed\": \"13\",\"ReceiveDirection\": \"42\"},\"motivationWhat\": {\"Accomplishment\": \"50\",\"Hygiene\": \"50\"},\"personality\": {\"UnderPressure\": {\"segmentScore\": 5334,\"segmentGraph\": [\"0.71\",\"0.43\",\"0.43\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Visionary\"},\"Primary\": {\"segmentScore\": 4644,\"segmentGraph\": [\"0.57\",\"0.86\",\"0.57\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Promoter\"},\"PerceivedByOthers\": {\"segmentScore\": 5543,\"segmentGraph\": [\"0.71\",\"0.71\",\"0.57\",\"0.43\"],\"isValid\": true,\"personalityType\": \"Instructor\"}},\"fundamentalNeeds\": {\"Security\": \"33\",\"Control\": \"33\",\"Significance\": \"35\"},\"decisionMaking\": {\"Rapid\": \"58\",\"Careful\": \"42\",\"Inward\": \"33\",\"Outward\": \"67\"}}}",
      "{\"companyId\":\"1\",\"firstName\":\"Pradeep\",\"email\":\"hiring@surepeople.com\",\"password\":\"\",\"roles\":[\"Hiring\",\"User\"],\"profileImage\":\"\",\"userStatus\": \"ASSESSMENT_PENDING\"}",
      "{\"id\":\"3\",\"firstName\": \"Indivial\",\"lastName\": \"User\",\"title\": \"\",\"email\": \"individual@surepeople.com\",\"accountId\": \"3\",\"password\": \"password\",\"address\": {\"country\": \"USA\",\"addressLine1\": \"12313\",\"city\": \"123123\",\"state\": \"AR\",\"zipCode\": \"12312\"},\"phoneNumber\": \"1231231\",\"userStatus\": \"VALID\",\"roles\": [\"User\",\"IndividualAccountAdministrator\"],\"gender\": \"M\",\"analysis\": {\"accuracy\": \"0\",\"processing\": {\"External\": \"60\",\"Internal\": \"40\",\"Cognitive\": \"40\",\"Spontaneous\": \"50\",\"Intuitive\": \"50\",\"Orderly\": \"50\",\"Concrete\": \"50\",\"Affective\": \"60\"},\"conflictManagement\": {\"Compromise\": \"17\",\"Accommodate\": \"20\",\"Compete\": \"20\",\"Collaborate\": \"20\",\"Avoid\": \"23\"},\"learningStyle\": {\"Analytical\": \"60\",\"Global\": \"40\"},\"motivationWhy\": {\"Affiliation\": \"70\",\"Compliance\": \"10\",\"AttainmentOfGoals\": \"70\",\"Power\": \"90\",\"RecognitionForEffort\": \"30\",\"Activity\": \"30\"},\"motivationHow\": {\"Freedom\": \"75\",\"AffirmedByOthers\": \"88\",\"ExchangeOfIdeas\": \"58\",\"TaskCompletion\": \"29\",\"Consistency\": \"25\",\"PrefersProcess\": \"71\",\"SelfAffirmed\": \"13\",\"ReceiveDirection\": \"42\"},\"motivationWhat\": {\"Accomplishment\": \"50\",\"Hygiene\": \"50\"},\"personality\": {\"UnderPressure\": {\"segmentScore\": 5334,\"segmentGraph\": [\"0.71\",\"0.43\",\"0.43\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Visionary\"},\"Primary\": {\"segmentScore\": 4644,\"segmentGraph\": [\"0.57\",\"0.86\",\"0.57\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Promoter\"},\"PerceivedByOthers\": {\"segmentScore\": 5543,\"segmentGraph\": [\"0.71\",\"0.71\",\"0.57\",\"0.43\"],\"isValid\": true,\"personalityType\": \"Instructor\"}},\"fundamentalNeeds\": {\"Security\": \"33\",\"Control\": \"33\",\"Significance\": \"35\"},\"decisionMaking\": {\"Rapid\": \"58\",\"Careful\": \"42\",\"Inward\": \"33\",\"Outward\": \"67\"}}}",
      "{\"id\":\"4\",\"companyId\":\"1\",\"firstName\":\"Sam\",\"lastName\":\"Singh\",\"email\":\"sam@surepeople.com\",\"password\":\"pwd123\",\"roles\":[\"User\"],\"userStatus\":\"ASSESSMENT_PENDING\"}",
      "{\"id\":\"7\",\"companyId\":\"7\",\"firstName\": \"Pradeep\",\"lastName\": \"Ruhil\",\"title\": \"\",\"email\": \"pradeep4@surepeople.com\",\"password\": \"password\",\"profileImage\":\"1\",\"address\": {\"country\": \"USA\",\"addressLine1\": \"12313\",\"city\": \"123123\",\"state\": \"AR\",\"zipCode\": \"12312\"},\"phoneNumber\": \"1231231\",\"userStatus\": \"VALID\",\"roles\": [\"User\",\"AccountAdministrator\",\"Hiring\"],\"gender\": \"M\",\"analysis\": {\"accuracy\": \"0\",\"processing\": {\"External\": \"60\",\"Internal\": \"40\",\"Cognitive\": \"40\",\"Spontaneous\": \"50\",\"Intuitive\": \"50\",\"Orderly\": \"50\",\"Concrete\": \"50\",\"Affective\": \"60\"},\"conflictManagement\": {\"Compromise\": \"17\",\"Accommodate\": \"20\",\"Compete\": \"20\",\"Collaborate\": \"20\",\"Avoid\": \"23\"},\"learningStyle\": {\"Analytical\": \"60\",\"Global\": \"40\"},\"motivationWhy\": {\"Affiliation\": \"70\",\"Compliance\": \"10\",\"AttainmentOfGoals\": \"70\",\"Power\": \"90\",\"RecognitionForEffort\": \"30\",\"Activity\": \"30\"},\"motivationHow\": {\"Freedom\": \"75\",\"AffirmedByOthers\": \"88\",\"ExchangeOfIdeas\": \"58\",\"TaskCompletion\": \"29\",\"Consistency\": \"25\",\"PrefersProcess\": \"71\",\"SelfAffirmed\": \"13\",\"ReceiveDirection\": \"42\"},\"motivationWhat\": {\"Accomplishment\": \"50\",\"Hygiene\": \"50\"},\"personality\": {\"UnderPressure\": {\"segmentScore\": 5334,\"segmentGraph\": [\"0.71\",\"0.43\",\"0.43\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Visionary\"},\"Primary\": {\"segmentScore\": 4644,\"segmentGraph\": [\"0.57\",\"0.86\",\"0.57\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Promoter\"},\"PerceivedByOthers\": {\"segmentScore\": 5543,\"segmentGraph\": [\"0.71\",\"0.71\",\"0.57\",\"0.43\"],\"isValid\": true,\"personalityType\": \"Instructor\"}},\"fundamentalNeeds\": {\"Security\": \"33\",\"Control\": \"33\",\"Significance\": \"35\"},\"decisionMaking\": {\"Rapid\": \"58\",\"Careful\": \"42\",\"Inward\": \"33\",\"Outward\": \"67\"}}}",
      "{\"id\":\"8\",\"companyId\":\"7\",\"firstName\": \"Pradeep\",\"lastName\": \"Ruhil\",\"title\": \"\",\"email\": \"shivankspnew@yopmail.com\",\"password\": \"password\",\"profileImage\":\"1\",\"address\": {\"country\": \"USA\",\"addressLine1\": \"12313\",\"city\": \"123123\",\"state\": \"AR\",\"zipCode\": \"12312\"},\"phoneNumber\": \"1231231\",\"userStatus\": \"VALID\",\"roles\": [\"User\",\"AccountAdministrator\",\"Hiring\"],\"gender\": \"M\",\"analysis\": {\"accuracy\": \"0\",\"processing\": {\"External\": \"60\",\"Internal\": \"40\",\"Cognitive\": \"40\",\"Spontaneous\": \"50\",\"Intuitive\": \"50\",\"Orderly\": \"50\",\"Concrete\": \"50\",\"Affective\": \"60\"},\"conflictManagement\": {\"Compromise\": \"17\",\"Accommodate\": \"20\",\"Compete\": \"20\",\"Collaborate\": \"20\",\"Avoid\": \"23\"},\"learningStyle\": {\"Analytical\": \"60\",\"Global\": \"40\"},\"motivationWhy\": {\"Affiliation\": \"70\",\"Compliance\": \"10\",\"AttainmentOfGoals\": \"70\",\"Power\": \"90\",\"RecognitionForEffort\": \"30\",\"Activity\": \"30\"},\"motivationHow\": {\"Freedom\": \"75\",\"AffirmedByOthers\": \"88\",\"ExchangeOfIdeas\": \"58\",\"TaskCompletion\": \"29\",\"Consistency\": \"25\",\"PrefersProcess\": \"71\",\"SelfAffirmed\": \"13\",\"ReceiveDirection\": \"42\"},\"motivationWhat\": {\"Accomplishment\": \"50\",\"Hygiene\": \"50\"},\"personality\": {\"UnderPressure\": {\"segmentScore\": 5334,\"segmentGraph\": [\"0.71\",\"0.43\",\"0.43\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Visionary\"},\"Primary\": {\"segmentScore\": 4644,\"segmentGraph\": [\"0.57\",\"0.86\",\"0.57\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Promoter\"},\"PerceivedByOthers\": {\"segmentScore\": 5543,\"segmentGraph\": [\"0.71\",\"0.71\",\"0.57\",\"0.43\"],\"isValid\": true,\"personalityType\": \"Instructor\"}},\"fundamentalNeeds\": {\"Security\": \"33\",\"Control\": \"33\",\"Significance\": \"35\"},\"decisionMaking\": {\"Rapid\": \"58\",\"Careful\": \"42\",\"Inward\": \"33\",\"Outward\": \"67\"}}}",
      "{ \"id\" : \"9\", \"firstName\" : \"SPBilling\", \"lastName\" : \"Admin\", \"title\" : \"SPBillingAdmin\", \"email\" : \"spbillingadmin@surepeople.com\", \"password\" : \"password\", \"address\" : { \"country\" : \"United States\", \"addressLine1\" : \"32\", \"addressLine2\" : \"\", \"city\" : \"chicago\", \"state\" : \"IL\", \"zipCode\" : \"32323\" }, \"phoneNumber\" : \"3424242424\", \"userStatus\" : \"VALID\", \"companyId\" : \"1\", \"analysis\" : { \"_class\" : \"com.sp.web.assessment.processing.AnalysisBeanDTO\", \"accuracy\" : \"100\", \"processing\" : { \"Affective\" : \"100\", \"Cognitive\" : \"0\", \"Intuitive\" : \"100\", \"Orderly\" : \"0\", \"External\" : \"0\", \"Concrete\" : \"0\", \"Internal\" : \"100\", \"Spontaneous\" : \"100\" }, \"conflictManagement\" : { \"Accommodate\" : \"23\", \"Compromise\" : \"17\", \"Compete\" : \"17\", \"Avoid\" : \"23\", \"Collaborate\" : \"20\" }, \"learningStyle\" : { \"Analytical\" : \"0\", \"Global\" : \"100\" }, \"motivationWhy\" : { \"Power\" : \"40\", \"Affiliation\" : \"50\", \"Compliance\" : \"60\", \"RecognitionForEffort\" : \"60\", \"Activity\" : \"50\", \"AttainmentOfGoals\" : \"40\" }, \"motivationHow\" : { \"SelfAffirmed\" : \"0\", \"PrefersProcess\" : \"100\", \"Freedom\" : \"67\", \"AffirmedByOthers\" : \"100\", \"TaskCompletion\" : \"0\", \"Consistency\" : \"33\", \"ExchangeOfIdeas\" : \"67\", \"ReceiveDirection\" : \"33\" }, \"motivationWhat\" : { \"Accomplishment\" : \"83\", \"Hygiene\" : \"17\" }, \"personality\" : { \"UnderPressure\" : { \"segmentScore\" : 6323, \"segmentGraph\" : [ \"0.86\", \"0.43\", \"0.29\", \"0.43\" ], \"isValid\" : true, \"personalityType\" : \"Visionary\" }, \"PerceivedByOthers\" : { \"segmentScore\" : 6424, \"segmentGraph\" : [ \"0.86\", \"0.57\", \"0.29\", \"0.57\" ], \"isValid\" : true, \"personalityType\" : \"Visionary\" }, \"Primary\" : { \"segmentScore\" : 5435, \"segmentGraph\" : [ \"0.71\", \"0.57\", \"0.43\", \"0.71\" ], \"isValid\" : true, \"personalityType\" : \"Navigator\" } }, \"fundamentalNeeds\" : { \"Control\" : \"30\", \"Significance\" : \"38\", \"Security\" : \"32\" }, \"decisionMaking\" : { \"Careful\" : \"67\", \"Inward\" : \"92\", \"Outward\" : \"8\", \"Rapid\" : \"33\" } }, \"roles\" : [ \"User\",\"BillingAdmin\" ], \"gender\" : \"F\", \"tagList\" : [ \"Asdf\" ], \"profileSettings\" : { \"isHiringAccessAllowed\" : false, \"is360ProfileAccessAllowed\" : false, \"isWorkspacePulseAllowed\" : false, \"token\" : \"6fe20bf5-26c2-48ed-ae22-0c43de6e67fe\", \"certificateProfilePublicView\" : false, \"autoUpdateLearning\" : true }, \"userGoalId\" : \"5603a389d4c607583a1c91c2\", \"tokenUrl\" : \"http://ec2-52-10-90-53.us-west-2.compute.amazonaws.com/sp/processToken/6cec3b5d-d38d-4fe4-8e59-60e8aa1dd744\", \"imageCount\" : 0, \"certificateNumber\" : \"60\" }", };
  
  private String[] feedbackUserArray = {
      "{\"id\":\"1\",\"feedbackFor\":\"1\",\"companyId\":\"1\",\"firstName\": \"Pradeep\",\"lastName\": \"Ruhil\",\"title\": \"\",\"email\": \"pradeep2@surepeople.com\",\"password\": \"password\",\"address\": {\"country\": \"USA\",\"addressLine1\": \"12313\",\"city\": \"123123\",\"state\": \"AR\",\"zipCode\": \"12312\"},\"phoneNumber\": \"1231231\",\"userStatus\": \"VALID\",\"roles\": [\"User\",\"AccountAdministrator\"],\"gender\": \"M\",\"analysis\": {\"accuracy\": \"0\",\"processing\": {\"External\": \"60\",\"Internal\": \"40\",\"Cognitive\": \"40\",\"Spontaneous\": \"50\",\"Intuitive\": \"50\",\"Orderly\": \"50\",\"Concrete\": \"50\",\"Affective\": \"60\"},\"conflictManagement\": {\"Compromise\": \"17\",\"Accommodate\": \"20\",\"Compete\": \"20\",\"Collaborate\": \"20\",\"Avoid\": \"23\"},\"learningStyle\": {\"Analytical\": \"60\",\"Global\": \"40\"},\"motivationWhy\": {\"Affiliation\": \"70\",\"Compliance\": \"10\",\"AttainmentOfGoals\": \"70\",\"Power\": \"90\",\"RecognitionForEffort\": \"30\",\"Activity\": \"30\"},\"motivationHow\": {\"Freedom\": \"75\",\"AffirmedByOthers\": \"88\",\"ExchangeOfIdeas\": \"58\",\"TaskCompletion\": \"29\",\"Consistency\": \"25\",\"PrefersProcess\": \"71\",\"SelfAffirmed\": \"13\",\"ReceiveDirection\": \"42\"},\"motivationWhat\": {\"Accomplishment\": \"50\",\"Hygiene\": \"50\"},\"personality\": {\"UnderPressure\": {\"segmentScore\": 5334,\"segmentGraph\": [\"0.71\",\"0.43\",\"0.43\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Examiner\"},\"Primary\": {\"segmentScore\": 4644,\"segmentGraph\": [\"0.57\",\"0.86\",\"0.57\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Encourager\"},\"PerceivedByOthers\": {\"segmentScore\": 5543,\"segmentGraph\": [\"0.71\",\"0.71\",\"0.57\",\"0.43\"],\"isValid\": true,\"personalityType\": \"Instructor\"}},\"fundamentalNeeds\": {\"Security\": \"33\",\"Control\": \"33\",\"Significance\": \"35\"},\"decisionMaking\": {\"Rapid\": \"58\",\"Careful\": \"42\",\"Inward\": \"33\",\"Outward\": \"67\"}}}",
      "{\"id\":\"5\",\"feedbackFor\":\"1\",\"companyId\":\"1\",\"firstName\": \"Pradeep\",\"lastName\": \"Ruhil\",\"title\": \"\",\"email\": \"admin@admin.com\",\"password\": \"password\",\"address\": {\"country\": \"USA\",\"addressLine1\": \"12313\",\"city\": \"123123\",\"state\": \"AR\",\"zipCode\": \"12312\"},\"phoneNumber\": \"1231231\",\"userStatus\": \"VALID\",\"roles\": [\"User\",\"AccountAdministrator\"],\"gender\": \"M\",\"analysis\": {\"accuracy\": \"0\",\"processing\": {\"External\": \"60\",\"Internal\": \"40\",\"Cognitive\": \"40\",\"Spontaneous\": \"50\",\"Intuitive\": \"50\",\"Orderly\": \"50\",\"Concrete\": \"50\",\"Affective\": \"60\"},\"conflictManagement\": {\"Compromise\": \"17\",\"Accommodate\": \"20\",\"Compete\": \"20\",\"Collaborate\": \"20\",\"Avoid\": \"23\"},\"learningStyle\": {\"Analytical\": \"60\",\"Global\": \"40\"},\"motivationWhy\": {\"Affiliation\": \"70\",\"Compliance\": \"10\",\"AttainmentOfGoals\": \"70\",\"Power\": \"90\",\"RecognitionForEffort\": \"30\",\"Activity\": \"30\"},\"motivationHow\": {\"Freedom\": \"75\",\"AffirmedByOthers\": \"88\",\"ExchangeOfIdeas\": \"58\",\"TaskCompletion\": \"29\",\"Consistency\": \"25\",\"PrefersProcess\": \"71\",\"SelfAffirmed\": \"13\",\"ReceiveDirection\": \"42\"},\"motivationWhat\": {\"Accomplishment\": \"50\",\"Hygiene\": \"50\"},\"personality\": {\"UnderPressure\": {\"segmentScore\": 5334,\"segmentGraph\": [\"0.71\",\"0.43\",\"0.43\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Visionary\"},\"Primary\": {\"segmentScore\": 4644,\"segmentGraph\": [\"0.57\",\"0.86\",\"0.57\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Promoter\"},\"PerceivedByOthers\": {\"segmentScore\": 5543,\"segmentGraph\": [\"0.71\",\"0.71\",\"0.57\",\"0.43\"],\"isValid\": true,\"personalityType\": \"Instructor\"}},\"fundamentalNeeds\": {\"Security\": \"33\",\"Control\": \"33\",\"Significance\": \"35\"},\"decisionMaking\": {\"Rapid\": \"58\",\"Careful\": \"42\",\"Inward\": \"33\",\"Outward\": \"67\"}}}",
      "{\"id\":\"2\",\"feedbackFor\":\"1\",\"companyId\":\"1\",\"firstName\": \"Indivial\",\"lastName\": \"User\",\"title\": \"\",\"email\": \"dax@surepeople.com\",\"password\": \"password\",\"address\": {\"country\": \"USA\",\"addressLine1\": \"12313\",\"city\": \"123123\",\"state\": \"AR\",\"zipCode\": \"12312\"},\"phoneNumber\": \"1231231\",\"userStatus\": \"VALID\",\"roles\": [\"User\",\"IndividualAccountAdministrator\"],\"gender\": \"M\",\"analysis\": {\"accuracy\": \"0\",\"processing\": {\"External\": \"60\",\"Internal\": \"40\",\"Cognitive\": \"40\",\"Spontaneous\": \"50\",\"Intuitive\": \"50\",\"Orderly\": \"50\",\"Concrete\": \"50\",\"Affective\": \"60\"},\"conflictManagement\": {\"Compromise\": \"17\",\"Accommodate\": \"20\",\"Compete\": \"20\",\"Collaborate\": \"20\",\"Avoid\": \"23\"},\"learningStyle\": {\"Analytical\": \"60\",\"Global\": \"40\"},\"motivationWhy\": {\"Affiliation\": \"70\",\"Compliance\": \"10\",\"AttainmentOfGoals\": \"70\",\"Power\": \"90\",\"RecognitionForEffort\": \"30\",\"Activity\": \"30\"},\"motivationHow\": {\"Freedom\": \"75\",\"AffirmedByOthers\": \"88\",\"ExchangeOfIdeas\": \"58\",\"TaskCompletion\": \"29\",\"Consistency\": \"25\",\"PrefersProcess\": \"71\",\"SelfAffirmed\": \"13\",\"ReceiveDirection\": \"42\"},\"motivationWhat\": {\"Accomplishment\": \"50\",\"Hygiene\": \"50\"},\"personality\": {\"UnderPressure\": {\"segmentScore\": 5334,\"segmentGraph\": [\"0.71\",\"0.43\",\"0.43\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Visionary\"},\"Primary\": {\"segmentScore\": 4644,\"segmentGraph\": [\"0.57\",\"0.86\",\"0.57\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Promoter\"},\"PerceivedByOthers\": {\"segmentScore\": 5543,\"segmentGraph\": [\"0.71\",\"0.71\",\"0.57\",\"0.43\"],\"isValid\": true,\"personalityType\": \"Instructor\"}},\"fundamentalNeeds\": {\"Security\": \"33\",\"Control\": \"33\",\"Significance\": \"35\"},\"decisionMaking\": {\"Rapid\": \"58\",\"Careful\": \"42\",\"Inward\": \"33\",\"Outward\": \"67\"}}}",
      "{\"id\":\"3\",\"feedbackFor\":\"1\",\"firstName\": \"Indivial\",\"lastName\": \"User\",\"title\": \"\",\"email\": \"individualExt@surepeople.com\",\"password\": \"password\",\"address\": {\"country\": \"USA\",\"addressLine1\": \"12313\",\"city\": \"123123\",\"state\": \"AR\",\"zipCode\": \"12312\"},\"phoneNumber\": \"1231231\",\"userStatus\": \"VALID\",\"roles\": [\"User\",\"IndividualAccountAdministrator\"],\"gender\": \"M\",\"analysis\": {\"accuracy\": \"0\",\"processing\": {\"External\": \"60\",\"Internal\": \"40\",\"Cognitive\": \"40\",\"Spontaneous\": \"50\",\"Intuitive\": \"50\",\"Orderly\": \"50\",\"Concrete\": \"50\",\"Affective\": \"60\"},\"conflictManagement\": {\"Compromise\": \"17\",\"Accommodate\": \"20\",\"Compete\": \"20\",\"Collaborate\": \"20\",\"Avoid\": \"23\"},\"learningStyle\": {\"Analytical\": \"60\",\"Global\": \"40\"},\"motivationWhy\": {\"Affiliation\": \"70\",\"Compliance\": \"10\",\"AttainmentOfGoals\": \"70\",\"Power\": \"90\",\"RecognitionForEffort\": \"30\",\"Activity\": \"30\"},\"motivationHow\": {\"Freedom\": \"75\",\"AffirmedByOthers\": \"88\",\"ExchangeOfIdeas\": \"58\",\"TaskCompletion\": \"29\",\"Consistency\": \"25\",\"PrefersProcess\": \"71\",\"SelfAffirmed\": \"13\",\"ReceiveDirection\": \"42\"},\"motivationWhat\": {\"Accomplishment\": \"50\",\"Hygiene\": \"50\"},\"personality\": {\"UnderPressure\": {\"segmentScore\": 5334,\"segmentGraph\": [\"0.71\",\"0.43\",\"0.43\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Visionary\"},\"Primary\": {\"segmentScore\": 4644,\"segmentGraph\": [\"0.57\",\"0.86\",\"0.57\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Promoter\"},\"PerceivedByOthers\": {\"segmentScore\": 5543,\"segmentGraph\": [\"0.71\",\"0.71\",\"0.57\",\"0.43\"],\"isValid\": true,\"personalityType\": \"Instructor\"}},\"fundamentalNeeds\": {\"Security\": \"33\",\"Control\": \"33\",\"Significance\": \"35\"},\"decisionMaking\": {\"Rapid\": \"58\",\"Careful\": \"42\",\"Inward\": \"33\",\"Outward\": \"67\"}}}",
      "{\"id\":\"4\",\"feedbackFor\":\"1\",\"companyId\":\"1\",\"firstName\":\"Sam\",\"lastName\":\"Singh\",\"email\":\"sam@surepeople.com\",\"password\":\"pwd123\",\"roles\":[\"User\"],\"userStatus\":\"ASSESSMENT_PENDING\"}" };
  
  private String[] groupArray = {
      "{\"id\":\"1\",\"name\":\"Executive\",\"companyId\":\"1\",\"groupLead\":\"admin@admin.com\",\"memberList\":null}",
      "{\"id\":\"2\",\"name\":\"AnotherGroup\",\"companyId\":\"1\",\"groupLead\":null,\"memberList\":[\"dax@surepeople.com\",\"pradeep1@surepeople.com\"]}",
      "{\"id\":\"3\",\"name\":\"Developer\",\"companyId\":\"1\",\"groupLead\":null,\"memberList\":null}" };
  
  private String[] companyArray = {
      "{\"id\":\"1\",\"featureList\" : [ \"Prism\",\"Blueprint\",\"RelationShipAdvisor\", \"OrganizationPlan\"  ], \"name\":\"SurePeople\", \"address\" : { \"country\" : \"Canada\", \"addressLine1\" : \"32323\", \"addressLine2\" : \"\", \"city\" : \"chicago\", \"state\" : \"il\", \"zipCode\" : \"323232\" },\"industry\":null,\"numberOfEmployees\":100,\"phoneNumber\":null,\"accountId\":\"1\",\"companyTheme\":{\"stylesMap\":{\"sp-icon-color\":\"#ff4000\",\"sp-header-accentbar-color\":\"#f63\",\"sp-signin-accentbar-color\":\"#f63\",\"sp-link-color\":\"#ff4000\",\"sp-header-title-color\":\"#f63\",\"sp-footer-background-color\":\"#f63\",\"sp-button-color\":\"ff4000\",\"sp-link-hover-color\":\"#000\",\"sp-main-nav-color\":\"#ffa200\",\"sp-panel-accentbar-color\":\"#ee5a1f\",\"sub-nav-link-color\":\"#000\",\"sp-sub-nav-background-color\":\"#ffa200\",\"sp-footer-navigation-text-color\":\"#ff\",\"sp-footer-navigation-hover-color\":\"#000\"},\"cssUrl\":\"{0}/theme/surepeople/defaultTheme.css\"},\"companyThemeActive\":false}",
      "{\"id\":\"2\",\"name\":\"Apple\", \"address\" : { \"country\" : \"Canada\", \"addressLine1\" : \"32323\", \"addressLine2\" : \"\", \"city\" : \"chicago\", \"state\" : \"il\", \"zipCode\" : \"323232\" },\"industry\":null,\"numberOfEmployees\":100,\"phoneNumber\":null,\"accountId\":\"2\"}",
      "{\"id\":\"3\",\"name\":\"SurePeople\", \"address\" : { \"country\" : \"Canada\", \"addressLine1\" : \"32323\", \"addressLine2\" : \"\", \"city\" : \"chicago\", \"state\" : \"il\", \"zipCode\" : \"323232\" },\"industry\":null,\"numberOfEmployees\":100,\"phoneNumber\":null,\"accountId\":\"3\"}",
      "{\"id\":\"4\",\"name\":\"Apple\", \"address\" : { \"country\" : \"Canada\", \"addressLine1\" : \"32323\", \"addressLine2\" : \"\", \"city\" : \"chicago\", \"state\" : \"il\", \"zipCode\" : \"323232\" },\"industry\":null,\"numberOfEmployees\":100,\"phoneNumber\":null,\"accountId\":\"4\"}",
      "{\"id\":\"5\",\"name\":\"SurePeople\", \"address\" : { \"country\" : \"Canada\", \"addressLine1\" : \"32323\", \"addressLine2\" : \"\", \"city\" : \"chicago\", \"state\" : \"il\", \"zipCode\" : \"323232\" },\"industry\":null,\"numberOfEmployees\":100,\"phoneNumber\":null,\"accountId\":\"5\"}",
      "{\"id\":\"6\",\"name\":\"Apple\", \"address\" : { \"country\" : \"Canada\", \"addressLine1\" : \"32323\", \"addressLine2\" : \"\", \"city\" : \"chicago\", \"state\" : \"il\", \"zipCode\" : \"323232\" },\"industry\":null,\"numberOfEmployees\":100,\"phoneNumber\":null,\"accountId\":\"6\"}",
      "{\"id\" :\"default\", \"name\" : \"SPAppDefault\", \"address\" : { \"country\" : \"USA\", \"addressLine1\" : \"12\", \"addressLine2\" : \"\", \"city\" : \"Chicago\", \"state\" : \"AZ\", \"zipCode\" : \"12121\" }, \"industry\" : \"1\", \"numberOfEmployees\" : 0, \"phoneNumber\" : \"90003000032\", \"accountId\" : \"54c3360cd4c6a5978c84d05c\", \"companyTheme\" :{\"stylesMap\":{\"spMainNavBackgroundColor\" : \"#000\", \"spIconColor\" : \"#f63\", \"spHeaderAccentbarColor\" : \"#f63\", \"spSigninAccentbarColor\" : \"#f63\", \"spLinkColor\" : \"#f63\", \"spHeaderTitleColor\" : \"#f63\", \"spFooterBackgroundColor\" : \"#666\", \"spButtonColor\" : \"#f63\", \"spButtonHoverColor\" : \"#cc3300\", \"spLinkHoverColor\" : \"#000\", \"spMainNavColor\" : \"#ffa200\", \"spPanelAccentbarColor\" : \"#cdcccb\", \"spSubNavLinkColor\" : \"#000\", \"spSubNavBackgroundColor\" : \"#ffa200\", \"spFooterNavigationTextColor\" : \"#fff\", \"spFooterNavigationHoverColor\" : \"#000\" },\"cssUrl\":\"{0}/theme/surepeople/defaultTheme.css\"},\"companyThemeActive\":false,\"restrictRelationShipAdvisor\":false,\"blockAllMembers\":false}",
      "{\"id\":\"7\",\"name\":\"SurePeople\", \"address\" : { \"country\" : \"Canada\", \"addressLine1\" : \"32323\", \"addressLine2\" : \"\", \"city\" : \"chicago\", \"state\" : \"il\", \"zipCode\" : \"323232\" },\"industry\":null,\"numberOfEmployees\":100,\"phoneNumber\":null,\"accountId\":\"7\"}" };
  
  private String[] accountArray = {
      "{\"id\":\"1\", \"status\" : \"VALID\", \"accountNumber\" : \"SP000122\", \"spPlanMap\" : { \"IntelligentHiring\" : { \"name\" : \"Intelligent hiring\", \"planStatus\" : \"ACTIVE\", \"licensePrice\" : \"300\", \"unitMemberPrice\" : \"100\", \"overrideMemberPrice\" : \"0\", \"unitAdminPrice\" : \"50\", \"overrideAdminPrice\" : \"0\", \"numMember\" : 20, \"creditBalance\" : 2600, \"numAdmin\" : 2, \"features\" : [ \"PrismLens\" ],  \"planType\" : \"IntelligentHiring\", \"planAdminNextChargeAmount\" : \"100\", \"paymentInstrumentId\" : \"7\", \"bundle\" : false, \"active\" : false, \"lastPaymentId\" : \"1\",  \"nextChargeAmount\" : 400, \"agreementTerm\" : 1, \"paymentType\" : \"WIRE\", \"deactivated\" : false, \"billingCycle\" : { \"billingCycleType\" : \"Monthly\", \"noOfMonths\" : 0 } }, \"Primary\" : { \"name\" : \"Erti\", \"planStatus\" : \"ACTIVE\", \"licensePrice\" : \"300\", \"unitMemberPrice\" : \"100\", \"overrideMemberPrice\" : \"0\", \"unitAdminPrice\" : \"50\", \"overrideAdminPrice\" : \"0\", \"numMember\" : 2, \"creditBalance\" : 2600, \"numAdmin\" : 2, \"features\" : [ \"PrismLens\" ],  \"planType\" : \"Primary\", \"planAdminNextChargeAmount\" : \"100\", \"paymentInstrumentId\" : \"7\", \"bundle\" : false, \"active\" : false, \"lastPaymentId\" : \"1\",  \"nextChargeAmount\" : 400, \"agreementTerm\" : 1, \"paymentType\" : \"WIRE\", \"deactivated\" : false, \"billingCycle\" : { \"billingCycleType\" : \"Monthly\", \"noOfMonths\" : 0 } } }, \"availableSubscriptions\" : 0, \"hiringSubscription\" : 0, \"type\" : \"Business\", \"lastPaymentId\" : \"58ad38df77c893d9ac9a74c8\", \"nextChargeAmount\" : 0, \"customerProfileId\" : \"SP000122\", \"agreementTerm\" : 0, \"deactivated\" : false, \"accountUpdateDetailHistory\" : { \"IntelligentHiring\" : [ {  \"noOfAccounts\" : 20, \"unitPrice\" : \"100\", \"overridePrice\" : \"0\", \"accountUpdateType\" : \"PlanMember\" }, {  \"noOfAccounts\" : 2, \"unitPrice\" : \"50\", \"overridePrice\" : \"0\", \"accountUpdateType\" : \"PlanAdmin\" } ] }, \"creditBalance\" : 0 }",
      "{\"id\":\"2\", \"status\" : \"VALID\", \"accountNumber\" : \"SP000123\", \"spPlanMap\" : { \"Primary\" : { \"name\" : \"Erti\", \"planStatus\" : \"ACTIVE\", \"licensePrice\" : \"300\", \"unitMemberPrice\" : \"100\", \"overrideMemberPrice\" : \"0\", \"unitAdminPrice\" : \"50\", \"overrideAdminPrice\" : \"0\", \"numMember\" : 20, \"creditBalance\" : 2600, \"numAdmin\" : 2, \"features\" : [ \"PrismLens\" ],  \"planType\" : \"Primary\", \"planAdminNextChargeAmount\" : \"100\", \"paymentInstrumentId\" : \"7\", \"bundle\" : false, \"active\" : false, \"lastPaymentId\" : \"58ad38df77c893d9ac9a74c8\",  \"nextChargeAmount\" : 400, \"agreementTerm\" : 1, \"paymentType\" : \"WIRE\", \"deactivated\" : false, \"billingCycle\" : { \"billingCycleType\" : \"Monthly\", \"noOfMonths\" : 0 } } }, \"availableSubscriptions\" : 0, \"hiringSubscription\" : 0, \"type\" : \"Business\", \"lastPaymentId\" : \"58ad38df77c893d9ac9a74c8\", \"nextChargeAmount\" : 0, \"customerProfileId\" : \"SP000122\", \"agreementTerm\" : 0, \"deactivated\" : false, \"accountUpdateDetailHistory\" : { \"Primary\" : [ {  \"noOfAccounts\" : 20, \"unitPrice\" : \"100\", \"overridePrice\" : \"0\", \"accountUpdateType\" : \"PlanMember\" }, {  \"noOfAccounts\" : 2, \"unitPrice\" : \"50\", \"overridePrice\" : \"0\", \"accountUpdateType\" : \"PlanAdmin\" } ] }, \"creditBalance\" : 0 }",
      "{\"id\":\"3\", \"status\" : \"VALID\", \"accountNumber\" : \"SP000124\", \"spPlanMap\" : { \"IntelligentHiring\" : { \"name\" : \"Intelligent hiring\", \"planStatus\" : \"ACTIVE\", \"licensePrice\" : \"300\", \"unitMemberPrice\" : \"100\", \"overrideMemberPrice\" : \"0\", \"unitAdminPrice\" : \"50\", \"overrideAdminPrice\" : \"0\", \"numMember\" : 20, \"creditBalance\" : 2600, \"numAdmin\" : 2, \"features\" : [ \"PrismLens\" ],  \"planType\" : \"IntelligentHiring\", \"planAdminNextChargeAmount\" : \"100\", \"paymentInstrumentId\" : \"7\", \"bundle\" : false, \"active\" : false, \"lastPaymentId\" : \"58ad38df77c893d9ac9a74c8\",  \"nextChargeAmount\" : 400, \"agreementTerm\" : 1, \"paymentType\" : \"WIRE\", \"deactivated\" : false, \"billingCycle\" : { \"billingCycleType\" : \"Monthly\", \"noOfMonths\" : 0 } } }, \"availableSubscriptions\" : 0, \"hiringSubscription\" : 0, \"type\" : \"Business\", \"lastPaymentId\" : \"58ad38df77c893d9ac9a74c8\", \"nextChargeAmount\" : 0, \"customerProfileId\" : \"SP000122\", \"agreementTerm\" : 0, \"deactivated\" : false, \"accountUpdateDetailHistory\" : { \"IntelligentHiring\" : [ {  \"noOfAccounts\" : 20, \"unitPrice\" : \"100\", \"overridePrice\" : \"0\", \"accountUpdateType\" : \"PlanMember\" }, {  \"noOfAccounts\" : 2, \"unitPrice\" : \"50\", \"overridePrice\" : \"0\", \"accountUpdateType\" : \"PlanAdmin\" } ] }, \"creditBalance\" : 0 }",
      "{\"id\":\"4\", \"status\" : \"VALID\", \"accountNumber\" : \"SP000125\", \"spPlanMap\" : { \"Primary\" : { \"name\" : \"Erti\", \"planStatus\" : \"ACTIVE\", \"licensePrice\" : \"300\", \"unitMemberPrice\" : \"100\", \"overrideMemberPrice\" : \"0\", \"unitAdminPrice\" : \"50\", \"overrideAdminPrice\" : \"0\", \"numMember\" : 20, \"creditBalance\" : 2600, \"numAdmin\" : 2, \"features\" : [ \"PrismLens\" ],  \"planType\" : \"Primary\", \"planAdminNextChargeAmount\" : \"100\", \"paymentInstrumentId\" : \"7\", \"bundle\" : false, \"active\" : false, \"lastPaymentId\" : \"58ad38df77c893d9ac9a74c8\",  \"nextChargeAmount\" : 400, \"agreementTerm\" : 1, \"paymentType\" : \"WIRE\", \"deactivated\" : false, \"billingCycle\" : { \"billingCycleType\" : \"Monthly\", \"noOfMonths\" : 0 } } }, \"availableSubscriptions\" : 0, \"hiringSubscription\" : 0, \"type\" : \"Business\", \"lastPaymentId\" : \"58ad38df77c893d9ac9a74c8\", \"nextChargeAmount\" : 0, \"customerProfileId\" : \"SP000122\", \"agreementTerm\" : 0, \"deactivated\" : false, \"accountUpdateDetailHistory\" : { \"Primary\" : [ {  \"noOfAccounts\" : 20, \"unitPrice\" : \"100\", \"overridePrice\" : \"0\", \"accountUpdateType\" : \"PlanMember\" }, {  \"noOfAccounts\" : 2, \"unitPrice\" : \"50\", \"overridePrice\" : \"0\", \"accountUpdateType\" : \"PlanAdmin\" } ] }, \"creditBalance\" : 0 }",
      "{\"id\":\"5\", \"status\" : \"VALID\", \"accountNumber\" : \"SP000126\", \"spPlanMap\" : { \"IntelligentHiring\" : { \"name\" : \"Intelligent hiring\", \"planStatus\" : \"ACTIVE\", \"licensePrice\" : \"300\", \"unitMemberPrice\" : \"100\", \"overrideMemberPrice\" : \"0\", \"unitAdminPrice\" : \"50\", \"overrideAdminPrice\" : \"0\", \"numMember\" : 20, \"creditBalance\" : 2600, \"numAdmin\" : 2, \"features\" : [ \"PrismLens\" ],  \"planType\" : \"IntelligentHiring\", \"planAdminNextChargeAmount\" : \"100\", \"paymentInstrumentId\" : \"7\", \"bundle\" : false, \"active\" : false, \"lastPaymentId\" : \"58ad38df77c893d9ac9a74c8\",  \"nextChargeAmount\" : 400, \"agreementTerm\" : 1, \"paymentType\" : \"WIRE\", \"deactivated\" : false, \"billingCycle\" : { \"billingCycleType\" : \"Monthly\", \"noOfMonths\" : 0 } } }, \"availableSubscriptions\" : 0, \"hiringSubscription\" : 0, \"type\" : \"Business\", \"lastPaymentId\" : \"58ad38df77c893d9ac9a74c8\", \"nextChargeAmount\" : 0, \"customerProfileId\" : \"SP000122\", \"agreementTerm\" : 0, \"deactivated\" : false, \"accountUpdateDetailHistory\" : { \"IntelligentHiring\" : [ {  \"noOfAccounts\" : 20, \"unitPrice\" : \"100\", \"overridePrice\" : \"0\", \"accountUpdateType\" : \"PlanMember\" }, {  \"noOfAccounts\" : 2, \"unitPrice\" : \"50\", \"overridePrice\" : \"0\", \"accountUpdateType\" : \"PlanAdmin\" } ] }, \"creditBalance\" : 0 }",
      "{\"id\":\"6\", \"status\" : \"VALID\", \"accountNumber\" : \"SP000127\", \"spPlanMap\" : { \"Primary\" : { \"name\" : \"Erti\", \"planStatus\" : \"ACTIVE\", \"licensePrice\" : \"300\", \"unitMemberPrice\" : \"100\", \"overrideMemberPrice\" : \"0\", \"unitAdminPrice\" : \"50\", \"overrideAdminPrice\" : \"0\", \"numMember\" : 20, \"creditBalance\" : 2600, \"numAdmin\" : 2, \"features\" : [ \"PrismLens\" ],  \"planType\" : \"Primary\", \"planAdminNextChargeAmount\" : \"100\", \"paymentInstrumentId\" : \"7\", \"bundle\" : false, \"active\" : false, \"lastPaymentId\" : \"58ad38df77c893d9ac9a74c8\",  \"nextChargeAmount\" : 400, \"agreementTerm\" : 1, \"paymentType\" : \"WIRE\", \"deactivated\" : false, \"billingCycle\" : { \"billingCycleType\" : \"Monthly\", \"noOfMonths\" : 0 } } }, \"availableSubscriptions\" : 0, \"hiringSubscription\" : 0, \"type\" : \"Business\", \"lastPaymentId\" : \"58ad38df77c893d9ac9a74c8\", \"nextChargeAmount\" : 0, \"customerProfileId\" : \"SP000122\", \"agreementTerm\" : 0, \"deactivated\" : false, \"accountUpdateDetailHistory\" : { \"Primary\" : [ {  \"noOfAccounts\" : 20, \"unitPrice\" : \"100\", \"overridePrice\" : \"0\", \"accountUpdateType\" : \"PlanMember\" }, {  \"noOfAccounts\" : 2, \"unitPrice\" : \"50\", \"overridePrice\" : \"0\", \"accountUpdateType\" : \"PlanAdmin\" } ] }, \"creditBalance\" : 0 }",
      "{\"id\":\"7\", \"status\" : \"VALID\", \"accountNumber\" : \"SP000128\", \"spPlanMap\" : { \"IntelligentHiring\" : { \"name\" : \"Intelligent hiring\", \"planStatus\" : \"ACTIVE\", \"licensePrice\" : \"300\", \"unitMemberPrice\" : \"100\", \"overrideMemberPrice\" : \"0\", \"unitAdminPrice\" : \"50\", \"overrideAdminPrice\" : \"0\", \"numMember\" : 20, \"creditBalance\" : 2600, \"numAdmin\" : 2, \"features\" : [ \"PrismLens\" ],  \"planType\" : \"IntelligentHiring\", \"planAdminNextChargeAmount\" : \"100\", \"paymentInstrumentId\" : \"7\", \"bundle\" : false, \"active\" : false, \"lastPaymentId\" : \"1\",  \"nextChargeAmount\" : 400, \"agreementTerm\" : 1, \"paymentType\" : \"WIRE\", \"deactivated\" : false, \"billingCycle\" : { \"billingCycleType\" : \"Monthly\", \"noOfMonths\" : 0 } },\"Primary\" : { \"name\" : \"Erti\", \"planStatus\" : \"ACTIVE\", \"licensePrice\" : \"300\", \"unitMemberPrice\" : \"100\", \"overrideMemberPrice\" : \"0\", \"unitAdminPrice\" : \"50\", \"overrideAdminPrice\" : \"0\", \"numMember\" : 20, \"creditBalance\" : 2600, \"numAdmin\" : 2, \"features\" : [ \"PrismLens\" ],  \"planType\" : \"Primary\", \"planAdminNextChargeAmount\" : \"100\", \"paymentInstrumentId\" : \"7\", \"bundle\" : false, \"active\" : false, \"lastPaymentId\" : \"1\",  \"nextChargeAmount\" : 400, \"agreementTerm\" : 1, \"paymentType\" : \"WIRE\", \"deactivated\" : false, \"billingCycle\" : { \"billingCycleType\" : \"Monthly\", \"noOfMonths\" : 0 } } }, \"availableSubscriptions\" : 0, \"hiringSubscription\" : 0, \"type\" : \"Business\", \"lastPaymentId\" : \"58ad38df77c893d9ac9a74c8\", \"nextChargeAmount\" : 0, \"customerProfileId\" : \"SP000122\", \"agreementTerm\" : 0, \"deactivated\" : false, \"accountUpdateDetailHistory\" : { \"Primary\" : [ {  \"noOfAccounts\" : 20, \"unitPrice\" : \"100\", \"overridePrice\" : \"0\", \"accountUpdateType\" : \"PlanMember\" }, {  \"noOfAccounts\" : 2, \"unitPrice\" : \"50\", \"overridePrice\" : \"0\", \"accountUpdateType\" : \"PlanAdmin\" } ] }, \"creditBalance\" : 0 }" };
  
  private String[] paymentInstrumentArray = { "{ \"id\" : \"7\", \"creditBalance\" : 50000, \"referenceNo\" : \"5e80ecdd-6222-4554-80e3-3f6d06008a22\", \"nameOnCard\" : \"Pradeep\", \"cardNumber\" : \"4012888818888\", \"month\" : \"02\", \"year\" : \"16\", \"cvv\" : \"1212\", \"country\" : \"United States\", \"zip\" : \"23323\", \"cardNumberLastFour\" : \"8888\", \"paymentType\" : \"WIRE\" }" };
  
  private String[] paymentRecord = {
      "{ \"id\" : \"7\", \"createdOn\" : \"2015-09-20\", \"paymentInstrument\" : { \"maskedCardNumber\" : \"XXXX-8888\", \"cardNumberLastFour\" : \"8888\", \"creditBalance\" : 48566.67, \"paymentType\" : \"WIRE\", \"creditCard\" : false }, \"accountId\" : \"7\", \"amount\" : 50, \"reason\" : \" SurePeople Candidate Account(s) added\", \"txnId\" : \"1a5bffbf-28ed-4da3-8d84-77e449ffe5fb\", \"orderNumber\" : 92,\"planType\":\"Primary\" }",
      "{ \"id\" : \"8\", \"createdOn\" : \"2015-09-20\", \"paymentInstrument\" : { \"maskedCardNumber\" : \"XXXX-8888\", \"cardNumberLastFour\" : \"8888\", \"creditBalance\" : 48566.67, \"paymentType\" : \"WIRE\", \"creditCard\" : false }, \"accountId\" : \"1\", \"amount\" : 50, \"reason\" : \" SurePeople Candidate Account(s) added\", \"txnId\" : \"1a5bffbf-28ed-4da3-8d84-77e449ffe5fb\", \"orderNumber\" : 92, \"planType\":\"Primary\" }" };
  
  private String[] tokenArray = { "{\"tokenId\" :\"2c843f73-f09c-4573-ab89-9405aff6696a\",\"tokenProcessorType\" :\"RESET_PASSWORD\",\"tokenType\" :\"TIME_BASED\",\"createdTime\" :\"1416371456291\",\"tokenUsedCount\" : 0,\"paramsMap\" : {\"userEmail\" :\"admin@admin.com\",\"expiresTime\" : 24,\"timeUnit\" :\"HOURS\" },\"tokenStatus\" :\"ENABLED\" }" };
  
  private String[] growthRequestArray = {
      "{\"id\" :\"1\", \"memberEmail\" : \"dax@surepeople.com\", \"requestedByEmail\" : \"pradeep1@surepeople.com\", \"requestType\" : \"INTERNAL\", \"startDate\" : \"2015-02-25\", \"endDate\" : \"2015-05-28\", \"goals\" : [ \"1\",\"4\"], \"requestStatus\" : \"NOT_INITIATED\" }",
      "{\"id\" :\"6\", \"memberEmail\" : \"dax@surepeople.com\", \"requestedByEmail\" : \"pradeep1@surepeople.com\", \"requestType\" : \"INTERNAL\", \"startDate\" : \"2014-11-30\", \"endDate\" : \"2014-12-28\", \"goals\" : [ \"1\",\"4\"], \"requestStatus\" : \"NOT_INITIATED\" }",
      "{\"id\" :\"3\", \"memberEmail\" : \"pradeep1@surepeople.com\", \"requestedByEmail\" : \"dax@surepeople.com\", \"requestType\" : \"INTERNAL\", \"startDate\" : \"2015-02-12\", \"endDate\" : \"2015-04-28\", \"goals\" : [\"4\"], \"requestStatus\" : \"ACTIVE\" }",
      "{ \"id\" : \"4\", \"memberEmail\" : \"dax@surepeople.com\", \"requestedByEmail\" : \"pradeep1@surepeople.com\", \"requestType\" : \"INTERNAL\", \"startDate\" : \"2015-11-29\", \"endDate\" : \"2015-12-29\", \"goals\" : [\"4\"], \"requestStatus\" : \"ACTIVE\" }",
      "{\"id\" :\"2\", \"memberEmail\" : \"individual@surepeople.com\", \"requestedByEmail\" : \"pradeep1@surepeople.com\", \"requestType\" : \"EXTERNAL\", \"startDate\" : \"2015-11-30\", \"endDate\" : \"2015-12-30\", \"goals\" : [ \"4\" ], \"requestStatus\" : \"DEACTIVE\" }",
      "{ \"id\" : \"5\", \"memberEmail\" : \"dax@surepeople.com\", \"requestedByEmail\" : \"pradeep1@surepeople.com\", \"requestType\" : \"INTERNAL\", \"startDate\" : \"2015-11-29\", \"endDate\" : \"2015-12-29\", \"goals\" : [ \"4\"], \"requestStatus\" : \"COMPLETED\" }" };
  
  private String[] growthTeam = { "{ \"id\" : \"1\", \"email\" : \"pradeep1@surepeople.com\", \"growthRequests\" : [ { \"id\" : \"1\", \"memberEmail\" : \"dax@surepeople.com\", \"requestedByEmail\" : \"pradeep1@surepeople.com\", \"requestType\" : \"INTERNAL\", \"startDate\" : \"2015-11-30\", \"endDate\" : \"2015-12-30\", \"goals\" : [ { \"goalType\" : \"WOrk Hard\" } ], \"requestStatus\" : \"ACTIVE\" }, { \"id\" : \"2\", \"memberEmail\" : \"dax@surepeople.com\", \"requestedByEmail\" : \"pradeep1@surepeople.com\", \"requestType\" : \"EXTERNAL\", \"startDate\" : \"2015-11-30\", \"endDate\" : \"2015-12-30\", \"goals\" : [ { \"goalType\" : \"WOrk Hard\" } ], \"requestStatus\" : \"ACTIVE\" } ] }" };
  
  private String[] growthArchivedArray = { " {\"id\" : \"1\",  \"memberEmail\" : \"dax@surepeople.com\", \"requestedByEmail\" : \"pradeep1@surepeople.com\", \"requestType\" : \"INTERNAL\", \"startDate\" : \"2015-11-29\", \"endDate\" : \"2015-12-29\", \"requestStatus\" : \"DEACTIVE\", \"feedbacks\" : [ ]}" };
  private MongoTemplate mongoTemplate;
  private final ObjectMapper om = new ObjectMapper();
  
  private String[] growthFeedbackQuestions = {
      "{\"id\":\"0\",\"number\":1,\"type\":\"Radio\",\"optionsList\":[\"Yes\",\"No\"],\"description\":\"Questions 1\",\"question\":\" Hellow queesetion 1\"}",
      "{\"id\":\"1\",\"number\":2,\"type\":\"Radio\",\"optionsList\":[\"Yes\",\"No\"],\"description\":\"Questions 2\",\"question\":\" Hellow queesetion 2\"}",
      "{\"id\":\"2\",\"number\":3,\"type\":\"Radio\",\"optionsList\":[\"Yes\",\"No\"],\"description\":\"Questions 3\",\"question\":\" Hellow queesetion 3\"}"
  
  };
  
  private String[] spGoals = {
      "{\"id\":\"1\",\"category\":\"GrowthAreas\",\"name\":\"Time Management\", \"description\":\"Achieve goals and meet deadlines with tips and tricks to manage workloads and prioritize tasks.\", \"status\" : \"ACTIVE\",\"mandatoryArticles\":[\"1\",\"2\"],\"developmentStrategyList\":[{\"id\":\"1\",\"dsDescription\":\"Some thing you need to improve in.\",\"videoUrl\":null,\"imageUrl\":null,\"active\":true},{\"id\":\"0\",\"dsDescription\":\"First development straegy\",\"videoUrl\":null,\"imageUrl\":null,\"active\":true}]}",
      "{\"id\":\"2\",\"category\":\"GrowthAreas\",\"name\":\"Constructive Criticism\", \"description\":\"Learn more about giving and receiving a well-meant critique that’s intended to help and improve.\", \"status\" : \"ACTIVE\",\"mandatoryArticles\":[\"2\"],\"developmentStrategyList\":[{\"id\":\"1\",\"dsDescription\":\"Ds1\",\"videoUrl\":null,\"imageUrl\":null,\"active\":true}]}",
      "{\"id\":\"3\",\"category\":\"GrowthAreas\",\"name\":\"Teamwork\", \"description\":\"Learn more about giving and receiving a well-meant critique that’s intended to help and improve.\", \"status\" : \"ACTIVE\",\"mandatoryArticles\":[\"2\"],\"developmentStrategyList\":[{\"id\":\"1\",\"dsDescription\":\"Ds2 ->>>> This is the theme. and is correct.\",\"videoUrl\":null,\"imageUrl\":null,\"active\":true}]}",
      "{\"id\":\"4\",\"category\":\"GrowthAreas\",\"name\":\"Teambuilding\", \"description\":\"Facilitate better teams by understanding how to employ talent based on skills matching..\", \"status\" : \"ACTIVE\",\"mandatoryArticles\":[\"2\"],\"developmentStrategyList\":[{\"id\":\"1\",\"dsDescription\":\"Some thing you need to improve in. DS3\",\"videoUrl\":null,\"imageUrl\":null,\"active\":true}]}",
      "{\"id\":\"5\",\"category\":\"GrowthAreas\",\"name\":\"Assertiveness\", \"description\":\"Find ways of being self-assured and confident without being aggressive.\", \"status\" : \"ACTIVE\",\"mandatoryArticles\":[\"1\",\"2\",\"10\",\"12\"],\"developmentStrategyList\":[{\"id\":\"1\",\"dsDescription\":\"Some thing you need to improve in.DS6\",\"videoUrl\":null,\"imageUrl\":null,\"active\":true}]}",
      "{ \"id\" : \"6\", \"name\" : \"GoalIndividual\", \"description\" : \"Hello this is individual goal\", \"category\" : \"Individual\", \"status\" : \"ACTIVE\"}",
      "{ \"id\" : \"7\", \"name\" : \"Goal1\", \"description\" : \"Hello this is correct\", \"category\" : \"Business\", \"status\" : \"ACTIVE\", \"accounts\" : [ \"1\" ] }",
      "{\"id\":\"9\",\"category\":\"GrowthAreas\",\"name\":\"Flexibility\", \"description\":\"Find ways of being self-assured and confident without being aggressive.\", \"status\" : \"ACTIVE\",\"mandatoryArticles\":[\"1\",\"2\",\"10\",\"12\"],\"developmentStrategyList\":[{\"id\":\"1\",\"dsDescription\":\"Some thing you need DS8 to improve in.\",\"videoUrl\":null,\"imageUrl\":null,\"active\":true}]}",
      "{\"id\":\"10\",\"category\":\"GrowthAreas\",\"name\":\"Compromise\", \"description\":\"Develop the ability to build consensus through a willingness to concede points, make tradeoffs and secure support through mutually beneficial solutions.\", \"status\" : \"ACTIVE\",\"mandatoryArticles\":[\"1\",\"2\",\"10\",\"12\"],\"developmentStrategyList\":[{\"id\":\"1\",\"dsDescription\":\"Some thing you DS 7 need to improve in.\",\"videoUrl\":null,\"imageUrl\":null,\"active\":true}]}",
      "{ \"id\" : \"8\", \"name\" : \"GoalAllAccount\", \"description\" : \"Hello this is correct\", \"category\" : \"Business\", \"status\" : \"ACTIVE\", \"accounts\" : [ \"1\", \"2\" ] }",
      "{\"id\" : \"11\",\"name\" : \"Planning\", \"description\" : \"Planning steps is for the planning for the company goals.\", \"category\" : \"ActionPlan\", \"status\" : \"ACTIVE\", \"mandatory\" : false, \"adminGoal\" : false, \"allAccounts\" : false, \"introVideo\" : { \"status\" : \"INACTIVE\", \"type\" : \"VIDEO\" }, \"keyOutcomes\" : { \"active\" : true, \"outcomesList\" : [ ] }, \"devStrategyActionCategoryList\" : [ { \"uid\" : \"568a2251d4c69a0f44c739790\", \"title\" : \"Tasks & Exercises\", \"helpText\" : \"Tasks & Exercises\", \"status\" : \"ACTIVE\", \"actionList\" : [ { \"uid\" : \"568a2251d4c69a0f44c739791\", \"title\" : \"Get Time lines\", \"description\" : \"Get time lines\", \"timeInMins\" : 70, \"type\" : \"Single\", \"active\" : true, \"actionData\" : [ { \"uid\" : \"568a2251d4c69a0f44c739792\", \"type\" : \"Web\", \"linkText\" : \"Hello\", \"url\" : \"http://www.hello.com\" } ], \"permissions\" : { \"Completion\" : true, \"Feedback\" : true, \"Note\" : true } } ] }, { \"uid\" : \"568a2251d4c69a0f44c739793\", \"title\" : \"Videos & Resources\", \"helpText\" : \"Videos & Resources\", \"status\" : \"INACTIVE\", \"actionList\" : [ ] }, { \"uid\" : \"568a2251d4c69a0f44c739794\", \"title\" : \"Formal Meetings\", \"status\" : \"INACTIVE\", \"actionList\" : [ ] } ] }" };
  
  private String[] blueprint = { "{ \"id\" : \"1000\",\"missionStatement\" : { \"uid\" : \"5651d25cd4c63b87e68557d90\", \"text\" : \"Learn New technology\" }, \"uidCount\" : 7, \"newFeedbackReceivedCount\" : 5, \"approver\" : { \"id\" :\"2\", \"firstName\" : \"Dec\", \"lastName\" : \"7\", \"email\" : \"dec701@yopmail.com\"}, \"category\" : \"Blueprint\", \"mandatoryArticles\" : [ ], \"status\" : \"PUBLISHED\", \"isMandatory\" : false, \"accounts\" : [ ], \"isAdminGoal\" : false, \"allAccounts\" : false, \"developmentStrategyList\" : [ ], \"devStrategyActionCategoryList\" : [ { \"uid\" : \"5651d25cd4c63b87e68557d91\", \"title\" : \"Whats new coming?\", \"status\" : \"ACTIVE\", \"actionList\" : [ { \"uid\" : \"5651d25cd4c63b87e68557d92\", \"title\" : \"Propose new Technology\", \"timeInMins\" : 0, \"type\" : \"Group\", \"active\" : true, \"actionData\" : [ { \"uid\" : \"5651d25cd4c63b87e68557d93\", \"title\" : \"IMplement new techonology\", \"permissions\" : { \"Completion\" : true } } ], \"permissions\" : { \"Note\" : true, \"Feedback\" : true } } ] } ] }" };
  
  private String[] personalityPracticeArea = {
      "{\"personalityType\":\"Navigator\",\"goalIds\":[\"1\",\"2\"],\"swotProfileMap\" : { \"Strengths\" : [ \"Forward-thinking1\", \"Ambitious1\", \"Innovative\", \"Welcomes challenges\", \"Authoritative\" ], \"Weakness\" : [ \"Can appear insensitive\", \"Intimidating\", \"Prefers to work alone\", \"May have control issues\" ], \"Opportunities\" : [ \"Defines clear goals for the team\", \"Delegates tasks\", \"Unafraid to try new and innovative approaches\", \"Tackles challenging situations\" ], \"Threats\" : [ \"Tends to be blunt and critical\", \"A strong goal orientation at the expense of methodology\", \"Feels confined by routine\", \"Thrives in challenging situations\", \"Highly authoritative\" ] }}",
      "{\"personalityType\":\"Supporter\",\"goalIds\":[\"9\",\"4\",\"3\",\"1\",\"2\",\"10\"],\"swotProfileMap\" : { \"Strengths\" : [ \"Forward-thinking2\", \"Ambitious1\", \"Innovative\", \"Welcomes challenges\", \"Authoritative\" ], \"Weakness\" : [ \"Can appear insensitive\", \"Intimidating\", \"Prefers to work alone\", \"May have control issues\" ], \"Opportunities\" : [ \"Defines clear goals for the team\", \"Delegates tasks\", \"Unafraid to try new and innovative approaches\", \"Tackles challenging situations\" ], \"Threats\" : [ \"Tends to be blunt and critical\", \"A strong goal orientation at the expense of methodology\", \"Feels confined by routine\", \"Thrives in challenging situations\", \"Highly authoritative\" ] }}",
      "{\"personalityType\":\"Refiner\",\"goalIds\":[\"10\",\"9\",\"1\",\"2\",\"1\",\"4\"],\"swotProfileMap\" : { \"Strengths\" : [ \"Forward-thinking3\", \"Ambitious1\", \"Innovative\", \"Welcomes challenges\", \"Authoritative\" ], \"Weakness\" : [ \"Can appear insensitive\", \"Intimidating\", \"Prefers to work alone\", \"May have control issues\" ], \"Opportunities\" : [ \"Defines clear goals for the team\", \"Delegates tasks\", \"Unafraid to try new and innovative approaches\", \"Tackles challenging situations\" ], \"Threats\" : [ \"Tends to be blunt and critical\", \"A strong goal orientation at the expense of methodology\", \"Feels confined by routine\", \"Thrives in challenging situations\", \"Highly authoritative\" ] }}",
      "{\"personalityType\":\"Promoter\",\"goalIds\":[\"2\",\"9\",\"1\",\"10\",\"3\",\"4\"],\"swotProfileMap\" : { \"Strengths\" : [ \"Forward-thinking4\", \"Ambitious1\", \"Innovative\", \"Welcomes challenges\", \"Authoritative\" ], \"Weakness\" : [ \"Can appear insensitive\", \"Intimidating\", \"Prefers to work alone\", \"May have control issues\" ], \"Opportunities\" : [ \"Defines clear goals for the team\", \"Delegates tasks\", \"Unafraid to try new and innovative approaches\", \"Tackles challenging situations\" ], \"Threats\" : [ \"Tends to be blunt and critical\", \"A strong goal orientation at the expense of methodology\", \"Feels confined by routine\", \"Thrives in challenging situations\", \"Highly authoritative\" ] }}",
      "{\"personalityType\":\"Encourager\",\"goalIds\":[\"2\",\"9\",\"1\",\"10\",\"3\",\"4\"],\"swotProfileMap\" : { \"Strengths\" : [ \"Forward-thinking5\", \"Ambitious1\", \"Innovative\", \"Welcomes challenges\", \"Authoritative\" ], \"Weakness\" : [ \"Can appear insensitive\", \"Intimidating\", \"Prefers to work alone\", \"May have control issues\" ], \"Opportunities\" : [ \"Defines clear goals for the team\", \"Delegates tasks\", \"Unafraid to try new and innovative approaches\", \"Tackles challenging situations\" ], \"Threats\" : [ \"Tends to be blunt and critical\", \"A strong goal orientation at the expense of methodology\", \"Feels confined by routine\", \"Thrives in challenging situations\", \"Highly authoritative\" ] }}",
      "{\"personalityType\":\"Researcher\",\"goalIds\":[\"2\",\"9\",\"1\",\"10\",\"3\",\"4\"],\"swotProfileMap\" : { \"Strengths\" : [ \"Forward-thinking5\", \"Ambitious1\", \"Innovative\", \"Welcomes challenges\", \"Authoritative\" ], \"Weakness\" : [ \"Can appear insensitive\", \"Intimidating\", \"Prefers to work alone\", \"May have control issues\" ], \"Opportunities\" : [ \"Defines clear goals for the team\", \"Delegates tasks\", \"Unafraid to try new and innovative approaches\", \"Tackles challenging situations\" ], \"Threats\" : [ \"Tends to be blunt and critical\", \"A strong goal orientation at the expense of methodology\", \"Feels confined by routine\", \"Thrives in challenging situations\", \"Highly authoritative\" ] }}",
      "{\"personalityType\":\"Examiner\",\"goalIds\":[\"2\",\"9\",\"1\",\"10\",\"3\",\"4\"],\"swotProfileMap\" : { \"Strengths\" : [ \"Forward-thinking5\", \"Ambitious1\", \"Innovative\", \"Welcomes challenges\", \"Authoritative\" ], \"Weakness\" : [ \"Can appear insensitive\", \"Intimidating\", \"Prefers to work alone\", \"May have control issues\" ], \"Opportunities\" : [ \"Defines clear goals for the team\", \"Delegates tasks\", \"Unafraid to try new and innovative approaches\", \"Tackles challenging situations\" ], \"Threats\" : [ \"Tends to be blunt and critical\", \"A strong goal orientation at the expense of methodology\", \"Feels confined by routine\", \"Thrives in challenging situations\", \"Highly authoritative\" ] }}",
      "{\"personalityType\":\"Motivator\",\"goalIds\":[\"2\",\"9\",\"1\",\"10\",\"3\",\"4\"],\"swotProfileMap\" : { \"Strengths\" : [ \"Forward-thinking5\", \"Ambitious1\", \"Innovative\", \"Welcomes challenges\", \"Authoritative\" ], \"Weakness\" : [ \"Can appear insensitive\", \"Intimidating\", \"Prefers to work alone\", \"May have control issues\" ], \"Opportunities\" : [ \"Defines clear goals for the team\", \"Delegates tasks\", \"Unafraid to try new and innovative approaches\", \"Tackles challenging situations\" ], \"Threats\" : [ \"Tends to be blunt and critical\", \"A strong goal orientation at the expense of methodology\", \"Feels confined by routine\", \"Thrives in challenging situations\", \"Highly authoritative\" ] }}",
      "{\"personalityType\":\"Ambassador\",\"goalIds\":[\"2\",\"9\",\"1\",\"10\",\"3\",\"4\"],\"swotProfileMap\" : { \"Strengths\" : [ \"Forward-thinking5\", \"Ambitious1\", \"Innovative\", \"Welcomes challenges\", \"Authoritative\" ], \"Weakness\" : [ \"Can appear insensitive\", \"Intimidating\", \"Prefers to work alone\", \"May have control issues\" ], \"Opportunities\" : [ \"Defines clear goals for the team\", \"Delegates tasks\", \"Unafraid to try new and innovative approaches\", \"Tackles challenging situations\" ], \"Threats\" : [ \"Tends to be blunt and critical\", \"A strong goal orientation at the expense of methodology\", \"Feels confined by routine\", \"Thrives in challenging situations\", \"Highly authoritative\" ] }}",
      "{\"personalityType\":\"Designer\",\"goalIds\":[\"2\",\"9\",\"1\",\"10\",\"3\",\"4\"],\"swotProfileMap\" : { \"Strengths\" : [ \"Forward-thinking5\", \"Ambitious1\", \"Innovative\", \"Welcomes challenges\", \"Authoritative\" ], \"Weakness\" : [ \"Can appear insensitive\", \"Intimidating\", \"Prefers to work alone\", \"May have control issues\" ], \"Opportunities\" : [ \"Defines clear goals for the team\", \"Delegates tasks\", \"Unafraid to try new and innovative approaches\", \"Tackles challenging situations\" ], \"Threats\" : [ \"Tends to be blunt and critical\", \"A strong goal orientation at the expense of methodology\", \"Feels confined by routine\", \"Thrives in challenging situations\", \"Highly authoritative\" ] }}",
      "{\"personalityType\":\"Pragmatist\",\"goalIds\":[\"2\",\"9\",\"1\",\"10\",\"3\",\"4\"],\"swotProfileMap\" : { \"Strengths\" : [ \"Forward-thinking5\", \"Ambitious1\", \"Innovative\", \"Welcomes challenges\", \"Authoritative\" ], \"Weakness\" : [ \"Can appear insensitive\", \"Intimidating\", \"Prefers to work alone\", \"May have control issues\" ], \"Opportunities\" : [ \"Defines clear goals for the team\", \"Delegates tasks\", \"Unafraid to try new and innovative approaches\", \"Tackles challenging situations\" ], \"Threats\" : [ \"Tends to be blunt and critical\", \"A strong goal orientation at the expense of methodology\", \"Feels confined by routine\", \"Thrives in challenging situations\", \"Highly authoritative\" ] }}",
      "{\"personalityType\":\"Innovator\",\"goalIds\":[\"2\",\"9\",\"1\",\"10\",\"3\",\"4\"],\"swotProfileMap\" : { \"Strengths\" : [ \"Forward-thinking5\", \"Ambitious1\", \"Innovative\", \"Welcomes challenges\", \"Authoritative\" ], \"Weakness\" : [ \"Can appear insensitive\", \"Intimidating\", \"Prefers to work alone\", \"May have control issues\" ], \"Opportunities\" : [ \"Defines clear goals for the team\", \"Delegates tasks\", \"Unafraid to try new and innovative approaches\", \"Tackles challenging situations\" ], \"Threats\" : [ \"Tends to be blunt and critical\", \"A strong goal orientation at the expense of methodology\", \"Feels confined by routine\", \"Thrives in challenging situations\", \"Highly authoritative\" ] }}",
      "{\"personalityType\":\"Instructor\",\"goalIds\":[\"2\",\"9\",\"1\",\"10\",\"3\",\"4\"],\"swotProfileMap\" : { \"Strengths\" : [ \"Forward-thinking5\", \"Ambitious1\", \"Innovative\", \"Welcomes challenges\", \"Authoritative\" ], \"Weakness\" : [ \"Can appear insensitive\", \"Intimidating\", \"Prefers to work alone\", \"May have control issues\" ], \"Opportunities\" : [ \"Defines clear goals for the team\", \"Delegates tasks\", \"Unafraid to try new and innovative approaches\", \"Tackles challenging situations\" ], \"Threats\" : [ \"Tends to be blunt and critical\", \"A strong goal orientation at the expense of methodology\", \"Feels confined by routine\", \"Thrives in challenging situations\", \"Highly authoritative\" ] }}",
      "{\"personalityType\":\"Actuary\",\"goalIds\":[\"2\",\"9\",\"1\",\"10\",\"3\",\"4\"],\"swotProfileMap\" : { \"Strengths\" : [ \"Forward-thinking5\", \"Ambitious1\", \"Innovative\", \"Welcomes challenges\", \"Authoritative\" ], \"Weakness\" : [ \"Can appear insensitive\", \"Intimidating\", \"Prefers to work alone\", \"May have control issues\" ], \"Opportunities\" : [ \"Defines clear goals for the team\", \"Delegates tasks\", \"Unafraid to try new and innovative approaches\", \"Tackles challenging situations\" ], \"Threats\" : [ \"Tends to be blunt and critical\", \"A strong goal orientation at the expense of methodology\", \"Feels confined by routine\", \"Thrives in challenging situations\", \"Highly authoritative\" ] }}",
      "{\"personalityType\":\"Tough_N_Tender\",\"goalIds\":[\"2\",\"9\",\"1\",\"10\",\"3\",\"4\"],\"swotProfileMap\" : { \"Strengths\" : [ \"Forward-thinking5\", \"Ambitious1\", \"Innovative\", \"Welcomes challenges\", \"Authoritative\" ], \"Weakness\" : [ \"Can appear insensitive\", \"Intimidating\", \"Prefers to work alone\", \"May have control issues\" ], \"Opportunities\" : [ \"Defines clear goals for the team\", \"Delegates tasks\", \"Unafraid to try new and innovative approaches\", \"Tackles challenging situations\" ], \"Threats\" : [ \"Tends to be blunt and critical\", \"A strong goal orientation at the expense of methodology\", \"Feels confined by routine\", \"Thrives in challenging situations\", \"Highly authoritative\" ] }}",
      "{\"personalityType\":\"Visionary\",\"goalIds\":[\"3\",\"5\"],\"swotProfileMap\" : { \"Strengths\" : [ \"Forward-thinking6\", \"Ambitious\", \"Innovative\", \"Welcomes challenges\", \"Authoritative\" ], \"Weakness\" : [ \"Can appear insensitive\", \"Intimidating\", \"Prefers to work alone\", \"May have control issues\" ], \"Opportunities\" : [ \"Defines clear goals for the team\", \"Delegates tasks\", \"Unafraid to try new and innovative approaches\", \"Tackles challenging situations\" ], \"Threats\" : [ \"Tends to be blunt and critical\", \"A strong goal orientation at the expense of methodology\", \"Feels confined by routine\", \"Thrives in challenging situations\", \"Highly authoritative\" ] }}" };
  
  private String[] textArticles = {
      "{\"id\":\"1\",\"articleSource\":\"Fast Company\", \"author\":[\"Chetan Bhagat\"],\"goals\":[\"1\", \"5\"],\"articleType\":\"TEXT\",\"createdOn\":\"2013-12-12\",\"score\":2,\"articleLinkLabel\":\"Education is best\",\"articleLinkUrl\":\"/sp/article/2ssd4323\"}",
      "{\"id\":\"2\",\"articleSource\":\"Fast Company\", \"author\":[\"Lary Bhagat\"],\"goals\":[\"1\", \"5\"],\"articleType\":\"TEXT\",\"createdOn\":\"2013-12-12\",\"score\":1,\"articleLinkLabel\":\"Team Lead is an art\",\"articleLinkUrl\":\"/sp/article/2ssd4323ds\"}", };
  
  private String[] videoArticles = {
      "{\"id\":\"3\",\"articleSource\":\"Fast Company\", \"author\":[\"Chetan Bhagat\"],\"goals\":[\"1\"],\"articleType\":\"VIDEO\",\"createdOn\":\"2013-12-12\",\"score\":2,\"articleLinkLabel\":\"Education is best 2\",\"articleLinkUrl\":\"1\"}",
      "{\"id\":\"4\", \"author\":[\"Chetan Bhagat\"],\"goals\":[\"3\"],\"articleType\":\"VIDEO\",\"createdOn\":\"2013-12-12\",\"score\":2,\"articleLinkLabel\":\"Education is best 3\",\"articleLinkUrl\":\"2\"}",
      "{\"id\":\"5\", \"author\":[\"Chetan Bhagat\"],\"goals\":[\"1\"],\"articleType\":\"VIDEO\",\"createdOn\":\"2013-12-12\",\"score\":2,\"articleLinkLabel\":\"Education is best 4\",\"articleLinkUrl\":\"3\"}",
      "{\"id\":\"6\", \"author\":[\"Chetan Bhagat\"],\"goals\":[\"1\"],\"articleType\":\"VIDEO\",\"createdOn\":\"2013-12-12\",\"score\":2,\"articleLinkLabel\":\"Education is best 5\",\"articleLinkUrl\":\"4\"}",
      "{\"id\":\"7\", \"author\":[\"Higly Bhagat\"],\"goals\":[\"1\"],\"articleType\":\"VIDEO\",\"createdOn\":\"2013-12-12\",\"score\":2,\"articleLinkLabel\":\"Education is best 6\",\"articleLinkUrl\":\"5\"}",
      "{\"id\":\"8\", \"author\":[\"Good Bhagat\"],\"goals\":[\"2\"],\"articleType\":\"VIDEO\",\"createdOn\":\"2013-12-12\",\"score\":2,\"articleLinkLabel\":\"Education is best 7\",\"articleLinkUrl\":\"6\"}",
      "{\"id\":\"9\", \"author\":[\"Sample Bhagat\"],\"goals\":[\"1\"],\"articleType\":\"VIDEO\",\"createdOn\":\"2013-12-12\",\"score\":2,\"articleLinkLabel\":\"Education is best 8\",\"articleLinkUrl\":\"7\"}",
      "{\"id\":\"10\", \"author\":[\"Tough Bhagat\"],\"goals\":[\"1\", \"5\"],\"articleType\":\"VIDEO\",\"createdOn\":\"2013-12-12\",\"score\":2,\"articleLinkLabel\":\"Education is best 9\",\"articleLinkUrl\":\"8\"}",
      "{\"id\":\"11\", \"author\":[\"Simple Bhagat\"],\"goals\":[\"1\"],\"articleType\":\"VIDEO\",\"createdOn\":\"2013-12-12\",\"score\":2,\"articleLinkLabel\":\"Education is best 10\",\"articleLinkUrl\":\"9\"}",
      "{\"id\":\"12\", \"author\":[\"Chetan Bhagat\"],\"goals\":[\"1\", \"5\"],\"articleType\":\"VIDEO\",\"createdOn\":\"2013-12-12\",\"score\":2,\"articleLinkLabel\":\"Education is best 11\",\"articleLinkUrl\":\"10\"}",
      "{\"id\":\"13\", \"author\":[\"Chetan Bhagat\"],\"goals\":[\"1\"],\"articleType\":\"VIDEO\",\"createdOn\":\"2013-12-12\",\"score\":2,\"articleLinkLabel\":\"Education is best 12\",\"articleLinkUrl\":\"11\"}",
      "{\"id\":\"14\", \"author\":[\"Chetan Bhagat\"],\"goals\":[\"1\"],\"articleType\":\"VIDEO\",\"createdOn\":\"2013-12-12\",\"score\":2,\"articleLinkLabel\":\"Education is best 13\",\"articleLinkUrl\":\"12\"}",
      "{\"id\":\"15\", \"author\":[\"Wow Bhagat\"],\"goals\":[\"1\"],\"articleType\":\"VIDEO\",\"createdOn\":\"2013-12-12\",\"score\":2,\"articleLinkLabel\":\"Education is best 14\",\"articleLinkUrl\":\"14\"}",
      "{\"id\":\"16\",\"articleSource\":\"SurePeople\", \"author\":[\"Extra Bhagat\"],\"goals\":[\"2\"],\"articleType\":\"VIDEO\",\"createdOn\":\"2013-12-12\",\"score\":2,\"articleLinkLabel\":\"Education is best 15\",\"articleLinkUrl\":\"13\",\"imageUrl\":\"http://imgurl\"}",
      "{\"id\":\"17\",\"articleSource\":\"SurePeople\", \"author\":[\"Ha ha Bhagat\"],\"goals\":[\"2\"],\"articleType\":\"VIDEO\",\"createdOn\":\"2013-12-12\",\"score\":2,\"articleLinkLabel\":\"Education is best 16\",\"articleLinkUrl\":\"14\",\"imageUrl\":\"http://imgurl\"}"
  
  };
  
  private String[] feedbackRequestArray = {
      "{\"id\" :\"1\",\"E\" : \"2\", \"feedbackUserId\" : \"1\", \"requestedById\" : \"1\", \"requestType\" : \"INTERNAL\", \"startDate\" : \"2015-01-01\", \"endDate\" : \"2015-02-28\", \"requestStatus\" : \"NOT_INITIATED\" }",
      "{\"id\" :\"3\",\"fbOrginalUserId\" : \"2\", \"feedbackUserId\" : \"2\", \"requestedById\" : \"1\", \"requestType\" : \"INTERNAL\", \"startDate\" : \"2015-01-01\", \"endDate\" : \"2015-02-28\", \"requestStatus\" : \"ACTIVE\" }",
      "{\"id\" :\"4\",\"fbOrginalUserId\" : \"2\", \"feedbackUserId\" : \"3\", \"requestedById\" :\"1\", \"requestType\" : \"INTERNAL\", \"startDate\" : \"2015-01-01\", \"endDate\" : \"2015-02-28\", \"requestStatus\" : \"ACTIVE\" }",
      "{\"id\" :\"2\",\"fbOrginalUserId\" : \"2\", \"feedbackUserId\" : \"4\", \"requestedById\" : \"1\", \"requestType\" : \"EXTERNAL\", \"startDate\" : \"2015-01-01\", \"endDate\" : \"2015-02-28\", \"requestStatus\" : \"DEACTIVE\" }" };
  
  private String[] hiringUser = {
      "{\"hiringRoles\" : [ \"Board\", \"CEO\" ], \"url\" : \"http://linkedin.com/pradeep1\", \"assessmentDueDate\" : \"2015-03-12\", \"createdOn\" : \"2015-02-12\", \"firstName\" : \"Sam1\", \"lastName\" : \"Abraham\", \"email\" : \"sam1@yopmail.com\", \"address\" : { \"country\" : \"Country\", \"addressLine1\" : \"Address Line 1\", \"addressLine2\" : \"Address Line 2\", \"city\" : \"City\", \"state\" : \"State\", \"zipCode\" : \"Zip\" }, \"phoneNumber\" : \"+919818399147\", \"userStatus\" : \"INVITATION_SENT\", \"companyId\" : \"1\", \"analysis\" : { \"accuracy\" : \"0\", \"processing\" : { \"External\" : \"60\", \"Internal\" : \"40\", \"Cognitive\" : \"40\", \"Spontaneous\" : \"50\", \"Intuitive\" : \"50\", \"Orderly\" : \"50\", \"Concrete\" : \"50\", \"Affective\" : \"60\" }, \"conflictManagement\" : { \"Compromise\" : \"17\", \"Accommodate\" : \"20\", \"Compete\" : \"20\", \"Collaborate\" : \"20\", \"Avoid\" : \"23\" }, \"learningStyle\" : { \"Analytical\" : \"60\", \"Global\" : \"40\" }, \"motivationWhy\" : { \"Affiliation\" : \"70\", \"Compliance\" : \"10\", \"AttainmentOfGoals\" : \"70\", \"Power\" : \"90\", \"RecognitionForEffort\" : \"30\", \"Activity\" : \"30\" }, \"motivationHow\" : { \"Freedom\" : \"75\", \"AffirmedByOthers\" : \"88\", \"ExchangeOfIdeas\" : \"58\", \"TaskCompletion\" : \"29\", \"Consistency\" : \"25\", \"PrefersProcess\" : \"71\", \"SelfAffirmed\" : \"13\", \"ReceiveDirection\" : \"42\" }, \"motivationWhat\" : { \"Accomplishment\" : \"50\", \"Hygiene\" : \"50\" }, \"personality\" : { \"Primary\" : { \"segmentScore\" : 4644, \"segmentGraph\" : [ \"0.57\", \"0.86\", \"0.57\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Actuary\" }, \"UnderPressure\" : { \"segmentScore\" : 5334, \"segmentGraph\" : [ \"0.71\", \"0.43\", \"0.43\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Visionary\" }, \"PerceivedByOthers\" : { \"segmentScore\" : 5543, \"segmentGraph\" : [ \"0.71\", \"0.71\", \"0.57\", \"0.43\" ], \"isValid\" : false, \"personalityType\" : \"Instructor\" } }, \"fundamentalNeeds\" : { \"Security\" : \"33\", \"Control\" : \"33\", \"Significance\" : \"35\" }, \"decisionMaking\" : { \"Rapid\" : \"58\", \"Careful\" : \"42\", \"Inward\" : \"33\", \"Outward\" : \"67\" } }, \"roles\" : [ \"HiringCandidate\" ], \"tagList\" : [ \"Tag1\", \"Tag2\" ], \"tokenUrl\" : \"http://uat.surepeople.com/token/someToken\", \"imageCount\" : 0 }",
      "{\"hiringRoles\" : [ \"Board\", \"CEO\" ], \"url\" : \"http://linkedin.com/pradeep2\", \"assessmentDueDate\" : \"2015-04-12\", \"createdOn\" : \"2015-02-12\", \"firstName\" : \"Sam2\", \"lastName\" : \"Abraham\", \"email\" : \"sam2@yopmail.com\", \"address\" : { \"country\" : \"Country\", \"addressLine1\" : \"Address Line 1\", \"addressLine2\" : \"Address Line 2\", \"city\" : \"City\", \"state\" : \"State\", \"zipCode\" : \"Zip\" }, \"phoneNumber\" : \"+919818399147\", \"userStatus\" : \"VALID\", \"companyId\" : \"1\", \"analysis\" : { \"accuracy\" : \"0\", \"processing\" : { \"External\" : \"60\", \"Internal\" : \"40\", \"Cognitive\" : \"40\", \"Spontaneous\" : \"50\", \"Intuitive\" : \"50\", \"Orderly\" : \"50\", \"Concrete\" : \"50\", \"Affective\" : \"60\" }, \"conflictManagement\" : { \"Compromise\" : \"17\", \"Accommodate\" : \"20\", \"Compete\" : \"20\", \"Collaborate\" : \"20\", \"Avoid\" : \"23\" }, \"learningStyle\" : { \"Analytical\" : \"60\", \"Global\" : \"40\" }, \"motivationWhy\" : { \"Affiliation\" : \"70\", \"Compliance\" : \"10\", \"AttainmentOfGoals\" : \"70\", \"Power\" : \"90\", \"RecognitionForEffort\" : \"30\", \"Activity\" : \"30\" }, \"motivationHow\" : { \"Freedom\" : \"75\", \"AffirmedByOthers\" : \"88\", \"ExchangeOfIdeas\" : \"58\", \"TaskCompletion\" : \"29\", \"Consistency\" : \"25\", \"PrefersProcess\" : \"71\", \"SelfAffirmed\" : \"13\", \"ReceiveDirection\" : \"42\" }, \"motivationWhat\" : { \"Accomplishment\" : \"50\", \"Hygiene\" : \"50\" }, \"personality\" : { \"Primary\" : { \"segmentScore\" : 4644, \"segmentGraph\" : [ \"0.57\", \"0.86\", \"0.57\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Actuary\" }, \"UnderPressure\" : { \"segmentScore\" : 5334, \"segmentGraph\" : [ \"0.71\", \"0.43\", \"0.43\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Visionary\" }, \"PerceivedByOthers\" : { \"segmentScore\" : 5543, \"segmentGraph\" : [ \"0.71\", \"0.71\", \"0.57\", \"0.43\" ], \"isValid\" : false, \"personalityType\" : \"Instructor\" } }, \"fundamentalNeeds\" : { \"Security\" : \"33\", \"Control\" : \"33\", \"Significance\" : \"35\" }, \"decisionMaking\" : { \"Rapid\" : \"58\", \"Careful\" : \"42\", \"Inward\" : \"33\", \"Outward\" : \"67\" } }, \"roles\" : [ \"HiringCandidate\" ], \"tagList\" : [ \"Tag1\", \"Tag2\" ], \"tokenUrl\" : \"http://uat.surepeople.com/token/someToken\", \"imageCount\" : 0 }",
      "{\"hiringRoles\" : [ \"Board\", \"CEO\" ], \"url\" : \"http://linkedin.com/pradeep3\", \"assessmentDueDate\" : \"2015-05-12\", \"createdOn\" : \"2015-02-12\", \"firstName\" : \"Sam3\", \"lastName\" : \"Abraham\", \"email\" : \"sam3@yopmail.com\", \"address\" : { \"country\" : \"Country\", \"addressLine1\" : \"Address Line 1\", \"addressLine2\" : \"Address Line 2\", \"city\" : \"City\", \"state\" : \"State\", \"zipCode\" : \"Zip\" }, \"phoneNumber\" : \"+919818399147\", \"userStatus\" : \"INVITATION_SENT\", \"companyId\" : \"1\", \"analysis\" : { \"accuracy\" : \"0\", \"processing\" : { \"External\" : \"60\", \"Internal\" : \"40\", \"Cognitive\" : \"40\", \"Spontaneous\" : \"50\", \"Intuitive\" : \"50\", \"Orderly\" : \"50\", \"Concrete\" : \"50\", \"Affective\" : \"60\" }, \"conflictManagement\" : { \"Compromise\" : \"17\", \"Accommodate\" : \"20\", \"Compete\" : \"20\", \"Collaborate\" : \"20\", \"Avoid\" : \"23\" }, \"learningStyle\" : { \"Analytical\" : \"60\", \"Global\" : \"40\" }, \"motivationWhy\" : { \"Affiliation\" : \"70\", \"Compliance\" : \"10\", \"AttainmentOfGoals\" : \"70\", \"Power\" : \"90\", \"RecognitionForEffort\" : \"30\", \"Activity\" : \"30\" }, \"motivationHow\" : { \"Freedom\" : \"75\", \"AffirmedByOthers\" : \"88\", \"ExchangeOfIdeas\" : \"58\", \"TaskCompletion\" : \"29\", \"Consistency\" : \"25\", \"PrefersProcess\" : \"71\", \"SelfAffirmed\" : \"13\", \"ReceiveDirection\" : \"42\" }, \"motivationWhat\" : { \"Accomplishment\" : \"50\", \"Hygiene\" : \"50\" }, \"personality\" : { \"Primary\" : { \"segmentScore\" : 4644, \"segmentGraph\" : [ \"0.57\", \"0.86\", \"0.57\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Actuary\" }, \"UnderPressure\" : { \"segmentScore\" : 5334, \"segmentGraph\" : [ \"0.71\", \"0.43\", \"0.43\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Visionary\" }, \"PerceivedByOthers\" : { \"segmentScore\" : 5543, \"segmentGraph\" : [ \"0.71\", \"0.71\", \"0.57\", \"0.43\" ], \"isValid\" : false, \"personalityType\" : \"Instructor\" } }, \"fundamentalNeeds\" : { \"Security\" : \"33\", \"Control\" : \"33\", \"Significance\" : \"35\" }, \"decisionMaking\" : { \"Rapid\" : \"58\", \"Careful\" : \"42\", \"Inward\" : \"33\", \"Outward\" : \"67\" } }, \"roles\" : [ \"HiringCandidate\" ], \"tagList\" : [ \"Tag1\", \"Tag2\" ], \"tokenUrl\" : \"http://uat.surepeople.com/token/someToken\", \"imageCount\" : 0 }",
      "{\"hiringRoles\" : [ \"Board\", \"CEO\" ], \"url\" : \"http://linkedin.com/pradeep4\", \"assessmentDueDate\" : \"2015-07-12\", \"createdOn\" : \"2015-02-12\", \"firstName\" : \"Sam4\", \"lastName\" : \"Abraham\", \"email\" : \"sam4@yopmail.com\", \"address\" : { \"country\" : \"Country\", \"addressLine1\" : \"Address Line 1\", \"addressLine2\" : \"Address Line 2\", \"city\" : \"City\", \"state\" : \"State\", \"zipCode\" : \"Zip\" }, \"phoneNumber\" : \"+919818399147\", \"userStatus\" : \"INVITATION_SENT\", \"companyId\" : \"1\", \"analysis\" : { \"accuracy\" : \"0\", \"processing\" : { \"External\" : \"60\", \"Internal\" : \"40\", \"Cognitive\" : \"40\", \"Spontaneous\" : \"50\", \"Intuitive\" : \"50\", \"Orderly\" : \"50\", \"Concrete\" : \"50\", \"Affective\" : \"60\" }, \"conflictManagement\" : { \"Compromise\" : \"17\", \"Accommodate\" : \"20\", \"Compete\" : \"20\", \"Collaborate\" : \"20\", \"Avoid\" : \"23\" }, \"learningStyle\" : { \"Analytical\" : \"60\", \"Global\" : \"40\" }, \"motivationWhy\" : { \"Affiliation\" : \"70\", \"Compliance\" : \"10\", \"AttainmentOfGoals\" : \"70\", \"Power\" : \"90\", \"RecognitionForEffort\" : \"30\", \"Activity\" : \"30\" }, \"motivationHow\" : { \"Freedom\" : \"75\", \"AffirmedByOthers\" : \"88\", \"ExchangeOfIdeas\" : \"58\", \"TaskCompletion\" : \"29\", \"Consistency\" : \"25\", \"PrefersProcess\" : \"71\", \"SelfAffirmed\" : \"13\", \"ReceiveDirection\" : \"42\" }, \"motivationWhat\" : { \"Accomplishment\" : \"50\", \"Hygiene\" : \"50\" }, \"personality\" : { \"Primary\" : { \"segmentScore\" : 4644, \"segmentGraph\" : [ \"0.57\", \"0.86\", \"0.57\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Actuary\" }, \"UnderPressure\" : { \"segmentScore\" : 5334, \"segmentGraph\" : [ \"0.71\", \"0.43\", \"0.43\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Visionary\" }, \"PerceivedByOthers\" : { \"segmentScore\" : 5543, \"segmentGraph\" : [ \"0.71\", \"0.71\", \"0.57\", \"0.43\" ], \"isValid\" : false, \"personalityType\" : \"Instructor\" } }, \"fundamentalNeeds\" : { \"Security\" : \"33\", \"Control\" : \"33\", \"Significance\" : \"35\" }, \"decisionMaking\" : { \"Rapid\" : \"58\", \"Careful\" : \"42\", \"Inward\" : \"33\", \"Outward\" : \"67\" } }, \"roles\" : [ \"HiringCandidate\" ], \"tagList\" : [ \"Tag1\", \"Tag2\" ], \"tokenUrl\" : \"http://uat.surepeople.com/token/someToken\", \"imageCount\" : 0 }",
      "{\"hiringRoles\" : [ \"Board\", \"CEO\" ], \"url\" : \"http://linkedin.com/pradeep5\", \"assessmentDueDate\" : \"2015-08-12\", \"createdOn\" : \"2015-02-12\", \"firstName\" : \"Sam5\", \"lastName\" : \"Abraham\", \"email\" : \"sam5@yopmail.com\", \"address\" : { \"country\" : \"Country\", \"addressLine1\" : \"Address Line 1\", \"addressLine2\" : \"Address Line 2\", \"city\" : \"City\", \"state\" : \"State\", \"zipCode\" : \"Zip\" }, \"phoneNumber\" : \"+919818399147\", \"userStatus\" : \"VALID\", \"companyId\" : \"1\", \"analysis\" : { \"accuracy\" : \"0\", \"processing\" : { \"External\" : \"60\", \"Internal\" : \"40\", \"Cognitive\" : \"40\", \"Spontaneous\" : \"50\", \"Intuitive\" : \"50\", \"Orderly\" : \"50\", \"Concrete\" : \"50\", \"Affective\" : \"60\" }, \"conflictManagement\" : { \"Compromise\" : \"17\", \"Accommodate\" : \"20\", \"Compete\" : \"20\", \"Collaborate\" : \"20\", \"Avoid\" : \"23\" }, \"learningStyle\" : { \"Analytical\" : \"60\", \"Global\" : \"40\" }, \"motivationWhy\" : { \"Affiliation\" : \"70\", \"Compliance\" : \"10\", \"AttainmentOfGoals\" : \"70\", \"Power\" : \"90\", \"RecognitionForEffort\" : \"30\", \"Activity\" : \"30\" }, \"motivationHow\" : { \"Freedom\" : \"75\", \"AffirmedByOthers\" : \"88\", \"ExchangeOfIdeas\" : \"58\", \"TaskCompletion\" : \"29\", \"Consistency\" : \"25\", \"PrefersProcess\" : \"71\", \"SelfAffirmed\" : \"13\", \"ReceiveDirection\" : \"42\" }, \"motivationWhat\" : { \"Accomplishment\" : \"50\", \"Hygiene\" : \"50\" }, \"personality\" : { \"Primary\" : { \"segmentScore\" : 4644, \"segmentGraph\" : [ \"0.57\", \"0.86\", \"0.57\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Actuary\" }, \"UnderPressure\" : { \"segmentScore\" : 5334, \"segmentGraph\" : [ \"0.71\", \"0.43\", \"0.43\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Visionary\" }, \"PerceivedByOthers\" : { \"segmentScore\" : 5543, \"segmentGraph\" : [ \"0.71\", \"0.71\", \"0.57\", \"0.43\" ], \"isValid\" : false, \"personalityType\" : \"Instructor\" } }, \"fundamentalNeeds\" : { \"Security\" : \"33\", \"Control\" : \"33\", \"Significance\" : \"35\" }, \"decisionMaking\" : { \"Rapid\" : \"58\", \"Careful\" : \"42\", \"Inward\" : \"33\", \"Outward\" : \"67\" } }, \"roles\" : [ \"HiringCandidate\" ], \"tagList\" : [ \"Tag1\", \"Tag2\" ], \"tokenUrl\" : \"http://uat.surepeople.com/token/someToken\", \"imageCount\" : 0 }",
      "{\"hiringRoles\" : [ \"Board\", \"CEO\" ], \"url\" : \"http://linkedin.com/pradeep6\", \"assessmentDueDate\" : \"2015-09-12\", \"createdOn\" : \"2015-02-12\", \"firstName\" : \"Sam6\", \"lastName\" : \"Abraham\", \"email\" : \"sam6@yopmail.com\", \"address\" : { \"country\" : \"Country\", \"addressLine1\" : \"Address Line 1\", \"addressLine2\" : \"Address Line 2\", \"city\" : \"City\", \"state\" : \"State\", \"zipCode\" : \"Zip\" }, \"phoneNumber\" : \"+919818399147\", \"userStatus\" : \"ASSESSMENT_PENDING\", \"companyId\" : \"1\", \"analysis\" : { \"accuracy\" : \"0\", \"processing\" : { \"External\" : \"60\", \"Internal\" : \"40\", \"Cognitive\" : \"40\", \"Spontaneous\" : \"50\", \"Intuitive\" : \"50\", \"Orderly\" : \"50\", \"Concrete\" : \"50\", \"Affective\" : \"60\" }, \"conflictManagement\" : { \"Compromise\" : \"17\", \"Accommodate\" : \"20\", \"Compete\" : \"20\", \"Collaborate\" : \"20\", \"Avoid\" : \"23\" }, \"learningStyle\" : { \"Analytical\" : \"60\", \"Global\" : \"40\" }, \"motivationWhy\" : { \"Affiliation\" : \"70\", \"Compliance\" : \"10\", \"AttainmentOfGoals\" : \"70\", \"Power\" : \"90\", \"RecognitionForEffort\" : \"30\", \"Activity\" : \"30\" }, \"motivationHow\" : { \"Freedom\" : \"75\", \"AffirmedByOthers\" : \"88\", \"ExchangeOfIdeas\" : \"58\", \"TaskCompletion\" : \"29\", \"Consistency\" : \"25\", \"PrefersProcess\" : \"71\", \"SelfAffirmed\" : \"13\", \"ReceiveDirection\" : \"42\" }, \"motivationWhat\" : { \"Accomplishment\" : \"50\", \"Hygiene\" : \"50\" }, \"personality\" : { \"Primary\" : { \"segmentScore\" : 4644, \"segmentGraph\" : [ \"0.57\", \"0.86\", \"0.57\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Actuary\" }, \"UnderPressure\" : { \"segmentScore\" : 5334, \"segmentGraph\" : [ \"0.71\", \"0.43\", \"0.43\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Visionary\" }, \"PerceivedByOthers\" : { \"segmentScore\" : 5543, \"segmentGraph\" : [ \"0.71\", \"0.71\", \"0.57\", \"0.43\" ], \"isValid\" : false, \"personalityType\" : \"Instructor\" } }, \"fundamentalNeeds\" : { \"Security\" : \"33\", \"Control\" : \"33\", \"Significance\" : \"35\" }, \"decisionMaking\" : { \"Rapid\" : \"58\", \"Careful\" : \"42\", \"Inward\" : \"33\", \"Outward\" : \"67\" } }, \"roles\" : [ \"HiringCandidate\" ], \"tagList\" : [ \"Tag1\", \"Tag2\" ], \"tokenUrl\" : \"http://uat.surepeople.com/token/someToken\", \"imageCount\" : 0 }",
      "{\"hiringRoles\" : [ \"Board\", \"CEO\" ], \"url\" : \"http://linkedin.com/pradeep7\", \"assessmentDueDate\" : \"2015-10-12\", \"createdOn\" : \"2015-02-12\", \"firstName\" : \"Sam7\", \"lastName\" : \"Abraham\", \"email\" : \"sam7@yopmail.com\", \"address\" : { \"country\" : \"Country\", \"addressLine1\" : \"Address Line 1\", \"addressLine2\" : \"Address Line 2\", \"city\" : \"City\", \"state\" : \"State\", \"zipCode\" : \"Zip\" }, \"phoneNumber\" : \"+919818399147\", \"userStatus\" : \"INVITATION_SENT\", \"companyId\" : \"1\", \"analysis\" : { \"accuracy\" : \"0\", \"processing\" : { \"External\" : \"60\", \"Internal\" : \"40\", \"Cognitive\" : \"40\", \"Spontaneous\" : \"50\", \"Intuitive\" : \"50\", \"Orderly\" : \"50\", \"Concrete\" : \"50\", \"Affective\" : \"60\" }, \"conflictManagement\" : { \"Compromise\" : \"17\", \"Accommodate\" : \"20\", \"Compete\" : \"20\", \"Collaborate\" : \"20\", \"Avoid\" : \"23\" }, \"learningStyle\" : { \"Analytical\" : \"60\", \"Global\" : \"40\" }, \"motivationWhy\" : { \"Affiliation\" : \"70\", \"Compliance\" : \"10\", \"AttainmentOfGoals\" : \"70\", \"Power\" : \"90\", \"RecognitionForEffort\" : \"30\", \"Activity\" : \"30\" }, \"motivationHow\" : { \"Freedom\" : \"75\", \"AffirmedByOthers\" : \"88\", \"ExchangeOfIdeas\" : \"58\", \"TaskCompletion\" : \"29\", \"Consistency\" : \"25\", \"PrefersProcess\" : \"71\", \"SelfAffirmed\" : \"13\", \"ReceiveDirection\" : \"42\" }, \"motivationWhat\" : { \"Accomplishment\" : \"50\", \"Hygiene\" : \"50\" }, \"personality\" : { \"Primary\" : { \"segmentScore\" : 4644, \"segmentGraph\" : [ \"0.57\", \"0.86\", \"0.57\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Actuary\" }, \"UnderPressure\" : { \"segmentScore\" : 5334, \"segmentGraph\" : [ \"0.71\", \"0.43\", \"0.43\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Visionary\" }, \"PerceivedByOthers\" : { \"segmentScore\" : 5543, \"segmentGraph\" : [ \"0.71\", \"0.71\", \"0.57\", \"0.43\" ], \"isValid\" : false, \"personalityType\" : \"Instructor\" } }, \"fundamentalNeeds\" : { \"Security\" : \"33\", \"Control\" : \"33\", \"Significance\" : \"35\" }, \"decisionMaking\" : { \"Rapid\" : \"58\", \"Careful\" : \"42\", \"Inward\" : \"33\", \"Outward\" : \"67\" } }, \"roles\" : [ \"HiringCandidate\" ], \"tagList\" : [ \"Tag1\", \"Tag2\" ], \"tokenUrl\" : \"http://uat.surepeople.com/token/someToken\", \"imageCount\" : 0 }",
      "{\"hiringRoles\" : [ \"Board\", \"CEO\" ], \"url\" : \"http://linkedin.com/pradeep8\", \"assessmentDueDate\" : \"2015-12-12\", \"createdOn\" : \"2015-02-12\", \"firstName\" : \"Sam8\", \"lastName\" : \"Abraham\", \"email\" : \"sam@8yopmail.com\", \"address\" : { \"country\" : \"Country\", \"addressLine1\" : \"Address Line 1\", \"addressLine2\" : \"Address Line 2\", \"city\" : \"City\", \"state\" : \"State\", \"zipCode\" : \"Zip\" }, \"phoneNumber\" : \"+919818399147\", \"userStatus\" : \"INVITATION_SENT\", \"companyId\" : \"1\", \"analysis\" : { \"accuracy\" : \"0\", \"processing\" : { \"External\" : \"60\", \"Internal\" : \"40\", \"Cognitive\" : \"40\", \"Spontaneous\" : \"50\", \"Intuitive\" : \"50\", \"Orderly\" : \"50\", \"Concrete\" : \"50\", \"Affective\" : \"60\" }, \"conflictManagement\" : { \"Compromise\" : \"17\", \"Accommodate\" : \"20\", \"Compete\" : \"20\", \"Collaborate\" : \"20\", \"Avoid\" : \"23\" }, \"learningStyle\" : { \"Analytical\" : \"60\", \"Global\" : \"40\" }, \"motivationWhy\" : { \"Affiliation\" : \"70\", \"Compliance\" : \"10\", \"AttainmentOfGoals\" : \"70\", \"Power\" : \"90\", \"RecognitionForEffort\" : \"30\", \"Activity\" : \"30\" }, \"motivationHow\" : { \"Freedom\" : \"75\", \"AffirmedByOthers\" : \"88\", \"ExchangeOfIdeas\" : \"58\", \"TaskCompletion\" : \"29\", \"Consistency\" : \"25\", \"PrefersProcess\" : \"71\", \"SelfAffirmed\" : \"13\", \"ReceiveDirection\" : \"42\" }, \"motivationWhat\" : { \"Accomplishment\" : \"50\", \"Hygiene\" : \"50\" }, \"personality\" : { \"Primary\" : { \"segmentScore\" : 4644, \"segmentGraph\" : [ \"0.57\", \"0.86\", \"0.57\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Actuary\" }, \"UnderPressure\" : { \"segmentScore\" : 5334, \"segmentGraph\" : [ \"0.71\", \"0.43\", \"0.43\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Visionary\" }, \"PerceivedByOthers\" : { \"segmentScore\" : 5543, \"segmentGraph\" : [ \"0.71\", \"0.71\", \"0.57\", \"0.43\" ], \"isValid\" : false, \"personalityType\" : \"Instructor\" } }, \"fundamentalNeeds\" : { \"Security\" : \"33\", \"Control\" : \"33\", \"Significance\" : \"35\" }, \"decisionMaking\" : { \"Rapid\" : \"58\", \"Careful\" : \"42\", \"Inward\" : \"33\", \"Outward\" : \"67\" } }, \"roles\" : [ \"HiringCandidate\" ], \"tagList\" : [ \"Tag1\", \"Tag2\" ], \"tokenUrl\" : \"http://uat.surepeople.com/token/someToken\", \"imageCount\" : 0 }",
      "{\"hiringRoles\" : [ \"Board\", \"CTO\" ], \"url\" : \"http://linkedin.com/pradeep9\", \"assessmentDueDate\" : \"2015-11-12\", \"createdOn\" : \"2015-02-12\", \"firstName\" : \"Sam9\", \"lastName\" : \"Abraham\", \"email\" : \"sam9@yopmail.com\", \"address\" : { \"country\" : \"Country\", \"addressLine1\" : \"Address Line 1\", \"addressLine2\" : \"Address Line 2\", \"city\" : \"City\", \"state\" : \"State\", \"zipCode\" : \"Zip\" }, \"phoneNumber\" : \"+919818399147\", \"userStatus\" : \"ASSESSMENT_PROGRESS\", \"companyId\" : \"1\", \"analysis\" : { \"accuracy\" : \"0\", \"processing\" : { \"External\" : \"60\", \"Internal\" : \"40\", \"Cognitive\" : \"40\", \"Spontaneous\" : \"50\", \"Intuitive\" : \"50\", \"Orderly\" : \"50\", \"Concrete\" : \"50\", \"Affective\" : \"60\" }, \"conflictManagement\" : { \"Compromise\" : \"17\", \"Accommodate\" : \"20\", \"Compete\" : \"20\", \"Collaborate\" : \"20\", \"Avoid\" : \"23\" }, \"learningStyle\" : { \"Analytical\" : \"60\", \"Global\" : \"40\" }, \"motivationWhy\" : { \"Affiliation\" : \"70\", \"Compliance\" : \"10\", \"AttainmentOfGoals\" : \"70\", \"Power\" : \"90\", \"RecognitionForEffort\" : \"30\", \"Activity\" : \"30\" }, \"motivationHow\" : { \"Freedom\" : \"75\", \"AffirmedByOthers\" : \"88\", \"ExchangeOfIdeas\" : \"58\", \"TaskCompletion\" : \"29\", \"Consistency\" : \"25\", \"PrefersProcess\" : \"71\", \"SelfAffirmed\" : \"13\", \"ReceiveDirection\" : \"42\" }, \"motivationWhat\" : { \"Accomplishment\" : \"50\", \"Hygiene\" : \"50\" }, \"personality\" : { \"Primary\" : { \"segmentScore\" : 4644, \"segmentGraph\" : [ \"0.57\", \"0.86\", \"0.57\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Actuary\" }, \"UnderPressure\" : { \"segmentScore\" : 5334, \"segmentGraph\" : [ \"0.71\", \"0.43\", \"0.43\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Visionary\" }, \"PerceivedByOthers\" : { \"segmentScore\" : 5543, \"segmentGraph\" : [ \"0.71\", \"0.71\", \"0.57\", \"0.43\" ], \"isValid\" : false, \"personalityType\" : \"Instructor\" } }, \"fundamentalNeeds\" : { \"Security\" : \"33\", \"Control\" : \"33\", \"Significance\" : \"35\" }, \"decisionMaking\" : { \"Rapid\" : \"58\", \"Careful\" : \"42\", \"Inward\" : \"33\", \"Outward\" : \"67\" } }, \"roles\" : [ \"HiringCandidate\" ], \"tagList\" : [ \"Tag1\", \"Tag2\" ], \"tokenUrl\" : \"http://uat.surepeople.com/token/someToken\", \"imageCount\" : 0 }",
      "{\"hiringRoles\" : [ \"Board\", \"CTO\" ], \"url\" : \"http://linkedin.com/pradeep10\", \"assessmentDueDate\" : \"2015-10-12\", \"createdOn\" : \"2015-02-12\", \"firstName\" : \"Sam10\", \"lastName\" : \"Abraham\", \"email\" : \"sam10@yopmail.com\", \"address\" : { \"country\" : \"Country\", \"addressLine1\" : \"Address Line 1\", \"addressLine2\" : \"Address Line 2\", \"city\" : \"City\", \"state\" : \"State\", \"zipCode\" : \"Zip\" }, \"phoneNumber\" : \"+919818399147\", \"userStatus\" : \"INVITATION_SENT\", \"companyId\" : \"1\", \"analysis\" : { \"accuracy\" : \"0\", \"processing\" : { \"External\" : \"60\", \"Internal\" : \"40\", \"Cognitive\" : \"40\", \"Spontaneous\" : \"50\", \"Intuitive\" : \"50\", \"Orderly\" : \"50\", \"Concrete\" : \"50\", \"Affective\" : \"60\" }, \"conflictManagement\" : { \"Compromise\" : \"17\", \"Accommodate\" : \"20\", \"Compete\" : \"20\", \"Collaborate\" : \"20\", \"Avoid\" : \"23\" }, \"learningStyle\" : { \"Analytical\" : \"60\", \"Global\" : \"40\" }, \"motivationWhy\" : { \"Affiliation\" : \"70\", \"Compliance\" : \"10\", \"AttainmentOfGoals\" : \"70\", \"Power\" : \"90\", \"RecognitionForEffort\" : \"30\", \"Activity\" : \"30\" }, \"motivationHow\" : { \"Freedom\" : \"75\", \"AffirmedByOthers\" : \"88\", \"ExchangeOfIdeas\" : \"58\", \"TaskCompletion\" : \"29\", \"Consistency\" : \"25\", \"PrefersProcess\" : \"71\", \"SelfAffirmed\" : \"13\", \"ReceiveDirection\" : \"42\" }, \"motivationWhat\" : { \"Accomplishment\" : \"50\", \"Hygiene\" : \"50\" }, \"personality\" : { \"Primary\" : { \"segmentScore\" : 4644, \"segmentGraph\" : [ \"0.57\", \"0.86\", \"0.57\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Actuary\" }, \"UnderPressure\" : { \"segmentScore\" : 5334, \"segmentGraph\" : [ \"0.71\", \"0.43\", \"0.43\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Visionary\" }, \"PerceivedByOthers\" : { \"segmentScore\" : 5543, \"segmentGraph\" : [ \"0.71\", \"0.71\", \"0.57\", \"0.43\" ], \"isValid\" : false, \"personalityType\" : \"Instructor\" } }, \"fundamentalNeeds\" : { \"Security\" : \"33\", \"Control\" : \"33\", \"Significance\" : \"35\" }, \"decisionMaking\" : { \"Rapid\" : \"58\", \"Careful\" : \"42\", \"Inward\" : \"33\", \"Outward\" : \"67\" } }, \"roles\" : [ \"HiringCandidate\" ], \"tagList\" : [ \"Tag1\", \"Tag2\" ], \"tokenUrl\" : \"http://uat.surepeople.com/token/someToken\", \"imageCount\" : 0 }",
      "{\"hiringRoles\" : [ \"Board\", \"CEO\" ], \"url\" : \"http://linkedin.com/pradeep11\", \"assessmentDueDate\" : \"2015-08-12\", \"createdOn\" : \"2015-02-12\", \"firstName\" : \"Sam11\", \"lastName\" : \"Abraham\", \"email\" : \"sam11@yopmail.com\", \"address\" : { \"country\" : \"Country\", \"addressLine1\" : \"Address Line 1\", \"addressLine2\" : \"Address Line 2\", \"city\" : \"City\", \"state\" : \"State\", \"zipCode\" : \"Zip\" }, \"phoneNumber\" : \"+919818399147\", \"userStatus\" : \"ASSESSMENT_PROGRESS\", \"companyId\" : \"1\", \"analysis\" : { \"accuracy\" : \"0\", \"processing\" : { \"External\" : \"60\", \"Internal\" : \"40\", \"Cognitive\" : \"40\", \"Spontaneous\" : \"50\", \"Intuitive\" : \"50\", \"Orderly\" : \"50\", \"Concrete\" : \"50\", \"Affective\" : \"60\" }, \"conflictManagement\" : { \"Compromise\" : \"17\", \"Accommodate\" : \"20\", \"Compete\" : \"20\", \"Collaborate\" : \"20\", \"Avoid\" : \"23\" }, \"learningStyle\" : { \"Analytical\" : \"60\", \"Global\" : \"40\" }, \"motivationWhy\" : { \"Affiliation\" : \"70\", \"Compliance\" : \"10\", \"AttainmentOfGoals\" : \"70\", \"Power\" : \"90\", \"RecognitionForEffort\" : \"30\", \"Activity\" : \"30\" }, \"motivationHow\" : { \"Freedom\" : \"75\", \"AffirmedByOthers\" : \"88\", \"ExchangeOfIdeas\" : \"58\", \"TaskCompletion\" : \"29\", \"Consistency\" : \"25\", \"PrefersProcess\" : \"71\", \"SelfAffirmed\" : \"13\", \"ReceiveDirection\" : \"42\" }, \"motivationWhat\" : { \"Accomplishment\" : \"50\", \"Hygiene\" : \"50\" }, \"personality\" : { \"Primary\" : { \"segmentScore\" : 4644, \"segmentGraph\" : [ \"0.57\", \"0.86\", \"0.57\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Actuary\" }, \"UnderPressure\" : { \"segmentScore\" : 5334, \"segmentGraph\" : [ \"0.71\", \"0.43\", \"0.43\", \"0.57\" ], \"isValid\" : false, \"personalityType\" : \"Visionary\" }, \"PerceivedByOthers\" : { \"segmentScore\" : 5543, \"segmentGraph\" : [ \"0.71\", \"0.71\", \"0.57\", \"0.43\" ], \"isValid\" : false, \"personalityType\" : \"Instructor\" } }, \"fundamentalNeeds\" : { \"Security\" : \"33\", \"Control\" : \"33\", \"Significance\" : \"35\" }, \"decisionMaking\" : { \"Rapid\" : \"58\", \"Careful\" : \"42\", \"Inward\" : \"33\", \"Outward\" : \"67\" } }, \"roles\" : [ \"HiringCandidate\" ], \"tagList\" : [ \"Tag1\", \"Tag2\" ], \"tokenUrl\" : \"http://uat.surepeople.com/token/someToken\", \"imageCount\" : 0 }"
  
  };
  
  private String[] hiringUserArchive = {
      "{ \"archivedOn\" : \"2015-05-08\", \"hiringRoles\" : [ \"Actor\" ], \"url\" : \"\", \"assessmentDueDate\" : \"2015-04-23\", \"references\" : [ \"54ebe75cd4c6811eb8ffb24a\", \"54ebe75cd4c6811eb8ffb248\", \"54ebe75cd4c6811eb8ffb249\" ], \"comments\" : [ ], \"hiringCoordinatorId\" : \"54c3360cd4c6a5978c84d05f\", \"createdOn\" : \"2015-02-23\", \"firstName\" : \"Salman\", \"lastName\" : \"Khan\", \"title\" : \"Actor\", \"email\" : \"salman1@dummysurepeople.com\", \"address\" : { \"country\" : \"United States\", \"addressLine1\" : \"Address1\", \"addressLine2\" : \"\", \"city\" : \"Newyork\", \"state\" : \"AZ\", \"zipCode\" : \"22323\" }, \"phoneNumber\" : \"3929293992\", \"userStatus\" : \"HIRED\", \"companyId\" : \"1\", \"analysis\" : { \"_class\" : \"com.sp.web.assessment.processing.AnalysisBeanDTO\", \"accuracy\" : \"100\", \"processing\" : { \"Spontaneous\" : \"100\", \"Concrete\" : \"0\", \"Intuitive\" : \"100\", \"Affective\" : \"100\", \"Orderly\" : \"0\", \"Internal\" : \"100\", \"External\" : \"0\", \"Cognitive\" : \"0\" }, \"conflictManagement\" : { \"Compromise\" : \"17\", \"Accommodate\" : \"27\", \"Avoid\" : \"20\", \"Compete\" : \"17\", \"Collaborate\" : \"20\" }, \"learningStyle\" : { \"Global\" : \"100\", \"Analytical\" : \"0\" }, \"motivationWhy\" : { \"Power\" : \"50\", \"AttainmentOfGoals\" : \"30\", \"Activity\" : \"50\", \"Compliance\" : \"50\", \"Affiliation\" : \"50\", \"RecognitionForEffort\" : \"70\" }, \"motivationHow\" : { \"SelfAffirmed\" : \"0\", \"Consistency\" : \"33\", \"PrefersProcess\" : \"100\", \"ReceiveDirection\" : \"33\", \"TaskCompletion\" : \"0\", \"ExchangeOfIdeas\" : \"67\", \"Freedom\" : \"67\", \"AffirmedByOthers\" : \"100\" }, \"motivationWhat\" : { \"Hygiene\" : \"17\", \"Accomplishment\" : \"83\" }, \"personality\" : { \"UnderPressure\" : { \"segmentScore\" : 6323, \"segmentGraph\" : [ \"0.86\", \"0.43\", \"0.29\", \"0.43\" ], \"isValid\" : true, \"personalityType\" : \"Visionary\" }, \"PerceivedByOthers\" : { \"segmentScore\" : 6424, \"segmentGraph\" : [ \"0.86\", \"0.57\", \"0.29\", \"0.57\" ], \"isValid\" : true, \"personalityType\" : \"Visionary\" }, \"Primary\" : { \"segmentScore\" : 5435, \"segmentGraph\" : [ \"0.71\", \"0.57\", \"0.43\", \"0.71\" ], \"isValid\" : true, \"personalityType\" : \"Navigator\" } }, \"fundamentalNeeds\" : { \"Significance\" : \"38\", \"Security\" : \"33\", \"Control\" : \"30\" }, \"decisionMaking\" : { \"Careful\" : \"67\", \"Inward\" : \"92\", \"Outward\" : \"8\", \"Rapid\" : \"33\" } }, \"roles\" : [ \"HiringCandidate\" ], \"gender\" : \"F\", \"dob\" : \"1986-07-06\", \"groupAssociationList\" : [ ], \"tagList\" : [ ], \"profileSettings\" : { \"isHiringAccessAllowed\" : false, \"is360ProfileAccessAllowed\" : false, \"isWorkspacePulseAllowed\" : false, \"token\" : \"d7af7aca-dbee-451b-b4be-dff7bf72a2bf\", \"certificateProfilePublicView\" : false }, \"userGoalId\" : \"54ed0859d4c68ab53a3e7676\", \"tokenUrl\" : \"http://uat.surepeople.com/sp/processToken/1c3622b3-bcfa-439a-b809-b29b24dc9957\", \"imageCount\" : 0, \"certificateNumber\" : \"1\", \"taskList\" : [ ] }",
      "{ \"archivedOn\" : \"2015-05-08\", \"hiringRoles\" : [ \"Actor\" ], \"url\" : \"\", \"assessmentDueDate\" : \"2015-04-23\", \"references\" : [ \"54ebe75cd4c6811eb8ffb24a\", \"54ebe75cd4c6811eb8ffb248\", \"54ebe75cd4c6811eb8ffb249\" ], \"comments\" : [ ], \"hiringCoordinatorId\" : \"54c3360cd4c6a5978c84d05f\", \"createdOn\" : \"2015-02-23\", \"firstName\" : \"Salman\", \"lastName\" : \"Khan\", \"title\" : \"Actor\", \"email\" : \"salman2@dummysurepeople.com\", \"address\" : { \"country\" : \"United States\", \"addressLine1\" : \"Address1\", \"addressLine2\" : \"\", \"city\" : \"Newyork\", \"state\" : \"AZ\", \"zipCode\" : \"22323\" }, \"phoneNumber\" : \"3929293992\", \"userStatus\" : \"HIRED\", \"companyId\" : \"1\", \"analysis\" : { \"_class\" : \"com.sp.web.assessment.processing.AnalysisBeanDTO\", \"accuracy\" : \"100\", \"processing\" : { \"Spontaneous\" : \"100\", \"Concrete\" : \"0\", \"Intuitive\" : \"100\", \"Affective\" : \"100\", \"Orderly\" : \"0\", \"Internal\" : \"100\", \"External\" : \"0\", \"Cognitive\" : \"0\" }, \"conflictManagement\" : { \"Compromise\" : \"17\", \"Accommodate\" : \"27\", \"Avoid\" : \"20\", \"Compete\" : \"17\", \"Collaborate\" : \"20\" }, \"learningStyle\" : { \"Global\" : \"100\", \"Analytical\" : \"0\" }, \"motivationWhy\" : { \"Power\" : \"50\", \"AttainmentOfGoals\" : \"30\", \"Activity\" : \"50\", \"Compliance\" : \"50\", \"Affiliation\" : \"50\", \"RecognitionForEffort\" : \"70\" }, \"motivationHow\" : { \"SelfAffirmed\" : \"0\", \"Consistency\" : \"33\", \"PrefersProcess\" : \"100\", \"ReceiveDirection\" : \"33\", \"TaskCompletion\" : \"0\", \"ExchangeOfIdeas\" : \"67\", \"Freedom\" : \"67\", \"AffirmedByOthers\" : \"100\" }, \"motivationWhat\" : { \"Hygiene\" : \"17\", \"Accomplishment\" : \"83\" }, \"personality\" : { \"UnderPressure\" : { \"segmentScore\" : 6323, \"segmentGraph\" : [ \"0.86\", \"0.43\", \"0.29\", \"0.43\" ], \"isValid\" : true, \"personalityType\" : \"Visionary\" }, \"PerceivedByOthers\" : { \"segmentScore\" : 6424, \"segmentGraph\" : [ \"0.86\", \"0.57\", \"0.29\", \"0.57\" ], \"isValid\" : true, \"personalityType\" : \"Visionary\" }, \"Primary\" : { \"segmentScore\" : 5435, \"segmentGraph\" : [ \"0.71\", \"0.57\", \"0.43\", \"0.71\" ], \"isValid\" : true, \"personalityType\" : \"Navigator\" } }, \"fundamentalNeeds\" : { \"Significance\" : \"38\", \"Security\" : \"33\", \"Control\" : \"30\" }, \"decisionMaking\" : { \"Careful\" : \"67\", \"Inward\" : \"92\", \"Outward\" : \"8\", \"Rapid\" : \"33\" } }, \"roles\" : [ \"HiringCandidate\" ], \"gender\" : \"F\", \"dob\" : \"1986-07-06\", \"groupAssociationList\" : [ ], \"tagList\" : [ ], \"profileSettings\" : { \"isHiringAccessAllowed\" : false, \"is360ProfileAccessAllowed\" : false, \"isWorkspacePulseAllowed\" : false, \"token\" : \"d7af7aca-dbee-451b-b4be-dff7bf72a2bf\", \"certificateProfilePublicView\" : false }, \"userGoalId\" : \"54ed0859d4c68ab53a3e7676\", \"tokenUrl\" : \"http://uat.surepeople.com/sp/processToken/1c3622b3-bcfa-439a-b809-b29b24dc9957\", \"imageCount\" : 0, \"certificateNumber\" : \"1\", \"taskList\" : [ ] }",
      "{ \"archivedOn\" : \"2015-05-08\", \"hiringRoles\" : [ \"Actor\" ], \"url\" : \"\", \"assessmentDueDate\" : \"2015-04-23\", \"references\" : [ \"54ebe75cd4c6811eb8ffb24a\", \"54ebe75cd4c6811eb8ffb248\", \"54ebe75cd4c6811eb8ffb249\" ], \"comments\" : [ ], \"hiringCoordinatorId\" : \"54c3360cd4c6a5978c84d05f\", \"createdOn\" : \"2015-02-23\", \"firstName\" : \"Salman\", \"lastName\" : \"Khan\", \"title\" : \"Actor\", \"email\" : \"salman3@dummysurepeople.com\", \"address\" : { \"country\" : \"United States\", \"addressLine1\" : \"Address1\", \"addressLine2\" : \"\", \"city\" : \"Newyork\", \"state\" : \"AZ\", \"zipCode\" : \"22323\" }, \"phoneNumber\" : \"3929293992\", \"userStatus\" : \"ASSESSMENT_PENDING\", \"companyId\" : \"1\", \"analysis\" : { \"_class\" : \"com.sp.web.assessment.processing.AnalysisBeanDTO\", \"accuracy\" : \"100\", \"processing\" : { \"Spontaneous\" : \"100\", \"Concrete\" : \"0\", \"Intuitive\" : \"100\", \"Affective\" : \"100\", \"Orderly\" : \"0\", \"Internal\" : \"100\", \"External\" : \"0\", \"Cognitive\" : \"0\" }, \"conflictManagement\" : { \"Compromise\" : \"17\", \"Accommodate\" : \"27\", \"Avoid\" : \"20\", \"Compete\" : \"17\", \"Collaborate\" : \"20\" }, \"learningStyle\" : { \"Global\" : \"100\", \"Analytical\" : \"0\" }, \"motivationWhy\" : { \"Power\" : \"50\", \"AttainmentOfGoals\" : \"30\", \"Activity\" : \"50\", \"Compliance\" : \"50\", \"Affiliation\" : \"50\", \"RecognitionForEffort\" : \"70\" }, \"motivationHow\" : { \"SelfAffirmed\" : \"0\", \"Consistency\" : \"33\", \"PrefersProcess\" : \"100\", \"ReceiveDirection\" : \"33\", \"TaskCompletion\" : \"0\", \"ExchangeOfIdeas\" : \"67\", \"Freedom\" : \"67\", \"AffirmedByOthers\" : \"100\" }, \"motivationWhat\" : { \"Hygiene\" : \"17\", \"Accomplishment\" : \"83\" }, \"personality\" : { \"UnderPressure\" : { \"segmentScore\" : 6323, \"segmentGraph\" : [ \"0.86\", \"0.43\", \"0.29\", \"0.43\" ], \"isValid\" : true, \"personalityType\" : \"Visionary\" }, \"PerceivedByOthers\" : { \"segmentScore\" : 6424, \"segmentGraph\" : [ \"0.86\", \"0.57\", \"0.29\", \"0.57\" ], \"isValid\" : true, \"personalityType\" : \"Visionary\" }, \"Primary\" : { \"segmentScore\" : 5435, \"segmentGraph\" : [ \"0.71\", \"0.57\", \"0.43\", \"0.71\" ], \"isValid\" : true, \"personalityType\" : \"Navigator\" } }, \"fundamentalNeeds\" : { \"Significance\" : \"38\", \"Security\" : \"33\", \"Control\" : \"30\" }, \"decisionMaking\" : { \"Careful\" : \"67\", \"Inward\" : \"92\", \"Outward\" : \"8\", \"Rapid\" : \"33\" } }, \"roles\" : [ \"HiringCandidate\" ], \"gender\" : \"M\", \"dob\" : \"1986-07-06\", \"groupAssociationList\" : [ ], \"tagList\" : [ ], \"profileSettings\" : { \"isHiringAccessAllowed\" : false, \"is360ProfileAccessAllowed\" : false, \"isWorkspacePulseAllowed\" : false, \"token\" : \"d7af7aca-dbee-451b-b4be-dff7bf72a2bf\", \"certificateProfilePublicView\" : false }, \"userGoalId\" : \"54ed0859d4c68ab53a3e7676\", \"tokenUrl\" : \"http://uat.surepeople.com/sp/processToken/1c3622b3-bcfa-439a-b809-b29b24dc9957\", \"imageCount\" : 0, \"certificateNumber\" : \"1\", \"taskList\" : [ ] }"
  
  };
  
  private String[] actionPlan = { "{ \"id\" : \"1\", \"name\" : \"SurePeople\", \"active\" : true, \"practiceAreaIdList\" : [ \"11\" ], \"companyId\" : \"1\", \"groupPermissionsList\" : [ ], \"forAllUsers\" : true, \"uidCount\" : 5 }" };
  
  public static void main(String[] args) throws JsonProcessingException {
    /*
     * Promotion promotion = new Promotion(); promotion.setName(\\"testInvalid");
     * promotion.setCode("testCodeInvalid"); DateTime startDate = DateTime.now();
     * promotion.setStartDate(startDate.minusMonths(1).toDate());
     * promotion.setEndDate(startDate.minusDays(5).toDate()); promotion.setUnitPrice(5.50d);
     * List<String> productList = new ArrayList<String>(); productList.add("1");
     * promotion.setProductIdList(productList);
     * promotion.setPromotionType(PromotionType.TIME_BASED);
     * 
     * ObjectMapper om = new ObjectMapper();
     * System.out.println(StringEscapeUtils.escapeJava(om.writeValueAsString(promotion)));
     * 
     * UserGroup g = new UserGroup(); g.setId("1"); g.setCompany("SurePeople");
     * g.setName("Executive"); g.setGroupLead("admin@admin.com"); ObjectMapper om = new
     * ObjectMapper(); System.out.println(StringEscapeUtils.escapeJava(om.writeValueAsString(g)));
     * 
     * Company c = new Company(); c.setId("1"); c.setName("SurePeople"); c.setAccountId("1");
     * c.setNumberOfEmployees(100); ObjectMapper om = new ObjectMapper();
     * System.out.println(StringEscapeUtils.escapeJava(om.writeValueAsString(c)));
     */
    
    /*
     * Account account = new Account(); account.setId("1"); account.setAvailableSubscriptions(25);
     * account.setHiringSubscription(25); account.setStatus(AccountStatus.VALID);
     * 
     * SPGoal goal = new SPGoal(); List<DevelopmentStrategy> listDevStrategy = new
     * ArrayList<DevelopmentStrategy>(); goal.setDevelopmentStrategyList(listDevStrategy);
     * DevelopmentStrategy devStrategy = new DevelopmentStrategy(); devStrategy.setId("1");
     * devStrategy.setActive(true);
     * devStrategy.setDsDescription("Some thing you need to improve in.");
     * listDevStrategy.add(devStrategy); devStrategy = new DevelopmentStrategy();
     * devStrategy.setId("2"); devStrategy.setActive(true);
     * devStrategy.setDsDescription("Another thing you need to improve in.");
     * 
     * ObjectMapper om = new ObjectMapper();
     * System.out.println(StringEscapeUtils.escapeJava(om.writeValueAsString(goal)));
     */
    DBSetup.createTheme();
  }
  
  private PasswordEncoder passwordEncoder;
  
  private User defaultUser;
  
  public DBSetup(MongoTemplate mongoTemplate) throws Exception {
    this.mongoTemplate = mongoTemplate;
    ObjectMapper om = new ObjectMapper();
    defaultUser = om.readValue(usersArray[0], User.class);
    passwordEncoder = (PasswordEncoder) ApplicationContextUtils.getApplicationContext().getBean(
        "passwordEncoder");
  }
  
  public DBSetup(MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder) throws Exception {
    this(mongoTemplate);
    this.passwordEncoder = passwordEncoder;
  }
  
  public void createProducts() throws JsonParseException, JsonMappingException, IOException {
    for (String productStr : productsArray) {
      Product p = om.readValue(productStr, Product.class);
      mongoTemplate.insert(p);
      LOG.debug("Product Inserted :" + p);
    }
  }
  
  public void createsPromotions() throws JsonParseException, JsonMappingException, IOException {
    LocalDate dateTime = LocalDate.now();
    for (String promotionsStr : promotionsArray) {
      Promotion promotions = om.readValue(promotionsStr, Promotion.class);
      try {
        if (promotions.getCode().equals("testCode")) {
          promotions.setStartDate(dateTime.minusMonths(1));
          promotions.setEndDate(dateTime.plusYears(1));
        } else if (promotions.getCode().equals("testCodeInvalid")) {
          promotions.setStartDate(dateTime.minusMonths(5));
          promotions.setEndDate(dateTime.minusMonths(1));
        }
        mongoTemplate.insert(promotions);
      } catch (Exception e) {
        e.printStackTrace();
      }
      LOG.debug("Promotion Inserted :" + promotions);
    }
  }
  
  /**
   * Removes all the product objects from mongo db
   */
  public void removeAllProducts() {
    removeAll("product");
  }
  
  /**
   * @param collectionName
   *          - removes all the objects from the given collection
   */
  public void removeAll(String collectionName) {
    LOG.debug("Removing all the " + collectionName + " !!!");
    WriteResult result;
    try {
      DBCollection collection = mongoTemplate.getCollection(collectionName);
      result = collection.remove(new BasicDBObject());
      LOG.debug("Number of " + collectionName + " deleted :" + result.getN());
      LOG.debug("Number of products deleted :" + result.getN());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Create a user.
   * 
   * @throws JsonParseException
   *           - exception
   * @throws JsonMappingException
   *           - exception
   * @throws IOException
   *           - exception
   */
  public void createUsers() throws JsonParseException, JsonMappingException, IOException {
    String[] dobArray = { "11/30/1994", "12/07/1985", "12/14/1965", "12/21/1955", "12/28/1945",
        "12/28/1935", "12/28/1975", "12/28/1967", "12/28/1955", "05/19/2013", "05/19/2038" };
    om.registerSubtypes(PersonalityBeanResponse.class, UserGoalProgress.class, SPGoal.class,
        UserArticleProgress.class);
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    int index = 0;
    for (String userStr : usersArray) {
      User user = om.readValue(userStr, User.class);
      String dob = dobArray[index++];
      user.setDob(DateTimeUtil.getLocalDate(dob));
      String encode = passwordEncoder.encode(user.getPassword());
      user.setPassword(encode);
      mongoTemplate.insert(user);
      LOG.debug("Product Inserted :" + user);
    }
  }
  
  /**
   * Create a user.
   * 
   * @throws JsonParseException
   *           - exception
   * @throws JsonMappingException
   *           - exception
   * @throws IOException
   *           - exception
   */
  public void createHiringUsers() throws JsonParseException, JsonMappingException, IOException {
    om.registerSubtypes(PersonalityBeanResponse.class, UserGoalProgress.class, SPGoal.class,
        UserArticleProgress.class);
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    for (String userStr : hiringUser) {
      HiringUser user = om.readValue(userStr, HiringUser.class);
      mongoTemplate.insert(user);
      LOG.debug("Product Inserted :" + user);
    }
  }
  
  /**
   * Create a user.
   * 
   * @throws JsonParseException
   *           - exception
   * @throws JsonMappingException
   *           - exception
   * @throws IOException
   *           - exception
   */
  public void createHiringArchiveUsers() throws JsonParseException, JsonMappingException,
      IOException {
    om.registerSubtypes(PersonalityBeanResponse.class, UserGoalProgress.class, SPGoal.class,
        UserArticleProgress.class);
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    for (String userStr : hiringUserArchive) {
      HiringUserArchive user = om.readValue(userStr, HiringUserArchive.class);
      mongoTemplate.insert(user);
      LOG.debug("Product Inserted :" + user);
    }
  }
  
  public void removeAllHiringArchiveUsers() {
    removeAll("hiringUserArchive");
  }
  
  public void removeAllPromotions() {
    removeAll("promotion");
  }
  
  public void removeAllUsers() {
    removeAll("user");
  }
  
  public void removeAllFeedbackUsers() {
    removeAll("feedbackUser");
  }
  
  public void removeAllCompanies() {
    removeAll("company");
  }
  
  public void removeAllAccounts() {
    removeAll("account");
  }
  
  public void removeAllInstruments() {
    removeAll("paymentInstrument");
  }
  
  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }
  
  public PasswordEncoder getPasswordEncoder() {
    return passwordEncoder;
  }
  
  public void removeAllAssistedUsers() {
    removeAll("assistedAccount");
  }
  
  /**
   * @return the defaultUser
   */
  public User getDefaultUser() {
    return defaultUser;
  }
  
  public void removeAllAssessmentProgressStore() {
    removeAll("assessmentProgressStore");
  }
  
  public void removeAllGroups() {
    removeAll("userGroup");
  }
  
  public int addGroups() throws JsonParseException, JsonMappingException, IOException {
    int index = 0;
    for (String groupStr : groupArray) {
      try {
        UserGroup userGroup = om.readValue(groupStr, UserGroup.class);
        mongoTemplate.insert(userGroup);
        LOG.debug("Group Inserted :" + userGroup);
        index++;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return index;
  }
  
  public void addTagsUser(String userName, List<String> tagList) {
    Update updateObj = new Update();
    updateObj.set("tagList", tagList);
    mongoTemplate.updateFirst(query(where("email").is(userName)), updateObj, User.class);
  }
  
  public void createCompanies() {
    for (String companyStr : companyArray) {
      try {
        Company c = om.readValue(companyStr, Company.class);
        mongoTemplate.insert(c);
        LOG.debug("Company Inserted :" + c);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  public void createAccounts() {
    for (String accountStr : accountArray) {
      try {
        Account a = om.readValue(accountStr, Account.class);
        DateTime dateTime = DateTime.now();
        a.setStartDate(dateTime.minusMonths(1).toDate());
        a.setBillingCycleStartDate(a.getStartDate());
        if (a.getSpPlanMap() != null) {
          a.getSpPlanMap().forEach((key, value) -> {
            int aggrementTerm = value.getAgreementTerm();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime aggreementEndTerm = now.plusYears(aggrementTerm);
            value.setAggreementEndDate(aggreementEndTerm);
            
          });
        }
        mongoTemplate.insert(a);
        LOG.debug("Account Inserted :" + a);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  public void createPaymentInstruments() {
    for (String paymentInstrument : paymentInstrumentArray) {
      try {
        CreditNotePaymentInstrument a = om.readValue(paymentInstrument,
            CreditNotePaymentInstrument.class);
        a.setCreateTime(LocalDateTime.now());
        mongoTemplate.insert(a);
        LOG.debug("PaymentInstrument Inserted :" + a);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  public void createPaymentRecord() {
    for (String paymentRecord : paymentRecord) {
      try {
        PaymentRecord record = om.readValue(paymentRecord, PaymentRecord.class);
        record.setCreatedOn(Calendar.getInstance().getTime());
        mongoTemplate.insert(record);
        LOG.debug("PaymentRecord Inserted :" + record);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  public void exhaustAccount() {
    // exhaust the account subscriptions
    Account account = mongoTemplate.findById("1", Account.class);
    account.setAvailableSubscriptions(0);
    account.setHiringSubscription(0);
    mongoTemplate.save(account);
  }
  
  public void removeAllTokens() {
    removeAll("token");
  }
  
  public void removeTokens() {
    removeAll("token");
  }
  
  public void createAddMemberTokens() {
    // valid time based token
    Token token = new Token();
    token.setCreatedTime(DateTime.now().getMillis());
    token.setId("1");
    Map<String, Object> paramsMap = new HashMap<String, Object>();
    paramsMap.put(Constants.PARAM_USER_EMAIL, "dax2@surepeople.com");
    token.setParamsMap(paramsMap);
    // token.setTimeUnit(TimeUnit.HOURS);
    // token.setTokenExpiresTime(24);
    token.setTokenType(TokenType.TIME_BASED);
    token.setTokenProcessorType(TokenProcessorType.ADD_MEMBER);
    token.setTokenStatus(TokenStatus.ENABLED);
    token.setTokenId("1");
    mongoTemplate.save(token);
    
    // invalid token time expired
    token.setId(null);
    token.setTokenId("2");
    token.setCreatedTime(DateTime.now().minusDays(2).getMillis());
    mongoTemplate.save(token);
  }
  
  public void addUpdate(Object objectToSave) {
    mongoTemplate.save(objectToSave);
  }
  
  public Object[] addTokens() throws JsonParseException, JsonMappingException, IOException {
    // exhaust the account subscriptions
    Object[] tokens = ArrayUtils.EMPTY_OBJECT_ARRAY;
    for (String token : tokenArray) {
      Token p = om.readValue(token, Token.class);
      p.setCreatedTime(System.currentTimeMillis());
      tokens = ArrayUtils.add(tokens, p);
      try {
        mongoTemplate.insert(p);
        
      } catch (Exception e) {
        e.printStackTrace();
      }
      LOG.debug("Token Inserted :" + p);
    }
    return tokens;
  }
  
  public User getUser(String email) {
    return mongoTemplate.findOne(query(where("email").is(email)), User.class);
  }
  
  public User getUser() {
    return getUser("admin@admin.com");
  }
  
  /**
   * Get an account form the given account id.
   * 
   * @param id
   *          - account id
   * @return the account
   */
  public Account getAccount(String id) {
    return mongoTemplate.findById(id, Account.class);
  }
  
  public Promotion getPromotion(String id) {
    return mongoTemplate.findById(id, Promotion.class);
  }
  
  public PaymentInstrument getPaymentInstrument(String id) {
    return mongoTemplate.findById(id, PaymentInstrument.class);
  }
  
  /**
   * 
   */
  public void removeGrowthTeam() {
    removeAll("growthTeam");
  }
  
  /**
   * 
   */
  public void removeGrowthRequest() {
    removeAll("growthRequest");
  }
  
  public void removeGrowthQuestion() {
    removeAll("growthFeedbackQuestions");
  }
  
  public void removeAllHiringUsers() {
    removeAll("hiringUser");
  }
  
  public void removeAllHiringUsersArchive() {
    removeAll("hiringUserArchive");
  }
  
  /**
   * 
   */
  public void removeGrowthRequestArchived() {
    removeAll("growthRequestArchived");
  }
  
  public void removeSpGoals() {
    removeAll("sPGoal");
  }
  
  public void removeArticles() {
    removeAll("articles");
  }
  
  public void createGrowthRequest() throws JsonParseException, JsonMappingException, IOException {
    String[] dateArray = { "11/30/2015", "12/07/2015", "12/14/2015", "12/21/2015", "12/28/2015" };
    for (String growthRe : growthRequestArray) {
      GrowthRequest p = om.readValue(growthRe, GrowthRequest.class);
      if (p.getId().equalsIgnoreCase("4")) {
        for (String dateArr : dateArray) {
          LocalDate localDate = DateTimeUtil.getLocalDate(dateArr);
          p.getPendingFeedbacks().add(localDate);
          p.getFeedbackIntervals().add(localDate);
        }
      }
      mongoTemplate.insert(p);
      LOG.debug("GrowthRequest Inserted :" + p);
    }
  }
  
  public static <T> T fromJSON(final TypeReference<T> type, final String jsonPacket) {
    T data = null;
    
    try {
      data = new ObjectMapper().readValue(jsonPacket, type);
    } catch (Exception e) {
      // Handle the problem
    }
    return data;
  }
  
  public void createQuestionsAssementQuestions() throws JsonParseException, JsonMappingException,
      IOException {
    for (String growthQestions : growthFeedbackQuestions) {
      GrowthFeedbackQuestions readValue = om.readValue(growthQestions,
          GrowthFeedbackQuestions.class);
      mongoTemplate.insert(readValue);
    }
  }
  
  public void creategrowthArchived() throws JsonParseException, JsonMappingException, IOException {
    for (String growthRe : growthArchivedArray) {
      GrowthRequestArchived p = om.readValue(growthRe, GrowthRequestArchived.class);
      mongoTemplate.insert(p);
      LOG.debug("GrowthRequestArchived Inserted :" + p);
    }
  }
  
  public <T> List<T> getAll(Class<T> className) {
    return mongoTemplate.findAll(className);
  }
  
  public Company getCompany(String companyId) {
    return mongoTemplate.findById(companyId, Company.class);
  }
  
  public void createGoals() throws JsonParseException, JsonMappingException, IOException {
    PersonalityType[] personaltyArr4 = { PersonalityType.Visionary };
    PersonalityType[] personaltyArr1 = { PersonalityType.Ambassador, PersonalityType.Instructor,
        PersonalityType.Pragmatist, PersonalityType.Promoter };
    PersonalityType[] personaltyArr2 = { PersonalityType.Visionary, PersonalityType.Instructor,
        PersonalityType.Navigator };
    PersonalityType[] personaltyArr3 = { PersonalityType.Visionary, PersonalityType.Instructor,
        PersonalityType.Navigator };
    PersonalityType[] personaltyArr5 = { PersonalityType.Encourager };
    
    Map<String, Set<PersonalityType>> personalityMap = new HashMap<String, Set<PersonalityType>>();
    personalityMap.put("1", new HashSet<PersonalityType>(Arrays.asList(personaltyArr1)));
    personalityMap.put("2", new HashSet<PersonalityType>(Arrays.asList(personaltyArr2)));
    personalityMap.put("3", new HashSet<PersonalityType>(Arrays.asList(personaltyArr3)));
    personalityMap.put("4", new HashSet<PersonalityType>(Arrays.asList(personaltyArr4)));
    personalityMap.put("5", new HashSet<PersonalityType>(Arrays.asList(personaltyArr5)));
    personalityMap.put("6", new HashSet<PersonalityType>(Arrays.asList(personaltyArr5)));
    personalityMap.put("7", new HashSet<PersonalityType>(Arrays.asList(personaltyArr5)));
    personalityMap.put("8", new HashSet<PersonalityType>(Arrays.asList(personaltyArr5)));
    for (String goals : spGoals) {
      
      SPGoal p = om.readValue(goals, SPGoal.class);
      p.setPersonalityType(personalityMap.get(p.getId()));
      mongoTemplate.insert(p);
      LOG.debug("SPGoal Inserted :" + p);
    }
  }
  
  /**
   * Creates the personality practice areas.
   * 
   * @throws Exception
   *           - while creating the same
   */
  public void createPersonalityPracticeAreas() throws Exception {
    for (String personalityPracticeArea : personalityPracticeArea) {
      
      PersonalityPracticeArea personalityPractice = om.readValue(personalityPracticeArea,
          PersonalityPracticeArea.class);
      mongoTemplate.insert(personalityPractice);
      LOG.debug("Personality Practice Area Inserted :" + personalityPractice);
    }
  }
  
  public void createFeedbackUsers() throws JsonParseException, JsonMappingException, IOException {
    om.registerSubtypes(PersonalityBeanResponse.class);
    // om.registerSubtypes(User.class);
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    for (String userStr : feedbackUserArray) {
      FeedbackUser user = om.readValue(userStr, FeedbackUser.class);
      user.setCreatedOn(LocalDate.now());
      String encode = passwordEncoder.encode(user.getPassword());
      user.setPassword(encode);
      mongoTemplate.insert(user);
      LOG.debug("Feebdack Inserted :" + user);
    }
  }
  
  public void createArticles() throws JsonParseException, JsonMappingException, IOException {
    for (String article : textArticles) {
      NewsCredTextArticle p = om.readValue(article, NewsCredTextArticle.class);
      mongoTemplate.insert(p);
      LOG.debug("Articles Inserted :" + p);
    }
    for (String article : videoArticles) {
      NewsCredVideoArticle p = om.readValue(article, NewsCredVideoArticle.class);
      mongoTemplate.insert(p);
      LOG.debug("Articles Inserted :" + p);
    }
    
  }
  
  public HiringUser getHiringCandidate(String candidateEmail, String companyId) {
    return mongoTemplate.findOne(
        query(where("email").is(candidateEmail).andOperator(where("companyId").is(companyId))),
        HiringUser.class);
  }
  
  public HiringUserArchive getHiringCandidateArchive(String candidateEmail, String companyId) {
    return mongoTemplate.findOne(
        query(where("email").is(candidateEmail).andOperator(where("companyId").is(companyId))),
        HiringUserArchive.class);
  }
  
  /**
   * Get the member count for employees.
   * 
   * @param companyId
   *          - company id
   * @return the count of employees
   */
  public long getMemberCount(String companyId) {
    return mongoTemplate.count(query(where("companyId").is(companyId)), User.class);
  }
  
  public void createFeedbackRequest() throws JsonParseException, JsonMappingException, IOException {
    for (String feedbackRe : feedbackRequestArray) {
      FeedbackRequest p = om.readValue(feedbackRe, FeedbackRequest.class);
      p.setTokenUrl("http://some.token.url");
      mongoTemplate.insert(p);
      LOG.debug("FeedbackRequest Inserted :" + p);
    }
  }
  
  public void createActionPlan() throws JsonParseException, JsonMappingException, IOException {
    for (String actionPlan : actionPlan) {
      ActionPlan p = om.readValue(actionPlan, ActionPlan.class);
      mongoTemplate.insert(p);
      LOG.debug("ActionPlan Inserted :" + p);
    }
  }
  
  public void createBluePrint() throws JsonParseException, JsonMappingException, IOException {
    for (String bluePrint : blueprint) {
      Blueprint p = om.readValue(bluePrint, Blueprint.class);
      p.setCreatedOn(LocalDateTime.now());
      p.setPublishedOn(LocalDateTime.now());
      mongoTemplate.insert(p);
      LOG.debug("FeedbackRequest Inserted :" + p);
    }
  }
  
  public void removeAllFeedbackRequest() {
    removeAll("feedbackRequest");
  }
  
  public void removeAllActionPlan() {
    removeAll("actionPlan");
  }
  
  public AssessmentProgressTracker getAssessmentProgress(String userId) {
    return mongoTemplate.findOne(query(where("userId").is(userId)), AssessmentProgressTracker.class);
  }
  
  /**
   * 
   */
  public void removeArchiveFeedbackRequests() {
    
    removeAll("feedbackArchiveRequest");
    
  }
  
  /**
   * 
   */
  public void removeAllArchiveFeedbackUser() {
    removeAll("feedbackUserArchive");
    
  }
  
  public UserGroup getUserGroup(String groupName) {
    return mongoTemplate.findOne(query(where("name").is(groupName)), UserGroup.class);
  }
  
  public FeedbackUser getFeedbackUserById(String id) {
    return mongoTemplate.findById(id, FeedbackUser.class);
  }
  
  public void removeAllLogs() {
    removeAllNotificationLogs();
    removeAllActivityLogs();
  }
  
  public void removeAllNotificationLogs() {
    removeAll("notificationLogMessage");
  }
  
  public void removeAllActivityLogs() {
    removeAll("activityLogMessage");
  }
  
  public void createSingUser(String email, String companyId) throws JsonParseException,
      JsonMappingException, IOException {
    
    String[] dobArray = { "11/30/1994", "12/07/1985", "12/14/1965", "12/21/1955", "12/28/1945",
        "12/28/1935", "12/28/1975", "12/28/1967", "12/28/1955", "05/19/2013", "05/19/2038" };
    om.registerSubtypes(PersonalityBeanResponse.class, UserGoalProgress.class, SPGoal.class,
        UserArticleProgress.class);
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    int index = 0;
    String userStr = usersArray[2];
    User user = om.readValue(userStr, User.class);
    user.setEmail(email);
    user.setId(null);
    user.setCompanyId(companyId);
    String dob = dobArray[index++];
    user.setDob(DateTimeUtil.getLocalDate(dob));
    String encode = passwordEncoder.encode(user.getPassword());
    user.setPassword(encode);
    mongoTemplate.insert(user);
    LOG.debug("User Inserted :" + user);
    
  }
  
  private String[] externalUsers = { "{\"id\":\"1\",\"firstName\": \"Indivial\",\"lastName\": \"User\",\"title\": \"\",\"email\": \"individual@surepeople.com\",\"password\": \"password\",\"address\": {\"country\": \"USA\",\"addressLine1\": \"12313\",\"city\": \"123123\",\"state\": \"AR\",\"zipCode\": \"12312\"},\"phoneNumber\": \"1231231\",\"userStatus\": \"VALID\",\"roles\": [\"User\",\"IndividualAccountAdministrator\"],\"gender\": \"M\",\"analysis\": {\"accuracy\": \"0\",\"processing\": {\"External\": \"60\",\"Internal\": \"40\",\"Cognitive\": \"40\",\"Spontaneous\": \"50\",\"Intuitive\": \"50\",\"Orderly\": \"50\",\"Concrete\": \"50\",\"Affective\": \"60\"},\"conflictManagement\": {\"Compromise\": \"17\",\"Accommodate\": \"20\",\"Compete\": \"20\",\"Collaborate\": \"20\",\"Avoid\": \"23\"},\"learningStyle\": {\"Analytical\": \"60\",\"Global\": \"40\"},\"motivationWhy\": {\"Affiliation\": \"70\",\"Compliance\": \"10\",\"AttainmentOfGoals\": \"70\",\"Power\": \"90\",\"RecognitionForEffort\": \"30\",\"Activity\": \"30\"},\"motivationHow\": {\"Freedom\": \"75\",\"AffirmedByOthers\": \"88\",\"ExchangeOfIdeas\": \"58\",\"TaskCompletion\": \"29\",\"Consistency\": \"25\",\"PrefersProcess\": \"71\",\"SelfAffirmed\": \"13\",\"ReceiveDirection\": \"42\"},\"motivationWhat\": {\"Accomplishment\": \"50\",\"Hygiene\": \"50\"},\"personality\": {\"UnderPressure\": {\"segmentScore\": 5334,\"segmentGraph\": [\"0.71\",\"0.43\",\"0.43\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Visionary\"},\"Primary\": {\"segmentScore\": 4644,\"segmentGraph\": [\"0.57\",\"0.86\",\"0.57\",\"0.57\"],\"isValid\": true,\"personalityType\": \"Promoter\"},\"PerceivedByOthers\": {\"segmentScore\": 5543,\"segmentGraph\": [\"0.71\",\"0.71\",\"0.57\",\"0.43\"],\"isValid\": true,\"personalityType\": \"Instructor\"}},\"fundamentalNeeds\": {\"Security\": \"33\",\"Control\": \"33\",\"Significance\": \"35\"},\"decisionMaking\": {\"Rapid\": \"58\",\"Careful\": \"42\",\"Inward\": \"33\",\"Outward\": \"67\"}}}" };
  
  /**
   * Create a user.
   * 
   * @throws JsonParseException
   *           - exception
   * @throws JsonMappingException
   *           - exception
   * @throws IOException
   *           - exception
   */
  public void createExternalUsers() throws JsonParseException, JsonMappingException, IOException {
    om.registerSubtypes(PersonalityBeanResponse.class, UserGoalProgress.class, SPGoal.class,
        UserArticleProgress.class, User.class);
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    for (String userStr : externalUsers) {
      ExternalUser user = om.readValue(userStr, ExternalUser.class);
      String encode = passwordEncoder.encode(user.getPassword());
      user.setPassword(encode);
      mongoTemplate.insert(user);
      LOG.debug("Product Inserted :" + user);
    }
  }
  
  public void removeExernalUsers() {
    removeAll("externalUser");
  }
  
  public void remove(Object object) {
    mongoTemplate.remove(object);
  }
  
  public void removeAllUserGoals() {
    removeAll("userGoal");
  }
  
  /**
   * @param string
   */
  public void removeUser(User user) {
    mongoTemplate.remove(user);
    
  }
  
  public static void createTheme() throws JsonProcessingException {
    Company company = new Company();
    
    CompanyTheme companyTheme = new CompanyTheme();
    company.setCompanyTheme(companyTheme);
    companyTheme.getStylesMap().put("sp-main-nav-color", "#ffa200");
    companyTheme.getStylesMap().put("sp-sub-nav-background-color", "#ffa200");
    companyTheme.getStylesMap().put("sub-nav-link-color", "#000");
    
    companyTheme.getStylesMap().put("sp-link-color", "#ff4000");
    companyTheme.getStylesMap().put("sp-button-color", "ff4000");
    companyTheme.getStylesMap().put("sp-link-hover-color", "#000");
    companyTheme.getStylesMap().put("sp-icon-color", "#ff4000");
    
    companyTheme.getStylesMap().put("sp-panel-accentbar-color", "#ee5a1f");
    
    companyTheme.getStylesMap().put("sp-header-accentbar-color", "#f63");
    companyTheme.getStylesMap().put("sp-signin-accentbar-color", "#f63");
    
    companyTheme.getStylesMap().put("sp-header-title-color", "#f63");
    
    companyTheme.getStylesMap().put("sp-footer-background-color", "#f63");
    companyTheme.getStylesMap().put("sp-footer-navigation-text-color", "#ff");
    companyTheme.getStylesMap().put("sp-footer-navigation-hover-color", "#000");
    
    ObjectMapper om = new ObjectMapper();
    System.out.println(om.writeValueAsString(companyTheme));
    
    CompanyTheme defaultCompTheme;
    try {
      defaultCompTheme = sf(companyTheme, om);
      System.out.println(defaultCompTheme);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
  
  /**
   * @param companyTheme
   * @param om
   * @return
   * @throws IOException
   * @throws JsonParseException
   * @throws JsonMappingException
   * @throws JsonProcessingException
   */
  @JsonCreator
  private static CompanyTheme sf(CompanyTheme companyTheme, ObjectMapper om) throws IOException,
      JsonParseException, JsonMappingException, JsonProcessingException {
    CompanyTheme defaultCompTheme = om.readValue(om.writeValueAsString(companyTheme),
        CompanyTheme.class);
    return defaultCompTheme;
  }
  
  public MongoTemplate getMongoTemplate() {
    return mongoTemplate;
  }

  public void createDefaultCompany() {
    String company = "{\"id\" :\"default\", \"name\" : \"SPAppDefault\", \"address\" : { \"country\" : \"USA\", \"addressLine1\" : \"12\", \"addressLine2\" : \"\", \"city\" : \"Chicago\", \"state\" : \"AZ\", \"zipCode\" : \"12121\" }, \"industry\" : \"1\", \"numberOfEmployees\" : 0, \"phoneNumber\" : \"90003000032\", \"accountId\" : \"54c3360cd4c6a5978c84d05c\", \"companyTheme\" :{\"stylesMap\":{\"spMainNavBackgroundColor\" : \"#000\", \"spIconColor\" : \"#f63\", \"spHeaderAccentbarColor\" : \"#f63\", \"spSigninAccentbarColor\" : \"#f63\", \"spLinkColor\" : \"#f63\", \"spHeaderTitleColor\" : \"#f63\", \"spFooterBackgroundColor\" : \"#666\", \"spButtonColor\" : \"#f63\", \"spButtonHoverColor\" : \"#cc3300\", \"spLinkHoverColor\" : \"#000\", \"spMainNavColor\" : \"#ffa200\", \"spPanelAccentbarColor\" : \"#cdcccb\", \"spSubNavLinkColor\" : \"#000\", \"spSubNavBackgroundColor\" : \"#ffa200\", \"spFooterNavigationTextColor\" : \"#fff\", \"spFooterNavigationHoverColor\" : \"#000\" },\"cssUrl\":\"{0}/theme/surepeople/defaultTheme.css\"},\"companyThemeActive\":false,\"restrictRelationShipAdvisor\":false,\"blockAllMembers\":false}";
    Company c;
    try {
      c = om.readValue(company, Company.class);
      mongoTemplate.insert(c);
      LOG.debug("Company Inserted :" + c);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
}
