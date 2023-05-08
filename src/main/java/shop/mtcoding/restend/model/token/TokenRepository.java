package shop.mtcoding.restend.model.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUuidAndStatus(String uuid, TokenStatus status);

    Optional<RefreshToken> findByUuid(String uuid);

}