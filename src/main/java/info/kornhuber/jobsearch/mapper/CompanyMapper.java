package info.kornhuber.jobsearch.mapper;

import info.kornhuber.jobsearch.dto.AddressResponseDTO;
import info.kornhuber.jobsearch.dto.CompanyResponseDTO;
import info.kornhuber.jobsearch.domain.entity.Address;
import info.kornhuber.jobsearch.domain.entity.Company;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CompanyMapper {

    private final AddressMapper addressMapper;

    public CompanyMapper(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    public CompanyResponseDTO toDto(Company company) {
        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.id = company.getId();
        dto.name = company.getName();
        dto.mail = company.getMail();
        dto.mailPerson = company.getMailPerson();
        dto.tel = company.getTel();
        dto.telPerson = company.getTelPerson();
        dto.summary = company.getSummary();
        dto.url = company.getUrl();
        dto.urlJobs = company.getUrlJobs();

        dto.addresses = new ArrayList<>();
        if (company.getAddresses() != null) {
            for (Address address : company.getAddresses()) {
                AddressResponseDTO addressDto = addressMapper.toDto(address);
                dto.addresses.add(addressDto);
            }
        }

        return dto;
    }

    public List<CompanyResponseDTO> toDtoList(List<Company> companies) {
        List<CompanyResponseDTO> result = new ArrayList<>();
        for (Company company : companies) {
            result.add(toDto(company));
        }
        return result;
    }
}
