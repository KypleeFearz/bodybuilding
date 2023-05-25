package ch.zhaw.bodybuilding.controller;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import ch.zhaw.bodybuilding.model.Training;
import ch.zhaw.bodybuilding.model.User;
import ch.zhaw.bodybuilding.model.UserChangeDTO;
import ch.zhaw.bodybuilding.repository.TrainingRepository;
import ch.zhaw.bodybuilding.repository.UserRepository;
import ch.zhaw.bodybuilding.security.TestSecurityConfig;
import ch.zhaw.bodybuilding.service.UserService;

@SpringBootTest
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class UserServiceControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    UserRepository userRepository;
    @Autowired
    TrainingRepository trainingRepository;
    @Autowired
    UserService userService;

    private static final String TEST_EMAIL = "test@csgo.com";
    private static final String TEST_STRING = "TEST-abc...xyz";
    private static ObjectMapper mapper = new ObjectMapper();
    private static ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

    @Test
    @Order(1)
    @WithMockUser
    public void testCreateUserTraining() throws Exception {
        Training training = new Training("Chestpress", "3", "3", "CHEST");
        var jsonBody = ow.writeValueAsString(training);

        // POST Json to service with authorization header
        mvc.perform(post("/api/training")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        // create a test user and convert to Json
        User user = new User(TEST_EMAIL, TEST_STRING);
        var jsonBody2 = ow.writeValueAsString(user);

        // POST Json to service with authorization header
        mvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody2)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    @Order(2)
    @WithMockUser
    public void testBuyTraining() throws Exception {
        Training training = trainingRepository.findByUbung("Chestpress");

        // create a test User to buy a training and convert to Json
        UserChangeDTO userChangeDTO = new UserChangeDTO();
        userChangeDTO.setTrainingId(training.getId());
        userChangeDTO.setUserName(TEST_STRING);
        var jsonBody = ow.writeValueAsString(userChangeDTO);

        // POST Json to service with authorization header
        mvc.perform(put("/api/service/me/buyTraining")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @Order(3)
    @WithMockUser
    public void testDeleteUserTraining() throws Exception {
        // DELETE user and training
        var result = mvc.perform(delete("/api/training/delete")
        .param("uebung", "Chestpress")
        .contentType(MediaType.TEXT_PLAIN)
        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

        // DELETED
        var json = result.getResponse().getContentAsString();
        assertTrue(json.contains("DELETED"));

        // Training should not exist
        Training training = trainingRepository.findByUbung("Chestpress");
        assertNull(training);

        // DELETE user by email 
        var result2 = mvc.perform(delete("/api/user/delete")
        .param("userName", TEST_STRING)
        .contentType(MediaType.TEXT_PLAIN)
        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

        // DELETED
        var json2 = result2.getResponse().getContentAsString();
        assertTrue(json2.contains("DELETED"));

        // User should not exist
        User user = userRepository.findFirstByEmail(TEST_EMAIL);
        assertNull(user);
    }    

}
