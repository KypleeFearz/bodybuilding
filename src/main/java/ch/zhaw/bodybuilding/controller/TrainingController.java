package ch.zhaw.bodybuilding.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ch.zhaw.bodybuilding.model.Training;
import ch.zhaw.bodybuilding.model.TrainingCreateDTO;
import ch.zhaw.bodybuilding.repository.TrainingRepository;

@RestController
@RequestMapping("/api")
public class TrainingController {

    @Autowired
    TrainingRepository trainingRepository;



    @PostMapping("/training")
    @Secured("ROLE_admin")
    public ResponseEntity<Training> createTraining(
            @RequestBody TrainingCreateDTO fDTO) {
                Training fDAO = new Training(fDTO.getUbung(), fDTO.getSatz(),fDTO.getWiederholung(),fDTO.getFokus().toUpperCase());
                Training f = trainingRepository.save(fDAO);
        return new ResponseEntity<>(f, HttpStatus.CREATED);
    }

    @GetMapping("/training")
   
    public ResponseEntity<List<Training>> getAllTraining() {
        List<Training> allFree = trainingRepository.findAll();
        return new ResponseEntity<>(allFree, HttpStatus.OK);
    }

    @GetMapping("/training/fokus")
   
    public ResponseEntity<List<Training>> getTrainingByFokus(@RequestParam String fokus) {
        List<Training> allFree = trainingRepository.findByFokus(fokus);
        if(!allFree.isEmpty()){
            return new ResponseEntity<>(allFree, HttpStatus.OK);
        }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/training/{id}")

    public ResponseEntity<Training> getTrainingById(@PathVariable String id) {
        Optional<Training> optTraining = trainingRepository.findById(id);
        if (optTraining.isPresent()) {
            return new ResponseEntity<>(optTraining.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
