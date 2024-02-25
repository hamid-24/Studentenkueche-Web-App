package de.hsflensburg.certainlyuncertainse2hausarbeit.tables;

import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTable extends JpaRepository<User, Integer> {
    User findByUsername(String username);
}
