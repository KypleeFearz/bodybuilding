package io.github.kypleefearz.bodybuilding.forum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.kypleefearz.bodybuilding.auth.CurrentUser;
import io.github.kypleefearz.bodybuilding.common.ForbiddenException;
import io.github.kypleefearz.bodybuilding.common.NotFoundException;

@ExtendWith(MockitoExtension.class)
class ForumServiceTest {

    private static final CurrentUser ALICE = new CurrentUser("auth0|alice", "alice", "alice@example.com", false);
    private static final CurrentUser BOB = new CurrentUser("auth0|bob", "bob", "bob@example.com", false);
    private static final CurrentUser ADMIN = new CurrentUser("auth0|admin", "admin", null, true);

    @Mock
    private ForumThreadRepository threadRepository;

    @InjectMocks
    private ForumService forumService;

    private Post openingPost;
    private ForumThread thread;

    @BeforeEach
    void setUp() {
        openingPost = new Post(ALICE.id(), ALICE.username(), "Upper/lower or PPL?");
        thread = new ForumThread("Best split?", openingPost);
    }

    @Test
    void createThreadUsesAuthorFromTokenAndTrimsInput() {
        when(threadRepository.save(any(ForumThread.class))).thenAnswer(returnsFirstArg());

        ForumThread created = forumService.createThread(ALICE, "  Best split?  ", "  Hello  ");

        assertThat(created.getTitle()).isEqualTo("Best split?");
        assertThat(created.getAuthorId()).isEqualTo(ALICE.id());
        assertThat(created.getPosts()).hasSize(1);
        assertThat(created.getPosts().get(0).getContent()).isEqualTo("Hello");
    }

    @Test
    void addPostAppendsReplyAndUpdatesActivity() {
        when(threadRepository.findById("t1")).thenReturn(Optional.of(thread));

        Post reply = forumService.addPost(BOB, "t1", "Upper/lower for sure");

        assertThat(thread.getPosts()).hasSize(2).last().isSameAs(reply);
        assertThat(thread.getLastActivityAt()).isEqualTo(reply.getCreatedAt());
        verify(threadRepository).save(thread);
    }

    @Test
    void authorCanEditOwnPost() {
        when(threadRepository.findById("t1")).thenReturn(Optional.of(thread));

        Post edited = forumService.editPost(ALICE, "t1", openingPost.getId(), "Edited");

        assertThat(edited.getContent()).isEqualTo("Edited");
        assertThat(edited.getEditedAt()).isNotNull();
        verify(threadRepository).save(thread);
    }

    @Test
    void otherUsersCannotEditPost() {
        when(threadRepository.findById("t1")).thenReturn(Optional.of(thread));

        assertThatThrownBy(() -> forumService.editPost(BOB, "t1", openingPost.getId(), "Hacked"))
                .isInstanceOf(ForbiddenException.class);
        assertThat(openingPost.getContent()).isEqualTo("Upper/lower or PPL?");
        verify(threadRepository, never()).save(any());
    }

    @Test
    void adminCanDeleteOtherUsersPost() {
        Post reply = new Post(BOB.id(), BOB.username(), "Spam");
        thread.addPost(reply);
        when(threadRepository.findById("t1")).thenReturn(Optional.of(thread));

        forumService.deletePost(ADMIN, "t1", reply.getId());

        assertThat(thread.getPosts()).containsExactly(openingPost);
    }

    @Test
    void strangerCannotDeleteThread() {
        when(threadRepository.findById("t1")).thenReturn(Optional.of(thread));

        assertThatThrownBy(() -> forumService.deleteThread(BOB, "t1")).isInstanceOf(ForbiddenException.class);
        verify(threadRepository, never()).delete(any(ForumThread.class));
    }

    @Test
    void unknownPostIsNotFound() {
        when(threadRepository.findById("t1")).thenReturn(Optional.of(thread));

        assertThatThrownBy(() -> forumService.deletePost(ALICE, "t1", "missing"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void unknownThreadIsNotFound() {
        when(threadRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> forumService.getThread("missing")).isInstanceOf(NotFoundException.class);
    }
}
