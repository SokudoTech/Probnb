package org.codevoke.probnb.service;

import lombok.RequiredArgsConstructor;

import org.codevoke.probnb.exceptions.*;
import org.codevoke.probnb.model.User;
import org.codevoke.probnb.dto.UserDTO;
import org.codevoke.probnb.repository.UserRepository;
import org.codevoke.probnb.repository.ImageRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    public UserDTO getUserById(Long id) {
        return convertToDTO(getUserEntityById(id));
    }

    protected User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> UserException.UserNotFound(id));
    }

    protected User convertToEntity(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        if (userDTO.getAvatar() != null)
            user.setAvatar(
                    imageRepository.findById(userDTO.getAvatar())
                            .orElseThrow(() -> ImageException.ImageNotFound(userDTO.getAvatar())));
        return user;
    }

    // convert entity to dto
    protected UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setAvatar(user.getAvatar().getId());
        return userDTO;
    }
}