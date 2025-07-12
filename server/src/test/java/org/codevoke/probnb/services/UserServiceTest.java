package org.codevoke.probnb.services;

import org.codevoke.probnb.dto.UserDTO;
import org.codevoke.probnb.exceptions.UserException;
import org.codevoke.probnb.exceptions.ImageException;
import org.codevoke.probnb.model.Image;
import org.codevoke.probnb.model.User;
import org.codevoke.probnb.repository.ImageRepository;
import org.codevoke.probnb.repository.UserRepository;
import org.codevoke.probnb.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;
    private Image testImage;

    @BeforeEach
    void setUp() {
        testImage = new Image();
        testImage.setId(1L);
        testImage.setPath("/test/path/image.png");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFirstname("Test");
        testUser.setLastname("User");
        testUser.setAvatar(testImage);

        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setUsername("testuser");
        testUserDTO.setFirstname("Test");
        testUserDTO.setLastname("User");
        testUserDTO.setAvatar(1L);
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUserDTO() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // when
        UserDTO result = userService.getUserById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getFirstname()).isEqualTo("Test");
        assertThat(result.getLastname()).isEqualTo("User");
        assertThat(result.getAvatar()).isEqualTo(1L);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowUserException() {
        // given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("User with id 999 not found");
    }

    @Test
    void convertToEntity_WhenUserDTOWithAvatar_ShouldReturnUserWithAvatar() {
        // given
        when(imageRepository.findById(1L)).thenReturn(Optional.of(testImage));

        // when
        User result = userService.convertToEntity(testUserDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getFirstname()).isEqualTo("Test");
        assertThat(result.getLastname()).isEqualTo("User");
        assertThat(result.getAvatar()).isEqualTo(testImage);
    }

    @Test
    void convertToEntity_WhenUserDTOWithoutAvatar_ShouldReturnUserWithoutAvatar() {
        // given
        testUserDTO.setAvatar(null);

        // when
        User result = userService.convertToEntity(testUserDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getAvatar()).isNull();
    }

    @Test
    void convertToEntity_WhenAvatarNotFound_ShouldThrowImageException() {
        // given
        when(imageRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.convertToEntity(testUserDTO))
                .isInstanceOf(ImageException.class);
    }

    @Test
    void convertToDTO_WhenUserWithAvatar_ShouldReturnUserDTOWithAvatarId() {
        // when
        UserDTO result = userService.convertToDTO(testUser);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getAvatar()).isEqualTo(1L);
    }

    @Test
    void convertToDTO_WhenUserWithoutAvatar_ShouldReturnUserDTOWithoutAvatar() {
        // given
        testUser.setAvatar(null);

        // when
        UserDTO result = userService.convertToDTO(testUser);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getAvatar()).isNull();
    }
} 