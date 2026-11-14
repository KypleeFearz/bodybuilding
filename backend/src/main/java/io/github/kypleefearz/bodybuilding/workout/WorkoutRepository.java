package io.github.kypleefearz.bodybuilding.workout;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WorkoutRepository extends MongoRepository<Workout, String> {

    List<Workout> findByMuscleGroup(MuscleGroup muscleGroup, Sort sort);
}
