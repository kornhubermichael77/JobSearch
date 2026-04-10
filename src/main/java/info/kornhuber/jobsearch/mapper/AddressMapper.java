package info.kornhuber.jobsearch.mapper;

import info.kornhuber.jobsearch.domain.entity.Address;
import info.kornhuber.jobsearch.dto.AddressResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Wandelt Address-Entities in Response-DTOs um.
 */
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

        // Technischer Besitzer der Adresse (aktueller User / Owner)
        dto.ownerUserId = address.getOwnerUserId();

        // Optionale Company-Zuordnung
        if (address.getCompany() != null) {
            dto.companyId = address.getCompany().getId();
        }

        return dto;
    }
}