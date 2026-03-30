package info.kornhuber.jobsearch.auth.repository;

import info.kornhuber.jobsearch.auth.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {

    Optional<PasswordResetTokenEntity> findByTokenHashAndUsedAtIsNull(String tokenHash);

    void deleteByUser_Id(Long userId);

    long countByUser_IdAndUsedAtIsNullAndExpiresAtAfter(Long userId, LocalDateTime now);

    @Query("""
    select t
    from PasswordResetTokenEntity t
    join fetch t.user
    where t.tokenHash = :tokenHash
      and t.usedAt is null
""")
    Optional<PasswordResetTokenEntity> findActiveByTokenHashWithUser(@Param("tokenHash") String tokenHash);
}