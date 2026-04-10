package info.kornhuber.jobsearch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class UpdateAddressRequest {

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