package io.github.kypleefearz.bodybuilding.forum;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A discussion thread. Posts are embedded because they are always read together with their thread.
 * {@link Version} enables optimistic locking, so two concurrent replies cannot overwrite each other.
 */
@Document("threads")
public class ForumThread {

    @Id
    private String id;
    private String title;
    private String authorId;
    private String authorName;
    private Instant createdAt;
    @Indexed
    private Instant lastActivityAt;
    private List<Post> posts = new ArrayList<>();
    @Version
    private Long version;

    protected ForumThread() {
        // for Spring Data
    }

    public ForumThread(String title, Post openingPost) {
        this.title = title;
        this.authorId = openingPost.getAuthorId();
        this.authorName = openingPost.getAuthorName();
        this.createdAt = openingPost.getCreatedAt();
        this.lastActivityAt = openingPost.getCreatedAt();
        this.posts.add(openingPost);
    }

    void addPost(Post post) {
        posts.add(post);
        lastActivityAt = post.getCreatedAt();
    }

    Optional<Post> findPost(String postId) {
        return posts.stream().filter(post -> post.getId().equals(postId)).findFirst();
    }

    boolean removePost(String postId) {
        return posts.removeIf(post -> post.getId().equals(postId));
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastActivityAt() {
        return lastActivityAt;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public Long getVersion() {
        return version;
    }
}
