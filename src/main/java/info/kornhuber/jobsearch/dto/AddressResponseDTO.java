package info.kornhuber.jobsearch.dto;

import java.time.LocalDateTime;

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
}