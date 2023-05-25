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

import ch.zhaw.bodybuilding.model.Forum;
import ch.zhaw.bodybuilding.repository.ForumRepository;
import ch.zhaw.bodybuilding.security.TestSecurityConfig;

@SpringBootTest
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class ForumControllerTest {
    
    @Autowired
    private MockMvc mvc;

    @Autowired
    ForumRepository forumRepository;

    private static final String TEST_CREATOR = "Nikola";

    private static ObjectMapper mapper = new ObjectMapper();
    private static ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

    @Test
    @Order(1)
    @WithMockUser
    public void testCreateUser() throws Exception {
        // create a test Forum and convert to Json
        Forum forum = new Forum(TEST_CREATOR);
        var jsonBody = ow.writeValueAsString(forum);

        // POST Json to service with authorization header
        mvc.perform(post("/api/forum")
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
       
        // GET Forum by creator 
        var result = mvc.perform(get("/all/forum/creator/"+TEST_CREATOR)
        .param("creator", TEST_CREATOR)
        .contentType(MediaType.TEXT_PLAIN))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();
        var json = result.getResponse().getContentAsString();

        Forum forum = forumRepository.findFirstByCreator(TEST_CREATOR);
        String forumId = forum.getId();       

        // GET Forum by id
        mvc.perform(get("/all/forum/"+forumId))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

        // GET all Forum function test
        mvc.perform(get("/all/forum")
        .contentType(MediaType.TEXT_PLAIN))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();


        //assertions
        assertFalse(json.isEmpty());
        assertTrue(json.contains(TEST_CREATOR));
    }

    @Test
    @Order(3)
    @WithMockUser
    public void testDeleteUser() throws Exception {
        // DELETE Forum
        var result = mvc.perform(delete("/api/forum/delete")
        .param("creator", TEST_CREATOR)
        .contentType(MediaType.TEXT_PLAIN)
        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

        // DELETED
        var json = result.getResponse().getContentAsString();
        assertTrue(json.contains("DELETED"));

        // Forum should not exist
        Forum forum = forumRepository.findFirstByCreator(TEST_CREATOR);
        assertNull(forum);
    }    

    
}
