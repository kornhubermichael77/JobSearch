package info.kornhuber.jobsearch.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "username darf nicht leer sein")
    public String username;

    @NotBlank(message = "password darf nicht leer sein")
    public String password;

    public boolean rememberMe;
}