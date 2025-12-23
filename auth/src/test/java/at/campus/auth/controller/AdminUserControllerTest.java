package at.campus.auth.controller;

import at.campus.auth.model.UserRole;
import at.campus.auth.security.JwtAuthenticationFilter;
import at.campus.auth.service.AdminUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
@AutoConfigureMockMvc(addFilters = false) // security execution OFF
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminUserService adminUserService;

    // ОБЯЗАТЕЛЬНО: глушим security-инфраструктуру
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private AuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private AccessDeniedHandler accessDeniedHandler;

    @Test
    void changeRole_shouldReturn204() throws Exception {
        // GIVEN
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        String json = """
            {
              "userId": "11111111-1111-1111-1111-111111111111",
              "role": "ADMIN"
            }
            """;

        // WHEN / THEN
        mockMvc.perform(post("/admin/users/change-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());

        verify(adminUserService).changeRole(userId, UserRole.ADMIN);
        verifyNoMoreInteractions(adminUserService);
    }

    @Test
    void disable_shouldReturn204() throws Exception {
        // GIVEN
        UUID userId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        String json = """
            {
              "userId": "22222222-2222-2222-2222-222222222222"
            }
            """;

        // WHEN / THEN
        mockMvc.perform(post("/admin/users/disable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());

        verify(adminUserService).disableUser(userId);
        verifyNoMoreInteractions(adminUserService);
    }

    @Test
    void enable_shouldReturn204() throws Exception {
        // GIVEN
        UUID userId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        String json = """
            {
              "userId": "33333333-3333-3333-3333-333333333333"
            }
            """;

        // WHEN / THEN
        mockMvc.perform(post("/admin/users/enable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());

        verify(adminUserService).enableUser(userId);
        verifyNoMoreInteractions(adminUserService);
    }

    @Test
    void listUsers_shouldReturn200() throws Exception {
        // GIVEN
        when(adminUserService.listUsers()).thenReturn(List.of());

        // WHEN / THEN
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk());

        verify(adminUserService).listUsers();
        verifyNoMoreInteractions(adminUserService);
    }
}
