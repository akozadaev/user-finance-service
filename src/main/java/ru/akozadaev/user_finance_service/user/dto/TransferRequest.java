package ru.akozadaev.user_finance_service.user.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TransferRequest(@NotNull @Positive Long toUserId,
		@NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal value) {
}
