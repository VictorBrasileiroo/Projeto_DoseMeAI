package com.dosemeai.DoseMeAI.services.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${resend.api.key}")
    private String resendApiKey;

    public void sendEmail(String to, String subject, String body) {
        String url = "https://api.resend.com/emails";

        Map<String, Object> payload = Map.of(
                "from", "delivered@resend.dev",
                "to", List.of(to),
                "subject", subject,
                "text", body
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resendApiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            var response = restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            throw e;
        }
    }
}
