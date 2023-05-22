package ch.zhaw.bodybuilding.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import ch.zhaw.bodybuilding.model.Mail;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    @Disabled
    public void testSendMail() {
        Mail mail = new Mail();
        mail.setMessage("Test");
        mail.setTo("n.milosavljevic@gmx.ch");
        Boolean result = mailService.sendMail(mail);
        assertTrue(result);
    }
}