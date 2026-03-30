package info.kornhuber.jobsearch.service;

import info.kornhuber.jobsearch.dto.MailTimelineDTO;
import info.kornhuber.jobsearch.dto.PhoneTimelineDTO;
import info.kornhuber.jobsearch.dto.TimelineItemDTO;
import info.kornhuber.jobsearch.domain.entity.Communication;
import info.kornhuber.jobsearch.domain.entity.MailCommunication;
import info.kornhuber.jobsearch.domain.entity.PhoneCommunication;
import info.kornhuber.jobsearch.enums.CommunicationStatus;
import info.kornhuber.jobsearch.enums.CommunicationType;
import info.kornhuber.jobsearch.mapper.TimelineMapper;
import info.kornhuber.jobsearch.domain.repository.CommunicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimelineServiceTest {

    @Mock
    private CommunicationRepository communicationRepository;

    @Mock
    private TimelineMapper timelineMapper;

    @InjectMocks
    private TimelineService timelineService;

    private Pageable pageable;
    private MailCommunication mailCommunication;
    private PhoneCommunication phoneCommunication;
    private MailTimelineDTO mailTimelineDTO;
    private PhoneTimelineDTO phoneTimelineDTO;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 20, Sort.by("date").descending());

        mailCommunication = new MailCommunication();
        mailCommunication.setId(1);
        mailCommunication.setDate(LocalDateTime.of(2026, 3, 25, 10, 30));
        mailCommunication.setPerson("Anna Recruiter");
        mailCommunication.setStatus(CommunicationStatus.INFORMATION_ERHALTEN);

        phoneCommunication = new PhoneCommunication();
        phoneCommunication.setId(2);
        phoneCommunication.setDate(LocalDateTime.of(2026, 3, 26, 9, 0));
        phoneCommunication.setPerson("Max Mustermann");
        phoneCommunication.setStatus(CommunicationStatus.TERMINVEREINBARUNG);

        mailTimelineDTO = new MailTimelineDTO();
        mailTimelineDTO.id = 1;
        mailTimelineDTO.type = CommunicationType.MAIL;
        mailTimelineDTO.person = "Anna Recruiter";
        mailTimelineDTO.date = LocalDateTime.of(2026, 3, 25, 10, 30);
        mailTimelineDTO.status = CommunicationStatus.INFORMATION_ERHALTEN;

        phoneTimelineDTO = new PhoneTimelineDTO();
        phoneTimelineDTO.id = 2;
        phoneTimelineDTO.type = CommunicationType.PHONE;
        phoneTimelineDTO.person = "Max Mustermann";
        phoneTimelineDTO.date = LocalDateTime.of(2026, 3, 26, 9, 0);
        phoneTimelineDTO.status = CommunicationStatus.TERMINVEREINBARUNG;
    }

    @Test
    void timeline_shouldReturnMappedPageWithoutFilters() {
        Page<Communication> repositoryPage =
                new PageImpl<>(List.of(mailCommunication), pageable, 1);

        when(communicationRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(repositoryPage);
        when(timelineMapper.toDto(mailCommunication)).thenReturn(mailTimelineDTO);

        Page<TimelineItemDTO> result = timelineService.timeline(
                null,
                null,
                null,
                null,
                null,
                pageable
        );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().id).isEqualTo(1);
        assertThat(result.getContent().getFirst().type).isEqualTo(CommunicationType.MAIL);
        assertThat(result.getContent().getFirst().status)
                .isEqualTo(CommunicationStatus.INFORMATION_ERHALTEN);

        verify(communicationRepository).findAll(any(Specification.class), eq(pageable));
        verify(timelineMapper).toDto(mailCommunication);
    }

    @Test
    void timeline_shouldReturnMappedPageWithAllFilters() {
        Page<Communication> repositoryPage =
                new PageImpl<>(List.of(phoneCommunication), pageable, 1);

        when(communicationRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(repositoryPage);
        when(timelineMapper.toDto(phoneCommunication)).thenReturn(phoneTimelineDTO);

        Page<TimelineItemDTO> result = timelineService.timeline(
                12,
                "PHONE",
                "Max",
                CommunicationStatus.TERMINVEREINBARUNG,
                LocalDate.of(2026, 3, 1),
                pageable
        );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().id).isEqualTo(2);
        assertThat(result.getContent().getFirst().type).isEqualTo(CommunicationType.PHONE);
        assertThat(result.getContent().getFirst().person).isEqualTo("Max Mustermann");
        assertThat(result.getContent().getFirst().status)
                .isEqualTo(CommunicationStatus.TERMINVEREINBARUNG);

        verify(communicationRepository).findAll(any(Specification.class), eq(pageable));
        verify(timelineMapper).toDto(phoneCommunication);
    }

    @Test
    void timeline_shouldReturnEmptyPageWhenRepositoryReturnsNoResults() {
        Page<Communication> repositoryPage =
                new PageImpl<>(List.of(), pageable, 0);

        when(communicationRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(repositoryPage);

        Page<TimelineItemDTO> result = timelineService.timeline(
                null,
                null,
                null,
                CommunicationStatus.OFFEN,
                null,
                pageable
        );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();

        verify(communicationRepository).findAll(any(Specification.class), eq(pageable));
        verifyNoInteractions(timelineMapper);
    }

    @Test
    void timeline_shouldThrowBadRequestWhenTypeIsUnknown() {
        assertThatThrownBy(() -> timelineService.timeline(
                null,
                "FOO",
                null,
                null,
                null,
                pageable
        ))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400 BAD_REQUEST")
                .hasMessageContaining("Unknown type: FOO");

        verifyNoInteractions(communicationRepository);
        verifyNoInteractions(timelineMapper);
    }

    @Test
    void timeline_shouldPassSpecificationAndPageableToRepository() {
        Page<Communication> repositoryPage =
                new PageImpl<>(List.of(mailCommunication, phoneCommunication), pageable, 2);

        when(communicationRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(repositoryPage);
        when(timelineMapper.toDto(mailCommunication)).thenReturn(mailTimelineDTO);
        when(timelineMapper.toDto(phoneCommunication)).thenReturn(phoneTimelineDTO);

        Page<TimelineItemDTO> result = timelineService.timeline(
                7,
                "MAIL",
                "Anna",
                CommunicationStatus.INFORMATION_ERHALTEN,
                LocalDate.of(2026, 3, 20),
                pageable
        );

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);

        verify(communicationRepository).findAll(any(Specification.class), eq(pageable));
        verify(timelineMapper).toDto(mailCommunication);
        verify(timelineMapper).toDto(phoneCommunication);
    }
}