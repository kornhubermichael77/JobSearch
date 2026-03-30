package info.kornhuber.jobsearch.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "password_reset_token")
public class PasswordResetTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_fk", nullable = false)
    private UserEntity user;

    @Setter
    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @Setter
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Setter
    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Setter
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}