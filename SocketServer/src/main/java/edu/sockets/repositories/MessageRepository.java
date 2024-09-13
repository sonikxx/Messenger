package edu.sockets.repositories;

import edu.sockets.models.Message;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message>{
    List<Message> findByRoom(Long roomId);
}
