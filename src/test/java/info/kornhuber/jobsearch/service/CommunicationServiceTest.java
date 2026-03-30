package info.kornhuber.jobsearch.service;

import info.kornhuber.jobsearch.dto.CommunicationResponseDTO;
import info.kornhuber.jobsearch.dto.CreateCommunicationRequest;
import info.kornhuber.jobsearch.domain.entity.Job;
import info.kornhuber.jobsearch.domain.entity.PhoneCommunication;
import info.kornhuber.jobsearch.enums.CommunicationStatus;
import info.kornhuber.jobsearch.enums.CommunicationType;
import info.kornhuber.jobsearch.enums.CommunicationDirection;
import info.kornhuber.jobsearch.mapper.CommunicationMapper;
import info.kornhuber.jobsearch.domain.repository.CommunicationRepository;
import info.kornhuber.jobsearch.domain.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunicationServiceTest {

    @Mock
    private CommunicationFactory communicationFactory;

    @Mock
    private CommunicationRepository communicationRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private CommunicationMapper communicationMapper;

    @InjectMocks
    private CommunicationService communicationService;

    private Job job;
    private PhoneCommunication phoneCommunication;
    private CreateCommunicationRequest phoneRequest;
    private CommunicationResponseDTO phoneResponseDto;

    @BeforeEach
    void setUp() {
        job = new Job();
        job.setId(10);

        phoneCommunication = new PhoneCommunication();
        phoneCommunication.setId(100);
        phoneCommunication.setJob(job);
        phoneCommunication.setDate(LocalDateTime.of(2026, 3, 20, 10, 30));
        phoneCommunication.setPerson("Max Mustermann");
        phoneCommunication.setRole("HR");
        phoneCommunication.setContent("Telefonisches Erstgespräch");
        phoneCommunication.setSidemarks("freundlich");
        phoneCommunication.setStatus(CommunicationStatus.TERMINVEREINBARUNG);
        phoneCommunication.setNumber("+43 660 1234567");
        phoneCommunication.setDirection(CommunicationDirection.OUT);

        phoneRequest = new CreateCommunicationRequest();
        phoneRequest.type = CommunicationType.PHONE;
        phoneRequest.jobId = 10;
        phoneRequest.date = LocalDateTime.of(2026, 3, 20, 10, 30);
        phoneRequest.person = "Max Mustermann";
        phoneRequest.role = "HR";
        phoneRequest.content = "Telefonisches Erstgespräch";
        phoneRequest.sidemarks = "freundlich";
        phoneRequest.status = CommunicationStatus.TERMINVEREINBARUNG;
        phoneRequest.number = "+43 660 1234567";
        phoneRequest.direction = CommunicationDirection.OUT;

        phoneResponseDto = new CommunicationResponseDTO();
        phoneResponseDto.id = 100;
        phoneResponseDto.type = CommunicationType.PHONE;
        phoneResponseDto.jobId = 10;
        phoneResponseDto.date = LocalDateTime.of(2026, 3, 20, 10, 30);
        phoneResponseDto.person = "Max Mustermann";
        phoneResponseDto.role = "HR";
        phoneResponseDto.content = "Telefonisches Erstgespräch";
        phoneResponseDto.sidemarks = "freundlich";
        phoneResponseDto.status = CommunicationStatus.TERMINVEREINBARUNG;
        phoneResponseDto.number = "+43 660 1234567";
        phoneResponseDto.direction = CommunicationDirection.OUT;
    }

    @Test
    void create_shouldCreatePhoneCommunicationAndReturnDto() {
        when(communicationFactory.create(phoneRequest)).thenReturn(phoneCommunication);
        when(jobRepository.findById(10)).thenReturn(Optional.of(job));
        when(communicationRepository.save(phoneCommunication)).thenReturn(phoneCommunication);
        when(communicationMapper.toDto(phoneCommunication)).thenReturn(phoneResponseDto);

        CommunicationResponseDTO result = communicationService.create(phoneRequest);

        assertThat(result).isNotNull();
        assertThat(result.id).isEqualTo(100);
        assertThat(result.type).isEqualTo(CommunicationType.PHONE);
        assertThat(result.jobId).isEqualTo(10);
        assertThat(result.number).isEqualTo("+43 660 1234567");
        assertThat(result.direction).isEqualTo(CommunicationDirection.OUT);

        verify(communicationFactory).create(phoneRequest);
        verify(jobRepository).findById(10);
        verify(communicationRepository).save(phoneCommunication);
        verify(communicationMapper).toDto(phoneCommunication);
    }

    @Test
    void create_shouldApplyCommonAndSpecificFieldsBeforeSave() {
        when(communicationFactory.create(phoneRequest)).thenReturn(phoneCommunication);
        when(jobRepository.findById(10)).thenReturn(Optional.of(job));
        when(communicationRepository.save(any(PhoneCommunication.class))).thenReturn(phoneCommunication);
        when(communicationMapper.toDto(phoneCommunication)).thenReturn(phoneResponseDto);

        communicationService.create(phoneRequest);

        ArgumentCaptor<PhoneCommunication> captor = ArgumentCaptor.forClass(PhoneCommunication.class);
        verify(communicationRepository).save(captor.capture());

        PhoneCommunication saved = captor.getValue();

        assertThat(saved.getJob()).isEqualTo(job);
        assertThat(saved.getDate()).isEqualTo(phoneRequest.date);
        assertThat(saved.getPerson()).isEqualTo(phoneRequest.person);
        assertThat(saved.getRole()).isEqualTo(phoneRequest.role);
        assertThat(saved.getContent()).isEqualTo(phoneRequest.content);
        assertThat(saved.getSidemarks()).isEqualTo(phoneRequest.sidemarks);
        assertThat(saved.getStatus()).isEqualTo(phoneRequest.status);
        assertThat(saved.getNumber()).isEqualTo(phoneRequest.number);
        assertThat(saved.getDirection()).isEqualTo(phoneRequest.direction);
    }

    @Test
    void create_shouldThrowWhenJobDoesNotExist() {
        when(communicationFactory.create(phoneRequest)).thenReturn(phoneCommunication);
        when(jobRepository.findById(10)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> communicationService.create(phoneRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Job not found: 10");

        verify(communicationRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateExistingCommunicationAndReturnDto() {
        when(communicationRepository.findById(100)).thenReturn(Optional.of(phoneCommunication));
        when(communicationMapper.toDto(phoneCommunication)).thenReturn(phoneResponseDto);
        when(jobRepository.findById(10)).thenReturn(Optional.of(job));
        when(communicationRepository.save(phoneCommunication)).thenReturn(phoneCommunication);

        CommunicationResponseDTO result = communicationService.update(100, phoneRequest);

        assertThat(result).isNotNull();
        assertThat(result.type).isEqualTo(CommunicationType.PHONE);
        assertThat(result.number).isEqualTo("+43 660 1234567");
        assertThat(result.direction).isEqualTo(CommunicationDirection.OUT);

        verify(communicationRepository).findById(100);
        verify(jobRepository).findById(10);
        verify(communicationRepository).save(phoneCommunication);
    }

    @Test
    void update_shouldThrowWhenTypeChanges() {
        CommunicationResponseDTO existingDto = new CommunicationResponseDTO();
        existingDto.id = 100;
        existingDto.type = CommunicationType.MAIL;

        when(communicationRepository.findById(100)).thenReturn(Optional.of(phoneCommunication));
        when(communicationMapper.toDto(phoneCommunication)).thenReturn(existingDto);

        assertThatThrownBy(() -> communicationService.update(100, phoneRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Communication-Typ darf nicht geändert werden");

        verify(communicationRepository, never()).save(any());
    }

    @Test
    void findById_shouldReturnMappedDto() {
        when(communicationRepository.findById(100)).thenReturn(Optional.of(phoneCommunication));
        when(communicationMapper.toDto(phoneCommunication)).thenReturn(phoneResponseDto);

        CommunicationResponseDTO result = communicationService.findById(100);

        assertThat(result).isNotNull();
        assertThat(result.id).isEqualTo(100);
        assertThat(result.type).isEqualTo(CommunicationType.PHONE);
    }
}