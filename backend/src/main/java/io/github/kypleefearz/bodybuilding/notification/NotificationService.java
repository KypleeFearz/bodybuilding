package io.github.kypleefearz.bodybuilding.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Sends e-mail notifications asynchronously. If no SMTP server is configured the notification is only logged,
 * so the app runs locally without any mail setup.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String from;

    public NotificationService(ObjectProvider<JavaMailSender> mailSender, @Value("${app.mail.from}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Async
    public void workoutAddedToPlan(String email, String username, String exercise) {
        if (email == null || email.isBlank()) {
            return;
        }
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            log.info("Mail not configured, skipping notification about '{}'", exercise);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("New workout in your plan: " + exercise);
        message.setText("""
                Hi %s,

                "%s" was added to your training plan. Have a great session!

                Your Bodybuilding team
                """.formatted(username, exercise));
        try {
            sender.send(message);
        } catch (MailException ex) {
            log.warn("Could not send notification mail", ex);
        }
    }
}
