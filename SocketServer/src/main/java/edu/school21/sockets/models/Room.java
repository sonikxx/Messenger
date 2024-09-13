package edu.school21.sockets.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    private Long id;
    private String name;
    private Long ownerId;

    public Room(String name, Long ownerId) {
        this.name = name;
        this.ownerId = ownerId;
    }
}
