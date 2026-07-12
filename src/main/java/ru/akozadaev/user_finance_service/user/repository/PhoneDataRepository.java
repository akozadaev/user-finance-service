package ru.akozadaev.user_finance_service.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.akozadaev.user_finance_service.user.model.PhoneDataEntity;

public interface PhoneDataRepository extends JpaRepository<PhoneDataEntity, Long> {
	Optional<PhoneDataEntity> findByPhone(String phone);
	boolean existsByPhone(String phone);
	long countByUserId(Long userId);
}
