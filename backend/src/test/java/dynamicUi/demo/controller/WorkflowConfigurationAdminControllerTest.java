package dynamicUi.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dynamicUi.demo.constant.Attribute;
import dynamicUi.demo.dto.WorkflowConfigurationDTO;
import dynamicUi.demo.dto.WorkflowConfigurationRequest;
import dynamicUi.demo.security.JwtAuthFilter;
import dynamicUi.demo.security.JwtUtil;
import dynamicUi.demo.security.LoginRateLimitFilter;
import dynamicUi.demo.security.SecurityConfig;
import dynamicUi.demo.service.WorkflowConfigurationService;
import lombok.With;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkflowConfigurationAdminController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, LoginRateLimitFilter.class})
public class WorkflowConfigurationAdminControllerTest {

     @Autowired
     private  WebApplicationContext context;

     private MockMvc mockMvc;

     @MockitoBean
     private WorkflowConfigurationService service;

     @MockitoBean
     private JwtUtil jwtUtil;

     private final ObjectMapper objectMapper = new ObjectMapper();

     @BeforeEach
     void setUp(){
         mockMvc = MockMvcBuilders.webAppContextSetup(context)
                 .apply(springSecurity()).build();
     }
     //----------- GET ------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEffective_returnsOk_whenCalledByAdmin() throws Exception{

         when(service.findForFacilityEffictive(eq("CHN001"))).thenReturn(List.of(
                 WorkflowConfigurationDTO.builder()
                         .id(1L)
                         .workflowStepId(5L)
                         .workflowStepCode("GATE_CHECK_IN")
                         .workflowStepName("Gate Check In")
                         .sequence(1)
                         .active(true)
                         .facilityId("CHN001")
                         .build())
         );
         mockMvc.perform(get("/api/admin/workflow-configurations")
                 .requestAttr(Attribute.SELECTED_FACILITY_ID, "CHN001"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$[0].workflowStepCode").value("GATE_CHECK_IN"));
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void getEffective_returnsForbidden_whenCalledByViewer() throws Exception{

        mockMvc.perform(get("/api/admin/workflow-configurations")
                .requestAttr(Attribute.SELECTED_FACILITY_ID, "CHN001"))
                .andExpect(status().isForbidden());
    }

    //----------------------- GET LIST -------------
    @Test
    @WithMockUser(roles = "ADMIN")
    void getFacility_returnsOk_whenCalledByAdmin() throws Exception{

        when(service.findForFacility(eq("CHN001"))).thenReturn(List.of(
                WorkflowConfigurationDTO.builder()
                        .id(1L)
                        .workflowStepId(5L)
                        .workflowStepCode("GATE_CHECK_IN")
                        .workflowStepName("Gate Check In")
                        .sequence(1)
                        .active(true)
                        .facilityId("CHN001")
                        .build())
        );
        mockMvc.perform(get("/api/admin/workflow-configurations/list")
                        .requestAttr(Attribute.SELECTED_FACILITY_ID, "CHN001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].workflowStepCode").value("GATE_CHECK_IN"));
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void getFacility_returnsForbidden_whenCalledByViewer() throws Exception{

        mockMvc.perform(get("/api/admin/workflow-configurations/list")
                        .requestAttr(Attribute.SELECTED_FACILITY_ID, "CHN001"))
                .andExpect(status().isForbidden());
    }

    //---------------------- POST -------------------

    @Test
    @WithMockUser(roles ="ADMIN")
    void create_returnsCreated_whenCalledByAdmin() throws Exception {

         WorkflowConfigurationRequest request = WorkflowConfigurationRequest.builder()
                         .workflowStepId(1L).active(true).sequence(3).build();

        WorkflowConfigurationDTO response = WorkflowConfigurationDTO.builder()
                .workflowStepId(1L).active(true).sequence(3).build();

         when(service.create(any(WorkflowConfigurationRequest.class),eq("CHN001"))).thenReturn(response);

        mockMvc.perform(post("/api/admin/workflow-configurations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .requestAttr(Attribute.SELECTED_FACILITY_ID,"CHN001")
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sequence").value(3));

    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void create_returnsForbidden_whenCalledByViewer() throws Exception{

        WorkflowConfigurationRequest request = WorkflowConfigurationRequest.builder()
                .workflowStepId(1L).active(true).sequence(3).build();


        mockMvc.perform(post("/api/admin/workflow-configurations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .requestAttr(Attribute.SELECTED_FACILITY_ID,"CHN001")
                )
                .andExpect(status().isForbidden());
    }

    // --------------------- PUT ----------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_returnsOk_whenConfigurationExists() throws Exception {

        WorkflowConfigurationRequest request = WorkflowConfigurationRequest.builder()
                .workflowStepId(1L).active(true).sequence(3).build();


        WorkflowConfigurationDTO response = WorkflowConfigurationDTO.builder()
                .workflowStepId(1L).active(true).sequence(3).build();

        when(service.update(eq(1L),any(WorkflowConfigurationRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/admin/workflow-configurations/1")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(request))
         )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sequence").value(3));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_returnsNotFound_whenConfigurationDoesNotExist() throws Exception{

         WorkflowConfigurationRequest request = WorkflowConfigurationRequest.builder()
                         .workflowStepId(2L).active(true).sequence(2).build();

         when(service.update(eq(999L),any(WorkflowConfigurationRequest.class)))
                 .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,"workflow configuration not found ; 999"));

         mockMvc.perform(put("/api/admin/workflow-congiurations")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(request))
         )
                 .andExpect(status().isNotFound());
    }



    @Test
    @WithMockUser(roles = "VIEWER")
    void update_returnsForbidden_whenCalledByViewer() throws Exception {

        WorkflowConfigurationRequest request = WorkflowConfigurationRequest.builder()
                .workflowStepId(1L).active(true).sequence(3).build();

        mockMvc.perform(put("/api/admin/workflow-configurations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden());
    }

    // ------------------ DELETE ---------------


    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_returnsNotFound_whenConfigurationDoesNotExist() throws Exception {

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow configuration not found: 999"))
                .when(service).delete(999L);

        mockMvc.perform(delete("/api/admin/workflow-configurations/999"))
                .andExpect(status().isNotFound());

        verify(service).delete(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_returnsNoContent_whenConfigurationExists() throws Exception {

         doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/admin/workflow-configurations/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void delete_returnsForbidden_whenCalledByViewer() throws Exception {

        mockMvc.perform(delete("/api/admin/workflow-configurations/1"))
                .andExpect(status().isForbidden());

    }


// ---------------------- Not logged in (representative) ----------------------

    @Test
    void getEffective_returnsForbidden_whenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/api/admin/workflow-configurations")
                        .requestAttr(Attribute.SELECTED_FACILITY_ID, "CHN001"))
                .andExpect(status().isForbidden());
    }
}
