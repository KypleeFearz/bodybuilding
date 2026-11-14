package io.github.kypleefearz.bodybuilding.forum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import io.github.kypleefearz.bodybuilding.auth.CurrentUser;
import io.github.kypleefearz.bodybuilding.common.ForbiddenException;
import io.github.kypleefearz.bodybuilding.common.NotFoundException;
import io.github.kypleefearz.bodybuilding.config.SecurityConfig;
import io.github.kypleefearz.bodybuilding.config.WebConfig;

@WebMvcTest(ForumController.class)
@Import({ SecurityConfig.class, WebConfig.class })
class ForumControllerTest {

    private static final String NS = "https://bodybuilding.app/";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ForumService forumService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void threadsArePublic() throws Exception {
        ForumThread thread = new ForumThread("Best split?", new Post("auth0|alice", "alice", "PPL?"));
        when(forumService.listThreads()).thenReturn(List.of(thread));

        mvc.perform(get("/api/threads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Best split?"))
                .andExpect(jsonPath("$[0].authorName").value("alice"))
                .andExpect(jsonPath("$[0].replies").value(0));
    }

    @Test
    void creatingThreadRequiresAuthentication() throws Exception {
        mvc.perform(post("/api/threads").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Hi\", \"content\": \"Hello\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authorIsTakenFromTokenNotFromRequest() throws Exception {
        when(forumService.createThread(any(), eq("Hi"), eq("Hello")))
                .thenReturn(new ForumThread("Hi", new Post("auth0|alice", "alice", "Hello")));

        mvc.perform(post("/api/threads")
                        .with(jwt().jwt(token -> token.subject("auth0|alice").claim(NS + "name", "alice")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Hi\", \"content\": \"Hello\", \"authorId\": \"auth0|mallory\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.posts[0].authorName").value("alice"));

        ArgumentCaptor<CurrentUser> user = ArgumentCaptor.forClass(CurrentUser.class);
        verify(forumService).createThread(user.capture(), eq("Hi"), eq("Hello"));
        assertThat(user.getValue().id()).isEqualTo("auth0|alice");
        assertThat(user.getValue().username()).isEqualTo("alice");
        assertThat(user.getValue().admin()).isFalse();
    }

    @Test
    void blankReplyIsRejected() throws Exception {
        mvc.perform(post("/api/threads/t1/posts").with(jwt())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"content\": \"  \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.content").exists());
    }

    @Test
    void editingSomeoneElsesPostIsForbidden() throws Exception {
        when(forumService.editPost(any(), eq("t1"), eq("p1"), eq("Edited")))
                .thenThrow(new ForbiddenException("Only the author can edit this post"));

        mvc.perform(put("/api/threads/t1/posts/p1").with(jwt())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"content\": \"Edited\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Forbidden"));
    }

    @Test
    void unknownThreadIsNotFound() throws Exception {
        when(forumService.getThread("missing")).thenThrow(new NotFoundException("Thread", "missing"));

        mvc.perform(get("/api/threads/missing")).andExpect(status().isNotFound());
    }
}
