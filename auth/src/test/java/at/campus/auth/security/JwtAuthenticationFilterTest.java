package at.campus.auth.security;

import at.campus.auth.model.User;
import at.campus.auth.model.UserRole;
import at.campus.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private UserDetailsService userDetailsService;
    private JwtAuthenticationFilter filter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userDetailsService = mock(UserDetailsService.class);

        filter = new JwtAuthenticationFilter(jwtService, userDetailsService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSkipFilter_forPublicEndpoint() throws Exception {
        // GIVEN: public endpoint
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/auth/login");
        when(request.getHeader("Authorization")).thenReturn("Bearer any");

        // WHEN
        filter.doFilter(request, response, filterChain);

        // THEN: chain continues, jwtService is never touched
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldSkipFilter_forOptionsRequest() throws Exception {
        // GIVEN: preflight
        when(request.getMethod()).thenReturn("OPTIONS");
        when(request.getRequestURI()).thenReturn("/api/test");

        // WHEN
        filter.doFilter(request, response, filterChain);

        // THEN
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldContinueChain_whenNoAuthorizationHeaderPresent() throws Exception {
        // GIVEN: request without Authorization header
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        // WHEN
        filter.doFilter(request, response, filterChain);

        // THEN: filter chain continues and no authentication is set
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void shouldContinueChain_whenAuthorizationHeaderIsNotBearer() throws Exception {
        // GIVEN
        when(request.getHeader("Authorization")).thenReturn("Basic abc");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        // WHEN
        filter.doFilter(request, response, filterChain);

        // THEN
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void shouldContinueChain_whenAuthenticationAlreadyPresent() throws Exception {
        // GIVEN: SecurityContext already authenticated
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("someone", null)
        );

        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        // WHEN
        filter.doFilter(request, response, filterChain);

        // THEN: should not re-authenticate, just continue
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void shouldSetAuthentication_whenJwtIsValid() throws Exception {
        // GIVEN
        String token = "valid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractUsername(token)).thenReturn("test@test.com");
        when(jwtService.extractTokenVersion(token)).thenReturn(0);

        User user = new User(
                "test@test.com",
                "tester",
                "hashedPassword",
                UserRole.STUDENT
        );

        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(user);

        // WHEN
        filter.doFilter(request, response, filterChain);

        // THEN: authentication is set and filter chain continues
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("test@test.com", SecurityContextHolder.getContext().getAuthentication().getName());

        verify(filterChain).doFilter(request, response);

        verify(jwtService).isTokenValid(token);
        verify(jwtService).extractUsername(token);
        verify(jwtService).extractTokenVersion(token);
        verify(userDetailsService).loadUserByUsername("test@test.com");
        verifyNoMoreInteractions(jwtService, userDetailsService);
    }

    @Test
    void shouldReturn401_whenJwtIsInvalid() throws Exception {
        // GIVEN
        String token = "invalid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        when(jwtService.isTokenValid(token)).thenReturn(false);

        // WHEN
        filter.doFilter(request, response, filterChain);

        // THEN: response is unauthorized and chain is NOT continued
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
        verify(filterChain, never()).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(jwtService).isTokenValid(token);
        verifyNoMoreInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void shouldReturn401_whenTokenVersionDoesNotMatch() throws Exception {
        // GIVEN
        String token = "valid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractUsername(token)).thenReturn("test@test.com");
        when(jwtService.extractTokenVersion(token)).thenReturn(5); // token says 5

        User user = new User(
                "test@test.com",
                "tester",
                "hashedPassword",
                UserRole.STUDENT
        );


        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(user);

        // WHEN
        filter.doFilter(request, response, filterChain);

        // THEN
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token revoked");
        verify(filterChain, never()).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldReturn401_whenUserDisabled() throws Exception {
        // GIVEN
        String token = "valid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractUsername(token)).thenReturn("test@test.com");
        when(jwtService.extractTokenVersion(token)).thenReturn(0);

        User user = new User(
                "test@test.com",
                "tester",
                "hashedPassword",
                UserRole.STUDENT
        );


        user.disable(); // enabled=false and tokenVersion++

        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(user);

        // WHEN
        filter.doFilter(request, response, filterChain);

        // THEN
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "User account disabled");
        verify(filterChain, never()).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
