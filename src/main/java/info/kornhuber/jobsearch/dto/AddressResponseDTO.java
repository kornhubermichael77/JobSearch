package info.kornhuber.jobsearch.dto;

import com.fasterxml.jackson.annotation.JsonInclude;


import java.time.LocalDateTime;
import java.util.List;

/**
 * Response-DTO für Adressen.
 *
 * Enthält sowohl die optionale companyId als auch die optionale ownerUserId,
 * damit der Client erkennen kann, in welchem Kontext die Adresse verwendet wird.
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // verhindern, dass null-Felder im JSON auftauchen
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

    /**
     * Optionale Hinweistexte zur zuletzt ausgeführten Operation.
     *
     * Beispiel:
     * - "Die Adresse wurde automatisch auch der Firma des Jobs zugeordnet."
     */
     public List<String> messages;
}