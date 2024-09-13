package edu.sockets.repositories;

import edu.sockets.models.Room;

import java.util.Optional;

public interface RoomRepository extends CrudRepository<Room>{
    Optional<Room> findByName(String name);
}
