package edu.school21.sockets.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String email;
    private String password;
    private Long lastRoomId;

    public User(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.lastRoomId = -1L;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.lastRoomId = -1L;
    }
}
