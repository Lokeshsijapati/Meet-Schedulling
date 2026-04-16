package com.meet.MeetSchedulling.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

@Async
public void sendMeetingEmail(String toEmail,
                             String meetLink,
                             String meetingTime) throws MessagingException {

    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);

    helper.setTo(toEmail);
    helper.setFrom("lokeshkr189@gmail.com");
    helper.setSubject("Meeting Scheduled");

    String content = "<h3>Meeting Scheduled</h3>"
            + "<p><b>Time:</b> " + meetingTime + "</p>"
            + "<p><b>Link:</b> <a href='" + meetLink + "'>" + meetLink + "</a></p>";

    helper.setText(content, true);

    mailSender.send(message);
}
}