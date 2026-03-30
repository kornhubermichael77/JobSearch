package info.kornhuber.jobsearch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank
    public String token;

    @NotBlank
    @Size(min = 8, max = 100)
    public String newPassword;
}
