package school.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import school.server.domain.RefreshToken;

import java.util.Optional;
@Transactional
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByKey(String key);
    void removeByValue(String value);
}
