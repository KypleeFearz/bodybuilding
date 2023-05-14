package ch.zhaw.bodybuilding.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.bodybuilding.model.Training;

public interface TrainingRepository extends
                MongoRepository<Training, String> {
}
