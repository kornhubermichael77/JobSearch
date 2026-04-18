package info.kornhuber.jobsearch.service;

import info.kornhuber.jobsearch.domain.entity.Address;
import info.kornhuber.jobsearch.domain.entity.Company;
import info.kornhuber.jobsearch.domain.entity.Job;
import info.kornhuber.jobsearch.domain.repository.AddressRepository;
import info.kornhuber.jobsearch.domain.repository.CompanyRepository;
import info.kornhuber.jobsearch.domain.repository.JobRepository;
import info.kornhuber.jobsearch.domain.repository.projection.JobWithCommunicationCountProjection;
import info.kornhuber.jobsearch.dto.CreateJobRequest;
import info.kornhuber.jobsearch.dto.JobResponseDTO;
import info.kornhuber.jobsearch.dto.UpdateJobAddressRequest;
import info.kornhuber.jobsearch.dto.UpdateJobRequest;
import info.kornhuber.jobsearch.enums.JobStatus;
import info.kornhuber.jobsearch.exception.ConflictException;
import info.kornhuber.jobsearch.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests für den JobService auf Basis der aktuellen, vereinfachten Logik:
 * - Job-Erstellung nur mit bestehender companyId
 * - companyId wird beim Update nicht mehr geändert
 * - addressId wird nicht mehr über UpdateJobRequest geändert
 * - Job-Adresse wird separat über updateJobAddress(...) gesetzt
 */
