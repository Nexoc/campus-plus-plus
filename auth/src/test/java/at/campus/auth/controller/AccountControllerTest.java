package at.campus.auth.controller;

import at.campus.auth.model.User;
import at.campus.auth.model.UserRole;
import at.campus.auth.security.JwtAuthenticationFilter;
import at.campus.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable Security filter chain execution for controller slice tests
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    // Prevent Spring from creating the real filter (which requires JwtService bean)
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // If your SecurityConfig wires these, mocking them avoids "missing bean" problems in slice tests
    @MockitoBean
    private AuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private AccessDeniedHandler accessDeniedHandler;

    @Test
    void changePassword_shouldReturn204() throws Exception {
        // GIVEN: request body JSON
        String jsonBody = """
            {
              "currentPassword": "oldPass",
              "newPassword": "newStrongPass"
            }
            """;

        // WHEN / THEN
        mockMvc.perform(post("/account/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isNoContent());

        verify(authService).changePassword("oldPass", "newStrongPass");
    }

    @Test
    void me_shouldReturnCurrentUser() throws Exception {
        // GIVEN
        User user = new User(
                "test@test.com",
                "hashedPassword",
                UserRole.STUDENT
        );
        user.setNickname("tester");

        when(authService.getCurrentUser()).thenReturn(user);

        // WHEN / THEN
        mockMvc.perform(get("/account/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.nickname").value("tester"))
                .andExpect(jsonPath("$.role").value("STUDENT"));

        verify(authService).getCurrentUser();
    }
}
