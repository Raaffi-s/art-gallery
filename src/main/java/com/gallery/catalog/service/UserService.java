package com.gallery.catalog.service;

import com.gallery.catalog.dto.UserDto;
import com.gallery.catalog.model.User;
import com.gallery.catalog.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private static final String USER_NOT_FOUND_MESSAGE = "User not found";

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBio(user.getBio());

        if (user.getPaintings() != null) {
            dto.setPaintingsCount(user.getPaintings().size());
        }
        if (user.getGalleries() != null) {
            dto.setGalleriesCount(user.getGalleries().size());
        }

        return dto;
    }

    private void updateUserFromDto(User user, UserDto dto) {
        user.setUsername(dto.getUsername().trim());
        user.setEmail(dto.getEmail().trim());
        user.setFullName(dto.getFullName());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setBio(dto.getBio());
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::convertToDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findWithDetailsById(id)
            .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_MESSAGE + id));
        return convertToDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username.trim())
            .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_MESSAGE + username));
        return convertToDto(user);
    }

    @Transactional
    public UserDto createUser(UserDto dto) {
        validateUserDto(dto);

        User user = new User();
        updateUserFromDto(user, dto);
        User saved = userRepository.save(user);
        return convertToDto(saved);
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto dto) {
        validateUserDto(dto);

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        updateUserFromDto(user, dto);
        User updated = userRepository.save(user);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    private void validateUserDto(UserDto dto) {
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
    }
}
