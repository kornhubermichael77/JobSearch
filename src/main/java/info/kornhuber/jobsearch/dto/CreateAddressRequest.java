package info.kornhuber.jobsearch.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Request-DTO zum Erstellen einer Adresse.
 * Wichtig:
 * Dieses DTO enthält bewusst KEIN jobId-Feld.
 * Der Erstellungskontext wird über den Endpunkt bestimmt:
 * - POST /api/companies/{companyId}/addresses
 * - POST /api/users/me/addresses
 * Dadurch ist die API klar und der Request fachlich sauber.
 */
public class CreateAddressRequest {

    @Size(max = 100, message = "street darf maximal 100 Zeichen haben")
    public String street;

    @Size(max = 10, message = "postcode darf maximal 10 Zeichen haben")
    public String postcode;

    @Size(max = 60, message = "city darf maximal 60 Zeichen haben")
    public String city;

    @Size(max = 50, message = "country darf maximal 50 Zeichen haben")
    public String country;

    @Size(max = 30, message = "number darf maximal 30 Zeichen haben")
    public String number;

    public Boolean headquarter;

    public Double distance;

    public LocalDateTime traveltime;
}
