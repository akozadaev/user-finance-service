package ru.akozadaev.user_finance_service.user.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.akozadaev.user_finance_service.user.model.AccountEntity;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
	Optional<AccountEntity> findByUserId(Long userId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select a from AccountEntity a where a.user.id in :userIds order by a.user.id")
	List<AccountEntity> findAllByUserIdsForUpdate(@Param("userIds") List<Long> userIds);

	@Modifying
	@Query(value = "update account set balance = least(round(balance * 1.10, 2), round(initial_balance * 2.07, 2)), version = version + 1 where balance < round(initial_balance * 2.07, 2)", nativeQuery = true)
	int accrueInterest();
}
