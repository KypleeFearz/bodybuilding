package ch.zhaw.bodybuilding.controller;


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

import ch.zhaw.bodybuilding.model.Forum;
import ch.zhaw.bodybuilding.model.ForumChangeDTO;
import ch.zhaw.service.ForumService;

@RestController
@RequestMapping("/api/service")
public class ForumServiceController {

   @Autowired
    ForumService forumService;

    @PutMapping("/createBeitrag")

    public ResponseEntity<Forum> createForum(
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
}
