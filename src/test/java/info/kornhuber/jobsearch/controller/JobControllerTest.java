package info.kornhuber.jobsearch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.kornhuber.jobsearch.config.GlobalExceptionHandler;
import info.kornhuber.jobsearch.dto.JobResponseDTO;
import info.kornhuber.jobsearch.dto.JobsForFilterResponseDTO;
import info.kornhuber.jobsearch.enums.JobStatus;
import info.kornhuber.jobsearch.exception.BadRequestException;
import info.kornhuber.jobsearch.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class JobControllerTest {

    private MockMvc mockMvc;
    private JobService jobService;

    @BeforeEach
    void setUp() {
        jobService = mock(JobService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new JobController(jobService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void create_shouldReturnCreatedJob_whenUsingExistingCompany() throws Exception {
        JobResponseDTO response = new JobResponseDTO();
        response.id = 100;
        response.companyId = 1;
        response.status = JobStatus.BEWORBEN;
        response.source = "LinkedIn";
        response.found = LocalDateTime.of(2026, 3, 24, 10, 0);

        when(jobService.create(any())).thenReturn(response);

        String json = """
                {
                  "companyId": 1,
                  "status": "BEWORBEN",
                  "source": "LinkedIn",
                  "found": "2026-03-24T10:00:00"
                }
                """;

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.companyId").value(1))
                .andExpect(jsonPath("$.status").value("BEWORBEN"))
                .andExpect(jsonPath("$.source").value("LinkedIn"));
    }

    @Test
    void create_shouldReturn400_whenStatusIsMissing() throws Exception {
        String json = """
                {
                  "companyId": 1
                }
                """;

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"));
    }

    @Test
    void create_shouldReturn400_whenBusinessRuleFails() throws Exception {
        when(jobService.create(any()))
                .thenThrow(new BadRequestException("Business rule failed"));

        String json = """
                {
                  "companyId": 1,
                  "status": "BEWORBEN"
                }
                """;

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Business rule failed"));
    }

    @Test
    void update_shouldReturnUpdatedJob() throws Exception {
        JobResponseDTO response = new JobResponseDTO();
        response.id = 100;
        response.companyId = 1;
        response.addressId = 10;
        response.status = JobStatus.BEWORBEN;
        response.source = "Xing";

        when(jobService.update(eq(100), any())).thenReturn(response);

        String json = """
                {
                  "status": "BEWORBEN",
                  "source": "Xing"
                }
                """;

        mockMvc.perform(put("/api/jobs/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.companyId").value(1))
                .andExpect(jsonPath("$.addressId").value(10))
                .andExpect(jsonPath("$.status").value("BEWORBEN"));
    }

    @Test
    void updateJobAddress_shouldReturnUpdatedJob() throws Exception {
        JobResponseDTO response = new JobResponseDTO();
        response.id = 100;
        response.companyId = 1;
        response.addressId = 20;
        response.status = JobStatus.BEWORBEN;

        when(jobService.updateJobAddress(eq(100), any())).thenReturn(response);

        String json = """
                {
                  "addressId": 20
                }
                """;

        mockMvc.perform(patch("/api/jobs/100/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.addressId").value(20));
    }

    @Test
    void all_shouldReturnFilteredJobs() throws Exception {
        JobResponseDTO response = new JobResponseDTO();
        response.id = 100;
        response.status = JobStatus.BEWORBEN;

        when(jobService.getAll(JobStatus.BEWORBEN, null)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/jobs")
                        .param("status", "BEWORBEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].status").value("BEWORBEN"));
    }

    @Test
    void options_shouldReturnReducedJobs() throws Exception {
        JobsForFilterResponseDTO response = new JobsForFilterResponseDTO();
        response.id = 100;
        response.companyId = 3;
        response.companyName = "OpenAI GmbH";
        response.status = JobStatus.BEWORBEN;
        response.source = "LinkedIn";

        when(jobService.getAllForFilter(JobStatus.BEWORBEN, 3)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/jobs/options")
                        .param("status", "BEWORBEN")
                        .param("companyId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].companyId").value(3))
                .andExpect(jsonPath("$[0].status").value("BEWORBEN"));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        doNothing().when(jobService).delete(100);

        mockMvc.perform(delete("/api/jobs/100"))
                .andExpect(status().isNoContent());

        verify(jobService).delete(100);
    }
}
