package ch.zhaw.bodybuilding.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.bodybuilding.model.Training;
import java.util.List;


public interface TrainingRepository extends
                MongoRepository<Training, String> {
                    List<Training> findByFokus(String fokus);
                    Training findByUbung(String ubung);
}
