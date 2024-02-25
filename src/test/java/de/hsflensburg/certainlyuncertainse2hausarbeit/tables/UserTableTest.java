package de.hsflensburg.certainlyuncertainse2hausarbeit.tables;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import java.util.Random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

@DataJpaTest
public class UserTableTest {

    @Autowired
    private UserTable userTable;

    @Test
    void testSaveUser() {
        User user = new User("geralt", "ompf123");
        User user2 = new User();
        User savedUser = userTable.save(user);


        assertThatException().isThrownBy(() -> {
            userTable.save(user2);
        });
        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(user);
        assertThat(savedUser.getId()).isEqualTo(user.getId());
    }

    @Test
    void testFindByUsername() {
        User user = new User("hardy", "ujli098");

        userTable.save(user);
        User savedUser = userTable.findByUsername(user.getUsername());

        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(user);
        assertThat(savedUser.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    void testFindById() {
        User user = new User("jason", "mehrj");
        user.setId(new Random().nextInt(1000));

        userTable.save(user);
        User savedUser = userTable.findById(user.getId()).get();

        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(user);
    }
}