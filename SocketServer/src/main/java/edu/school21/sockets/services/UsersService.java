package edu.school21.sockets.services;

import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.Room;
import edu.school21.sockets.models.User;

import java.util.List;
import java.util.Optional;

public interface UsersService {
    void signUp(String email, String password);
    Long signIn(String email, String password);
    void createMessage(User sender, String text, Long room);
    Optional<User> getUser(String email);
    List<Message> getMessages(Long roomId);
    Long createRoom(User user, String name);
    List<Room> findAllRooms();
    void updateRoom(User user, Long roomId);
}
