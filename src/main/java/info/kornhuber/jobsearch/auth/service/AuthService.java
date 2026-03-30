package info.kornhuber.jobsearch.auth.service;

import info.kornhuber.jobsearch.auth.entity.PasswordResetTokenEntity;
import info.kornhuber.jobsearch.auth.repository.PasswordResetTokenRepository;
import info.kornhuber.jobsearch.dto.ForgotPasswordRequest;
import info.kornhuber.jobsearch.dto.RegisterRequest;
import info.kornhuber.jobsearch.dto.ResetPasswordRequest;
import info.kornhuber.jobsearch.dto.UserResponseDTO;
import info.kornhuber.jobsearch.auth.entity.Role;
import info.kornhuber.jobsearch.auth.entity.UserEntity;
import info.kornhuber.jobsearch.exception.BadRequestException;
import info.kornhuber.jobsearch.multitenancy.TenantDatabaseService;
import info.kornhuber.jobsearch.auth.repository.RoleRepository;
import info.kornhuber.jobsearch.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import info.kornhuber.jobsearch.exception.ConflictException;
import info.kornhuber.jobsearch.exception.NotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TenantDatabaseService tenantDatabaseService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final TokenHashService tokenHashService;
    private final PasswordResetMailService passwordResetMailService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            TenantDatabaseService tenantDatabaseService,
            PasswordResetTokenRepository passwordResetTokenRepository,
            TokenHashService tokenHashService,
            PasswordResetMailService passwordResetMailService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tenantDatabaseService = tenantDatabaseService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.tokenHashService = tokenHashService;
        this.passwordResetMailService = passwordResetMailService;
    }

    public UserResponseDTO register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.username)) {
            throw new ConflictException("Username existiert bereits");
        }

        if (userRepository.existsByEmail(req.email)) {
            throw new ConflictException("Email existiert bereits");
        }

        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new NotFoundException("ROLE_USER fehlt in der Datenbank"));

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
                .orElseThrow(() -> new NotFoundException("User nicht gefunden: " + username));

        return toDto(user);
    }

    private UserResponseDTO toDto(UserEntity user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.id = user.getId();
        dto.username = user.getUsername();
        dto.email = user.getEmail();
        dto.enabled = user.getEnabled();
        dto.roles = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());
        return dto;
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest req) {
        userRepository.findByEmail(req.email).ifPresent(user -> {
            passwordResetTokenRepository.deleteByUser_Id(user.getId());

            String rawToken = UUID.randomUUID().toString().replace("-", "")
                    + UUID.randomUUID().toString().replace("-", "");

            String tokenHash = tokenHashService.sha256(rawToken);

            PasswordResetTokenEntity entity = new PasswordResetTokenEntity();
            entity.setUser(user);
            entity.setTokenHash(tokenHash);
            entity.setCreatedAt(java.time.LocalDateTime.now());
            entity.setExpiresAt(java.time.LocalDateTime.now().plusMinutes(30));

            passwordResetTokenRepository.save(entity);
            passwordResetMailService.sendResetMail(user.getEmail(), rawToken);
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest req) {
        String tokenHash = tokenHashService.sha256(req.token);

        PasswordResetTokenEntity tokenEntity = passwordResetTokenRepository
                .findActiveByTokenHashWithUser(tokenHash)
                .orElseThrow(() -> new BadRequestException("Ungültiger oder abgelaufener Token"));

        if (tokenEntity.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new BadRequestException("Ungültiger oder abgelaufener Token");
        }

        UserEntity user = tokenEntity.getUser();
        user.setPasswordHash(passwordEncoder.encode(req.newPassword));
        userRepository.save(user);

        tokenEntity.setUsedAt(java.time.LocalDateTime.now());
        passwordResetTokenRepository.save(tokenEntity);
    }
}