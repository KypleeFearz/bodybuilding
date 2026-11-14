package io.github.kypleefearz.bodybuilding.user;

import java.time.Instant;

public record UserProfileResponse(String id, String username, String email, int savedWorkouts, Instant createdAt) {

    public static UserProfileResponse from(UserProfile profile) {
        return new UserProfileResponse(profile.getId(), profile.getUsername(), profile.getEmail(),
                profile.getSavedWorkoutIds().size(), profile.getCreatedAt());
    }
}
