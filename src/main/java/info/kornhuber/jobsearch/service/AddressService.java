package info.kornhuber.jobsearch.service;

import info.kornhuber.jobsearch.auth.entity.UserEntity;
import info.kornhuber.jobsearch.auth.service.CurrentUserService;
import info.kornhuber.jobsearch.domain.entity.Address;
import info.kornhuber.jobsearch.domain.entity.Company;
import info.kornhuber.jobsearch.domain.entity.Job;
import info.kornhuber.jobsearch.domain.repository.AddressRepository;
import info.kornhuber.jobsearch.domain.repository.CompanyRepository;
import info.kornhuber.jobsearch.domain.repository.JobRepository;
import info.kornhuber.jobsearch.dto.AddressResponseDTO;
import info.kornhuber.jobsearch.dto.CreateAddressRequest;
import info.kornhuber.jobsearch.dto.UpdateAddressRequest;
import info.kornhuber.jobsearch.exception.BadRequestException;
import info.kornhuber.jobsearch.exception.NotFoundException;
import info.kornhuber.jobsearch.mapper.AddressMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service für CRUD-Operationen rund um Adressen.
 *
 * - Adressen werden explizit entweder
 *   - für eine Company oder
 *   - für einen Job (und dessen Company) oder
 *   - für den aktuell eingeloggten User
 *   erstellt
 *
 */
@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final AddressMapper addressMapper;
    private final CurrentUserService currentUserService;

    public AddressService(
            AddressRepository addressRepository,
            CompanyRepository companyRepository, JobRepository jobRepository,
            AddressMapper addressMapper,
            CurrentUserService currentUserService
    ) {
        this.addressRepository = addressRepository;
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.addressMapper = addressMapper;
        this.currentUserService = currentUserService;
    }

    /**
     * Erstellt eine Adresse für eine konkrete Company.
     *
     * @param companyId ID der Company
     * @param req       Request-Daten der Adresse
     * @return gespeicherte Adresse als DTO
     */
    public AddressResponseDTO createForCompany(Integer companyId, CreateAddressRequest req) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Company not found: " + companyId));

        Address address = new Address();
        applyFields(address, req);

        address.setCompany(company);

        Address saved = addressRepository.save(address);
        return addressMapper.toDto(saved);
    }

    /**
     * Erstellt eine Adresse für einen konkreten Job.
     *
     * @param jobId ID des Jobs
     * @param req   Request-Daten der Adresse
     * @return gespeicherte Adresse als DTO
     */
    public AddressResponseDTO createForJob(Integer jobId, CreateAddressRequest req) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found: " + jobId));
        if (job.getCompany() == null) {
            throw new BadRequestException("Job has no company assigned");
        }
        Company company = job.getCompany();

        Address address = new Address();
        applyFields(address, req);

        address.setCompany(company);
        Address saved = addressRepository.save(address);
        AddressResponseDTO dto = addressMapper.toDto(saved);
        // Meldung, dass diese Job-Adresse nun auch automatisch bei der Firma des Jobs eingetragen wurde (=Service-Feature).
        dto.messages = List.of("Die Adresse wurde automatisch auch der Firma des Jobs zugeordnet.");
        return dto;
    }

    /**
     * Erstellt eine Adresse für den aktuell eingeloggten User.
     *
     * Diese Adresse ist nicht an eine Company oder einen Job gebunden und dient der Berechnung des Arbeitsweges
     */
    public AddressResponseDTO createForCurrentUser(CreateAddressRequest req) {
        UserEntity currentUser = currentUserService.requireCurrentUser();

        Address address = new Address();
        applyFields(address, req);

        address.setOwnerUserId(currentUser.getId());

        Address saved = addressRepository.save(address);
        return addressMapper.toDto(saved);
    }

    /**
     * Liefert alle Adressen des aktuell eingeloggten Users.
     */
    public List<AddressResponseDTO> getCurrentUserAddresses() {
        UserEntity currentUser = currentUserService.requireCurrentUser();

        return addressRepository.findByOwnerUserIdOrderByIdDesc(currentUser.getId()).stream()
                .map(addressMapper::toDto)
                .toList();
    }

    /**
     * Liefert eine Adresse anhand ihrer ID.
     */
    public AddressResponseDTO getById(Integer id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Address not found: " + id));

        return addressMapper.toDto(address);
    }

    /**
     * Aktualisiert eine bestehende Adresse.
     */
    public AddressResponseDTO update(Integer id, UpdateAddressRequest req) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Address not found: " + id));

        applyFields(address, req);

        Address saved = addressRepository.save(address);
        return addressMapper.toDto(saved);
    }

    /**
     * Löscht eine Adresse anhand ihrer ID.
     */
    public void delete(Integer id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Address not found: " + id));

        addressRepository.delete(address);
    }

    /**
     * Überträgt Felder aus dem Create-Request in die Entity.
     */
    private void applyFields(Address address, CreateAddressRequest req) {
        address.setStreet(req.street);
        address.setPostcode(req.postcode);
        address.setCity(req.city);
        address.setCountry(req.country);
        address.setNumber(req.number);
        address.setHeadquarter(req.headquarter);
        address.setDistance(req.distance);
        address.setTraveltime(req.traveltime);
    }

    /**
     * Überträgt Felder aus dem Update-Request in die Entity.
     */
    private void applyFields(Address address, UpdateAddressRequest req) {
        address.setStreet(req.street);
        address.setPostcode(req.postcode);
        address.setCity(req.city);
        address.setCountry(req.country);
        address.setNumber(req.number);
        address.setHeadquarter(req.headquarter);
        address.setDistance(req.distance);
        address.setTraveltime(req.traveltime);
    }
}