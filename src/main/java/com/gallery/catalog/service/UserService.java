package com.gallery.catalog.service;

import com.gallery.catalog.dto.UserDto;
import com.gallery.catalog.exception.UserNotFoundException;
import com.gallery.catalog.model.User;
import com.gallery.catalog.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserDto convertToDto(User user) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getAvatarUrl(),
            user.getBio(),
            user.getGalleries() != null ? user.getGalleries().size() : null
        );
    }

    private void updateUserFromDto(User user, UserDto dto) {
        user.setUsername(dto.username().trim());
        user.setEmail(dto.email().trim());
        user.setFullName(dto.fullName());
        user.setAvatarUrl(dto.avatarUrl());
        user.setBio(dto.bio());
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
            .orElseThrow(() -> new UserNotFoundException(id));
        return convertToDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username.trim())
            .orElseThrow(() -> new UserNotFoundException(username));
        return convertToDto(user);
    }

    @Transactional
    public UserDto createUser(UserDto dto) {
        validateUserDto(dto);
        User user = new User();
        updateUserFromDto(user, dto);
        return convertToDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto dto) {
        validateUserDto(dto);
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        updateUserFromDto(user, dto);
        return convertToDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    private void validateUserDto(UserDto dto) {
        if (dto.username() == null || dto.username().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (dto.email() == null || dto.email().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
    }
}
