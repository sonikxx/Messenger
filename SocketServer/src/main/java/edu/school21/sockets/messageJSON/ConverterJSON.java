package edu.school21.sockets.messageJSON;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConverterJSON {

    public static MessageJSON deserialisation(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        MessageJSON newMessage = null;
        try {
            newMessage = objectMapper.readValue(message, MessageJSON.class);
        } catch (JsonProcessingException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(-1);
        }
        return newMessage;
    }

    public static String serialisation(String msg, Long senderId) {
        try {
            MessageJSON message = new MessageJSON(msg, senderId);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(message);
            return jsonString;
        } catch (JsonProcessingException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }
}
