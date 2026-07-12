package ru.akozadaev.user_finance_service.user.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.akozadaev.user_finance_service.user.dto.TransferRequest;
import ru.akozadaev.user_finance_service.user.dto.TransferResponse;
import ru.akozadaev.user_finance_service.user.service.TransferService;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {
	private final TransferService transferService;
	public TransferController(TransferService transferService) { this.transferService = transferService; }

	@PostMapping
	public TransferResponse transfer(Authentication authentication, @Valid @RequestBody TransferRequest request) {
		return transferService.transfer((Long) authentication.getPrincipal(), request);
	}
}
