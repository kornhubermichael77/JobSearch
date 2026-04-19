package info.kornhuber.jobsearch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.kornhuber.jobsearch.config.GlobalExceptionHandler;
import info.kornhuber.jobsearch.dto.CommunicationResponseDTO;
import info.kornhuber.jobsearch.enums.CommunicationDirection;
import info.kornhuber.jobsearch.enums.CommunicationStatus;
import info.kornhuber.jobsearch.enums.CommunicationType;
import info.kornhuber.jobsearch.service.CommunicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommunicationControllerTest {

    private MockMvc mockMvc;
    private CommunicationService communicationService;

    @BeforeEach
    void setUp() {
        communicationService = mock(CommunicationService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new CommunicationController(communicationService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void getById_shouldReturnCommunication() throws Exception {
        CommunicationResponseDTO response = new CommunicationResponseDTO();
        response.id = 100;
        response.type = CommunicationType.PHONE;
        response.jobId = 10;
        response.date = LocalDateTime.of(2026, 3, 20, 10, 30);
        response.status = CommunicationStatus.TERMINVEREINBARUNG;
        response.number = "+43 660 1234567";
        response.direction = CommunicationDirection.OUT;

        when(communicationService.getById(100)).thenReturn(response);

        mockMvc.perform(get("/api/communications/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.type").value("PHONE"));
    }

    @Test
    void create_shouldReturnCreatedCommunication() throws Exception {
        CommunicationResponseDTO response = new CommunicationResponseDTO();
        response.id = 100;
        response.type = CommunicationType.PHONE;
        response.jobId = 10;
        response.date = LocalDateTime.of(2026, 3, 20, 10, 30);
        response.status = CommunicationStatus.TERMINVEREINBARUNG;
        response.number = "+43 660 1234567";
        response.direction = CommunicationDirection.OUT;

        when(communicationService.create(any())).thenReturn(response);

        String json = """
                {
                  "type": "PHONE",
                  "jobId": 10,
                  "date": "2026-03-20T10:30:00",
                  "person": "Max Mustermann",
                  "role": "HR",
                  "content": "Telefonisches Erstgespräch",
                  "sidemarks": "freundlich",
                  "status": "TERMINVEREINBARUNG",
                  "number": "+43 660 1234567",
                  "direction": "OUT"
                }
                """;

        mockMvc.perform(post("/api/communications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.type").value("PHONE"));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        doNothing().when(communicationService).delete(100);

        mockMvc.perform(delete("/api/communications/100"))
                .andExpect(status().isNoContent());

        verify(communicationService).delete(100);
    }

    @Test
    void update_shouldReturnUpdatedCommunication() throws Exception {
        CommunicationResponseDTO response = new CommunicationResponseDTO();
        response.id = 100;
        response.type = CommunicationType.PHONE;
        response.jobId = 10;
        response.number = "+43 660 7654321";
        response.direction = CommunicationDirection.IN;
        response.status = CommunicationStatus.INFORMATION_ERHALTEN;

        when(communicationService.update(eq(100), any())).thenReturn(response);

        String json = """
                {
                  "date": "2026-03-21T09:00:00",
                  "person": "Max Mustermann",
                  "role": "HR",
                  "content": "Rückruf erhalten",
                  "sidemarks": "sehr positiv",
                  "status": "INFORMATION_ERHALTEN",
                  "number": "+43 660 7654321",
                  "direction": "IN"
                }
                """;

        mockMvc.perform(put("/api/communications/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.direction").value("IN"))
                .andExpect(jsonPath("$.status").value("INFORMATION_ERHALTEN"));
    }
}

class CommunicationControllerValidationTest {

    private MockMvc mockMvc;
    private CommunicationService communicationService;

    @BeforeEach
    void setUp() {
        communicationService = mock(CommunicationService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new CommunicationController(communicationService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void create_shouldReturn400WhenTypeIsMissing() throws Exception {
        String json = """
            {
              "jobId": 10,
              "date": "2026-03-20T10:30:00",
              "status": "TERMINVEREINBARUNG",
              "direction": "OUT"
            }
            """;

        mockMvc.perform(post("/api/communications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("type"));
    }

    @Test
    void create_shouldReturn400WhenJobIdIsMissing() throws Exception {
        String json = """
                {
                  "type": "PHONE",
                  "date": "2026-03-20T10:30:00",
                  "status": "TERMINVEREINBARUNG",
                  "direction": "OUT"
                }
                """;

        mockMvc.perform(post("/api/communications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("jobId"));
    }

    @Test
    void create_shouldReturn400WhenEnumValueIsInvalid() throws Exception {
        String json = """
                {
                  "type": "PHONE",
                  "jobId": 10,
                  "date": "2026-03-20T10:30:00",
                  "status": "NICHT_GUELTIG",
                  "direction": "OUT"
                }
                """;

        mockMvc.perform(post("/api/communications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Malformed JSON or invalid enum value in request body"));
    }

    @Test
    void create_shouldReturn201WhenRequestIsValid() throws Exception {
        CommunicationResponseDTO response = new CommunicationResponseDTO();
        response.id = 100;
        response.type = CommunicationType.PHONE;
        response.jobId = 10;
        response.date = LocalDateTime.of(2026, 3, 20, 10, 30);
        response.status = CommunicationStatus.TERMINVEREINBARUNG;
        response.number = "+43 660 1234567";
        response.direction = CommunicationDirection.OUT;

        when(communicationService.create(any())).thenReturn(response);

        String json = """
                {
                  "type": "PHONE",
                  "jobId": 10,
                  "date": "2026-03-20T10:30:00",
                  "person": "Max Mustermann",
                  "role": "HR",
                  "content": "Telefonisches Erstgespräch",
                  "sidemarks": "freundlich",
                  "status": "TERMINVEREINBARUNG",
                  "number": "+43 660 1234567",
                  "direction": "OUT"
                }
                """;

        mockMvc.perform(post("/api/communications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.type").value(CommunicationType.PHONE.name()))
                .andExpect(jsonPath("$.jobId").value(10))
                .andExpect(jsonPath("$.number").value("+43 660 1234567"))
                .andExpect(jsonPath("$.direction").value(CommunicationDirection.OUT.name()));
    }
}
