package ru.akozadaev.user_finance_service.user.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import ru.akozadaev.user_finance_service.user.model.UserEntity;

public record UserResponse(Long id, String name, LocalDate dateOfBirth, BigDecimal balance,
		List<ContactResponse> emails, List<ContactResponse> phones) {

	public static UserResponse from(UserEntity user) {
		return new UserResponse(user.getId(), user.getName(), user.getDateOfBirth(), user.getAccount().getBalance(),
				user.getEmails().stream().map(item -> new ContactResponse(item.getId(), item.getEmail())).toList(),
				user.getPhones().stream().map(item -> new ContactResponse(item.getId(), item.getPhone())).toList());
	}
}
