package io.github.kypleefearz.bodybuilding.user;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import io.github.kypleefearz.bodybuilding.auth.CurrentUser;
import io.github.kypleefearz.bodybuilding.common.NotFoundException;
import io.github.kypleefearz.bodybuilding.notification.NotificationService;
import io.github.kypleefearz.bodybuilding.workout.Workout;
import io.github.kypleefearz.bodybuilding.workout.WorkoutRepository;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final WorkoutRepository workoutRepository;
    private final NotificationService notificationService;
    private final MongoTemplate mongoTemplate;

    public UserProfileService(UserProfileRepository userProfileRepository, WorkoutRepository workoutRepository,
            NotificationService notificationService, MongoTemplate mongoTemplate) {
        this.userProfileRepository = userProfileRepository;
        this.workoutRepository = workoutRepository;
        this.notificationService = notificationService;
        this.mongoTemplate = mongoTemplate;
    }

    /** Returns the profile of the current user and creates it on first login. */
    public UserProfile getOrCreate(CurrentUser user) {
        return userProfileRepository.findById(user.id())
                .map(profile -> profile.updateIdentity(user.username(), user.email())
                        ? userProfileRepository.save(profile)
                        : profile)
                .orElseGet(() -> userProfileRepository.save(new UserProfile(user.id(), user.username(), user.email())));
    }

    /** Workouts in the user's plan, in the order they were added. */
    public List<Workout> getPlan(CurrentUser user) {
        UserProfile profile = getOrCreate(user);
        Map<String, Workout> byId = workoutRepository.findAllById(profile.getSavedWorkoutIds()).stream()
                .collect(Collectors.toMap(Workout::getId, Function.identity()));
        return profile.getSavedWorkoutIds().stream().map(byId::get).filter(Objects::nonNull).toList();
    }

    /** Adds a workout to the plan. Idempotent: adding the same workout twice has no further effect. */
    public List<Workout> addToPlan(CurrentUser user, String workoutId) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new NotFoundException("Workout", workoutId));
        UserProfile profile = getOrCreate(user);
        if (profile.getSavedWorkoutIds().add(workoutId)) {
            userProfileRepository.save(profile);
            notificationService.workoutAddedToPlan(profile.getEmail(), profile.getUsername(), workout.getExercise());
        }
        return getPlan(user);
    }

    public void removeFromPlan(CurrentUser user, String workoutId) {
        UserProfile profile = getOrCreate(user);
        if (profile.getSavedWorkoutIds().remove(workoutId)) {
            userProfileRepository.save(profile);
        }
    }

    public List<UserProfile> listAll() {
        return userProfileRepository.findAll(Sort.by("username"));
    }

    public void delete(String id) {
        if (!userProfileRepository.existsById(id)) {
            throw new NotFoundException("User", id);
        }
        userProfileRepository.deleteById(id);
    }

    /** Called when a workout is deleted from the library. */
    public void removeWorkoutFromAllPlans(String workoutId) {
        mongoTemplate.updateMulti(
                Query.query(Criteria.where("savedWorkoutIds").is(workoutId)),
                new Update().pull("savedWorkoutIds", workoutId),
                UserProfile.class);
    }
}
