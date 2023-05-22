package ch.zhaw.bodybuilding.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ch.zhaw.bodybuilding.model.Forum;
import ch.zhaw.bodybuilding.model.ForumChangeDTO;
import ch.zhaw.bodybuilding.service.ForumService;

@RestController
@RequestMapping("/api/service")
public class ForumServiceController {

   @Autowired
    ForumService forumService;

    @PutMapping("/createBeitrag")

    public ResponseEntity<Forum> createBeitrag(
            @RequestBody ForumChangeDTO fDTO, @AuthenticationPrincipal Jwt jwt) {
        String name=jwt.getClaimAsString("nickname");
        String text=fDTO.getText();
        String creator=fDTO.getCreator();
        Optional<Forum>forum=forumService.createBeitrag(name, text, creator);
        if (forum.isPresent()){
        return new ResponseEntity<>(forum.get(),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @PutMapping("/editBeitrag")

    public ResponseEntity<Forum> editBeitrag(
            @RequestBody ForumChangeDTO fDTO, @RequestParam String newText, @AuthenticationPrincipal Jwt jwt) {
        String name=jwt.getClaimAsString("nickname");
        String text=fDTO.getText();
        String creator=fDTO.getCreator();
        Optional<Forum>forum=forumService.editBeitrag(newText, text, creator, name);
        if (forum.isPresent()){
        return new ResponseEntity<>(forum.get(),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @PutMapping("/deleteBeitrag")

    public ResponseEntity<Forum> deleteBeitrag(
            @RequestBody ForumChangeDTO fDTO, @AuthenticationPrincipal Jwt jwt) {
        String text=fDTO.getText();
        String creator=fDTO.getCreator();
        String beitragCreator=fDTO.getBeitragCreator();
        String user = jwt.getClaimAsString("nickname");
        List<String> userRole= null;
        if (jwt.hasClaim("user_roles")) {
            userRole = jwt.getClaimAsStringList("user_roles");
        }
        Optional<Forum>forum=forumService.deleteBeitrag(text, beitragCreator, creator, userRole, user);
        if (forum.isPresent()){
        return new ResponseEntity<>(forum.get(),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
