package org.codevoke.probnb.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.codevoke.probnb.dto.RegisterDTO;
import org.codevoke.probnb.dto.UserDTO;
import org.codevoke.probnb.exceptions.ServerException;
import org.codevoke.probnb.exceptions.UserException;
import org.codevoke.probnb.model.Auth;
import org.codevoke.probnb.model.User;
import org.codevoke.probnb.repository.AuthRepository;
import org.codevoke.probnb.repository.ImageRepository;
import org.codevoke.probnb.repository.UserRepository;
import org.codevoke.probnb.utils.PasswordHasher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService {
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    @Transactional
    public RegisterDTO register(RegisterDTO registerDTO) {
        UserDTO userDTO = convertToUserDTO(registerDTO);

        UserService userService = new UserService(userRepository, imageRepository);

        User user = userService.convertToEntity(userDTO);
        Auth auth = convertToEntity(registerDTO);
        auth.setUser(user);

        try {
            userRepository.save(user);
            authRepository.save(auth);
            return convertToDTO(auth, user);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = e.getMessage();

            if (errorMessage != null && (errorMessage.contains("username") || errorMessage.contains("user_username_unique"))) { //зависит от БД, лучше константы
                throw UserException.UsernameAlreadyExist(userDTO.getUsername());
            } else if (errorMessage != null && (errorMessage.contains("email") || errorMessage.contains("auth_email_unique"))) { //зависит от БД, лучше константы
                throw UserException.EmailAlreadyExists(registerDTO.getEmail());
            } else {
                System.err.println("Unexpected DataIntegrityViolationException: " + errorMessage);
                throw ServerException.DatabaseError("error during registration - " + e.getMessage());
            }
        } catch (Exception e) {
            throw ServerException.DatabaseError("unhandled exception: " + e.getMessage());
        }
    }


    private Auth convertToEntity(RegisterDTO registerDTO) {
        Auth auth = new Auth();
        auth.setEmail(registerDTO.getEmail());
        auth.setPassword(
                PasswordHasher.hash(registerDTO.getPassword())
        );
        return auth;
    }

    private RegisterDTO convertToDTO(Auth auth, User user) {
        return RegisterDTO.builder()
                .id(user.getId())
                .email(auth.getEmail())
                .password(auth.getPassword())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .avatar(user.getAvatar() != null ? user.getAvatar().getId() : null)
                .build();
    }

    private UserDTO convertToUserDTO(RegisterDTO registerDTO) {
        return UserDTO.builder()
                .username(registerDTO.getUsername())
                .firstname(registerDTO.getFirstname())
                .lastname(registerDTO.getLastname())
                .avatar(registerDTO.getAvatar())
                .build();
    }
}
