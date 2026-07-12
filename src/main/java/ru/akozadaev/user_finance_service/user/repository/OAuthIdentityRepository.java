package ru.akozadaev.user_finance_service.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.akozadaev.user_finance_service.user.model.OAuthIdentityEntity;

public interface OAuthIdentityRepository extends JpaRepository<OAuthIdentityEntity, Long> {
	Optional<OAuthIdentityEntity> findBySubject(String subject);
}
