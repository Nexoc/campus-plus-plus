package at.campus.auth.service;

import at.campus.auth.dto.AuthResponse;
import at.campus.auth.exception.EmailAlreadyExistsException;
import at.campus.auth.exception.InvalidCredentialsException;
import at.campus.auth.model.User;
import at.campus.auth.model.UserRole;
import at.campus.auth.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @AfterEach
    void tearDown() {
        // Keep tests isolated
        SecurityContextHolder.clearContext();
    }

    // --------------------------------------------------
    // REGISTER
    // --------------------------------------------------

    @Test
    void register_shouldSaveUser_whenEmailDoesNotExist() {
        // GIVEN
        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // WHEN
        authService.register("test@test.com", "password123", "nickname");

        // THEN
        verify(userRepository).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        // GIVEN
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        // WHEN / THEN
        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register("test@test.com", "password123", "nickname")
        );

        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    // --------------------------------------------------
    // LOGIN
    // --------------------------------------------------

    @Test
    void login_shouldReturnAuthResponse_whenAuthenticationSucceeds() {
        // GIVEN
        User user = new User("test@test.com", "encodedPassword", UserRole.STUDENT);

        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        // WHEN
        AuthResponse response = authService.login("test@test.com", "password123");

        // THEN
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("test@test.com");
        verify(jwtService).generateToken(user);
        verifyNoMoreInteractions(authenticationManager, userRepository, jwtService);
    }

    @Test
    void login_shouldThrowException_whenAuthenticationFails() {
        // GIVEN
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("bad credentials"));

        // WHEN / THEN
        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login("test@test.com", "password123")
        );

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_shouldThrowException_whenUserNotFoundAfterSuccessfulAuth() {
        // GIVEN: auth manager says OK but DB has no user
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());

        // WHEN / THEN
        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login("test@test.com", "password123")
        );

        verify(jwtService, never()).generateToken(any());
    }

    // --------------------------------------------------
    // CHANGE PASSWORD
    // --------------------------------------------------

    @Test
    void changePassword_shouldSaveUser_whenCurrentPasswordMatches() {
        // GIVEN: authenticated user in SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@test.com", null)
        );

        User user = new User("test@test.com", "encodedPassword", UserRole.STUDENT);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("oldPassword", "encodedPassword"))
                .thenReturn(true);

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("newEncodedPassword");

        // WHEN
        authService.changePassword("oldPassword", "newPassword");

        // THEN
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).matches("oldPassword", "encodedPassword");
        verify(passwordEncoder).encode("newPassword");
    }

    @Test
    void changePassword_shouldThrowException_whenCurrentPasswordDoesNotMatch() {
        // GIVEN
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@test.com", null)
        );

        User user = new User("test@test.com", "encodedPassword", UserRole.STUDENT);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrongOld", "encodedPassword"))
                .thenReturn(false);

        // WHEN / THEN
        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.changePassword("wrongOld", "newPassword")
        );

        verify(userRepository, never()).save(any());
    }

    // --------------------------------------------------
    // CURRENT USER
    // --------------------------------------------------

    @Test
    void getCurrentUserEmail_shouldReturnEmailFromSecurityContext() {
        // GIVEN
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@test.com", null)
        );

        // WHEN
        String email = authService.getCurrentUserEmail();

        // THEN
        assertEquals("test@test.com", email);
    }

    @Test
    void getCurrentUser_shouldReturnUser() {
        // GIVEN
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@test.com", null)
        );

        User user = new User("test@test.com", "encodedPassword", UserRole.STUDENT);

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        // WHEN
        User result = authService.getCurrentUser();

        // THEN
        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    void getCurrentUser_shouldThrowException_whenUserNotFound() {
        // GIVEN
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@test.com", null)
        );

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());

        // WHEN / THEN
        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.getCurrentUser()
        );
    }
}
