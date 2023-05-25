package school.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.server.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByUsername(String username);
}
