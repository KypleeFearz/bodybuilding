package ch.zhaw.bodybuilding.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import ch.zhaw.bodybuilding.model.Forum;
import ch.zhaw.bodybuilding.model.ForumCreateDTO;
import ch.zhaw.bodybuilding.repository.ForumRepository;

@RestController
public class ForumController {

    @Autowired
    ForumRepository forumRepository;



    @PostMapping("/api/forum")

    public ResponseEntity<Forum> createForum(
            @RequestBody ForumCreateDTO fDTO) {
                Forum fDAO = new Forum(fDTO.getCreator());
                Forum f = forumRepository.save(fDAO);
        return new ResponseEntity<>(f, HttpStatus.CREATED);
    }

    @GetMapping("/all/forum")
   
    public ResponseEntity<List<Forum>> getAllForum() {
        List<Forum> allFree = forumRepository.findAll();
        return new ResponseEntity<>(allFree, HttpStatus.OK);
    }

    @GetMapping("/all/forum/{id}")

    public ResponseEntity<Forum> getForumById(@PathVariable String id) {
        Optional<Forum> optForum = forumRepository.findById(id);
        if (optForum.isPresent()) {
            return new ResponseEntity<>(optForum.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/all/forum/creator/{creator}")

    public ResponseEntity<Forum> getForumByCreator(@PathVariable String creator) {
        Forum optForum = forumRepository.findFirstByCreator(creator);
        if (optForum != null) {
            return new ResponseEntity<>(optForum, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/api/forum/delete")
    @Secured("ROLE_admin")
    public ResponseEntity<String> deleteForumByCreator(@RequestParam String creator) {
       Forum forum = forumRepository.findFirstByCreator(creator);
        forumRepository.deleteById(forum.getId());
        return ResponseEntity.status(HttpStatus.OK).body("DELETED");
    }
}
