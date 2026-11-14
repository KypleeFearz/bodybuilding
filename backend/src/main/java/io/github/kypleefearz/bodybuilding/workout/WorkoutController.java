package io.github.kypleefearz.bodybuilding.workout;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.kypleefearz.bodybuilding.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/workouts")
@Tag(name = "Workouts", description = "Shared workout library")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping
    @Operation(summary = "List workouts, optionally filtered by muscle group")
    public List<WorkoutResponse> list(@RequestParam(required = false) MuscleGroup muscleGroup) {
        return workoutService.list(muscleGroup).stream().map(WorkoutResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single workout")
    public WorkoutResponse get(@PathVariable String id) {
        return WorkoutResponse.from(workoutService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Create a workout (admin)")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    public ResponseEntity<WorkoutResponse> create(@Valid @RequestBody WorkoutRequest request) {
        Workout workout = workoutService.create(request);
        return ResponseEntity.created(URI.create("/api/workouts/" + workout.getId()))
                .body(WorkoutResponse.from(workout));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Delete a workout (admin)")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        workoutService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
