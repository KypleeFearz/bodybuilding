package io.github.kypleefearz.bodybuilding.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import io.github.kypleefearz.bodybuilding.auth.CurrentUser;
import io.github.kypleefearz.bodybuilding.common.NotFoundException;
import io.github.kypleefearz.bodybuilding.notification.NotificationService;
import io.github.kypleefearz.bodybuilding.workout.MuscleGroup;
import io.github.kypleefearz.bodybuilding.workout.Workout;
import io.github.kypleefearz.bodybuilding.workout.WorkoutRepository;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    private static final CurrentUser ALICE = new CurrentUser("auth0|alice", "alice", "alice@example.com", false);

    @Mock
    private UserProfileRepository userProfileRepository;
    @Mock
    private WorkoutRepository workoutRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    void createsProfileOnFirstAccess() {
        when(userProfileRepository.findById(ALICE.id())).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(returnsFirstArg());

        UserProfile profile = userProfileService.getOrCreate(ALICE);

        assertThat(profile.getId()).isEqualTo(ALICE.id());
        assertThat(profile.getUsername()).isEqualTo("alice");
        assertThat(profile.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void syncsChangedIdentityFromToken() {
        UserProfile existing = new UserProfile(ALICE.id(), "old-name", "old@example.com");
        when(userProfileRepository.findById(ALICE.id())).thenReturn(Optional.of(existing));
        when(userProfileRepository.save(existing)).thenReturn(existing);

        UserProfile profile = userProfileService.getOrCreate(ALICE);

        assertThat(profile.getUsername()).isEqualTo("alice");
        verify(userProfileRepository).save(existing);
    }

    @Test
    void doesNotWriteUnchangedProfile() {
        UserProfile existing = new UserProfile(ALICE.id(), ALICE.username(), ALICE.email());
        when(userProfileRepository.findById(ALICE.id())).thenReturn(Optional.of(existing));

        userProfileService.getOrCreate(ALICE);

        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void addingTheSameWorkoutTwiceNotifiesOnlyOnce() {
        Workout squat = new Workout("Back Squat", 5, 5, MuscleGroup.LEGS, null);
        ReflectionTestUtils.setField(squat, "id", "w1");
        UserProfile profile = new UserProfile(ALICE.id(), ALICE.username(), ALICE.email());
        when(workoutRepository.findById("w1")).thenReturn(Optional.of(squat));
        when(userProfileRepository.findById(ALICE.id())).thenReturn(Optional.of(profile));
        when(workoutRepository.findAllById(any())).thenReturn(List.of(squat));

        userProfileService.addToPlan(ALICE, "w1");
        List<Workout> plan = userProfileService.addToPlan(ALICE, "w1");

        assertThat(plan).containsExactly(squat);
        assertThat(profile.getSavedWorkoutIds()).containsExactly("w1");
        verify(userProfileRepository, times(1)).save(profile);
        verify(notificationService, times(1)).workoutAddedToPlan("alice@example.com", "alice", "Back Squat");
    }

    @Test
    void addingUnknownWorkoutFails() {
        when(workoutRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.addToPlan(ALICE, "missing"))
                .isInstanceOf(NotFoundException.class);
        verifyNoInteractions(notificationService);
    }
}
