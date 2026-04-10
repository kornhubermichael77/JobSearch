package info.kornhuber.jobsearch.controller;

import info.kornhuber.jobsearch.auth.service.AuthService;
import info.kornhuber.jobsearch.dto.ForgotPasswordRequest;
import info.kornhuber.jobsearch.dto.LoginRequest;
import info.kornhuber.jobsearch.dto.RegisterRequest;
import info.kornhuber.jobsearch.dto.ResetPasswordRequest;
import info.kornhuber.jobsearch.dto.UserResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final RememberMeServices rememberMeServices;

    public AuthController(AuthService service,
                          AuthenticationManager authenticationManager,
                          SecurityContextRepository securityContextRepository,
                          RememberMeServices rememberMeServices) {
        this.service = service;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.rememberMeServices = rememberMeServices;
    }

    /**
     * Registriert einen neuen Benutzer.
     *
     * HTTP 201 signalisiert die erfolgreiche Erstellung der User-Ressource.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(@Valid @RequestBody RegisterRequest req) {
        return service.register(req);
    }

    /**
     * Authentifiziert einen Benutzer und erstellt eine Session.
     *
     * HTTP 200 ist hier passend, da keine neue API-Ressource erstellt,
     * sondern ein Login-Vorgang durchgeführt wird.
     */
    @PostMapping("/login")
    public UserResponseDTO login(@Valid @RequestBody LoginRequest req,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(req.username, req.password);

        Authentication authentication = authenticationManager.authenticate(authRequest);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        request.getSession(true);
        securityContextRepository.saveContext(context, request, response);

        if (req.rememberMe) {
            rememberMeServices.loginSuccess(request, response, authentication);
        }

        return service.loadCurrentUser(authentication.getName());
    }

    @GetMapping("/me")
    public UserResponseDTO me(Authentication authentication) {
        return service.loadCurrentUser(authentication.getName());
    }

    /**
     * Startet den Forgot-Password-Prozess.
     *
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
     *
     * HTTP 204 ist passend, weil die Aktion erfolgreich war
     * und kein Response-Body zurückgegeben werden muss.
     */
    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        service.resetPassword(req);
    }
}