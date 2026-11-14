package io.github.kypleefearz.bodybuilding.workout;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import io.github.kypleefearz.bodybuilding.common.NotFoundException;
import io.github.kypleefearz.bodybuilding.user.UserProfileService;

@Service
public class WorkoutService {

    private static final Sort BY_EXERCISE = Sort.by("exercise");

    private final WorkoutRepository workoutRepository;
    private final UserProfileService userProfileService;

    public WorkoutService(WorkoutRepository workoutRepository, UserProfileService userProfileService) {
        this.workoutRepository = workoutRepository;
        this.userProfileService = userProfileService;
    }

    public List<Workout> list(MuscleGroup muscleGroup) {
        return muscleGroup == null
                ? workoutRepository.findAll(BY_EXERCISE)
                : workoutRepository.findByMuscleGroup(muscleGroup, BY_EXERCISE);
    }

    public Workout get(String id) {
        return workoutRepository.findById(id).orElseThrow(() -> new NotFoundException("Workout", id));
    }

    public Workout create(WorkoutRequest request) {
        Workout workout = new Workout(request.exercise().trim(), request.sets(), request.reps(),
                request.muscleGroup(), request.notes());
        return workoutRepository.save(workout);
    }

    /** Deletes the workout and removes it from every personal plan that references it. */
    public void delete(String id) {
        if (!workoutRepository.existsById(id)) {
            throw new NotFoundException("Workout", id);
        }
        workoutRepository.deleteById(id);
        userProfileService.removeWorkoutFromAllPlans(id);
    }
}
