package school.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.server.domain.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByKey(String key);
}
