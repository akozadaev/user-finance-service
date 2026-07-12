package ru.akozadaev.user_finance_service.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.akozadaev.user_finance_service.user.repository.AccountRepository;

@Component
@ConditionalOnProperty(name = "finance.balance-accrual.enabled", havingValue = "true", matchIfMissing = true)
public class BalanceScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(BalanceScheduler.class);
	private final AccountRepository accountRepository;
	private final CacheManager cacheManager;

	public BalanceScheduler(AccountRepository accountRepository, CacheManager cacheManager) {
		this.accountRepository = accountRepository;
		this.cacheManager = cacheManager;
	}

	@Transactional
	@Scheduled(fixedDelayString = "${finance.balance-accrual.fixed-delay-ms:30000}")
	public void accrue() {
		int updated = accountRepository.accrueInterest();
		var cache = cacheManager.getCache("users");
		if (cache != null) cache.clear();
		if (updated > 0) LOGGER.info("Начислены проценты на {} счетов", updated);
	}
}
