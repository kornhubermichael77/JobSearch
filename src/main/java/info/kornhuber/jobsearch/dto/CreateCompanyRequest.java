package info.kornhuber.jobsearch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCompanyRequest {

    @NotBlank(message = "name darf nicht leer sein")
    @Size(max = 100)
    public String name;

    @Size(max = 150)
    public String mail;

    @Size(max = 100)
    public String mailPerson;

    @Size(max = 25)
    public String tel;

    @Size(max = 100)
    public String telPerson;

    public String summary;

    @Size(max = 500)
    public String url;

    @Size(max = 500)
    public String urlJobs;
}
