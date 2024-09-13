package edu.sockets.repositories;

import edu.sockets.models.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component("RoomRepositoryImpl")
public class RoomRepositoryImpl implements RoomRepository {
    private JdbcTemplate dataSource;

    @Autowired
    public RoomRepositoryImpl(DataSource dataSource) {
        this.dataSource = new JdbcTemplate(dataSource);
        initTable();
    }

    public void initTable() {
        String queryDrop = "DROP TABLE IF EXISTS chat.room CASCADE";
        String queryCreate = "CREATE TABLE IF NOT EXISTS chat.room (\n" +
                "\tid SERIAl PRIMARY KEY,\n" +
                "\towner INTEGER REFERENCES chat.user(id),\n" +
                "\tname VARCHAR(200)\n" +
                ");\n";
        dataSource.execute(queryDrop);
        dataSource.execute(queryCreate);
    }

    @Override
    public Optional<Room> findById(Long id) {
        String query = "SELECT * FROM chat.room WHERE id = ?";
        Room room = this.dataSource.queryForObject(query, new Object[]{id}, new RowMapper<Room>() {
            public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Room(rs.getLong("id"), rs.getString("name"), rs.getLong("owner"));
            }
        });
        return Optional.ofNullable(room);
    }

    @Override
    public List<Room> findAll() {
        String query = "SELECT * FROM chat.room";
        return this.dataSource.query(query,
                (rs, rowNum) -> new Room(rs.getLong("id"), rs.getString("name"), rs.getLong("owner")));
    }

    @Override
    public void save(Room entity) {
        if (entity == null)
            throw new RuntimeException("Invalid save null entity");
        String query = "INSERT INTO chat.room (owner, name) VALUES(?, ?)";
        int affectedRows = this.dataSource.update(query, new Object[]{entity.getOwnerId(), entity.getName()});
        if (affectedRows != 1)
            throw new RuntimeException("Invalid save room id=" + entity.getId());
    }

    @Override
    public void update(Room entity) {
        if (entity == null)
            throw new RuntimeException("Invalid update null entity");
        String query = "UPDATE chat.room SET owner = ?, name = ? WHERE id = ?";
        int affectedRows = this.dataSource.update(query, new Object[]{entity.getOwnerId(), entity.getName(), entity.getId()});
        if (affectedRows != 1)
            throw new RuntimeException("Invalid update room id=" + entity.getId());
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM chat.room where id = ?";
        int affectedRows = this.dataSource.update(query, new Object[]{id});
        if (affectedRows != 1)
            throw new RuntimeException("Invalid delete room id=" + id);
    }

    @Override
    public Optional<Room> findByName(String name) {
        String query = "SELECT * FROM chat.room WHERE name = ?";
        Room room = this.dataSource.queryForObject(query, new Object[]{name}, new RowMapper<Room>() {
            public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Room(rs.getLong("id"), rs.getString("name"), rs.getLong("owner"));
            }
        });
        return Optional.ofNullable(room);
    }
}
