package org.codevoke.probnb.repositories;

import org.codevoke.probnb.model.Auth;
import org.codevoke.probnb.model.User;
import org.codevoke.probnb.repository.AuthRepository;
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
class AuthRepositoryTest {

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
    private AuthRepository authRepository;

    @Test
    void findByEmail_WhenAuthExists_ShouldReturnAuth() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setFirstname("Test");
        user.setLastname("User");
        entityManager.persistAndFlush(user);

        Auth auth = new Auth();
        auth.setEmail("test@example.com");
        auth.setPassword("hashedpassword");
        auth.setUser(user);
        entityManager.persistAndFlush(auth);

        // when
        Optional<Auth> found = authRepository.findByEmail("test@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getPassword()).isEqualTo("hashedpassword");
        assertThat(found.get().getUser().getUsername()).isEqualTo("testuser");
    }

    @Test
    void findByEmail_WhenAuthDoesNotExist_ShouldReturnEmpty() {
        // when
        Optional<Auth> found = authRepository.findByEmail("nonexistent@example.com");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void save_WhenNewAuth_ShouldPersistAuth() {
        // given
        User user = new User();
        user.setUsername("newuser");
        user.setFirstname("New");
        user.setLastname("User");
        User savedUser = entityManager.persistAndFlush(user);

        Auth auth = new Auth();
        auth.setEmail("new@example.com");
        auth.setPassword("newhashedpassword");
        auth.setUser(savedUser);

        // when
        Auth saved = authRepository.save(auth);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("new@example.com");
        assertThat(saved.getPassword()).isEqualTo("newhashedpassword");
        
        // verify it's actually persisted
        Auth found = entityManager.find(Auth.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void save_WhenExistingAuth_ShouldUpdateAuth() {
        // given
        User user = new User();
        user.setUsername("updateuser");
        user.setFirstname("Update");
        user.setLastname("User");
        User savedUser = entityManager.persistAndFlush(user);

        Auth auth = new Auth();
        auth.setEmail("update@example.com");
        auth.setPassword("originalpassword");
        auth.setUser(savedUser);
        Auth saved = authRepository.save(auth);

        // when
        saved.setPassword("updatedpassword");
        Auth updated = authRepository.save(saved);

        // then
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getPassword()).isEqualTo("updatedpassword");
        
        // verify it's actually updated in database
        Auth found = entityManager.find(Auth.class, saved.getId());
        assertThat(found.getPassword()).isEqualTo("updatedpassword");
    }

    @Test
    void findById_WhenAuthExists_ShouldReturnAuth() {
        // given
        User user = new User();
        user.setUsername("finduser");
        user.setFirstname("Find");
        user.setLastname("User");
        User savedUser = entityManager.persistAndFlush(user);

        Auth auth = new Auth();
        auth.setEmail("find@example.com");
        auth.setPassword("findpassword");
        auth.setUser(savedUser);
        Auth saved = authRepository.save(auth);

        // when
        Optional<Auth> found = authRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("find@example.com");
    }

    @Test
    void findById_WhenAuthDoesNotExist_ShouldReturnEmpty() {
        // when
        Optional<Auth> found = authRepository.findById(999L);

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void delete_WhenAuthExists_ShouldRemoveAuth() {
        // given
        User user = new User();
        user.setUsername("deleteuser");
        user.setFirstname("Delete");
        user.setLastname("User");
        User savedUser = entityManager.persistAndFlush(user);

        Auth auth = new Auth();
        auth.setEmail("delete@example.com");
        auth.setPassword("deletepassword");
        auth.setUser(savedUser);
        Auth saved = authRepository.save(auth);

        // when
        authRepository.delete(saved);

        // then
        Optional<Auth> found = authRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }
} 