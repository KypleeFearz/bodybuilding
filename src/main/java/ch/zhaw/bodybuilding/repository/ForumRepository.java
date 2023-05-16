package ch.zhaw.bodybuilding.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.bodybuilding.model.Forum;

public interface ForumRepository extends
                MongoRepository<Forum, String> {
                    Forum findFirstByCreator(String creator);
}
