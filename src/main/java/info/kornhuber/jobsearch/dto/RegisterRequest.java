package info.kornhuber.jobsearch.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "username darf nicht leer sein")
    @Size(min = 1, max = 255, message = "username muss zwischen 1 und 255 Zeichen lang sein")
    public String username;

    @NotBlank(message = "password darf nicht leer sein")
    @Size(min = 6, message = "password muss mindestens 6 Zeichen lang sein")
    public String password;

    @NotBlank(message = "email darf nicht leer sein")
    @Email(message = "email muss gültig sein")
    @Size(max = 255, message = "email darf maximal 255 Zeichen haben")
    public String email;

}