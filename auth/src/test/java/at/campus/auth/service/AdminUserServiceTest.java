package at.campus.auth.service;

import at.campus.auth.dto.AdminUserResponse;
import at.campus.auth.model.User;
import at.campus.auth.model.UserRole;
import at.campus.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminUserService adminUserService;

    @Test
    void changeRole_shouldChangeUserRole_whenUserExists() {
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        adminUserService.changeRole(userId, UserRole.ADMIN);

        verify(user).changeRole(UserRole.ADMIN);
        verify(userRepository).findById(userId);
    }

    @Test
    void changeRole_shouldThrowException_whenUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> adminUserService.changeRole(userId, UserRole.ADMIN)
        );
    }

    @Test
    void disableUser_shouldDisableUser() {
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        adminUserService.disableUser(userId);

        verify(user).disable();
    }

    @Test
    void enableUser_shouldEnableUser() {
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        adminUserService.enableUser(userId);

        verify(user).enable();
    }

    @Test
    void listUsers_shouldReturnAdminUserResponses() {
        User user = mock(User.class);

        when(user.getId()).thenReturn(UUID.randomUUID());
        when(user.getEmail()).thenReturn("admin@test.com");
        when(user.getNickname()).thenReturn("admin");
        when(user.getRole()).thenReturn(UserRole.ADMIN);
        when(user.isEnabled()).thenReturn(true);
        when(user.isAccountNonLocked()).thenReturn(true);
        when(user.getCreatedAt()).thenReturn(Instant.now());

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<AdminUserResponse> result = adminUserService.listUsers();

        assertEquals(1, result.size());
        assertEquals("admin@test.com", result.getFirst().getEmail());
        assertEquals(UserRole.ADMIN, result.getFirst().getRole());
    }

}
