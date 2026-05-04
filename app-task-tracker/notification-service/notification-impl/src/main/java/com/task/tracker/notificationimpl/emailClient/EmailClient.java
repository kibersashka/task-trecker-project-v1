package com.task.tracker.notificationimpl.emailClient;

import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class EmailClient {

    private final OkHttpClient client;

    private static final String URL = "https://api.resend.com/emails";

    @Value("${email.api.key}")
    public String apiKey;

    public void sendEmail(String to, String subject, String body) {

        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        String json = """
        {
          "from": "Task Tracker <onboarding@resend.dev>",
          "to": ["%s"],
          "subject": "%s",
          "html": "%s"
        }
        """.formatted(to, subject, body);

        Request request = new Request.Builder()
                .url(URL)
                .post(RequestBody.create(json, JSON))
                .addHeader("Authorization", "Bearer %s".formatted(apiKey))
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Email failed: " + response.body().string());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}