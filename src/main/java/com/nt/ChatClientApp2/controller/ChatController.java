package com.nt.ChatClientApp2.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import com.nt.ChatClientApp2.model.ChatRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RestController
@RequestMapping("/chat")
@CrossOrigin
public class ChatController {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private static final MediaType JSON = MediaType.parse("application/json");

    @PostMapping("/ask")
    public String askGroq(@org.springframework.web.bind.annotation.RequestBody ChatRequest request) throws IOException {

        String msg = request.getMessage().replace("\"", "\\\"");

        String json = "{"
                + "\"model\":\"llama-3.3-70b-versatile\","
                + "\"messages\":[{\"role\":\"user\",\"content\":\"" + msg + "\"}]"
                + "}";

        RequestBody body = RequestBody.create(JSON, json);

        Request req = new Request.Builder()
                .url("https://api.groq.com/openai/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + groqApiKey)
                .post(body)
                .build();

        Response res = client.newCall(req).execute();
        String responseBody = res.body().string();

        // Parse to extract content only
        JsonNode root = mapper.readTree(responseBody);
        JsonNode contentNode = root.path("choices").get(0).path("message").path("content");

        if (!contentNode.isMissingNode()) {
            return contentNode.asText();
        }

        return "No response";
    }
}
