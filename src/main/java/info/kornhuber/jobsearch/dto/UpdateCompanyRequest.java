package info.kornhuber.jobsearch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateCompanyRequest {

    @NotBlank(message = "name darf nicht leer sein")
    @Size(min = 1, max = 100, message = "name muss zwischen 1 und 100 Zeichen lang sein")
    public String name;

    @Size(max = 150, message = "mail darf maximal 150 Zeichen haben")
    public String mail;

    @Size(max = 100, message = "mailPerson darf maximal 100 Zeichen haben")
    public String mailPerson;

    @Size(max = 25, message = "tel darf maximal 25 Zeichen haben")
    public String tel;

    @Size(max = 100, message = "telPerson darf maximal 100 Zeichen haben")
    public String telPerson;

    @Size(max = 5000, message = "summary darf maximal 5000 Zeichen haben")
    public String summary;

    @Size(max = 500, message = "url darf maximal 500 Zeichen haben")
    public String url;

    @Size(max = 500, message = "urlJobs darf maximal 500 Zeichen haben")
    public String urlJobs;
}