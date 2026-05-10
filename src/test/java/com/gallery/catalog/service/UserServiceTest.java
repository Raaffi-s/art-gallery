package com.gallery.catalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gallery.catalog.dto.UserDto;
import com.gallery.catalog.model.Gallery;
import com.gallery.catalog.model.User;
import com.gallery.catalog.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsersReturnsDtos() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setFullName("Alice");
        user.setAvatarUrl("avatar");
        user.setBio("bio");
        user.setGalleries(List.of(new Gallery(), new Gallery()));

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).username()).isEqualTo("alice");
        assertThat(result.get(0).email()).isEqualTo("alice@example.com");
        assertThat(result.get(0).galleriesCount()).isEqualTo(2);
    }

    @Test
    void getAllUsersReturnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).isEmpty();
    }

    @Test
    void getAllUsersReturnsNullGalleriesCountWhenGalleriesAreNull() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setGalleries(null);

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).galleriesCount()).isNull();
    }

    @Test
    void getUserByIdReturnsDto() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");

        when(userRepository.findWithDetailsById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.username()).isEqualTo("alice");
        assertThat(result.email()).isEqualTo("alice@example.com");
    }

    @Test
    void getUserByIdReturnsNullGalleriesCountWhenDetailsContainNoGalleries() {
        User user = new User();
        user.setId(10L);
        user.setUsername("bob");
        user.setEmail("bob@example.com");
        user.setGalleries(null);

        when(userRepository.findWithDetailsById(10L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(10L);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.galleriesCount()).isNull();
    }

    @Test
    void getUserByIdThrowsWhenMissing() {
        when(userRepository.findWithDetailsById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found: 99");
    }

    @Test
    void getUserByUsernameTrimsInput() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        UserDto result = userService.getUserByUsername("  alice  ");

        assertThat(result.username()).isEqualTo("alice");
        assertThat(result.email()).isEqualTo("alice@example.com");
    }

    @Test
    void getUserByUsernameReturnsNullGalleriesCountWhenUserHasNoGalleries() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setGalleries(null);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        UserDto result = userService.getUserByUsername(" alice ");

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.galleriesCount()).isNull();
    }

    @Test
    void getUserByUsernameThrowsWhenMissing() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByUsername("ghost"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found: ghost");
    }

    @Test
    void createUserSavesTrimmedFields() {
        User saved = new User();
        saved.setId(1L);
        saved.setUsername("alice");
        saved.setEmail("alice@example.com");
        saved.setFullName("Alice");
        saved.setAvatarUrl("avatar");
        saved.setBio("bio");

        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserDto dto = new UserDto(
            null, "  alice  ", "  alice@example.com  ", "Alice", "avatar", "bio", null
        );

        UserDto result = userService.createUser(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        assertThat(captor.getValue().getUsername()).isEqualTo("alice");
        assertThat(captor.getValue().getEmail()).isEqualTo("alice@example.com");
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.username()).isEqualTo("alice");
    }

    @Test
    void createUserStoresOptionalFieldsToo() {
        User saved = new User();
        saved.setId(1L);
        saved.setUsername("alice");
        saved.setEmail("alice@example.com");
        saved.setFullName("Alice Doe");
        saved.setAvatarUrl("avatar");
        saved.setBio("bio");

        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserDto result = userService.createUser(
            new UserDto(null, " alice ", " alice@example.com ", "Alice Doe", "avatar", "bio", null)
        );

        assertThat(result.fullName()).isEqualTo("Alice Doe");
        assertThat(result.avatarUrl()).isEqualTo("avatar");
        assertThat(result.bio()).isEqualTo("bio");
    }

    @Test
    void createUserAcceptsTrimmedUsernameAndEmail() {
        User saved = new User();
        saved.setId(2L);
        saved.setUsername("bob");
        saved.setEmail("bob@example.com");

        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserDto result = userService.createUser(
            new UserDto(null, "  bob  ", "  bob@example.com  ", null, null, null, null)
        );

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.username()).isEqualTo("bob");
        assertThat(result.email()).isEqualTo("bob@example.com");
    }

    @Test
    void createUserThrowsWhenUsernameMissing() {
        UserDto dto = new UserDto(null, "   ", "alice@example.com", null, null, null, null);

        assertThatThrownBy(() -> userService.createUser(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Username is required");
    }

    @Test
    void createUserThrowsWhenUsernameIsNull() {
        UserDto dto = new UserDto(null, null, "alice@example.com", null, null, null, null);

        assertThatThrownBy(() -> userService.createUser(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Username is required");
    }

    @Test
    void createUserThrowsWhenEmailMissing() {
        UserDto dto = new UserDto(null, "alice", "   ", null, null, null, null);

        assertThatThrownBy(() -> userService.createUser(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Email is required");
    }

    @Test
    void createUserThrowsWhenEmailIsNull() {
        UserDto dto = new UserDto(null, "alice", null, null, null, null, null);

        assertThatThrownBy(() -> userService.createUser(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Email is required");
    }

    @Test
    void updateUserUpdatesExistingUser() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("old");
        existing.setEmail("old@example.com");

        User saved = new User();
        saved.setId(1L);
        saved.setUsername("newUser");
        saved.setEmail("new@example.com");
        saved.setFullName("New Name");
        saved.setAvatarUrl("avatar");
        saved.setBio("bio");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(saved);

        UserDto dto = new UserDto(
            null, "  newUser  ", "  new@example.com  ", "New Name", "avatar", "bio", null
        );

        UserDto result = userService.updateUser(1L, dto);

        assertThat(existing.getUsername()).isEqualTo("newUser");
        assertThat(existing.getEmail()).isEqualTo("new@example.com");
        assertThat(existing.getFullName()).isEqualTo("New Name");
        assertThat(result.username()).isEqualTo("newUser");
        assertThat(result.email()).isEqualTo("new@example.com");
    }

    @Test
    void updateUserUpdatesAllOptionalFields() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("old");
        existing.setEmail("old@example.com");
        existing.setFullName("Old Name");
        existing.setAvatarUrl("old-avatar");
        existing.setBio("old-bio");

        User saved = new User();
        saved.setId(1L);
        saved.setUsername("new");
        saved.setEmail("new@example.com");
        saved.setFullName("New Name");
        saved.setAvatarUrl("new-avatar");
        saved.setBio("new-bio");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(saved);

        UserDto result = userService.updateUser(
            1L,
            new UserDto(null, " new ", " new@example.com ", "New Name", "new-avatar", "new-bio", null)
        );

        assertThat(existing.getUsername()).isEqualTo("new");
        assertThat(existing.getEmail()).isEqualTo("new@example.com");
        assertThat(existing.getFullName()).isEqualTo("New Name");
        assertThat(existing.getAvatarUrl()).isEqualTo("new-avatar");
        assertThat(existing.getBio()).isEqualTo("new-bio");
        assertThat(result.fullName()).isEqualTo("New Name");
        assertThat(result.avatarUrl()).isEqualTo("new-avatar");
        assertThat(result.bio()).isEqualTo("new-bio");
    }

    @Test
    void updateUserCanSetOptionalFieldsToNull() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("old");
        existing.setEmail("old@example.com");
        existing.setFullName("Old Name");
        existing.setAvatarUrl("old-avatar");
        existing.setBio("old-bio");

        User saved = new User();
        saved.setId(1L);
        saved.setUsername("new");
        saved.setEmail("new@example.com");
        saved.setFullName(null);
        saved.setAvatarUrl(null);
        saved.setBio(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(saved);

        UserDto result = userService.updateUser(
            1L,
            new UserDto(null, " new ", " new@example.com ", null, null, null, null)
        );

        assertThat(existing.getUsername()).isEqualTo("new");
        assertThat(existing.getEmail()).isEqualTo("new@example.com");
        assertThat(existing.getFullName()).isNull();
        assertThat(existing.getAvatarUrl()).isNull();
        assertThat(existing.getBio()).isNull();
        assertThat(result.fullName()).isNull();
        assertThat(result.avatarUrl()).isNull();
        assertThat(result.bio()).isNull();
    }

    @Test
    void updateUserThrowsWhenMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto dto = new UserDto(null, "alice", "alice@example.com", null, null, null, null);

        assertThatThrownBy(() -> userService.updateUser(1L, dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found: 1");
    }

    @Test
    void deleteUserDeletesWhenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserThrowsWhenMissing() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found: 1");
    }
}
