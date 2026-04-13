package info.kornhuber.jobsearch.service;

import info.kornhuber.jobsearch.dto.*;
import info.kornhuber.jobsearch.domain.entity.Address;
import info.kornhuber.jobsearch.domain.entity.Company;
import info.kornhuber.jobsearch.domain.entity.Job;
import info.kornhuber.jobsearch.enums.JobStatus;
import info.kornhuber.jobsearch.exception.ConflictException;
import info.kornhuber.jobsearch.exception.NotFoundException;
import info.kornhuber.jobsearch.mapper.JobMapper;
import info.kornhuber.jobsearch.domain.repository.AddressRepository;
import info.kornhuber.jobsearch.domain.repository.CompanyRepository;
import info.kornhuber.jobsearch.domain.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final AddressRepository addressRepository;
    private final JobMapper jobMapper;


    public JobService(
            JobRepository jobRepository,
            CompanyRepository companyRepository,
            AddressRepository addressRepository,
            JobMapper jobMapper
    ) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.addressRepository = addressRepository;
        this.jobMapper = jobMapper;
    }

    public List<JobResponseDTO> getAll(JobStatus status, Integer companyId) {
        return jobRepository.findAllWithCommunicationCount(status, companyId).stream()
                .map(p -> {
                    JobResponseDTO dto = new JobResponseDTO();
                    dto.id = p.getId();
                    dto.companyId = p.getCompanyId();
                    dto.companyName = p.getCompanyName();
                    dto.addressId = p.getAddressId();
                    dto.city = p.getCity();
                    dto.postcode = p.getPostcode();
                    dto.country = p.getCountry();
                    dto.street = p.getStreet();
                    dto.number = p.getNumber();
                    dto.headquarter = p.getHeadquarter();
                    dto.distance = p.getDistance();
                    dto.traveltime = p.getTraveltime();
                    dto.found = p.getFound();
                    dto.source = p.getSource();
                    dto.url = p.getUrl();
                    dto.text = p.getText();
                    dto.status = p.getStatus();
                    dto.mail = p.getMail();
                    dto.mailPerson = p.getMailPerson();
                    dto.tel = p.getTel();
                    dto.telPerson = p.getTelPerson();
                    dto.teilzeit = p.getTeilzeit();
                    dto.gleitzeit = p.getGleitzeit();
                    dto.homeoffice = p.getHomeoffice();
                    dto.features = p.getFeatures();
                    dto.communicationCount = p.getCommunicationCount();
                    return dto;
                })
                .toList();
    }

    public List<JobsForFilterResponseDTO> getAllForFilter(JobStatus status, Integer companyId) {
        return jobRepository.findAllWithCommunicationCount(status, companyId).stream()
                .map(p -> {
                    JobsForFilterResponseDTO dto = new JobsForFilterResponseDTO();
                    dto.id = p.getId();
                    dto.companyId = p.getCompanyId();
                    dto.companyName = p.getCompanyName();
                    dto.source = p.getSource();
                    dto.text = p.getText();
                    dto.status = p.getStatus();
                    return dto;
                })
                .toList();
    }

    public JobResponseDTO getById(Integer id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job not found: " + id));

        return jobMapper.toDto(job);
    }

    public JobResponseDTO create(CreateJobRequest req) {
        Job job = new Job();

        apply(job, req);

        Job saved = jobRepository.save(job);
        return jobMapper.toDto(saved);
    }

    public JobResponseDTO update(Integer id, UpdateJobRequest req) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job not found: " + id));

        apply(job, req);

        Job saved = jobRepository.save(job);
        return jobMapper.toDto(saved);
    }

    /**
     * Weist einem Job nachträglich eine Adresse zu.
     *
     * @param id ID des Jobs
     * @param req beinhaltet die addressId, die dem Job nachträglich zugewiesen wird
     * @return gespeicherte Job-Daten als DTO
     */
    public JobResponseDTO updateJobAddress(Integer id, UpdateJobAddressRequest req) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job not found: " + id));

        if (req.addressId != null) {
            // dem Job wird eine Adresse zugewiesen
            Address address = addressRepository.findById(req.addressId)
                    .orElseThrow(() -> new NotFoundException("Address not found: " + req.addressId));

            if (job.getCompany() != null) {
                if (address.getCompany() == null) {
                    throw new ConflictException("Address is not assigned to any company");
                }

                if (!address.getCompany().getId().equals(job.getCompany().getId())) {
                    throw new ConflictException("Address does not belong to the job's company");
                }
            }
            job.setAddress(address);
        } else {
            // dem Job soll künftig keine Adresse mehr zugewiesen sein
            job.setAddress(null);
        }

        Job saved = jobRepository.save(job);
        return jobMapper.toDto(saved);
    }

    public void delete(Integer id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job not found: " + id));

        jobRepository.delete(job);
    }

    private void apply(Job job, CreateJobRequest req) {
        Company company = companyRepository.findById(req.companyId)
                .orElseThrow(() -> new NotFoundException("Company not found: " + req.companyId));
        job.setCompany(company);

        job.setFound(req.found);
        job.setSource(req.source);
        job.setUrl(req.url);
        job.setText(req.text);
        job.setStatus(req.status);
        job.setMail(req.mail);
        job.setMailPerson(req.mailPerson);
        job.setTel(req.tel);
        job.setTelPerson(req.telPerson);
        job.setTeilzeit(req.teilzeit);
        job.setGleitzeit(req.gleitzeit);
        job.setHomeoffice(req.homeoffice);
        job.setFeatures(req.features);
    }

    /**
     * Apply changes from an UpdateJobRequest to a Job entity. (partielles Update!)
     */
    private void apply(Job job, UpdateJobRequest req) {
        job.setFound(req.found);
        job.setSource(req.source);
        job.setUrl(req.url);
        job.setText(req.text);
        job.setStatus(req.status);
        job.setMail(req.mail);
        job.setMailPerson(req.mailPerson);
        job.setTel(req.tel);
        job.setTelPerson(req.telPerson);
        job.setTeilzeit(req.teilzeit);
        job.setGleitzeit(req.gleitzeit);
        job.setHomeoffice(req.homeoffice);
        job.setFeatures(req.features);
    }
}