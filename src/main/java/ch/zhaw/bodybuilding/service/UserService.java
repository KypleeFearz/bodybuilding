package ch.zhaw.bodybuilding.service;

import java.util.Arrays;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.bodybuilding.model.Training;
import ch.zhaw.bodybuilding.model.User;
import ch.zhaw.bodybuilding.repository.TrainingRepository;
import ch.zhaw.bodybuilding.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TrainingRepository trainingRepository;

    public Optional<User> buyTraining(String userName, String trainingId) {
        Optional<Training> training = trainingRepository.findById(trainingId);
       User user = userRepository.findFirstByName(userName);
        if (user !=null && training.isPresent()) {
            Training[] plaene = user.getPlaene();
            if (plaene != null) {
                Training[] newPlaene = Arrays.copyOf(plaene, plaene.length + 1);
                newPlaene[newPlaene.length - 1] = training.get();
                user.setPlaene(newPlaene);
            } else {
                Training[] newPlaene = { training.get() };
                user.setPlaene(newPlaene);
            }
            userRepository.save(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
