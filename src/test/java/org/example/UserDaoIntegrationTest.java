package org.example;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.example.dao.UserDao;
import org.example.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserDaoIntegrationTest {


    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test_db")
            .withUsername("user")
            .withPassword("pass");

    private UserDao userDao;

    @BeforeAll
    void setUp() {
        postgresContainer.start();

        TestHibernateUtil.configure(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword()
        );

        userDao = new UserDao();
    }

    @AfterAll
    void tearDown() {
        TestHibernateUtil.shutdown();
        postgresContainer.stop();
    }

    @Test
    void testSaveAndFindById() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");

        userDao.save(user);

        Optional<User> found = userDao.findById(user.getId());
        assertTrue(found.isPresent());
        assertEquals("Alice", found.get().getName());
    }

    @Test
    void testFindAll() {
        User user1 = new User();
        user1.setName("John");
        user1.setEmail("john@example.com");

        User user2 = new User();
        user2.setName("Jane");
        user2.setEmail("jane@example.com");

        userDao.save(user1);
        userDao.save(user2);

        List<User> all = userDao.findAll();
        assertTrue(all.size() >= 2); // Можно проверять конкретно, если база чистая
    }

    @Test
    void testUpdate() {
        User user = new User();
        user.setName("Bob");
        user.setEmail("bob@example.com");
        userDao.save(user);

        user.setName("Robert");
        userDao.update(user);

        Optional<User> updated = userDao.findById(user.getId());
        assertTrue(updated.isPresent());
        assertEquals("Robert", updated.get().getName());
    }

    @Test
    void testDelete() {
        User user = new User();
        user.setName("ToDelete");
        user.setEmail("delete@example.com");
        userDao.save(user);

        userDao.delete(user.getId());

        Optional<User> deleted = userDao.findById(user.getId());
        assertTrue(deleted.isEmpty());
    }
}
