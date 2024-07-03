package sg.ncl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;
import org.springframework.core.env.Environment;
import javax.mail.MessagingException;

import javax.annotation.PostConstruct;

@Service
public class MailServices {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private Environment env;

    private String support_email;
    @PostConstruct
    public void init() {
        support_email = env.getProperty("spring.mail.username");
    }

    public void sendEmail(String subject, String email, String messageContent) throws MessagingException {

        SimpleMailMessage message = new SimpleMailMessage();
        System.out.println("support email address" + support_email);

        message.setFrom(support_email); // Change to your from email address
        message.setTo(support_email); // Change to your support email address
        message.setSubject(subject);
        message.setText("Dear NCL Support,\n\n"+messageContent);
        emailSender.send(message);
    }
}

