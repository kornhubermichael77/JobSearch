package info.kornhuber.jobsearch.dto;

import java.util.List;

public class CompanyResponseDTO {
    public Integer id;
    public String name;
    public String mail;
    public String mailPerson;
    public String tel;
    public String telPerson;
    public String summary;
    public String url;
    public String urlJobs;

    public Long jobCount;
    public List<AddressResponseDTO> addresses;
}
