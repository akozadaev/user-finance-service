package ru.akozadaev.user_finance_service.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(@NotBlank String login, @NotBlank @Size(min = 8, max = 500) String password) {
}
