package ru.akozadaev.user_finance_service.user.dto;

import java.math.BigDecimal;

public record TransferResponse(Long fromUserId, Long toUserId, BigDecimal value, BigDecimal balance) {
}
