package info.kornhuber.jobsearch.dto;

import java.time.LocalDateTime;

/**
 * Response-DTO für Adressen.
 *
 * Enthält sowohl die optionale companyId als auch die optionale ownerUserId,
 * damit der Client erkennen kann, in welchem Kontext die Adresse verwendet wird.
 */
public class AddressResponseDTO {
    public Integer id;
    public String street;
    public String number;
    public String postcode;
    public String city;
    public String country;

    public Boolean headquarter;
    public Double distance;
    public LocalDateTime traveltime;

    public Integer companyId;
    public Long ownerUserId;
}