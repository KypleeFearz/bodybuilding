package io.github.kypleefearz.bodybuilding.workout;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import io.github.kypleefearz.bodybuilding.common.NotFoundException;
import io.github.kypleefearz.bodybuilding.config.SecurityConfig;
import io.github.kypleefearz.bodybuilding.config.WebConfig;

@WebMvcTest(WorkoutController.class)
@Import({ SecurityConfig.class, WebConfig.class })
class WorkoutControllerTest {

    private static final String VALID_WORKOUT = """
            {"exercise": "Back Squat", "sets": 5, "reps": 5, "muscleGroup": "LEGS"}
            """;

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private WorkoutService workoutService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void listingIsPublic() throws Exception {
        when(workoutService.list(null)).thenReturn(List.of(squat()));

        mvc.perform(get("/api/workouts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].exercise").value("Back Squat"))
                .andExpect(jsonPath("$[0].muscleGroup").value("LEGS"));
    }

    @Test
    void listingCanBeFilteredByMuscleGroup() throws Exception {
        mvc.perform(get("/api/workouts").param("muscleGroup", "LEGS")).andExpect(status().isOk());

        verify(workoutService).list(MuscleGroup.LEGS);
    }

    @Test
    void unknownMuscleGroupIsBadRequest() throws Exception {
        mvc.perform(get("/api/workouts").param("muscleGroup", "NECK")).andExpect(status().isBadRequest());
    }

    @Test
    void unknownWorkoutReturnsProblemDetail() throws Exception {
        when(workoutService.get("missing")).thenThrow(new NotFoundException("Workout", "missing"));

        mvc.perform(get("/api/workouts/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not found"))
                .andExpect(jsonPath("$.detail").value("Workout 'missing' was not found"));
    }

    @Test
    void creatingRequiresAuthentication() throws Exception {
        mvc.perform(post("/api/workouts").contentType(MediaType.APPLICATION_JSON).content(VALID_WORKOUT))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void creatingRequiresAdminRole() throws Exception {
        mvc.perform(post("/api/workouts").with(jwt())
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_WORKOUT))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateWorkout() throws Exception {
        when(workoutService.create(any(WorkoutRequest.class))).thenReturn(squat());

        mvc.perform(post("/api/workouts").with(admin())
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_WORKOUT))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/workouts/w1"))
                .andExpect(jsonPath("$.id").value("w1"));
    }

    @Test
    void invalidWorkoutReturnsFieldErrors() throws Exception {
        String invalid = """
                {"exercise": "", "sets": 0, "reps": 5}
                """;

        mvc.perform(post("/api/workouts").with(admin())
                        .contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.exercise").exists())
                .andExpect(jsonPath("$.errors.sets").exists())
                .andExpect(jsonPath("$.errors.muscleGroup").exists());
    }

    @Test
    void adminCanDeleteWorkout() throws Exception {
        mvc.perform(delete("/api/workouts/w1").with(admin())).andExpect(status().isNoContent());

        verify(workoutService).delete("w1");
    }

    private static Workout squat() {
        Workout workout = new Workout("Back Squat", 5, 5, MuscleGroup.LEGS, null);
        ReflectionTestUtils.setField(workout, "id", "w1");
        return workout;
    }

    private static RequestPostProcessor admin() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_admin"));
    }
}
