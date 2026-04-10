package info.kornhuber.jobsearch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.kornhuber.jobsearch.config.GlobalExceptionHandler;
import info.kornhuber.jobsearch.dto.CommunicationResponseDTO;
import info.kornhuber.jobsearch.enums.CommunicationStatus;
import info.kornhuber.jobsearch.enums.CommunicationType;
import info.kornhuber.jobsearch.enums.CommunicationDirection;
import info.kornhuber.jobsearch.service.CommunicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void delete_shouldReturn200() throws Exception {
        doNothing().when(communicationService).delete(100);

        mockMvc.perform(delete("/api/communications/100"))
                .andExpect(status().isOk());

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
                  "type": "PHONE",
                  "jobId": 10,
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
    private ObjectMapper objectMapper;
    private CommunicationService communicationService;

    @BeforeEach
    void setUp() {
        communicationService = mock(CommunicationService.class);

        CommunicationController controller = new CommunicationController(communicationService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
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
                .andExpect(jsonPath("$.errors.type").value("type darf nicht null sein"));
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
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.jobId").value("jobId darf nicht null sein"));
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
                .andExpect(jsonPath("$.error").value("Ungültiger Enum-Wert im Request"));
    }

    @Test
    void create_shouldReturn200WhenRequestIsValid() throws Exception {
        CommunicationResponseDTO response = new CommunicationResponseDTO();
        response.id = 100;
        response.type = CommunicationType.PHONE;
        response.jobId = 10;
        response.date = LocalDateTime.of(2026, 3, 20, 10, 30);
        response.status = info.kornhuber.jobsearch.enums.CommunicationStatus.TERMINVEREINBARUNG;
        response.number = "+43 660 1234567";
        response.direction = CommunicationDirection.OUT;

        when(communicationService.create(org.mockito.ArgumentMatchers.any())).thenReturn(response);

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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.type").value(CommunicationType.PHONE.name()))
                .andExpect(jsonPath("$.jobId").value(10))
                .andExpect(jsonPath("$.number").value("+43 660 1234567"))
                .andExpect(jsonPath("$.direction").value(CommunicationDirection.OUT.name()));
    }
}