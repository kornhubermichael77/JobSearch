package info.kornhuber.jobsearch.mapper;

import info.kornhuber.jobsearch.dto.AddressResponseDTO;
import info.kornhuber.jobsearch.domain.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressResponseDTO toDto(Address address) {
        AddressResponseDTO dto = new AddressResponseDTO();
        dto.id = address.getId();
        dto.street = address.getStreet();
        dto.postcode = address.getPostcode();
        dto.city = address.getCity();
        dto.country = address.getCountry();
        dto.number = address.getNumber();
        dto.headquarter = address.getHeadquarter();
        dto.distance = address.getDistance();
        dto.traveltime = address.getTraveltime();

        if (address.getCompany() != null) {
            dto.companyId = address.getCompany().getId();
        }

        return dto;
    }
}