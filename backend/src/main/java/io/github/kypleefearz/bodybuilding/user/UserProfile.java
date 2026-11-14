package io.github.kypleefearz.bodybuilding.user;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Application-side profile of an Auth0 user. The id is the Auth0 subject, so no separate lookup by e-mail
 * or nickname is needed (nicknames are neither unique nor stable).
 */
@Document("users")
public class UserProfile {

    @Id
    private String id;
    private String username;
    private String email;
    private Set<String> savedWorkoutIds = new LinkedHashSet<>();
    private Instant createdAt;

    protected UserProfile() {
        // for Spring Data
    }

    public UserProfile(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = Instant.now();
    }

    /** Keeps name and e-mail in sync with the identity provider. Returns {@code true} if anything changed. */
    boolean updateIdentity(String username, String email) {
        boolean changed = !Objects.equals(this.username, username) || !Objects.equals(this.email, email);
        this.username = username;
        this.email = email;
        return changed;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getSavedWorkoutIds() {
        return savedWorkoutIds;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
