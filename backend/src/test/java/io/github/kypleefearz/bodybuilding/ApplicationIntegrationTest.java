package io.github.kypleefearz.bodybuilding;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.jayway.jsonpath.JsonPath;

import io.github.kypleefearz.bodybuilding.forum.ForumThreadRepository;
import io.github.kypleefearz.bodybuilding.user.UserProfileRepository;
import io.github.kypleefearz.bodybuilding.workout.WorkoutRepository;

/**
 * End-to-end tests against a real MongoDB started by Testcontainers. Skipped automatically when Docker is missing.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class ApplicationIntegrationTest {

    private static final String NS = "https://bodybuilding.app/";

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @Autowired
    private MockMvc mvc;
    @Autowired
    private WorkoutRepository workoutRepository;
    @Autowired
    private ForumThreadRepository threadRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;

    @BeforeEach
    void cleanDatabase() {
        workoutRepository.deleteAll();
        threadRepository.deleteAll();
        userProfileRepository.deleteAll();
    }

    @Test
    void workoutPlanLifecycle() throws Exception {
        String body = mvc.perform(post("/api/workouts").with(user("auth0|admin", "admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"exercise\": \"Deadlift\", \"sets\": 5, \"reps\": 5, \"muscleGroup\": \"BACK\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String workoutId = JsonPath.read(body, "$.id");

        // Adding twice is idempotent
        mvc.perform(put("/api/me/plan/" + workoutId).with(user("auth0|alice", "alice")))
                .andExpect(status().isOk());
        mvc.perform(put("/api/me/plan/" + workoutId).with(user("auth0|alice", "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].exercise").value("Deadlift"));

        mvc.perform(get("/api/me").with(user("auth0|alice", "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.savedWorkoutIds[0]").value(workoutId));

        // Deleting a workout removes it from every plan
        mvc.perform(delete("/api/workouts/" + workoutId).with(user("auth0|admin", "admin", "admin")))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/me/plan").with(user("auth0|alice", "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void forumPermissions() throws Exception {
        String body = mvc.perform(post("/api/threads").with(user("auth0|alice", "alice"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Best split?\", \"content\": \"PPL or upper/lower?\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String threadId = JsonPath.read(body, "$.id");
        String postId = JsonPath.read(body, "$.posts[0].id");

        mvc.perform(put("/api/threads/" + threadId + "/posts/" + postId).with(user("auth0|bob", "bob"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"content\": \"Hacked\"}"))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/threads/" + threadId + "/posts").with(user("auth0|bob", "bob"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"content\": \"Upper/lower!\"}"))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/threads/" + threadId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts.length()").value(2))
                .andExpect(jsonPath("$.posts[0].content").value("PPL or upper/lower?"));

        mvc.perform(delete("/api/threads/" + threadId).with(user("auth0|bob", "bob")))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/threads/" + threadId).with(user("auth0|admin", "admin", "admin")))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/threads/" + threadId)).andExpect(status().isNotFound());
    }

    @Test
    void anonymousUsersCanReadButNotWrite() throws Exception {
        mvc.perform(get("/api/threads")).andExpect(status().isOk());
        mvc.perform(get("/api/workouts")).andExpect(status().isOk());
        mvc.perform(get("/api/config")).andExpect(status().isOk());
        mvc.perform(get("/api/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/users").with(user("auth0|alice", "alice"))).andExpect(status().isForbidden());
    }

    private static RequestPostProcessor user(String id, String name, String... roles) {
        GrantedAuthority[] authorities = Arrays.stream(roles)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toArray(GrantedAuthority[]::new);
        return jwt()
                .jwt(token -> token.subject(id).claim(NS + "name", name).claim(NS + "email", name + "@example.com"))
                .authorities(authorities);
    }
}
