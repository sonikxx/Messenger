package edu.school21.sockets.services;

import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.Room;
import edu.school21.sockets.repositories.MessageRepository;
import edu.school21.sockets.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import edu.school21.sockets.models.User;
import edu.school21.sockets.repositories.UsersRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component("UsersServiceImpl")
public class UsersServiceImpl implements UsersService {
    private UsersRepository usersRepository;
    private PasswordEncoder passwordEncoder;
    private MessageRepository messageRepository;
    private RoomRepository roomRepository;

    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder, MessageRepository messageRepository, RoomRepository roomRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public void signUp(String email, String password) {
        if (usersRepository.findByEmail(email).isPresent())
            throw new RuntimeException("User with login " + email +" already exist");
        User user = new User(email, passwordEncoder.encode(password));
        usersRepository.save(user);
    }

    @Override
    public Long signIn(String email, String password) {
        Optional<User> user = usersRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword()) )
            return user.get().getId();
        return null;
    }

    @Override
    public void createMessage(User sender, String text, Long room) {
        LocalDateTime now = LocalDateTime.now();
        messageRepository.save(new Message(sender.getId(), text, Timestamp.valueOf(now), room));
    }

    @Override
    public Optional<User> getUser(String email) {
        return usersRepository.findByEmail(email);
    }

    @Override
    public List<Message> getMessages(Long roomId) {
        return messageRepository.findByRoom(roomId);
    }

    @Override
    public Long createRoom(User user, String name) {
        Room room = new Room(name, user.getId());
        roomRepository.save(room);
        room = roomRepository.findByName(name).get();
        return room.getId();
    }

    @Override
    public List<Room> findAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public void updateRoom(User user, Long roomId) {
        user.setLastRoomId(roomId);
        usersRepository.update(user);
    }
}
