package ch.zhaw.bodybuilding.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import ch.zhaw.bodybuilding.model.Forum;
import ch.zhaw.bodybuilding.model.ForumChangeDTO;
import ch.zhaw.bodybuilding.repository.ForumRepository;
import ch.zhaw.bodybuilding.security.TestSecurityConfig;
import ch.zhaw.bodybuilding.service.ForumService;

@SpringBootTest
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class ForumServiceControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    ForumRepository forumRepository;

    @Autowired
    ForumService forumService;

    private static final String TEST_CREATOR = "n.milosavljevic";
    private static final String TEST_TEXT = "Hallo";

    private static ObjectMapper mapper = new ObjectMapper();
    private static ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

    @Test
    @Order(1)
    @WithMockUser
    public void testCreateBeitrag() throws Exception {
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

        // create a test user and convert to Json
        ForumChangeDTO forumChangeDTO = new ForumChangeDTO();
        forumChangeDTO.setCreator(TEST_CREATOR);
        forumChangeDTO.setText(TEST_TEXT);
        forumChangeDTO.setBeitragCreator(null);
        var jsonBody2 = ow.writeValueAsString(forumChangeDTO);

        // POST Json to service with authorization header
        mvc.perform(put("/api/service/createBeitrag")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody2)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @Order(2)
    @WithMockUser
    public void testEditBeitrag() throws Exception {
        ForumChangeDTO forumChangeDTO = new ForumChangeDTO();
        forumChangeDTO.setCreator(TEST_CREATOR);
        forumChangeDTO.setText(TEST_TEXT);
        forumChangeDTO.setBeitragCreator(null);
        var jsonBody = ow.writeValueAsString(forumChangeDTO);

        // GET user by email
        var result = mvc.perform(put("/api/service/editBeitrag")
                .param("newText", "CSGO")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var json = result.getResponse().getContentAsString();

        Forum forum = forumRepository.findFirstByCreator(TEST_CREATOR);

        // assertions
        assertFalse(json.isEmpty());
        assertTrue(json.contains(TEST_CREATOR));
        if (forum != null) {
            assertTrue(forum.getBeitraege().length > 0);
        }
    }

    @Test
    @Order(3)
    @WithMockUser
    public void testDeleteBeitrag() throws Exception {
        ForumChangeDTO forumChangeDTO = new ForumChangeDTO();
        forumChangeDTO.setCreator(TEST_CREATOR);
        forumChangeDTO.setText(TEST_TEXT);
        forumChangeDTO.setBeitragCreator(TEST_CREATOR);
        var jsonBody = ow.writeValueAsString(forumChangeDTO);

        // DELETE user by email
       mvc.perform(put("/api/service/deleteBeitrag")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // User should not exist
        Forum forum = forumRepository.findFirstByCreator(TEST_CREATOR);
        if(forum != null){
            assertTrue(forum.getBeitraege().length == 0);
        }
         mvc.perform(delete("/api/forum/delete")
        .param("creator", TEST_CREATOR)
        .contentType(MediaType.TEXT_PLAIN)
        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();
    }

}
