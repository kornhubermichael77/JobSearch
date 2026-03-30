package info.kornhuber.jobsearch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.kornhuber.jobsearch.config.GlobalExceptionHandler;
import info.kornhuber.jobsearch.dto.MailTimelineDTO;
import info.kornhuber.jobsearch.dto.PhoneTimelineDTO;
import info.kornhuber.jobsearch.dto.TimelineItemDTO;
import info.kornhuber.jobsearch.enums.CommunicationStatus;
import info.kornhuber.jobsearch.enums.CommunicationType;
import info.kornhuber.jobsearch.service.TimelineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TimelineControllerTest {

    private MockMvc mockMvc;
    private TimelineService timelineService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        timelineService = mock(TimelineService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TimelineController(timelineService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void timeline_shouldReturnFirstPageWithoutFilters() throws Exception {
        TimelineItemDTO dto = new PhoneTimelineDTO();
        dto.id = 1;
        dto.type = CommunicationType.PHONE;
        dto.jobId = 10;
        dto.person = "Anna Recruiter";
        dto.date = LocalDateTime.of(2026, 3, 25, 10, 30);
        dto.status = CommunicationStatus.OFFEN;

        PageRequest pageable = PageRequest.of(0, 20, Sort.by("date").descending());

        when(timelineService.timeline(
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(pageable)
        )).thenReturn(new PageImpl<>(List.of(dto), pageable, 1));

        mockMvc.perform(get("/api/timeline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].type").value("PHONE"))
                .andExpect(jsonPath("$.content[0].jobId").value(10))
                .andExpect(jsonPath("$.content[0].person").value("Anna Recruiter"))
                .andExpect(jsonPath("$.content[0].status").value("OFFEN"))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void timeline_shouldFilterByStatus() throws Exception {
        TimelineItemDTO dto = new MailTimelineDTO();
        dto.id = 2;
        dto.type = CommunicationType.MAIL;
        dto.jobId = 11;
        dto.person = "HR Team";
        dto.date = LocalDateTime.of(2026, 3, 26, 9, 0);
        dto.status = CommunicationStatus.INFORMATION_ERHALTEN;

        PageRequest pageable = PageRequest.of(0, 20, Sort.by("date").descending());

        when(timelineService.timeline(
                eq(null),
                eq(null),
                eq(null),
                eq(CommunicationStatus.INFORMATION_ERHALTEN),
                eq(null),
                eq(pageable)
        )).thenReturn(new PageImpl<>(List.of(dto), pageable, 1));

        mockMvc.perform(get("/api/timeline")
                        .param("status", "INFORMATION_ERHALTEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].type").value("MAIL"))
                .andExpect(jsonPath("$.content[0].status").value("INFORMATION_ERHALTEN"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void timeline_shouldFilterByAllSupportedParameters() throws Exception {
        TimelineItemDTO dto = new PhoneTimelineDTO();
        dto.id = 3;
        dto.type = CommunicationType.PHONE;
        dto.jobId = 12;
        dto.person = "Max Mustermann";
        dto.date = LocalDateTime.of(2026, 3, 27, 8, 15);
        dto.status = CommunicationStatus.TERMINVEREINBARUNG;

        PageRequest pageable = PageRequest.of(0, 20, Sort.by("date").descending());

        when(timelineService.timeline(
                eq(12),
                eq("PHONE"),
                eq("Max"),
                eq(CommunicationStatus.TERMINVEREINBARUNG),
                eq(LocalDate.of(2026, 3, 1)),
                eq(pageable)
        )).thenReturn(new PageImpl<>(List.of(dto), pageable, 1));

        mockMvc.perform(get("/api/timeline")
                        .param("jobId", "12")
                        .param("type", "PHONE")
                        .param("person", "Max")
                        .param("status", "TERMINVEREINBARUNG")
                        .param("from", "2026-03-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(3))
                .andExpect(jsonPath("$.content[0].jobId").value(12))
                .andExpect(jsonPath("$.content[0].type").value("PHONE"))
                .andExpect(jsonPath("$.content[0].person").value("Max Mustermann"))
                .andExpect(jsonPath("$.content[0].status").value("TERMINVEREINBARUNG"));
    }

    @Test
    void timeline_shouldUseCustomPageAndSize() throws Exception {
        PageRequest pageable = PageRequest.of(1, 5, Sort.by("date").descending());

        when(timelineService.timeline(
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(pageable)
        )).thenReturn(new PageImpl<>(List.of(), pageable, 0));

        mockMvc.perform(get("/api/timeline")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void timeline_shouldReturn400WhenStatusEnumIsInvalid() throws Exception {
        mockMvc.perform(get("/api/timeline")
                        .param("status", "NICHT_GUELTIG"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}