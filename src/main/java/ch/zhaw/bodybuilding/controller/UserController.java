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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ch.zhaw.bodybuilding.model.User;
import ch.zhaw.bodybuilding.model.UserCreateDTO;
import ch.zhaw.bodybuilding.repository.UserRepository;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepository userRepository;



    @PostMapping("/user")
@Secured("ROLE_admin")
    public ResponseEntity<User> createUser(
            @RequestBody UserCreateDTO fDTO) {
        User fDAO = new User(fDTO.getEmail(), fDTO.getName());
        User f = userRepository.save(fDAO);
        return new ResponseEntity<>(f, HttpStatus.CREATED);
    }

    @GetMapping("/user")
   
    public ResponseEntity<List<User>> getAllUser() {
        List<User> allFree = userRepository.findAll();
        return new ResponseEntity<>(allFree, HttpStatus.OK);
    }

    @GetMapping("/users/{name}")
   
    public ResponseEntity<List<User>> getAllUserByName(@PathVariable String name) {
        List<User> allFree = userRepository.findByName(name);
        return new ResponseEntity<>(allFree, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")

    public ResponseEntity<User> getUserById(@PathVariable String id) {
        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isPresent()) {
            return new ResponseEntity<>(optUser.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/name/{name}")

    public ResponseEntity<User> getUserByName(@PathVariable String name) {
        User optUser = userRepository.findFirstByName(name);
        if (optUser!=null) {
            return new ResponseEntity<>(optUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/user/delete")
    @Secured("ROLE_admin")
    public ResponseEntity<String> deleteUserById(@RequestParam String userName) {
        User user = userRepository.findFirstByName(userName);
        userRepository.deleteById(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body("DELETED");
    }
}
