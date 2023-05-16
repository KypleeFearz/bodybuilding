package ch.zhaw.bodybuilding.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.zhaw.bodybuilding.model.Forum;
import ch.zhaw.bodybuilding.model.ForumCreateDTO;
import ch.zhaw.bodybuilding.repository.ForumRepository;

@RestController
@RequestMapping("/all")
public class ForumController {

    @Autowired
    ForumRepository forumRepository;



    @PostMapping("/forum")

    public ResponseEntity<Forum> createForum(
            @RequestBody ForumCreateDTO fDTO) {
                Forum fDAO = new Forum(fDTO.getCreator());
                Forum f = forumRepository.save(fDAO);
        return new ResponseEntity<>(f, HttpStatus.CREATED);
    }

    @GetMapping("/forum")
   
    public ResponseEntity<List<Forum>> getAllForum() {
        List<Forum> allFree = forumRepository.findAll();
        return new ResponseEntity<>(allFree, HttpStatus.OK);
    }

    @GetMapping("/forum/{id}")

    public ResponseEntity<Forum> getForumById(@PathVariable String id) {
        Optional<Forum> optForum = forumRepository.findById(id);
        if (optForum.isPresent()) {
            return new ResponseEntity<>(optForum.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
