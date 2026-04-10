package info.kornhuber.jobsearch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "username darf nicht leer sein")
    @Size(min = 1, max = 255, message = "username muss zwischen 1 und 255 Zeichen lang sein")
    public String username;

    @NotBlank(message = "password darf nicht leer sein")
    @Size(min = 8, max = 100, message = "password muss zwischen 8 und 100 Zeichen lang sein")
    public String password;

    public boolean rememberMe;
}