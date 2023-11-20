package com.peepersoak.adventurecraftcore.openAI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.peepersoak.adventurecraftcore.AdventureCraftCore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OpenAI {
    private final String OPEN_AI_KEY;

    public OpenAI() {
        OPEN_AI_KEY = AdventureCraftCore.getInstance().getConfig().getString("OPEN_AI_KEY");
    }

    public String generate(String message, String instruction) {
        try {
            String ENDPOINT = "https://api.openai.com/v1/chat/completions";
            URL url = new URL(ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            // Set headers, including the Authorization header with your API key
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + OPEN_AI_KEY);
            // Enable input and output streams
            connection.setDoOutput(true);

            // Send the request body (JSON payload)
            // Adjust the payload based on the specific OpenAI API and task
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode messages = objectMapper.createArrayNode();

            ObjectNode system = objectMapper.createObjectNode();
            system.put("role", "system");
            system.put("content", instruction);

            ObjectNode user = objectMapper.createObjectNode();
            user.put("role", "user");
            user.put("content", message);

            messages.add(system);
            messages.add(user);

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("model", "gpt-4-1106-preview");
            objectNode.set("messages", messages);

            String jsonString = objectMapper.writeValueAsString(objectNode);

            // Get the output stream from the connection
            try (OutputStream outputStream = connection.getOutputStream()) {
                // Write the JSON string to the output stream
                byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }

            // Get the API response
            int responseCode = connection.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            System.out.println(response);

            ObjectMapper reponseMap = new ObjectMapper();
            JsonNode responseNode = reponseMap.readTree(response.toString());

            String finalSettings = null;
            if (responseNode.has("choices") && responseNode.get("choices").isArray()) {
                ArrayNode choices = (ArrayNode) responseNode.get("choices");
                System.out.println(1);
                if (!choices.isEmpty()) {
                    System.out.println(2);
                    JsonNode choice = choices.get(0);
                    if (choice.has("message")) {
                        System.out.println(3);
                        JsonNode messageObj = choice.get("message");
                        if (messageObj.has("content")) {
                            System.out.println(3);
                            String content = messageObj.get("content").asText();
                            finalSettings = content;
                            System.out.println("Content: " + content);
                        }
                    }
                }
            }

            return finalSettings;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
