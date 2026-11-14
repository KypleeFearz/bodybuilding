package io.github.kypleefearz.bodybuilding.forum;

import java.time.Instant;
import java.util.UUID;

/** A reply inside a {@link ForumThread}. Embedded in the thread document. */
public class Post {

    private String id;
    private String authorId;
    private String authorName;
    private String content;
    private Instant createdAt;
    private Instant editedAt;

    protected Post() {
        // for Spring Data
    }

    public Post(String authorId, String authorName, String content) {
        this.id = UUID.randomUUID().toString();
        this.authorId = authorId;
        this.authorName = authorName;
        this.content = content;
        this.createdAt = Instant.now();
    }

    void edit(String newContent) {
        this.content = newContent;
        this.editedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getEditedAt() {
        return editedAt;
    }
}
