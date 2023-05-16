package ch.zhaw.bodybuilding.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.bodybuilding.model.User;

public interface UserRepository extends
                MongoRepository<User, String> {
        User findFirstByEmail(String email);
        User findFirstByName(String name);
}
