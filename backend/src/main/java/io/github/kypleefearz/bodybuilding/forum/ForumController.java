package io.github.kypleefearz.bodybuilding.forum;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.kypleefearz.bodybuilding.auth.CurrentUser;
import io.github.kypleefearz.bodybuilding.config.OpenApiConfig;
import io.github.kypleefearz.bodybuilding.forum.ForumDtos.CreateThreadRequest;
import io.github.kypleefearz.bodybuilding.forum.ForumDtos.PostRequest;
import io.github.kypleefearz.bodybuilding.forum.ForumDtos.PostResponse;
import io.github.kypleefearz.bodybuilding.forum.ForumDtos.ThreadResponse;
import io.github.kypleefearz.bodybuilding.forum.ForumDtos.ThreadSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/threads")
@Tag(name = "Forum", description = "Community discussion threads")
public class ForumController {

    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @GetMapping
    @Operation(summary = "List threads, most recently active first")
    public List<ThreadSummary> list() {
        return forumService.listThreads().stream().map(ThreadSummary::from).toList();
    }

    @GetMapping("/{threadId}")
    @Operation(summary = "Get a thread with all posts")
    public ThreadResponse get(@PathVariable String threadId) {
        return ThreadResponse.from(forumService.getThread(threadId));
    }

    @PostMapping
    @Operation(summary = "Start a new thread")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    public ResponseEntity<ThreadResponse> create(@Parameter(hidden = true) CurrentUser user,
            @Valid @RequestBody CreateThreadRequest request) {
        ForumThread thread = forumService.createThread(user, request.title(), request.content());
        return ResponseEntity.created(URI.create("/api/threads/" + thread.getId()))
                .body(ThreadResponse.from(thread));
    }

    @DeleteMapping("/{threadId}")
    @Operation(summary = "Delete a thread (author or admin)")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    public ResponseEntity<Void> delete(@Parameter(hidden = true) CurrentUser user, @PathVariable String threadId) {
        forumService.deleteThread(user, threadId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{threadId}/posts")
    @Operation(summary = "Reply to a thread")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    public ResponseEntity<PostResponse> addPost(@Parameter(hidden = true) CurrentUser user,
            @PathVariable String threadId, @Valid @RequestBody PostRequest request) {
        Post post = forumService.addPost(user, threadId, request.content());
        return ResponseEntity.created(URI.create("/api/threads/" + threadId + "/posts/" + post.getId()))
                .body(PostResponse.from(post));
    }

    @PutMapping("/{threadId}/posts/{postId}")
    @Operation(summary = "Edit your own post")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    public PostResponse editPost(@Parameter(hidden = true) CurrentUser user, @PathVariable String threadId,
            @PathVariable String postId, @Valid @RequestBody PostRequest request) {
        return PostResponse.from(forumService.editPost(user, threadId, postId, request.content()));
    }

    @DeleteMapping("/{threadId}/posts/{postId}")
    @Operation(summary = "Delete a post (author or admin)")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    public ResponseEntity<Void> deletePost(@Parameter(hidden = true) CurrentUser user, @PathVariable String threadId,
            @PathVariable String postId) {
        forumService.deletePost(user, threadId, postId);
        return ResponseEntity.noContent().build();
    }
}
