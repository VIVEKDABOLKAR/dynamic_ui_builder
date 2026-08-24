package dynamicUi.demo.controller;

import dynamicUi.demo.constant.UserRoleAdminController;
import dynamicUi.demo.dto.UserRoleResponse;
import dynamicUi.demo.security.JwtAuthFilter;
import dynamicUi.demo.security.JwtUtil;
import dynamicUi.demo.security.LoginRateLimitFilter;
import dynamicUi.demo.security.SecurityConfig;
import dynamicUi.demo.service.UserRoleAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRoleAdminController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, LoginRateLimitFilter.class})
public class UserRoleAdminControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private UserRoleAdminService service;

    @MockitoBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_returnsUserList_whenCalledByAdmin() throws Exception {
        when(service.getAllUsers()).thenReturn(List.of(
                new UserRoleResponse(1L, "admin", "ROLE_ADMIN"),
                new UserRoleResponse(2L, "john", "ROLE_VIEWER")
        ));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[1].role").value("ROLE_VIEWER"));
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void getAllUsers_returnsForbidden_whenCalledByViewer() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_returnsForbidden_whenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }
}