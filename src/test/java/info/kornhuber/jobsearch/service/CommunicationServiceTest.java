package info.kornhuber.jobsearch.service;

import info.kornhuber.jobsearch.dto.CommunicationResponseDTO;
import info.kornhuber.jobsearch.dto.CreateCommunicationRequest;
import info.kornhuber.jobsearch.domain.entity.Communication;
import info.kornhuber.jobsearch.domain.entity.Job;
import info.kornhuber.jobsearch.domain.entity.PhoneCommunication;
import info.kornhuber.jobsearch.dto.UpdateCommunicationRequest;
import info.kornhuber.jobsearch.enums.CommunicationStatus;
import info.kornhuber.jobsearch.enums.CommunicationType;
import info.kornhuber.jobsearch.enums.CommunicationDirection;
import info.kornhuber.jobsearch.exception.NotFoundException;
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
    private CreateCommunicationRequest createPhoneRequest;
    private UpdateCommunicationRequest updatePhoneRequest;
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

        createPhoneRequest = new CreateCommunicationRequest();
        createPhoneRequest.jobId = 10;
        createPhoneRequest.type = CommunicationType.PHONE;
        createPhoneRequest.date = LocalDateTime.of(2026, 3, 20, 10, 30);
        createPhoneRequest.person = "Max Mustermann";
        createPhoneRequest.role = "HR";
        createPhoneRequest.content = "Telefonisches Erstgespräch";
        createPhoneRequest.sidemarks = "freundlich";
        createPhoneRequest.status = CommunicationStatus.TERMINVEREINBARUNG;
        createPhoneRequest.number = "+43 660 1234567";
        createPhoneRequest.direction = CommunicationDirection.OUT;

        updatePhoneRequest = new UpdateCommunicationRequest();
        updatePhoneRequest.date = LocalDateTime.of(2026, 3, 20, 10, 30);
        updatePhoneRequest.person = "Max Mustermann";
        updatePhoneRequest.role = "HR";
        updatePhoneRequest.content = "Telefonisches Erstgespräch";
        updatePhoneRequest.sidemarks = "freundlich";
        updatePhoneRequest.status = CommunicationStatus.TERMINVEREINBARUNG;
        updatePhoneRequest.number = "+43 660 1234567";
        updatePhoneRequest.direction = CommunicationDirection.OUT;

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
        when(communicationFactory.create(createPhoneRequest)).thenReturn(phoneCommunication);
        when(jobRepository.findById(10)).thenReturn(Optional.of(job));
        when(communicationRepository.save(phoneCommunication)).thenReturn(phoneCommunication);
        when(communicationMapper.toDto(phoneCommunication)).thenReturn(phoneResponseDto);

        CommunicationResponseDTO result = communicationService.create(createPhoneRequest);

        assertThat(result).isNotNull();
        assertThat(result.id).isEqualTo(100);
        assertThat(result.type).isEqualTo(CommunicationType.PHONE);
        assertThat(result.jobId).isEqualTo(10);
        assertThat(result.number).isEqualTo("+43 660 1234567");
        assertThat(result.direction).isEqualTo(CommunicationDirection.OUT);

        verify(communicationFactory).create(createPhoneRequest);
        verify(jobRepository).findById(10);
        verify(communicationRepository).save(phoneCommunication);
        verify(communicationMapper).toDto(phoneCommunication);
    }

    @Test
    void create_shouldApplyCommonAndSpecificFieldsBeforeSave() {
        when(communicationFactory.create(createPhoneRequest)).thenReturn(phoneCommunication);
        when(jobRepository.findById(10)).thenReturn(Optional.of(job));
        when(communicationRepository.save(any(PhoneCommunication.class))).thenReturn(phoneCommunication);
        when(communicationMapper.toDto(phoneCommunication)).thenReturn(phoneResponseDto);

        communicationService.create(createPhoneRequest);

        ArgumentCaptor<PhoneCommunication> captor = ArgumentCaptor.forClass(PhoneCommunication.class);
        verify(communicationRepository).save(captor.capture());

        PhoneCommunication saved = captor.getValue();

        assertThat(saved.getJob()).isEqualTo(job);
        assertThat(saved.getDate()).isEqualTo(createPhoneRequest.date);
        assertThat(saved.getPerson()).isEqualTo(createPhoneRequest.person);
        assertThat(saved.getRole()).isEqualTo(createPhoneRequest.role);
        assertThat(saved.getContent()).isEqualTo(createPhoneRequest.content);
        assertThat(saved.getSidemarks()).isEqualTo(createPhoneRequest.sidemarks);
        assertThat(saved.getStatus()).isEqualTo(createPhoneRequest.status);
        assertThat(saved.getNumber()).isEqualTo(createPhoneRequest.number);
        assertThat(saved.getDirection()).isEqualTo(createPhoneRequest.direction);
    }

    @Test
    void create_shouldThrowWhenJobDoesNotExist() {
        when(communicationFactory.create(createPhoneRequest)).thenReturn(phoneCommunication);
        when(jobRepository.findById(10)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> communicationService.create(createPhoneRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Job not found: 10");

        verify(communicationRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateExistingCommunicationAndReturnDto() {
        when(communicationRepository.findById(100)).thenReturn(Optional.of(phoneCommunication));
        when(communicationRepository.save(phoneCommunication)).thenReturn(phoneCommunication);
        when(communicationMapper.toDto(phoneCommunication)).thenReturn(phoneResponseDto);

        CommunicationResponseDTO result = communicationService.update(100, updatePhoneRequest);

        assertThat(result).isNotNull();
        assertThat(result.type).isEqualTo(CommunicationType.PHONE);
        assertThat(result.number).isEqualTo("+43 660 1234567");
        assertThat(result.direction).isEqualTo(CommunicationDirection.OUT);

        verify(communicationRepository).findById(100);
        verify(communicationRepository).save(phoneCommunication);
        verify(communicationMapper, atLeastOnce()).toDto(phoneCommunication);
    }

    @Test
    void getById_shouldReturnMappedDto() {
        when(communicationRepository.findById(100)).thenReturn(Optional.of(phoneCommunication));
        when(communicationMapper.toDto(phoneCommunication)).thenReturn(phoneResponseDto);

        CommunicationResponseDTO result = communicationService.getById(100);

        assertThat(result).isNotNull();
        assertThat(result.id).isEqualTo(100);
        assertThat(result.type).isEqualTo(CommunicationType.PHONE);
    }

    @Test
    void delete_shouldLoadAndDeleteExistingCommunication() {
        when(communicationRepository.findById(100)).thenReturn(Optional.of(phoneCommunication));
        doNothing().when(communicationRepository).delete(phoneCommunication);

        communicationService.delete(100);

        verify(communicationRepository).findById(100);
        verify(communicationRepository).delete(phoneCommunication);
        verify(communicationRepository, never()).deleteById(any());
    }

    @Test
    void delete_shouldThrowWhenCommunicationDoesNotExist() {
        when(communicationRepository.findById(404)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> communicationService.delete(404))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Communication not found: 404");

        verify(communicationRepository, never()).delete(any(Communication.class));
        verify(communicationRepository, never()).deleteById(any());
    }
}
