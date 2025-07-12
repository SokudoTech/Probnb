package org.codevoke.probnb.repositories;

import org.codevoke.probnb.model.Room;
import org.codevoke.probnb.model.User;
import org.codevoke.probnb.repository.RoomRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RoomRepositoryTest {

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
    private RoomRepository roomRepository;

    @Test
    void findByHostId_WhenRoomsExist_ShouldReturnRooms() {
        // given
        User host = new User();
        host.setUsername("testhost");
        host.setFirstname("Test");
        host.setLastname("Host");
        User savedHost = entityManager.persistAndFlush(host);

        Room room1 = new Room();
        room1.setTitle("Room 1");
        room1.setHost(savedHost);
        room1.setLocation("City 1");
        room1.setRoomType("Apartment");
        entityManager.persistAndFlush(room1);

        Room room2 = new Room();
        room2.setTitle("Room 2");
        room2.setHost(savedHost);
        room2.setLocation("City 2");
        room2.setRoomType("House");
        entityManager.persistAndFlush(room2);

        // when
        List<Room> found = roomRepository.findByHostId(savedHost.getId());

        // then
        assertThat(found).hasSize(2);
        assertThat(found.get(0).getTitle()).isEqualTo("Room 1");
        assertThat(found.get(1).getTitle()).isEqualTo("Room 2");
    }

    @Test
    void findByHostId_WhenNoRooms_ShouldReturnEmptyList() {
        // given
        User host = new User();
        host.setUsername("emptyhost");
        host.setFirstname("Empty");
        host.setLastname("Host");
        User savedHost = entityManager.persistAndFlush(host);

        // when
        List<Room> found = roomRepository.findByHostId(savedHost.getId());

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void findByRoomTypeContainingIgnoreCase_WhenRoomsExist_ShouldReturnRooms() {
        // given
        User host = new User();
        host.setUsername("typehost");
        host.setFirstname("Type");
        host.setLastname("Host");
        User savedHost = entityManager.persistAndFlush(host);

        Room apartment = new Room();
        apartment.setTitle("Apartment Room");
        apartment.setHost(savedHost);
        apartment.setRoomType("Apartment");
        entityManager.persistAndFlush(apartment);

        Room house = new Room();
        house.setTitle("House Room");
        house.setHost(savedHost);
        house.setRoomType("House");
        entityManager.persistAndFlush(house);

        // when
        List<Room> found = roomRepository.findByRoomTypeContainingIgnoreCase("apartment");

        // then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getRoomType()).isEqualTo("Apartment");
    }

    @Test
    void findByLocationContainingIgnoreCase_WhenRoomsExist_ShouldReturnRooms() {
        // given
        User host = new User();
        host.setUsername("locationhost");
        host.setFirstname("Location");
        host.setLastname("Host");
        User savedHost = entityManager.persistAndFlush(host);

        Room room1 = new Room();
        room1.setTitle("Moscow Room");
        room1.setHost(savedHost);
        room1.setLocation("Moscow");
        entityManager.persistAndFlush(room1);

        Room room2 = new Room();
        room2.setTitle("SPb Room");
        room2.setHost(savedHost);
        room2.setLocation("Saint Petersburg");
        entityManager.persistAndFlush(room2);

        // when
        List<Room> found = roomRepository.findByLocationContainingIgnoreCase("moscow");

        // then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getLocation()).isEqualTo("Moscow");
    }

    @Test
    void findByRoomTypeContainingIgnoreCaseAndRoomsCountAndLocationContainingIgnoreCase_WhenRoomsExist_ShouldReturnRooms() {
        // given
        User host = new User();
        host.setUsername("filterhost");
        host.setFirstname("Filter");
        host.setLastname("Host");
        User savedHost = entityManager.persistAndFlush(host);

        Room room1 = new Room();
        room1.setTitle("Filtered Room");
        room1.setHost(savedHost);
        room1.setRoomType("Apartment");
        room1.setRoomsCount(2);
        room1.setLocation("Moscow");
        entityManager.persistAndFlush(room1);

        Room room2 = new Room();
        room2.setTitle("Other Room");
        room2.setHost(savedHost);
        room2.setRoomType("House");
        room2.setRoomsCount(3);
        room2.setLocation("SPb");
        entityManager.persistAndFlush(room2);

        // when
        List<Room> found = roomRepository.findByRoomTypeContainingIgnoreCaseAndRoomsCountAndLocationContainingIgnoreCase(
                "apartment", 2, "moscow");

        // then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getTitle()).isEqualTo("Filtered Room");
    }

    @Test
    void save_WhenNewRoom_ShouldPersistRoom() {
        // given
        User host = new User();
        host.setUsername("savehost");
        host.setFirstname("Save");
        host.setLastname("Host");
        User savedHost = entityManager.persistAndFlush(host);

        Room room = new Room();
        room.setTitle("New Room");
        room.setHost(savedHost);
        room.setLocation("New City");
        room.setRoomType("Apartment");

        // when
        Room saved = roomRepository.save(room);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("New Room");
        
        // verify it's actually persisted
        Room found = entityManager.find(Room.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("New Room");
    }

    @Test
    void findById_WhenRoomExists_ShouldReturnRoom() {
        // given
        User host = new User();
        host.setUsername("findhost");
        host.setFirstname("Find");
        host.setLastname("Host");
        User savedHost = entityManager.persistAndFlush(host);

        Room room = new Room();
        room.setTitle("Find Room");
        room.setHost(savedHost);
        Room saved = roomRepository.save(room);

        // when
        Optional<Room> found = roomRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Find Room");
    }

    @Test
    void findById_WhenRoomDoesNotExist_ShouldReturnEmpty() {
        // when
        Optional<Room> found = roomRepository.findById(999L);

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void delete_WhenRoomExists_ShouldRemoveRoom() {
        // given
        User host = new User();
        host.setUsername("deletehost");
        host.setFirstname("Delete");
        host.setLastname("Host");
        User savedHost = entityManager.persistAndFlush(host);

        Room room = new Room();
        room.setTitle("Delete Room");
        room.setHost(savedHost);
        Room saved = roomRepository.save(room);

        // when
        roomRepository.delete(saved);

        // then
        Optional<Room> found = roomRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }
} 