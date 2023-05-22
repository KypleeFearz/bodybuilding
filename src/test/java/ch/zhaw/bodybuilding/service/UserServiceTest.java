package ch.zhaw.bodybuilding.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


import ch.zhaw.bodybuilding.model.Training;
import ch.zhaw.bodybuilding.model.User;
import ch.zhaw.bodybuilding.repository.TrainingRepository;
import ch.zhaw.bodybuilding.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testBuyTraining() {
        Training training = new Training();
        training.setId("1");
        String user = "Marko";
        User benutzer = new User();
        benutzer.setName(user);
        benutzer.setEmail("test");
        when(trainingRepository.findById("1")).thenReturn(Optional.of(training));
        when(userRepository.findFirstByName("Marko")).thenReturn(benutzer);
        Optional<User> result = userService.buyTraining(user, training.getId());
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getPlaene().length);
    }
}