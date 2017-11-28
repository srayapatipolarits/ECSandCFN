/**
 * 
 */
package com.sp.web.controller.pulse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @author pradeepruhil
 *
 */
public class SseControllerTest extends SPTestLoggedInBase {
  
  @Test
  public void getMessage() throws Exception {
    
    MvcResult result = mockMvc.perform(get("/sse/push"))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    
    
  }
  
}
