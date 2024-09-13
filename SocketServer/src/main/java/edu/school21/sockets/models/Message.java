package edu.school21.sockets.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private Long id;
    private Long senderId;
    private String text;
    private Timestamp time;
    private Long roomId;

    public Message(Long senderId, String text, Timestamp time, Long roomId) {
        this.senderId = senderId;
        this.text = text;
        this.time = time;
        this.roomId = roomId;
    }
}
