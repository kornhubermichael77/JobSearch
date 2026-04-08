package info.kornhuber.jobsearch.service;

import info.kornhuber.jobsearch.domain.entity.Job;
import info.kornhuber.jobsearch.domain.repository.JobRepository;
import info.kornhuber.jobsearch.dto.AddressResponseDTO;
import info.kornhuber.jobsearch.dto.CreateAddressRequest;
import info.kornhuber.jobsearch.dto.UpdateAddressRequest;
import info.kornhuber.jobsearch.domain.entity.Address;
import info.kornhuber.jobsearch.domain.entity.Company;
import info.kornhuber.jobsearch.exception.NotFoundException;
import info.kornhuber.jobsearch.mapper.AddressMapper;
import info.kornhuber.jobsearch.domain.repository.AddressRepository;
import info.kornhuber.jobsearch.domain.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final CompanyRepository companyRepository;
    private final AddressMapper addressMapper;
    private final JobRepository jobRepository;

    public AddressService(
            AddressRepository addressRepository,
            CompanyRepository companyRepository,
            AddressMapper addressMapper, JobRepository jobRepository
    ) {
        this.addressRepository = addressRepository;
        this.companyRepository = companyRepository;
        this.addressMapper = addressMapper;
        this.jobRepository = jobRepository;
    }

    public AddressResponseDTO createForCompany(Integer companyId, CreateAddressRequest req) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Company not found: " + companyId));

        Address address = new Address();
        applyFields(address, req);
        address.setCompany(company);

        Address saved = addressRepository.save(address);
        return addressMapper.toDto(saved);
    }

    public AddressResponseDTO createForJob(CreateAddressRequest req) {

        Address address = new Address();
        Job job = jobRepository.findById(req.jobId)
                .orElseThrow(() -> new NotFoundException("Job not found: " + req.jobId));
        address.setCompany(job.getCompany());
        applyFields(address, req);

        Address saved = addressRepository.save(address);
        return addressMapper.toDto(saved);
    }

    public AddressResponseDTO findById(Integer id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Address not found: " + id));

        return addressMapper.toDto(address);
    }

    public AddressResponseDTO update(Integer id, UpdateAddressRequest req) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Address not found: " + id));

        applyFields(address, req);

        Address saved = addressRepository.save(address);
        return addressMapper.toDto(saved);
    }

    public void delete(Integer id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Address not found: " + id));

        addressRepository.delete(address);
    }

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