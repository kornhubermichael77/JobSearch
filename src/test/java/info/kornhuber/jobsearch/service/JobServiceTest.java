package info.kornhuber.jobsearch.service;

import info.kornhuber.jobsearch.dto.CreateAddressRequest;
import info.kornhuber.jobsearch.dto.CreateCompanyRequest;
import info.kornhuber.jobsearch.dto.CreateJobRequest;
import info.kornhuber.jobsearch.dto.JobResponseDTO;
import info.kornhuber.jobsearch.dto.UpdateJobRequest;
import info.kornhuber.jobsearch.domain.entity.Address;
import info.kornhuber.jobsearch.domain.entity.Company;
import info.kornhuber.jobsearch.domain.entity.Job;
import info.kornhuber.jobsearch.enums.CommunicationStatus;
import info.kornhuber.jobsearch.exception.BadRequestException;
import info.kornhuber.jobsearch.exception.ConflictException;
import info.kornhuber.jobsearch.mapper.JobMapper;
import info.kornhuber.jobsearch.domain.repository.AddressRepository;
import info.kornhuber.jobsearch.domain.repository.CompanyRepository;
import info.kornhuber.jobsearch.domain.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import info.kornhuber.jobsearch.domain.repository.projection.JobWithCommunicationCountProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        job = new Job();
        job.setId(100);
        job.setCompany(company1);
        job.setAddress(address1);
        job.setStatus(CommunicationStatus.OFFEN);
        job.setSource("LinkedIn");
        job.setFound(LocalDateTime.of(2026, 3, 24, 10, 0));

        jobResponse = new JobResponseDTO();
        jobResponse.id = 100;
        jobResponse.companyId = 1;
        jobResponse.addressId = 10;
        jobResponse.status = CommunicationStatus.OFFEN;
        jobResponse.source = "LinkedIn";
        jobResponse.found = LocalDateTime.of(2026, 3, 24, 10, 0);
    }

    @Test
    void create_shouldCreateJobWithExistingCompanyOnly() {
        CreateJobRequest req = new CreateJobRequest();
        req.companyId = 1;
        req.status = CommunicationStatus.OFFEN;
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
        assertThat(result.addressId).isEqualTo(10); // kommt hier aus Mock-DTO

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(jobRepository).save(captor.capture());

        Job savedJob = captor.getValue();
        assertThat(savedJob.getCompany()).isEqualTo(company1);
        assertThat(savedJob.getAddress()).isNull();
        assertThat(savedJob.getStatus()).isEqualTo(CommunicationStatus.OFFEN);
        assertThat(savedJob.getSource()).isEqualTo("LinkedIn");
    }

    @Test
    void create_shouldCreateJobWithNewCompany() {
        CreateJobRequest req = new CreateJobRequest();
        req.status = CommunicationStatus.OFFEN;
        req.source = "LinkedIn";

        req.newCompany = new CreateCompanyRequest();
        req.newCompany.name = "Neue Firma";
        req.newCompany.mail = "hr@neu.example";
        req.newCompany.tel = "+43 1 123456";

        Company savedCompany = new Company();
        savedCompany.setId(99);
        savedCompany.setName("Neue Firma");

        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> {
            Job saved = invocation.getArgument(0);
            saved.setId(100);
            return saved;
        });
        when(jobMapper.toDto(any(Job.class))).thenReturn(jobResponse);

        jobService.create(req);

        verify(companyRepository).save(any(Company.class));

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(jobRepository).save(captor.capture());

        Job savedJob = captor.getValue();
        assertThat(savedJob.getCompany()).isEqualTo(savedCompany);
    }

    @Test
    void create_shouldCreateJobWithExistingCompanyAndExistingAddress() {
        CreateJobRequest req = new CreateJobRequest();
        req.companyId = 1;
        req.addressId = 10;
        req.status = CommunicationStatus.OFFEN;

        when(companyRepository.findById(1)).thenReturn(Optional.of(company1));
        when(addressRepository.findById(10)).thenReturn(Optional.of(address1));
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jobMapper.toDto(any(Job.class))).thenReturn(jobResponse);

        jobService.create(req);

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(jobRepository).save(captor.capture());

        Job savedJob = captor.getValue();
        assertThat(savedJob.getCompany()).isEqualTo(company1);
        assertThat(savedJob.getAddress()).isEqualTo(address1);
    }

    @Test
    void create_shouldThrowWhenAddressDoesNotBelongToSelectedCompany() {
        CreateJobRequest req = new CreateJobRequest();
        req.companyId = 1;
        req.addressId = 20;
        req.status = CommunicationStatus.OFFEN;

        when(companyRepository.findById(1)).thenReturn(Optional.of(company1));
        when(addressRepository.findById(20)).thenReturn(Optional.of(address2));

        assertThatThrownBy(() -> jobService.create(req))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Address does not belong to the selected company");

        verify(jobRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowWhenBothCompanyIdAndNewCompanyAreSet() {
        CreateJobRequest req = new CreateJobRequest();
        req.companyId = 1;
        req.newCompany = new CreateCompanyRequest();
        req.newCompany.name = "Neue Firma";
        req.status = CommunicationStatus.OFFEN;

        assertThatThrownBy(() -> jobService.create(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Either known company or new company must be set");

        verify(jobRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowWhenNeitherCompanyIdNorNewCompanyIsSet() {
        CreateJobRequest req = new CreateJobRequest();
        req.status = CommunicationStatus.OFFEN;

        assertThatThrownBy(() -> jobService.create(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Either known company or new company must be set");

        verify(jobRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowWhenBothAddressIdAndNewAddressAreSet() {
        CreateJobRequest req = new CreateJobRequest();
        req.companyId = 1;
        req.addressId = 10;
        req.newAddress = new CreateAddressRequest();
        req.newAddress.city = "Wien";
        req.status = CommunicationStatus.OFFEN;

        when(companyRepository.findById(1)).thenReturn(Optional.of(company1));

        assertThatThrownBy(() -> jobService.create(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Only one of addressId or newAddress may be set");

        verify(jobRepository, never()).save(any());
    }

    @Test
    void create_shouldCreateJobWithNewAddressForResolvedCompany() {
        CreateJobRequest req = new CreateJobRequest();
        req.companyId = 1;
        req.status = CommunicationStatus.OFFEN;

        req.newAddress = new CreateAddressRequest();
        req.newAddress.street = "Neue Straße";
        req.newAddress.number = "12";
        req.newAddress.postcode = "1030";
        req.newAddress.city = "Wien";
        req.newAddress.country = "Österreich";

        Address savedAddress = new Address();
        savedAddress.setId(88);
        savedAddress.setCompany(company1);

        when(companyRepository.findById(1)).thenReturn(Optional.of(company1));
        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jobMapper.toDto(any(Job.class))).thenReturn(jobResponse);

        jobService.create(req);

        verify(addressRepository).save(any(Address.class));

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(jobRepository).save(captor.capture());

        Job savedJob = captor.getValue();
        assertThat(savedJob.getAddress()).isEqualTo(savedAddress);
        assertThat(savedJob.getCompany()).isEqualTo(company1);
    }

    @Test
    void update_shouldKeepExistingCompanyAndAddressWhenIdsAreNotProvided() {
        UpdateJobRequest req = new UpdateJobRequest();
        req.status = CommunicationStatus.INFORMATION_ERHALTEN;
        req.source = "Xing";

        when(jobRepository.findById(100)).thenReturn(Optional.of(job));
        when(jobRepository.save(job)).thenReturn(job);
        when(jobMapper.toDto(job)).thenReturn(jobResponse);

        jobService.update(100, req);

        assertThat(job.getCompany()).isEqualTo(company1);
        assertThat(job.getAddress()).isEqualTo(address1);
        assertThat(job.getStatus()).isEqualTo(CommunicationStatus.INFORMATION_ERHALTEN);
        assertThat(job.getSource()).isEqualTo("Xing");
    }

    @Test
    void update_shouldChangeCompanyAndAddressWhenValidIdsAreProvided() {
        UpdateJobRequest req = new UpdateJobRequest();
        req.companyId = 2;
        req.addressId = 20;
        req.status = CommunicationStatus.OFFEN;

        when(jobRepository.findById(100)).thenReturn(Optional.of(job));
        when(companyRepository.findById(2)).thenReturn(Optional.of(company2));
        when(addressRepository.findById(20)).thenReturn(Optional.of(address2));
        when(jobRepository.save(job)).thenReturn(job);
        when(jobMapper.toDto(job)).thenReturn(jobResponse);

        jobService.update(100, req);

        assertThat(job.getCompany()).isEqualTo(company2);
        assertThat(job.getAddress()).isEqualTo(address2);
    }

    @Test
    void update_shouldThrowWhenSelectedAddressDoesNotBelongToSelectedCompany() {
        UpdateJobRequest req = new UpdateJobRequest();
        req.companyId = 1;
        req.addressId = 20;
        req.status = CommunicationStatus.OFFEN;

        when(jobRepository.findById(100)).thenReturn(Optional.of(job));
        when(companyRepository.findById(1)).thenReturn(Optional.of(company1));
        when(addressRepository.findById(20)).thenReturn(Optional.of(address2));

        assertThatThrownBy(() -> jobService.update(100, req))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Address does not belong to the selected company");

        verify(jobRepository, never()).save(any());
    }

    @Test
    void findById_shouldReturnMappedDto() {
        when(jobRepository.findById(100)).thenReturn(Optional.of(job));
        when(jobMapper.toDto(job)).thenReturn(jobResponse);

        JobResponseDTO result = jobService.findById(100);

        assertThat(result).isNotNull();
        assertThat(result.id).isEqualTo(100);
    }

    @Test
    void findAll_shouldFilterByCompanyId() {
        JobWithCommunicationCountProjection projection = mock(JobWithCommunicationCountProjection.class);

        when(projection.getId()).thenReturn(100);
        when(projection.getCompanyId()).thenReturn(3);
        when(projection.getCompanyName()).thenReturn("OpenAI GmbH");
        when(projection.getAddressId()).thenReturn(10);
        when(projection.getCity()).thenReturn("Wien");
        when(projection.getStreet()).thenReturn("Hauptstraße");
        when(projection.getNumber()).thenReturn("1");
        when(projection.getFound()).thenReturn(LocalDateTime.of(2026, 3, 24, 10, 0));
        when(projection.getSource()).thenReturn("LinkedIn");
        when(projection.getUrl()).thenReturn(null);
        when(projection.getText()).thenReturn(null);
        when(projection.getStatus()).thenReturn(CommunicationStatus.OFFEN);
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

        List<JobResponseDTO> result = jobService.findAll(null, 3);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id).isEqualTo(100);
        assertThat(result.getFirst().companyId).isEqualTo(3);
        assertThat(result.getFirst().status).isEqualTo(CommunicationStatus.OFFEN);
        assertThat(result.getFirst().communicationCount).isEqualTo(2L);

        verify(jobRepository).findAllWithCommunicationCount(null, 3);
    }

    @Test
    void findAll_shouldFilterByStatusAndCompanyId() {
        JobWithCommunicationCountProjection projection = mock(JobWithCommunicationCountProjection.class);

        when(projection.getId()).thenReturn(100);
        when(projection.getCompanyId()).thenReturn(3);
        when(projection.getCompanyName()).thenReturn("OpenAI GmbH");
        when(projection.getAddressId()).thenReturn(10);
        when(projection.getCity()).thenReturn("Wien");
        when(projection.getStreet()).thenReturn("Hauptstraße");
        when(projection.getNumber()).thenReturn("1");
        when(projection.getFound()).thenReturn(LocalDateTime.of(2026, 3, 24, 10, 0));
        when(projection.getSource()).thenReturn("LinkedIn");
        when(projection.getUrl()).thenReturn(null);
        when(projection.getText()).thenReturn(null);
        when(projection.getStatus()).thenReturn(CommunicationStatus.OFFEN);
        when(projection.getMail()).thenReturn(null);
        when(projection.getMailPerson()).thenReturn(null);
        when(projection.getTel()).thenReturn(null);
        when(projection.getTelPerson()).thenReturn(null);
        when(projection.getTeilzeit()).thenReturn(null);
        when(projection.getGleitzeit()).thenReturn(null);
        when(projection.getHomeoffice()).thenReturn(null);
        when(projection.getFeatures()).thenReturn(null);
        when(projection.getCommunicationCount()).thenReturn(3L);

        when(jobRepository.findAllWithCommunicationCount(CommunicationStatus.OFFEN, 3))
                .thenReturn(List.of(projection));

        List<JobResponseDTO> result = jobService.findAll(CommunicationStatus.OFFEN, 3);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id).isEqualTo(100);
        assertThat(result.getFirst().companyId).isEqualTo(3);
        assertThat(result.getFirst().status).isEqualTo(CommunicationStatus.OFFEN);
        assertThat(result.getFirst().communicationCount).isEqualTo(3L);

        verify(jobRepository).findAllWithCommunicationCount(CommunicationStatus.OFFEN, 3);
    }


}