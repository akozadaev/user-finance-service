package ru.akozadaev.user_finance_service.user.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akozadaev.user_finance_service.common.exception.ResourceNotFoundException;
import ru.akozadaev.user_finance_service.user.dto.TransferRequest;
import ru.akozadaev.user_finance_service.user.dto.TransferResponse;
import ru.akozadaev.user_finance_service.user.model.AccountEntity;
import ru.akozadaev.user_finance_service.user.repository.AccountRepository;

@Service
public class TransferService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);
	private final AccountRepository accountRepository;

	public TransferService(AccountRepository accountRepository) { this.accountRepository = accountRepository; }

	@CacheEvict(value = "users", allEntries = true)
	@Transactional
	public TransferResponse transfer(Long fromUserId, TransferRequest request) {
		if (fromUserId.equals(request.toUserId())) throw new IllegalArgumentException("Нельзя перевести деньги самому себе");
		List<Long> userIds = List.of(fromUserId, request.toUserId()).stream().sorted().toList();
		Map<Long, AccountEntity> accounts = accountRepository.findAllByUserIdsForUpdate(userIds).stream()
				.collect(Collectors.toMap(account -> account.getUser().getId(), Function.identity()));
		AccountEntity from = accounts.get(fromUserId);
		AccountEntity to = accounts.get(request.toUserId());
		if (from == null) throw new ResourceNotFoundException("Счет отправителя не найден");
		if (to == null) throw new ResourceNotFoundException("Счет получателя не найден");
		BigDecimal amount = request.value();
		if (from.getBalance().compareTo(amount) < 0) throw new IllegalArgumentException("Недостаточно средств");
		from.setBalance(from.getBalance().subtract(amount));
		to.setBalance(to.getBalance().add(amount));
		LOGGER.info("Перевод выполнен: fromUserId={}, toUserId={}, amount={}", fromUserId, request.toUserId(), amount);
		return new TransferResponse(fromUserId, request.toUserId(), amount, from.getBalance());
	}
}
