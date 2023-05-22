package ch.zhaw.bodybuilding.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.bodybuilding.model.Mail;
import ch.zhaw.bodybuilding.model.User;
import ch.zhaw.bodybuilding.model.UserChangeDTO;
import ch.zhaw.bodybuilding.service.MailService;
import ch.zhaw.bodybuilding.service.UserService;

@RestController
@RequestMapping("/api/service")
public class UserServiceController {
    @Autowired 
    UserService userService;

    @Autowired
    MailService mailService;
    
    @PutMapping("/me/buyTraining")
    public ResponseEntity<User> buyTraining(@RequestBody UserChangeDTO dto, @AuthenticationPrincipal Jwt jwt){
        String userEmail = jwt.getClaimAsString("email");
        Optional<User> user=userService.buyTraining(dto.getUserName(), dto.getTrainingId());
        if(user.isPresent()){
            sendMail(userEmail);
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    private void sendMail(String userEmail) {
        var mail = new Mail();
        mail.setTo(userEmail);
        mail.setSubject("Gekauftes Training ");
        String mailMessage = "Lieber Benutzer, ihr gekauftes Training ist nun für sie ersichtlich.";
        mail.setMessage(mailMessage);
        mailService.sendMail(mail);
    }
}
