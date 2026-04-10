package info.kornhuber.jobsearch.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {

    @NotBlank(message = "email darf nicht leer sein")
    @Email(message = "Invalid email address")
    public String email;
}