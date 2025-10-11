package practice.app.journalapp.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
public class EmailServiceTests {
    @Autowired
    EmailService emailService;
    @Disabled
    @Test
    public void sendMail(){
        emailService.sendEmail("17sourav.singh@gmail.com","This is Testing Email","Hello Mr Sourabh You are such a great person Thankyou for your hardwork you are puting in for your future self. Your Future self -Sourabh");
    }
}
