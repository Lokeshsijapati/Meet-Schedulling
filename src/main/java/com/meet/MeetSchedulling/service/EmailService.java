package com.meet.MeetSchedulling.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    @Value("${manager.email}")
    private String managerEmail;

    @Async
    public void sendMeetingEmail(String userEmail, String meetLink, String meetingTime) {

        try {
            ApiClient client = Configuration.getDefaultApiClient();
            ApiKeyAuth apiKeyAuth = (ApiKeyAuth) client.getAuthentication("api-key");
            apiKeyAuth.setApiKey(apiKey);

            TransactionalEmailsApi api = new TransactionalEmailsApi(client);

            String content = "<h3>Meeting Scheduled</h3>"
                    + "<p><b>Time:</b> " + meetingTime + "</p>"
                    + "<p><b>Link:</b> <a href='" + meetLink + "'>" + meetLink + "</a></p>";

            SendSmtpEmailSender sender = new SendSmtpEmailSender()
                    .email(senderEmail)
                    .name(senderName);

            List<SendSmtpEmailTo> toList = List.of(
                    new SendSmtpEmailTo().email(userEmail),
                    new SendSmtpEmailTo().email(managerEmail)
            );

            SendSmtpEmail email = new SendSmtpEmail()
                    .sender(sender)
                    .to(toList)
                    .subject("Meeting Scheduled")
                    .htmlContent(content);

            api.sendTransacEmail(email);

            log.info("Mail sent to user: {} and manager: {}", userEmail, managerEmail);

        } catch (Exception e) {
            log.error("Email failed", e);
            throw new RuntimeException("Email failed", e);
        }
    }
}
