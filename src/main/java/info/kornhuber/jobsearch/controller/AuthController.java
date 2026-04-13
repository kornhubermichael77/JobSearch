package info.kornhuber.jobsearch.controller;

import info.kornhuber.jobsearch.auth.service.AuthService;
import info.kornhuber.jobsearch.dto.ForgotPasswordRequest;
import info.kornhuber.jobsearch.dto.RegisterRequest;
import info.kornhuber.jobsearch.dto.ResetPasswordRequest;
import info.kornhuber.jobsearch.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.web.csrf.CsrfToken;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    /**
     * Registriert einen neuen Benutzer.
     * HTTP 201 signalisiert die erfolgreiche Erstellung der User-Ressource.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(@Valid @RequestBody RegisterRequest req) {
        return service.register(req);
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

    @GetMapping("/me")
    public UserResponseDTO me(Authentication authentication) {
        return service.loadCurrentUser(authentication.getName());
    }

    /**
     * Startet den Forgot-Password-Prozess.
     * HTTP 204 ist passend, weil der Request erfolgreich verarbeitet wurde,
     * aber kein Response-Body benötigt wird.
     */
    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        service.forgotPassword(req);
    }

    /**
     * Setzt das Passwort anhand eines Reset-Tokens zurück.
     * HTTP 204 ist passend, weil die Aktion erfolgreich war
     * und kein Response-Body zurückgegeben werden muss.
     */
    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        service.resetPassword(req);
    }
}