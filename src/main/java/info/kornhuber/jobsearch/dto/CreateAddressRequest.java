package info.kornhuber.jobsearch.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CreateAddressRequest {

    public Integer jobId;

    @Size(max = 100)
    public String street;

    @Size(max = 10)
    public String postcode;

    @Size(max = 60)
    public String city;

    @Size(max = 50)
    public String country;

    @Size(max = 30)
    public String number;

    public Boolean headquarter;

    public Double distance;

    public LocalDateTime traveltime;
}
