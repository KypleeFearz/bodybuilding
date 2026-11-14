package io.github.kypleefearz.bodybuilding.workout;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** A single exercise prescription in the shared workout library, e.g. "Bench Press, 4 x 8, chest". */
@Document("workouts")
public class Workout {

    @Id
    private String id;
    private String exercise;
    private int sets;
    private int reps;
    @Indexed
    private MuscleGroup muscleGroup;
    private String notes;
    private Instant createdAt;

    protected Workout() {
        // for Spring Data
    }

    public Workout(String exercise, int sets, int reps, MuscleGroup muscleGroup, String notes) {
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
        this.muscleGroup = muscleGroup;
        this.notes = notes;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getExercise() {
        return exercise;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public MuscleGroup getMuscleGroup() {
        return muscleGroup;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
