package io.github.kypleefearz.bodybuilding.workout;

public record WorkoutResponse(String id, String exercise, int sets, int reps, MuscleGroup muscleGroup, String notes) {

    public static WorkoutResponse from(Workout workout) {
        return new WorkoutResponse(workout.getId(), workout.getExercise(), workout.getSets(), workout.getReps(),
                workout.getMuscleGroup(), workout.getNotes());
    }
}
