package io.github.kypleefearz.bodybuilding.forum;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ForumThreadRepository extends MongoRepository<ForumThread, String> {

    List<ForumThread> findAllByOrderByLastActivityAtDesc();
}
