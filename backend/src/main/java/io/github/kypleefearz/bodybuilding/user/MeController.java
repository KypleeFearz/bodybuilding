package io.github.kypleefearz.bodybuilding.user;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.kypleefearz.bodybuilding.auth.CurrentUser;
import io.github.kypleefearz.bodybuilding.config.OpenApiConfig;
import io.github.kypleefearz.bodybuilding.workout.WorkoutResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Endpoints for the logged-in user. The user is always taken from the token, never from the request body. */
@RestController
@RequestMapping("/api/me")
@Tag(name = "Me", description = "Profile and personal training plan of the logged-in user")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class MeController {

    public record MeResponse(String id, String username, String email, boolean admin, Set<String> savedWorkoutIds) {
    }

    private final UserProfileService userProfileService;

    public MeController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    @Operation(summary = "Get the current user's profile")
    public MeResponse me(@Parameter(hidden = true) CurrentUser user) {
        UserProfile profile = userProfileService.getOrCreate(user);
        return new MeResponse(profile.getId(), profile.getUsername(), profile.getEmail(), user.admin(),
                profile.getSavedWorkoutIds());
    }

    @GetMapping("/plan")
    @Operation(summary = "List the workouts in the current user's plan")
    public List<WorkoutResponse> plan(@Parameter(hidden = true) CurrentUser user) {
        return userProfileService.getPlan(user).stream().map(WorkoutResponse::from).toList();
    }

    @PutMapping("/plan/{workoutId}")
    @Operation(summary = "Add a workout to the plan (idempotent)")
    public List<WorkoutResponse> addToPlan(@Parameter(hidden = true) CurrentUser user, @PathVariable String workoutId) {
        return userProfileService.addToPlan(user, workoutId).stream().map(WorkoutResponse::from).toList();
    }

    @DeleteMapping("/plan/{workoutId}")
    @Operation(summary = "Remove a workout from the plan")
    public ResponseEntity<Void> removeFromPlan(@Parameter(hidden = true) CurrentUser user, @PathVariable String workoutId) {
        userProfileService.removeFromPlan(user, workoutId);
        return ResponseEntity.noContent().build();
    }
}
