package info.kornhuber.jobsearch.auth.service;

import info.kornhuber.jobsearch.dto.RegisterRequest;
import info.kornhuber.jobsearch.dto.UserResponseDTO;
import info.kornhuber.jobsearch.auth.entity.Role;
import info.kornhuber.jobsearch.auth.entity.UserEntity;
import info.kornhuber.jobsearch.multitenancy.TenantDatabaseService;
import info.kornhuber.jobsearch.auth.repository.RoleRepository;
import info.kornhuber.jobsearch.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TenantDatabaseService tenantDatabaseService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            TenantDatabaseService tenantDatabaseService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tenantDatabaseService = tenantDatabaseService;
    }

    public UserResponseDTO register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.username)) {
            throw new RuntimeException("Username existiert bereits");
        }

        if (userRepository.existsByEmail(req.email)) {
            throw new RuntimeException("Email existiert bereits");
        }

        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER fehlt in der Datenbank"));

        String tenantDbName = "tenant_" + UUID.randomUUID().toString().replace("-", "");
        try {
            tenantDatabaseService.createTenantDatabase(tenantDbName);

            UserEntity user = new UserEntity();
            user.setUsername(req.username);
            user.setPasswordHash(passwordEncoder.encode(req.password));
            user.setEmail(req.email);
            user.setEnabled(true);
            user.setTenantDbName(tenantDbName);
            user.getRoles().add(userRole);

            UserEntity saved = userRepository.save(user);
            return toDto(saved);

        } catch (Exception e) {
            try {
                tenantDatabaseService.dropTenantDatabase(tenantDbName);
            } catch (Exception ignored) {
            }
            throw e;
        }
    }

    public UserResponseDTO loadCurrentUser(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User nicht gefunden: " + username));

        return toDto(user);
    }

    private UserResponseDTO toDto(UserEntity user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.id = user.getId();
        dto.username = user.getUsername();
        dto.email = user.getEmail();
        dto.enabled = user.getEnabled();
        dto.tenantDbName = user.getTenantDbName();
        dto.roles = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());
        return dto;
    }
}