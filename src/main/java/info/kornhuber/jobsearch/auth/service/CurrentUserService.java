package info.kornhuber.jobsearch.auth.service;

import info.kornhuber.jobsearch.auth.entity.UserEntity;
import info.kornhuber.jobsearch.auth.repository.UserRepository;
import info.kornhuber.jobsearch.exception.NotFoundException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Liefert den aktuell authentifizierten User aus dem Spring-Security-Kontext.
 *
 * Warum eine eigene Klasse?
 * - vermeidet wiederholten Zugriff auf SecurityContextHolder in mehreren Services
 * - macht den Code testbarer und klarer
 * - bündelt die Logik "wer ist der aktuelle User?" an einer Stelle
 */
@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Liefert den aktuell eingeloggten Benutzer.
     *
     * Ablauf:
     * 1. Authentication aus dem SecurityContext lesen
     * 2. prüfen, ob überhaupt ein echter eingeloggter User existiert
     * 3. Benutzername auslesen
     * 4. UserEntity aus der Auth-DB laden
     *
     * @return aktuell authentifizierter User
     */
    public UserEntity requireCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("No authenticated user available");
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Current user not found: " + username));
    }
}