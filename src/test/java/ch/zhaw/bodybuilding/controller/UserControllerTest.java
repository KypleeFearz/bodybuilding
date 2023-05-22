package ch.zhaw.bodybuilding.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ch.zhaw.bodybuilding.model.User;
import ch.zhaw.bodybuilding.repository.UserRepository;
import ch.zhaw.bodybuilding.security.TestSecurityConfig;

@SpringBootTest
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class UserControllerTest {
    
    @Autowired
    private MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    private static final String TEST_EMAIL = "test.abc.xyz@gmail.com";
    private static final String TEST_STRING = "TEST-abc...xyz";
    private static ObjectMapper mapper = new ObjectMapper();
    private static ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

    @Test
    @Order(1)
    @WithMockUser
    public void testCreateUser() throws Exception {
        // create a test user and convert to Json
        User user = new User(TEST_EMAIL, TEST_STRING);
        var jsonBody = ow.writeValueAsString(user);

        // POST Json to service with authorization header
        mvc.perform(post("/api/user")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBody)
        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
        .andDo(print())
        .andExpect(status().isCreated())
        .andReturn();
    }

    @Test
    @Order(2)
    @WithMockUser
    public void testGetUser() throws Exception {
        // GET user by email 
        var result = mvc.perform(get("/api/user/name/"+TEST_STRING)
        .contentType(MediaType.TEXT_PLAIN))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();
        var json = result.getResponse().getContentAsString();

        User user = userRepository.findFirstByEmail(TEST_EMAIL);
        String userId = user.getId();

        // GET user by id
        mvc.perform(get("/api/user/"+userId)
        .contentType(MediaType.TEXT_PLAIN))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

        // GET all users function test
        mvc.perform(get("/api/user")
        .contentType(MediaType.TEXT_PLAIN))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

        // GET all users function test
        mvc.perform(get("/api/user/name/"+TEST_STRING)
        .contentType(MediaType.TEXT_PLAIN))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

        //assertions
        assertFalse(json.isEmpty());
        assertTrue(json.contains(TEST_STRING));
        assertTrue(json.contains(TEST_EMAIL));
    }

    @Test
    @Order(3)
    @WithMockUser
    public void testDeleteUser() throws Exception {
        // DELETE user by email 
        var result = mvc.perform(delete("/api/user/delete")
        .param("userName", TEST_STRING)
        .contentType(MediaType.TEXT_PLAIN)
        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

        // DELETED
        var json = result.getResponse().getContentAsString();
        assertTrue(json.contains("DELETED"));

        // User should not exist
        User user = userRepository.findFirstByEmail(TEST_EMAIL);
        assertNull(user);
    }    

    
}
