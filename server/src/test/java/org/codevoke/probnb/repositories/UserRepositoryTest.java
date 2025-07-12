package org.codevoke.probnb.repositories;

import org.codevoke.probnb.model.User;
import org.codevoke.probnb.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("probnb_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @AfterAll
    static void tearDown() {
        if (postgres != null && postgres.isRunning()) {
            postgres.stop();
        }
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setFirstname("Test");
        user.setLastname("User");
        entityManager.persistAndFlush(user);

        // when
        Optional<User> found = userRepository.findByUsername("testuser");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getFirstname()).isEqualTo("Test");
        assertThat(found.get().getLastname()).isEqualTo("User");
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // when
        Optional<User> found = userRepository.findByUsername("nonexistent");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void save_WhenNewUser_ShouldPersistUser() {
        // given
        User user = new User();
        user.setUsername("newuser");
        user.setFirstname("New");
        user.setLastname("User");

        // when
        User saved = userRepository.save(user);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("newuser");
        
        // verify it's actually persisted
        User found = entityManager.find(User.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("newuser");
    }

    @Test
    void save_WhenExistingUser_ShouldUpdateUser() {
        // given
        User user = new User();
        user.setUsername("updateuser");
        user.setFirstname("Original");
        user.setLastname("User");
        User saved = userRepository.save(user);

        // when
        saved.setFirstname("Updated");
        User updated = userRepository.save(saved);

        // then
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getFirstname()).isEqualTo("Updated");
        
        // verify it's actually updated in database
        User found = entityManager.find(User.class, saved.getId());
        assertThat(found.getFirstname()).isEqualTo("Updated");
    }

    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        // given
        User user = new User();
        user.setUsername("finduser");
        user.setFirstname("Find");
        user.setLastname("User");
        User saved = userRepository.save(user);

        // when
        Optional<User> found = userRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("finduser");
    }

    @Test
    void findById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // when
        Optional<User> found = userRepository.findById(999L);

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void delete_WhenUserExists_ShouldRemoveUser() {
        // given
        User user = new User();
        user.setUsername("deleteuser");
        user.setFirstname("Delete");
        user.setLastname("User");
        User saved = userRepository.save(user);

        // when
        userRepository.delete(saved);

        // then
        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }
} 