@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private JobMapper jobMapper;

    @InjectMocks
    private JobService jobService;

    private Company company1;
    private Company company2;
    private Address address1;
    private Address address2;
    private Address addressWithoutCompany;
    private Job job;
    private JobResponseDTO jobResponse;

    @BeforeEach
    void setUp() {
        company1 = new Company();
        company1.setId(1);
        company1.setName("OpenAI GmbH");

        company2 = new Company();
        company2.setId(2);
        company2.setName("Anthropic GmbH");

        address1 = new Address();
        address1.setId(10);
        address1.setStreet("Hauptstraße");
        address1.setNumber("1");
        address1.setPostcode("1010");
        address1.setCity("Wien");
        address1.setCountry("Österreich");
        address1.setCompany(company1);

        address2 = new Address();
        address2.setId(20);
        address2.setStreet("Nebenstraße");
        address2.setNumber("5");
        address2.setPostcode("1020");
        address2.setCity("Wien");
        address2.setCountry("Österreich");
        address2.setCompany(company2);

        addressWithoutCompany = new Address();
        addressWithoutCompany.setId(30);
        addressWithoutCompany.setStreet("Freie Straße");
        addressWithoutCompany.setCity("Wien");
        addressWithoutCompany.setCompany(null);

        job = new Job();
        job.setId(100);
        job.setCompany(company1);
        job.setAddress(address1);
        job.setStatus(JobStatus.BEWORBEN);
        job.setSource("LinkedIn");
        job.setFound(LocalDateTime.of(2026, 3, 24, 10, 0));

        jobResponse = new JobResponseDTO();
        jobResponse.id = 100;
        jobResponse.companyId = 1;
        jobResponse.addressId = 10;
        jobResponse.status = JobStatus.BEWORBEN;
        jobResponse.source = "LinkedIn";
        jobResponse.found = LocalDateTime.of(2026, 3, 24, 10, 0);
        jobResponse.communicationCount = 0L;
    }

    @Test
    void create_shouldCreateJobWithExistingCompanyOnly() {
        CreateJobRequest req = new CreateJobRequest();
        req.companyId = 1;
        req.status = JobStatus.BEWORBEN;
        req.source = "LinkedIn";
        req.found = LocalDateTime.of(2026, 3, 24, 10, 0);

        when(companyRepository.findById(1)).thenReturn(Optional.of(company1));
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> {
            Job saved = invocation.getArgument(0);
            saved.setId(100);
            return saved;
        });
        when(jobMapper.toDto(any(Job.class))).thenReturn(jobResponse);

        JobResponseDTO result = jobService.create(req);

        assertThat(result).isNotNull();
        assertThat(result.id).isEqualTo(100);
        assertThat(result.companyId).isEqualTo(1);
        assertThat(result.status).isEqualTo(JobStatus.BEWORBEN);

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(jobRepository).save(captor.capture());

        Job savedJob = captor.getValue();
        assertThat(savedJob.getCompany()).isEqualTo(company1);
        assertThat(savedJob.getAddress()).isNull();
        assertThat(savedJob.getSource()).isEqualTo("LinkedIn");
        assertThat(savedJob.getFound()).isEqualTo(LocalDateTime.of(2026, 3, 24, 10, 0));
    }

    @Test
    void create_shouldThrowWhenCompanyDoesNotExist() {
        CreateJobRequest req = new CreateJobRequest();
        req.companyId = 999;
        req.status = JobStatus.BEWORBEN;

        when(companyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobService.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Company not found: 999");

        verify(jobRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateMutableFieldsButKeepExistingCompanyAndAddress() {
        UpdateJobRequest req = new UpdateJobRequest();
        req.status = JobStatus.BEWORBEN;
        req.source = "Xing";
        req.url = "https://example.com/job/100";
        req.text = "Aktualisierte Notizen";
        req.mail = "jobs@example.com";
        req.mailPerson = "Anna HR";
        req.tel = "+43 1 123456";
        req.telPerson = "Anna";
        req.teilzeit = "nein";
        req.gleitzeit = "ja";
        req.homeoffice = "teilweise";
        req.features = "Java, Spring Boot";

        when(jobRepository.findById(100)).thenReturn(Optional.of(job));
        when(jobRepository.save(job)).thenReturn(job);
        when(jobMapper.toDto(job)).thenReturn(jobResponse);

        jobService.update(100, req);

        assertThat(job.getCompany()).isEqualTo(company1);
        assertThat(job.getAddress()).isEqualTo(address1);
        assertThat(job.getStatus()).isEqualTo(JobStatus.BEWORBEN);
        assertThat(job.getSource()).isEqualTo("Xing");
        assertThat(job.getUrl()).isEqualTo("https://example.com/job/100");
        assertThat(job.getText()).isEqualTo("Aktualisierte Notizen");
        assertThat(job.getMail()).isEqualTo("jobs@example.com");
        assertThat(job.getMailPerson()).isEqualTo("Anna HR");
        assertThat(job.getTel()).isEqualTo("+43 1 123456");
        assertThat(job.getTelPerson()).isEqualTo("Anna");
        assertThat(job.getTeilzeit()).isEqualTo("nein");
        assertThat(job.getGleitzeit()).isEqualTo("ja");
        assertThat(job.getHomeoffice()).isEqualTo("teilweise");
        assertThat(job.getFeatures()).isEqualTo("Java, Spring Boot");
    }

    @Test
    void updateJobAddress_shouldAssignAddressWhenItBelongsToJobsCompany() {
        UpdateJobAddressRequest req = new UpdateJobAddressRequest();
        req.addressId = 10;

        when(jobRepository.findById(100)).thenReturn(Optional.of(job));
        when(addressRepository.findById(10)).thenReturn(Optional.of(address1));
        when(jobRepository.save(job)).thenReturn(job);
        when(jobMapper.toDto(job)).thenReturn(jobResponse);

        JobResponseDTO result = jobService.updateJobAddress(100, req);

        assertThat(result).isNotNull();
        assertThat(job.getAddress()).isEqualTo(address1);

        verify(jobRepository).save(job);
    }

    @Test
    void updateJobAddress_shouldRemoveAddressAssignmentWhenAddressIdIsNull() {
        UpdateJobAddressRequest req = new UpdateJobAddressRequest();
        req.addressId = null;

        when(jobRepository.findById(100)).thenReturn(Optional.of(job));
        when(jobRepository.save(job)).thenReturn(job);
        when(jobMapper.toDto(job)).thenReturn(jobResponse);

        jobService.updateJobAddress(100, req);

        assertThat(job.getAddress()).isNull();
        verify(jobRepository).save(job);
        verify(addressRepository, never()).findById(any());
    }

    @Test
    void updateJobAddress_shouldThrowWhenAddressDoesNotBelongToJobsCompany() {
        UpdateJobAddressRequest req = new UpdateJobAddressRequest();
        req.addressId = 20;

        when(jobRepository.findById(100)).thenReturn(Optional.of(job));
        when(addressRepository.findById(20)).thenReturn(Optional.of(address2));

        assertThatThrownBy(() -> jobService.updateJobAddress(100, req))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Address does not belong to the job's company");

        verify(jobRepository, never()).save(any());
    }

    @Test
    void updateJobAddress_shouldThrowWhenAddressHasNoCompany() {
        UpdateJobAddressRequest req = new UpdateJobAddressRequest();
        req.addressId = 30;

        when(jobRepository.findById(100)).thenReturn(Optional.of(job));
        when(addressRepository.findById(30)).thenReturn(Optional.of(addressWithoutCompany));

        assertThatThrownBy(() -> jobService.updateJobAddress(100, req))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Address is not assigned to any company");

        verify(jobRepository, never()).save(any());
    }

    @Test
    void getById_shouldReturnMappedDto() {
        when(jobRepository.findById(100)).thenReturn(Optional.of(job));
        when(jobMapper.toDto(job)).thenReturn(jobResponse);

        JobResponseDTO result = jobService.getById(100);

        assertThat(result).isNotNull();
        assertThat(result.id).isEqualTo(100);
        assertThat(result.companyId).isEqualTo(1);
    }

    @Test
    void getAll_shouldFilterByCompanyId() {
        JobWithCommunicationCountProjection projection = mock(JobWithCommunicationCountProjection.class);

        when(projection.getId()).thenReturn(100);
        when(projection.getCompanyId()).thenReturn(3);
        when(projection.getCompanyName()).thenReturn("OpenAI GmbH");
        when(projection.getAddressId()).thenReturn(10);
        when(projection.getCity()).thenReturn("Wien");
        when(projection.getStreet()).thenReturn("Hauptstraße");
        when(projection.getNumber()).thenReturn("1");
        when(projection.getPostcode()).thenReturn("1010");
        when(projection.getCountry()).thenReturn("Österreich");
        when(projection.getHeadquarter()).thenReturn(true);
        when(projection.getDistance()).thenReturn(10.0);
        when(projection.getTraveltime()).thenReturn(LocalDateTime.of(2026, 3, 24, 10, 0));
        when(projection.getFound()).thenReturn(LocalDateTime.of(2026, 3, 24, 10, 0));
        when(projection.getSource()).thenReturn("LinkedIn");
        when(projection.getUrl()).thenReturn(null);
        when(projection.getText()).thenReturn(null);
        when(projection.getStatus()).thenReturn(JobStatus.BEWORBEN);
        when(projection.getMail()).thenReturn(null);
        when(projection.getMailPerson()).thenReturn(null);
        when(projection.getTel()).thenReturn(null);
        when(projection.getTelPerson()).thenReturn(null);
        when(projection.getTeilzeit()).thenReturn(null);
        when(projection.getGleitzeit()).thenReturn(null);
        when(projection.getHomeoffice()).thenReturn(null);
        when(projection.getFeatures()).thenReturn(null);
        when(projection.getCommunicationCount()).thenReturn(2L);

        when(jobRepository.findAllWithCommunicationCount(null, 3))
                .thenReturn(List.of(projection));

        List<JobResponseDTO> result = jobService.getAll(null, 3);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id).isEqualTo(100);
        assertThat(result.getFirst().companyId).isEqualTo(3);
        assertThat(result.getFirst().status).isEqualTo(JobStatus.BEWORBEN);
        assertThat(result.getFirst().communicationCount).isEqualTo(2L);

        verify(jobRepository).findAllWithCommunicationCount(null, 3);
    }

    @Test
    void getAll_shouldFilterByStatusAndCompanyId() {
        JobWithCommunicationCountProjection projection = mock(JobWithCommunicationCountProjection.class);

        when(projection.getId()).thenReturn(100);
        when(projection.getCompanyId()).thenReturn(3);
        when(projection.getCompanyName()).thenReturn("OpenAI GmbH");
        when(projection.getAddressId()).thenReturn(10);
        when(projection.getCity()).thenReturn("Wien");
        when(projection.getStreet()).thenReturn("Hauptstraße");
        when(projection.getNumber()).thenReturn("1");
        when(projection.getPostcode()).thenReturn("1010");
        when(projection.getCountry()).thenReturn("Österreich");
        when(projection.getHeadquarter()).thenReturn(true);
        when(projection.getDistance()).thenReturn(10.0);
        when(projection.getTraveltime()).thenReturn(LocalDateTime.of(2026, 3, 24, 10, 0));
        when(projection.getFound()).thenReturn(LocalDateTime.of(2026, 3, 24, 10, 0));
        when(projection.getSource()).thenReturn("LinkedIn");
        when(projection.getUrl()).thenReturn(null);
        when(projection.getText()).thenReturn(null);
        when(projection.getStatus()).thenReturn(JobStatus.BEWORBEN);
        when(projection.getMail()).thenReturn(null);
        when(projection.getMailPerson()).thenReturn(null);
        when(projection.getTel()).thenReturn(null);
        when(projection.getTelPerson()).thenReturn(null);
        when(projection.getTeilzeit()).thenReturn(null);
        when(projection.getGleitzeit()).thenReturn(null);
        when(projection.getHomeoffice()).thenReturn(null);
        when(projection.getFeatures()).thenReturn(null);
        when(projection.getCommunicationCount()).thenReturn(3L);

        when(jobRepository.findAllWithCommunicationCount(JobStatus.BEWORBEN, 3))
                .thenReturn(List.of(projection));

        List<JobResponseDTO> result = jobService.getAll(JobStatus.BEWORBEN, 3);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id).isEqualTo(100);
        assertThat(result.getFirst().companyId).isEqualTo(3);
        assertThat(result.getFirst().status).isEqualTo(JobStatus.BEWORBEN);
        assertThat(result.getFirst().communicationCount).isEqualTo(3L);

        verify(jobRepository).findAllWithCommunicationCount(JobStatus.BEWORBEN, 3);
    }
}