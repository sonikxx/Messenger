package edu.school21.sockets.messageJSON;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageJSON {
    private String text;
    private Long senderId;
}
