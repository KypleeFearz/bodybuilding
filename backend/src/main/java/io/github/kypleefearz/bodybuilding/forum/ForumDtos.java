package io.github.kypleefearz.bodybuilding.forum;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request and response bodies of the forum API. */
public final class ForumDtos {

    private ForumDtos() {
    }

    public record CreateThreadRequest(
            @NotBlank @Size(max = 120) String title,
            @NotBlank @Size(max = 5000) String content) {
    }

    public record PostRequest(@NotBlank @Size(max = 5000) String content) {
    }

    public record ThreadSummary(String id, String title, String authorName, Instant createdAt,
            Instant lastActivityAt, int replies) {

        static ThreadSummary from(ForumThread thread) {
            return new ThreadSummary(thread.getId(), thread.getTitle(), thread.getAuthorName(),
                    thread.getCreatedAt(), thread.getLastActivityAt(), Math.max(0, thread.getPosts().size() - 1));
        }
    }

    public record PostResponse(String id, String authorId, String authorName, String content, Instant createdAt,
            Instant editedAt) {

        static PostResponse from(Post post) {
            return new PostResponse(post.getId(), post.getAuthorId(), post.getAuthorName(), post.getContent(),
                    post.getCreatedAt(), post.getEditedAt());
        }
    }

    public record ThreadResponse(String id, String title, String authorId, String authorName, Instant createdAt,
            List<PostResponse> posts) {

        static ThreadResponse from(ForumThread thread) {
            return new ThreadResponse(thread.getId(), thread.getTitle(), thread.getAuthorId(),
                    thread.getAuthorName(), thread.getCreatedAt(),
                    thread.getPosts().stream().map(PostResponse::from).toList());
        }
    }
}
