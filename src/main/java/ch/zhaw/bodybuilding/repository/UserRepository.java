package ch.zhaw.bodybuilding.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.bodybuilding.model.User;
import java.util.List;


public interface UserRepository extends
                MongoRepository<User, String> {
        User findFirstByEmail(String email);
        User findFirstByName(String name);
        List<User> findByName(String name);
}
