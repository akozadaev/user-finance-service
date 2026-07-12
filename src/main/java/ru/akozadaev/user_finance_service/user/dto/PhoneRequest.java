package ru.akozadaev.user_finance_service.user.dto;

import jakarta.validation.constraints.Pattern;

public record PhoneRequest(@Pattern(regexp = "^7\\d{10}$", message = "Телефон должен иметь формат 79207865432") String phone) {
}
