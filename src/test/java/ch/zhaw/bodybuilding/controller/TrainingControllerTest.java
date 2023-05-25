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

import ch.zhaw.bodybuilding.model.Training;
import ch.zhaw.bodybuilding.repository.TrainingRepository;
import ch.zhaw.bodybuilding.security.TestSecurityConfig;

@SpringBootTest
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class TrainingControllerTest {
    
    @Autowired
    private MockMvc mvc;

    @Autowired
    TrainingRepository trainingRepository;

    private static final String TEST_UBUNG = "chestpress";
    private static final String TEST_SATZ = "3";
    private static final String TEST_WIEDERHOLUNG = "3";
    private static final String TEST_FOKUS = "CHEST";
    private static ObjectMapper mapper = new ObjectMapper();
    private static ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

    @Test
    @Order(1)
    @WithMockUser
    public void testCreateTraining() throws Exception {
        // create a test training and convert to Json
        Training training = new Training(TEST_UBUNG, TEST_SATZ, TEST_WIEDERHOLUNG, TEST_FOKUS);
        var jsonBody = ow.writeValueAsString(training);

        // POST Json to service with authorization header
        mvc.perform(post("/api/training")
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
    public void testGetTraining() throws Exception {
       
        // GET training by uebung 
        var result = mvc.perform(get("/api/training/uebung/"+TEST_UBUNG)
        .contentType(MediaType.TEXT_PLAIN))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();
        var json = result.getResponse().getContentAsString();

        Training training = trainingRepository.findByUbung(TEST_UBUNG);
        String trainingId = training.getId();       

        // GET training by id
        mvc.perform(get("/api/training/"+trainingId))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

        // GET all traingings function test
        mvc.perform(get("/api/training")
        .contentType(MediaType.TEXT_PLAIN))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

        // GET all Trainings fokus test
        mvc.perform(get("/api/training/fokus")
        .param("fokus", TEST_FOKUS)
        .contentType(MediaType.TEXT_PLAIN)
        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

        //assertions
        assertFalse(json.isEmpty());
        assertTrue(json.contains(TEST_UBUNG));
        assertTrue(json.contains(TEST_SATZ));
        assertTrue(json.contains(TEST_WIEDERHOLUNG));
        assertTrue(json.contains(TEST_FOKUS));
    }

    @Test
    @Order(3)
    @WithMockUser
    public void testDeleteTraining() throws Exception {
        // DELETE training 
        var result = mvc.perform(delete("/api/training/delete")
        .param("uebung", TEST_UBUNG)
        .contentType(MediaType.TEXT_PLAIN)
        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

        // DELETED
        var json = result.getResponse().getContentAsString();
        assertTrue(json.contains("DELETED"));

        // Training should not exist
        Training training = trainingRepository.findByUbung(TEST_UBUNG);
        assertNull(training);
    }    

    
}
