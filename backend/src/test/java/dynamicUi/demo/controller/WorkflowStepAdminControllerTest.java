package dynamicUi.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dynamicUi.demo.entity.WorkflowStep;
import dynamicUi.demo.security.JwtAuthFilter;
import dynamicUi.demo.security.JwtUtil;
import dynamicUi.demo.security.LoginRateLimitFilter;
import dynamicUi.demo.security.SecurityConfig;
import dynamicUi.demo.service.WorkflowStepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkflowStepAdminController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, LoginRateLimitFilter.class})
public class WorkflowStepAdminControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private WorkflowStepService service;

    @MockitoBean
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

//   ------------------ GET ------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_returnsOk_whenCalledByAdmin() throws Exception{

        WorkflowStep step = WorkflowStep.builder()
                .id(1L).name("Gate Check In").code("GATE_CHECK_IN").description("Truck arrives at a gate")
                .build();

        when(service.findAll()).thenReturn(List.of(step));

        mockMvc.perform(get("/api/admin/workflow-steps"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Gate Check In"))
                .andExpect(jsonPath("$[0].code").value("GATE_CHECK_IN"));
    }

    @Test
    @WithMockUser(roles ="VIEWER")
    void getAll_returnsForbidden_whenCalledByViewer() throws Exception{
        mockMvc.perform(get("/api/admin/workflow-steps"))
                .andExpect(status().isForbidden());
    }

    // ----------------- POST --------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returnsCreated_whenCalledByAdmin() throws Exception{

        WorkflowStep request = WorkflowStep.builder()
                        .id(2L).name("Truck Inspection").code("TRUCK_INSPECTION")
                        .build();

        WorkflowStep saved =WorkflowStep.builder()
                .id(2L).name("Truck Inspection").code("TRUCK_INSPECTION")
                .build();

        when(service.create(any(WorkflowStep.class))).thenReturn(saved);

        mockMvc.perform(post("/api/admin/workflow-steps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.code").value("TRUCK_INSPECTION"));
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void create_returnsForbidden_whenCalledByViewer() throws Exception{

        WorkflowStep request = WorkflowStep.builder()
                .id(2L).name("Truck Inspection").code("TRUCK_INSPECITON")
                .build();

        mockMvc.perform(post("/api/admin/workflow-steps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

    }

    // ----------------------- UPDATE ---------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_returnsOk_whenStepExists() throws Exception{

        WorkflowStep request = WorkflowStep.builder()
                        .id(1L).name("Truck Inspection").code("TRUCK_INSPECTION_UPDATED").build();

        WorkflowStep updated =  WorkflowStep.builder()
                .id(1L).name("Truck Inspection").code("TRUCK_INSPECTION_UPDATED").build();

        when(service.update(eq(1L),any(WorkflowStep.class))).thenReturn(updated);

        mockMvc.perform(put("/api/admin/workflow-steps/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TRUCK_INSPECTION_UPDATED"));
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void update_returnsForbidden_whenCalledByViewer() throws Exception{

        WorkflowStep request = WorkflowStep.builder()
                .id(1L).name("Truck Inspection").code("TRUCK_INSPECTION_UPDATED").build();

        mockMvc.perform(put("/api/admin/workflow-steps/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ---------------- DELETE ----------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_returnsNoContent_whenStepExists() throws Exception{
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/admin/workflow-steps/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
}
}
