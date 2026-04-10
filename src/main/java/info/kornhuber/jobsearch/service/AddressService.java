package info.kornhuber.jobsearch.service;

import info.kornhuber.jobsearch.auth.entity.UserEntity;
import info.kornhuber.jobsearch.auth.service.CurrentUserService;
import info.kornhuber.jobsearch.domain.entity.Address;
import info.kornhuber.jobsearch.domain.entity.Company;
import info.kornhuber.jobsearch.domain.repository.AddressRepository;
import info.kornhuber.jobsearch.domain.repository.CompanyRepository;
import info.kornhuber.jobsearch.dto.AddressResponseDTO;
import info.kornhuber.jobsearch.dto.CreateAddressRequest;
import info.kornhuber.jobsearch.dto.UpdateAddressRequest;
import info.kornhuber.jobsearch.exception.NotFoundException;
import info.kornhuber.jobsearch.mapper.AddressMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service für CRUD-Operationen rund um Adressen.
 *
 * Wichtige API-Entscheidung:
 * - Adressen werden nicht mehr "für Job" erstellt
 * - eine Adresse wird explizit entweder
 *   - für eine Company oder
 *   - für den aktuell eingeloggten User
 *   erstellt
 *
 * Das macht die API klarer und reduziert implizite Logik im Request-Body.
 */
@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final CompanyRepository companyRepository;
    private final AddressMapper addressMapper;
    private final CurrentUserService currentUserService;

    public AddressService(
            AddressRepository addressRepository,
            CompanyRepository companyRepository,
            AddressMapper addressMapper,
            CurrentUserService currentUserService
    ) {
        this.addressRepository = addressRepository;
        this.companyRepository = companyRepository;
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
        UserEntity currentUser = currentUserService.requireCurrentUser();

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Company not found: " + companyId));

        Address address = new Address();
        applyFields(address, req);

        address.setCompany(company);
        address.setOwnerUserId(currentUser.getId());

        Address saved = addressRepository.save(address);
        return addressMapper.toDto(saved);
    }

    /**
     * Erstellt eine Adresse für den aktuell eingeloggten User.
     *
     * Diese Adresse ist nicht an eine Company gebunden.
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