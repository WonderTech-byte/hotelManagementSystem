package org.sammy.hotelmanagement.notification;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sammy.hotelmanagement.config.MailjetConfig;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailjetEmailService {

    private final MailjetConfig mailjetConfig;

    public void sendEmail(String toEmail, String recipientName, String subject, String body) {
        try {
            MailjetClient client = createMailjetClient();
            
            JSONObject message = buildEmailMessage(toEmail, recipientName, subject, body);
            JSONObject request = new JSONObject();
            request.put(Emailv31.MESSAGES, new JSONArray().put(message));

            MailjetRequest mailjetRequest = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray().put(message));
            
            MailjetResponse response = client.post(mailjetRequest);
            log.info("Email sent to {} | Subject: {} | Response: {}", toEmail, subject, response.getStatus());
        } catch (MailjetException e) {
            log.error("Failed to send email to {} | Error: {}", toEmail, e.getMessage(), e);
        }
    }

    private JSONObject buildEmailMessage(String toEmail, String recipientName, String subject, String body) {
        JSONObject message = new JSONObject();
        
        message.put(Emailv31.Message.FROM, new JSONObject()
                .put("Email", mailjetConfig.getSenderEmail())
                .put("Name", mailjetConfig.getSenderName()));
        
        message.put(Emailv31.Message.TO, new JSONArray()
                .put(new JSONObject()
                        .put("Email", toEmail)
                        .put("Name", recipientName)));
        
        message.put(Emailv31.Message.SUBJECT, subject);
        message.put(Emailv31.Message.TEXTPART, body);
        
        return message;
    }

    private MailjetClient createMailjetClient() {
        return new MailjetClient(mailjetConfig.getApiKey(), mailjetConfig.getSecretKey());
    }
}
