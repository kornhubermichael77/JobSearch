package info.kornhuber.jobsearch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank(message = "token darf nicht leer sein")
    public String token;

    @NotBlank(message = "newPassword darf nicht leer sein")
    @Size(min = 8, max = 100, message = "newPassword muss zwischen 8 und 100 Zeichen lang sein" )
    public String newPassword;
}
