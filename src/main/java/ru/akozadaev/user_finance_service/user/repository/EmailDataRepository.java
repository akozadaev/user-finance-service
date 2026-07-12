package ru.akozadaev.user_finance_service.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.akozadaev.user_finance_service.user.model.EmailDataEntity;

public interface EmailDataRepository extends JpaRepository<EmailDataEntity, Long> {
	Optional<EmailDataEntity> findByEmailIgnoreCase(String email);
	boolean existsByEmailIgnoreCase(String email);
	long countByUserId(Long userId);
}
