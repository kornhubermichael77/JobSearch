package info.kornhuber.jobsearch.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import info.kornhuber.jobsearch.enums.JobStatus;

@JsonInclude(JsonInclude.Include.NON_NULL) // verhindern, dass null-Felder im JSON auftauchen
public class JobResponseDTO {

    public Integer id;

    public Integer companyId;
    public String companyName;

    public Integer addressId;

    public String street;
    public String number;
    public String postcode;
    public String city;
    public String country;
    public Boolean headquarter;
    public Double distance;
    public LocalDateTime traveltime;

    public LocalDateTime found;
    public String source;
    public String url;
    public String text;
    public JobStatus status;

    public String mail;
    public String mailPerson;
    public String tel;
    public String telPerson;

    public String teilzeit;
    public String gleitzeit;
    public String homeoffice;
    public String features;
    public Long communicationCount;

}