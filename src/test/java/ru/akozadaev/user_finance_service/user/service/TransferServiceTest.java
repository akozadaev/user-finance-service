package ru.akozadaev.user_finance_service.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.akozadaev.user_finance_service.user.dto.TransferRequest;
import ru.akozadaev.user_finance_service.user.model.AccountEntity;
import ru.akozadaev.user_finance_service.user.model.UserEntity;
import ru.akozadaev.user_finance_service.user.repository.AccountRepository;

class TransferServiceTest {

	private AccountRepository repository;
	private TransferService service;
	private AccountEntity from;
	private AccountEntity to;

	@BeforeEach
	void setUp() {
		repository = mock(AccountRepository.class);
		service = new TransferService(repository);
		from = account(1L, "100.00");
		to = account(2L, "50.00");
		when(repository.findAllByUserIdsForUpdate(List.of(1L, 2L))).thenReturn(List.of(from, to));
	}

	@Test
	void transfersMoneyAtomically() {
		var result = service.transfer(1L, new TransferRequest(2L, new BigDecimal("30.00")));
		assertThat(from.getBalance()).isEqualByComparingTo("70.00");
		assertThat(to.getBalance()).isEqualByComparingTo("80.00");
		assertThat(result.balance()).isEqualByComparingTo("70.00");
	}

	@Test
	void rejectsTransferWhenBalanceIsInsufficient() {
		assertThatThrownBy(() -> service.transfer(1L, new TransferRequest(2L, new BigDecimal("100.01"))))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Недостаточно средств");
		assertThat(from.getBalance()).isEqualByComparingTo("100.00");
		assertThat(to.getBalance()).isEqualByComparingTo("50.00");
	}

	private AccountEntity account(Long userId, String balance) {
		UserEntity user = mock(UserEntity.class);
		when(user.getId()).thenReturn(userId);
		return new AccountEntity(user, new BigDecimal(balance));
	}
}
