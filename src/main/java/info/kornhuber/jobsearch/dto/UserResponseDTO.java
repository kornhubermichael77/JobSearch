package info.kornhuber.jobsearch.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDTO {

    public Long id;
    public String username;
    public String email;
    public Boolean enabled;
    public Set<String> roles;
}