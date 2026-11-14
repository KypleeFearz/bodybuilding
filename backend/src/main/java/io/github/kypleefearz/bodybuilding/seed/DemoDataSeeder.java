package io.github.kypleefearz.bodybuilding.seed;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import io.github.kypleefearz.bodybuilding.forum.ForumThread;
import io.github.kypleefearz.bodybuilding.forum.ForumThreadRepository;
import io.github.kypleefearz.bodybuilding.forum.Post;
import io.github.kypleefearz.bodybuilding.workout.MuscleGroup;
import io.github.kypleefearz.bodybuilding.workout.Workout;
import io.github.kypleefearz.bodybuilding.workout.WorkoutRepository;

/** Fills an empty database with sample content so the app can be explored right after {@code docker compose up}. */
@Component
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true")
class DemoDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final WorkoutRepository workoutRepository;
    private final ForumThreadRepository threadRepository;

    DemoDataSeeder(WorkoutRepository workoutRepository, ForumThreadRepository threadRepository) {
        this.workoutRepository = workoutRepository;
        this.threadRepository = threadRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (workoutRepository.count() == 0) {
            workoutRepository.saveAll(List.of(
                    new Workout("Barbell Bench Press", 4, 8, MuscleGroup.CHEST, "Pause briefly on the chest."),
                    new Workout("Incline Dumbbell Press", 3, 10, MuscleGroup.CHEST, null),
                    new Workout("Cable Fly", 3, 15, MuscleGroup.CHEST, "Focus on the squeeze."),
                    new Workout("Deadlift", 5, 5, MuscleGroup.BACK, "Neutral spine, brace hard."),
                    new Workout("Pull-up", 4, 8, MuscleGroup.BACK, "Add weight once 12 reps are easy."),
                    new Workout("Barbell Row", 4, 10, MuscleGroup.BACK, null),
                    new Workout("Back Squat", 5, 5, MuscleGroup.LEGS, "Hit at least parallel."),
                    new Workout("Romanian Deadlift", 3, 10, MuscleGroup.LEGS, null),
                    new Workout("Walking Lunge", 3, 12, MuscleGroup.LEGS, "Reps per leg."),
                    new Workout("Overhead Press", 4, 6, MuscleGroup.SHOULDERS, null),
                    new Workout("Lateral Raise", 4, 15, MuscleGroup.SHOULDERS, "Light weight, strict form."),
                    new Workout("Barbell Curl", 3, 10, MuscleGroup.ARMS, null),
                    new Workout("Skull Crusher", 3, 12, MuscleGroup.ARMS, null),
                    new Workout("Hanging Leg Raise", 3, 12, MuscleGroup.CORE, null),
                    new Workout("Kettlebell Swing", 4, 20, MuscleGroup.FULL_BODY, "Hinge, do not squat.")));
            log.info("Seeded demo workouts");
        }

        if (threadRepository.count() == 0) {
            ForumThread split = new ForumThread("Best split for a beginner?",
                    new Post("demo|alex", "alex", "I can train 4 days a week. Upper/lower or push/pull/legs?"));
            ForumThread deload = new ForumThread("How often do you deload?",
                    new Post("demo|sam", "sam", "Every 6 weeks feels about right for me. What works for you?"));
            threadRepository.saveAll(List.of(split, deload));
            log.info("Seeded demo forum threads");
        }
    }
}
