package info.kornhuber.jobsearch.controller;

import info.kornhuber.jobsearch.dto.LoginRequest;
import info.kornhuber.jobsearch.dto.RegisterRequest;
import info.kornhuber.jobsearch.dto.UserResponseDTO;
import info.kornhuber.jobsearch.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService service, AuthenticationManager authenticationManager) {
        this.service = service;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public UserResponseDTO register(@Valid @RequestBody RegisterRequest req) {
        return service.register(req);
    }

    @PostMapping("/login")
    public UserResponseDTO login(@Valid @RequestBody LoginRequest req,
                                 HttpServletRequest request) {

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(req.username, req.password);

        Authentication authentication = authenticationManager.authenticate(authRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.getSession(true); // Session erzeugen

        return service.loadCurrentUser(authentication.getName());
    }

    @GetMapping("/me")
    public UserResponseDTO me(Authentication authentication) {
        return service.loadCurrentUser(authentication.getName());
    }
}