package com.erpews.service;

import com.erpews.entity.EmailContent;
import com.erpews.repository.EmailContentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.connector.Request;
import org.apache.hc.core5.http.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@PropertySource("classpath:system.properties")
public class EmailSender {

    private final String postmarkUrl;
    private final String postmarkToken;
    private final Logger logger;

    @Autowired
    private EmailContentRepository emailContentRepository;

    public EmailSender(@Value("${postmark.url}") String postmarkUrl, @Value("${postmark.token}") String postmarkToken) {
        this.postmarkUrl = postmarkUrl;
        this.postmarkToken = postmarkToken;
        logger = LoggerFactory.getLogger(getClass());
    }

    public String sendPost(String apiUrl, Map<String, String> headers, Map<String, Object> body) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(body);

      //  Request request = Request.post(apiUrl);
        if (headers != null) {
           // headers.forEach(request::addHeader);
        }
        if (body != null) {
           // request.bodyString(jsonString, ContentType.APPLICATION_JSON);
        }
       // return request.execute().returnContent().asString();
        return "";
    }

    public boolean sendEmail(String title, String subject, String body) {
        EmailContent emailContent = emailContentRepository.findByTitle(title);

        if (emailContent == null) {
            logger.error("Email content with title '{}' not found.", title);
            return false;
        }

        String to = emailContent.getTo();
        String from = emailContent.getFrom();
        String cc = emailContent.getCc();


        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("X-Postmark-Server-Token", postmarkToken);

        Map<String, Object> emailPayload = new HashMap<>();
        emailPayload.put("From", from);
        emailPayload.put("To", to);
        if (cc != null && !cc.trim().isEmpty()) {
            String[] ccEmails = cc.split(",");
            emailPayload.put("Cc", String.join(",", ccEmails));
        }
        emailPayload.put("Subject", subject);
        emailPayload.put("HtmlBody", body);
        emailPayload.put("MessageStream", "outbound");

        try {
            String response = sendPost(postmarkUrl, headers, emailPayload);
            return response != null;
        } catch (IOException e) {
            logger.error("sendEmail", e);
            return false;
        }
    }
}
