package io.github.kypleefearz.bodybuilding.workout;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record WorkoutRequest(
        @NotBlank @Size(max = 80) String exercise,
        @Min(1) @Max(20) int sets,
        @Min(1) @Max(100) int reps,
        @NotNull MuscleGroup muscleGroup,
        @Size(max = 500) String notes) {
}
