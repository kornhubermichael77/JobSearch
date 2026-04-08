package info.kornhuber.jobsearch.dto;

import info.kornhuber.jobsearch.enums.CommunicationStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CreateJobRequest {

    // entweder bestehende Firma
    public Integer companyId;
    // oder neue Firma
    public CreateCompanyRequest newCompany;

    // entweder bestehende Adresse
    public Integer addressId;
    // oder neue Adresse
    public CreateAddressRequest newAddress;

    // restliche Jobfelder ...
    public LocalDateTime found;

    @Size(max = 150, message = "source darf maximal 150 Zeichen haben")
    public String source;

    @Size(max = 500, message = "url darf maximal 500 Zeichen haben")
    public String url;

    @Size(max = 10000, message = "text darf maximal 10000 Zeichen haben")
    public String text;

    @NotNull(message = "status darf nicht null sein")
    public CommunicationStatus status;

    //@Email(message = "mail muss eine gültige E-Mail-Adresse sein")
    @Size(max = 150, message = "mail darf maximal 150 Zeichen haben")
    public String mail;

    @Size(max = 100, message = "mailPerson darf maximal 100 Zeichen haben")
    public String mailPerson;

    @Size(max = 25, message = "tel darf maximal 25 Zeichen haben")
    public String tel;

    @Size(max = 100, message = "telPerson darf maximal 100 Zeichen haben")
    public String telPerson;

    @Size(max = 100, message = "teilzeit darf maximal 100 Zeichen haben")
    public String teilzeit;

    @Size(max = 100, message = "gleitzeit darf maximal 100 Zeichen haben")
    public String gleitzeit;

    @Size(max = 100, message = "homeoffice darf maximal 100 Zeichen haben")
    public String homeoffice;

    @Size(max = 2500, message = "features darf maximal 2500 Zeichen haben")
    public String features;
}