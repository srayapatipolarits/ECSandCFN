package com.sp.web.alexa;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.amazon.speech.json.SpeechletResponseEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.json.JSONObject;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class AlexaRequestControllerTest extends SPTestLoggedInBase {
  
  @Test
  public void test() throws Exception {
    ObjectMapper om = new ObjectMapper();
    
    String request = "{\r\n  \"session\": {\r\n    \"sessionId\": \"SessionId.916bf95d-0b92-4389-a176-f03f7fea3027\",\r\n    \"application\": {\r\n      \"applicationId\": \"amzn1.ask.skill.be60c2ff-e64d-4ce2-9068-4b27536bc235\"\r\n    },\r\n    \"attributes\": {},\r\n    \"user\": {\r\n      \"userId\": \"amzn1.ask.account.AG7VWPVTI3FN2F5SRECXGH47U5QJAIRULA37DWTPFTP4DUFCVR7T2NFOHXOJOZZ2UIKJXPDHXPNH6VBM26CTF5ENZ37HIYNDJRPAMPOQMOJQJVWXJ7DJ7FLDOAWUGIXZKZZG34FFDZHP5KTG43VGH7MYLTAY5RF5PKL4UJOM7AL2IIHEEFJ25NRN5RHELII5V3MXDE4IZMCNCAA\",\r\n      \"accessToken\": \"2cfbc087-a30e-44dd-b33b-1464ee723243\"\r\n    },\r\n    \"new\": true\r\n  },\r\n  \"request\": {\r\n    \"type\": \"IntentRequest\",\r\n    \"requestId\": \"EdwRequestId.270ac9de-8b69-4296-a132-3faf51d5eac5\",\r\n    \"locale\": \"en-US\",\r\n    \"timestamp\": \"2017-01-07T11:37:35Z\",\r\n    \"intent\": {\r\n      \"name\": \"coachme\",\r\n      \"slots\": {\r\n        \"Name\": {\r\n          \"name\": \"Name\",\r\n          \"value\": \"Dax Abraham\"\r\n        }\r\n      }\r\n    }\r\n  },\r\n  \"version\": \"1.0\"\r\n}";
    
    MvcResult result = this.mockMvc
        .perform(
            post("/alexa/request").requestAttr("SPEECH_REQUEST", request.getBytes())
                .content(request).contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(content().contentType("application/json")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    String response = result.getResponse().getContentAsString();
    JSONObject jsonObject = new JSONObject(response);
    String  userId = (String) ((org.json.JSONObject)jsonObject.get("sessionAttributes")).get("userid");
    String yesINtentRequest = "{\r\n  \"session\": {\r\n    \"sessionId\": \"SessionId.06304142-c415-40f1-aa55-2e7480ba893c\",\r\n    \"application\": {\r\n      \"applicationId\": \"amzn1.ask.skill.be60c2ff-e64d-4ce2-9068-4b27536bc235\"\r\n    },\r\n    \"attributes\": {\r\n      \"actionType\": \"CoachMe\",\r\n      \"nextStep\": \"UnderPressure\",\r\n      \"userid\": \""+userId+"\"\r\n    },\r\n    \"user\": {\r\n      \"userId\": \"amzn1.ask.account.AG7VWPVTI3FN2F5SRECXGH47U5QJAIRULA37DWTPFTP4DUFCVR7T2NFOHXOJOZZ2UIKJXPDHXPNH6VBM26CTF5ENZ37HIYNDJRPAMPOQMOJQJVWXJ7DJ7FLDOAWUGIXZKZZG34FFDZHP5KTG43VGH7MYLTAY5RF5PKL4UJOM7AL2IIHEEFJ25NRN5RHELII5V3MXDE4IZMCNCAA\",\r\n      \"accessToken\": \"2cfbc087-a30e-44dd-b33b-1464ee723243\"\r\n    },\r\n    \"new\": false\r\n  },\r\n  \"request\": {\r\n    \"type\": \"IntentRequest\",\r\n    \"requestId\": \"EdwRequestId.10fb183a-5761-44f4-aa6b-546048c3830d\",\r\n    \"locale\": \"en-US\",\r\n    \"timestamp\": \"2017-01-07T12:28:44Z\",\r\n    \"intent\": {\r\n      \"name\": \"yes\",\r\n      \"slots\": {}\r\n    }\r\n  },\r\n  \"version\": \"1.0\"\r\n}";
    result = this.mockMvc
        .perform(
            post("/alexa/request").requestAttr("SPEECH_REQUEST", yesINtentRequest.getBytes())
                .content(request).contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(content().contentType("application/json")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    

   String yesINtent =  "{\r\n  \"session\": {\r\n    \"sessionId\": \"SessionId.06304142-c415-40f1-aa55-2e7480ba893c\",\r\n    \"application\": {\r\n      \"applicationId\": \"amzn1.ask.skill.be60c2ff-e64d-4ce2-9068-4b27536bc235\"\r\n    },\r\n    \"attributes\": {\r\n      \"actionType\": \"CoachMe\",\r\n      \"nextStep\": \"GetRelationShip\",\r\n      \"userid\": \""+userId+"\"\r\n    },\r\n    \"user\": {\r\n      \"userId\": \"amzn1.ask.account.AG7VWPVTI3FN2F5SRECXGH47U5QJAIRULA37DWTPFTP4DUFCVR7T2NFOHXOJOZZ2UIKJXPDHXPNH6VBM26CTF5ENZ37HIYNDJRPAMPOQMOJQJVWXJ7DJ7FLDOAWUGIXZKZZG34FFDZHP5KTG43VGH7MYLTAY5RF5PKL4UJOM7AL2IIHEEFJ25NRN5RHELII5V3MXDE4IZMCNCAA\",\r\n      \"accessToken\": \"2cfbc087-a30e-44dd-b33b-1464ee723243\"\r\n    },\r\n    \"new\": false\r\n  },\r\n  \"request\": {\r\n    \"type\": \"IntentRequest\",\r\n    \"requestId\": \"EdwRequestId.10fb183a-5761-44f4-aa6b-546048c3830d\",\r\n    \"locale\": \"en-US\",\r\n    \"timestamp\": \"2017-01-07T12:28:44Z\",\r\n    \"intent\": {\r\n      \"name\": \"yes\",\r\n      \"slots\": {}\r\n    }\r\n  },\r\n  \"version\": \"1.0\"\r\n}";
    
      result = this.mockMvc
      .perform(
          post("/alexa/request").requestAttr("SPEECH_REQUEST", yesINtent.getBytes())
              .content(request).contentType(MediaType.APPLICATION_JSON).session(session))
      .andExpect(content().contentType("application/json")).andReturn();
  log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  }
}
