package info.kornhuber.jobsearch.service;

import info.kornhuber.jobsearch.dto.*;
import info.kornhuber.jobsearch.domain.entity.Address;
import info.kornhuber.jobsearch.domain.entity.Company;
import info.kornhuber.jobsearch.domain.entity.Job;
import info.kornhuber.jobsearch.enums.JobStatus;
import info.kornhuber.jobsearch.exception.BadRequestException;
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

    public List<JobResponseDTO> findAll(JobStatus status, Integer companyId) {
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

    public List<JobsForFilterResponseDTO> findAllForFilter(JobStatus status, Integer companyId) {
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

    public JobResponseDTO findById(Integer id) {
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

    public JobResponseDTO updateJobAddressId(Integer id, UpdateJobAddressRequest req) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job not found: " + id));

        if (req.addressId != null) {
            Address address = addressRepository.findById(req.addressId)
                    .orElseThrow(() -> new NotFoundException("Address not found: " + req.addressId));

            if (job.getCompany() != null) {
                if (address.getCompany() == null || !address.getCompany().getId().equals(job.getCompany().getId())) {
                    address.setCompany(job.getCompany());
                }
            }
            job.setAddress(address);
        } else {
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
        Company company = resolveCompany(req);          // Hilfsmethode, siehe unten
        Address address = resolveAddress(req, company); // Hilfsmethode, siehe unten

        job.setCompany(company);
        job.setAddress(address);
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
     * lädt die Company, falls companyId gesetzt ist
     * lädt die Address, falls addressId gesetzt ist
     * prüft bei gesetzten beiden IDs, ob die Adresse wirklich zu dieser Firma gehört
     * setzt erst danach die Werte am Job
     */
    private void apply(Job job, UpdateJobRequest req) {
        Company company = job.getCompany();
        Address address = job.getAddress();

        if (req.companyId != null) {
            company = companyRepository.findById(req.companyId)
                    .orElseThrow(() -> new NotFoundException("Company not found: " + req.companyId));
        }

        if (req.addressId != null) {
            address = addressRepository.findById(req.addressId)
                    .orElseThrow(() -> new NotFoundException("Address not found: " + req.addressId));
        }

        if (company != null && address != null) {
            if (address.getCompany() == null || !address.getCompany().getId().equals(company.getId())) {

                address.setCompany(company);
                addressRepository.save(address);
                //                throw new ConflictException("Address does not belong to the selectedd company");
            }
        }

        job.setCompany(company);
        job.setAddress(address);
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

    private Company resolveCompany(CreateJobRequest req) {
        if ((req.companyId != null && req.newCompany != null)
                || (req.companyId == null && req.newCompany == null)) {
            throw new BadRequestException("Either known company or new company must be set");
        }

        if (req.companyId != null) {
            return companyRepository.findById(req.companyId)
                    .orElseThrow(() -> new NotFoundException("Company not found: " + req.companyId));
        }

        Company company = new Company();
        company.setName(req.newCompany.name);
        company.setMail(req.newCompany.mail);
        company.setMailPerson(req.newCompany.mailPerson);
        company.setTel(req.newCompany.tel);
        company.setTelPerson(req.newCompany.telPerson);
        company.setSummary(req.newCompany.summary);
        company.setUrl(req.newCompany.url);
        company.setUrlJobs(req.newCompany.urlJobs);

        return companyRepository.save(company);
    }

    private Address resolveAddress(CreateJobRequest req, Company company) {
        if (req.addressId != null && req.newAddress != null) {
            throw new BadRequestException("Only one of addressId or newAddress may be set");
        }
        // Todo: req.newAddress wird ev nie der Fall sein, weil Jobs nie gleichzeitig eine Adresse mitanlegen! Sauber entfernen!
        if (req.addressId != null) {
            Address address = addressRepository.findById(req.addressId)
                    .orElseThrow(() -> new NotFoundException("Address not found: " + req.addressId));

            if (!address.getCompany().getId().equals(company.getId())) {
                throw new ConflictException("Address does not belong to the selected company");
            } else if (address.getCompany() == null) {
                address.setCompany(company);
                address = addressRepository.save(address);
            }

            return address;
        }

        if (req.newAddress != null) {
            Address address = new Address();
            address.setStreet(req.newAddress.street);
            address.setPostcode(req.newAddress.postcode);
            address.setCity(req.newAddress.city);
            address.setCountry(req.newAddress.country);
            address.setNumber(req.newAddress.number);
            address.setHeadquarter(req.newAddress.headquarter);
            address.setDistance(req.newAddress.distance);
            address.setTraveltime(req.newAddress.traveltime);
            address.setCompany(company);

            return addressRepository.save(address);
        }

        return null;
    }
}