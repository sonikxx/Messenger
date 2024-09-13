package edu.school21.sockets.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import edu.school21.sockets.models.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component("UsersRepositoryImpl")
public class UsersRepositoryImpl implements UsersRepository {
    private JdbcTemplate dataSource;

    @Autowired
    public UsersRepositoryImpl(DataSource dataSource) {
        this.dataSource = new JdbcTemplate(dataSource);
        initTable();
    }

    private void initTable() {
        String queryDrop = "DROP TABLE IF EXISTS chat.user CASCADE";
        String queryCreate = "CREATE TABLE IF NOT EXISTS chat.user (\n" +
                "\tid SERIAl PRIMARY KEY,\n" +
                "\temail VARCHAR(50),\n" +
                "\t\"password\" VARCHAR(100),\n" +
                "\troom INTEGER\n" +
                ");\n";
        dataSource.execute(queryDrop);
        dataSource.execute(queryCreate);
    }

    @Override
    public Optional<User> findById(Long id) {
        String query = "SELECT * FROM chat.user WHERE id = ?";
        User user = this.dataSource.queryForObject(query, new Object[]{id}, new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new User(rs.getLong("id"), rs.getString("email"), rs.getString("password"));
            }
        });
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String query = "SELECT * FROM chat.user WHERE email = ?";
        try {
            User user = this.dataSource.queryForObject(query, new Object[]{email}, new RowMapper<User>() {
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new User(rs.getLong("id"), rs.getString("email"), rs.getString("password"), rs.getLong("room"));
                }
            });
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        String query = "SELECT * FROM chat.user";
        return this.dataSource.query(query,
                (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("email"), rs.getString("password")));
    }

    @Override
    public void save(User entity) {
        if (entity == null)
            throw new RuntimeException("Invalid save null entity");
        String query = "INSERT INTO chat.user (email, \"password\", room) VALUES(?, ?, ?)";
        int affectedRows = this.dataSource.update(query, new Object[]{entity.getEmail(), entity.getPassword(), entity.getLastRoomId()});
        if (affectedRows != 1)
            throw new RuntimeException("Invalid save user id=" + entity.getId());
    }

    @Override
    public void update(User entity) {
        if (entity == null)
            throw new RuntimeException("Invalid update null entity");
        String query = "UPDATE chat.user SET email = ?, \"password\" = ? , room = ? WHERE id = ?";
        int affectedRows = this.dataSource.update(query, new Object[]{entity.getEmail(), entity.getPassword(), entity.getLastRoomId(), entity.getId()});
        if (affectedRows != 1)
            throw new RuntimeException("Invalid update user id=" + entity.getId());
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM chat.user where id = ?";
        int affectedRows = this.dataSource.update(query, new Object[]{id});
        if (affectedRows != 1)
            throw new RuntimeException("Invalid delete user id=" + id);
    }
}
