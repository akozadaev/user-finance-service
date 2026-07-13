package ru.akozadaev.user_finance_service.user.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.akozadaev.user_finance_service.user.dto.EmailRequest;
import ru.akozadaev.user_finance_service.user.dto.PhoneRequest;
import ru.akozadaev.user_finance_service.user.dto.UserResponse;
import ru.akozadaev.user_finance_service.user.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;
	public UserController(UserService userService) { this.userService = userService; }

	@GetMapping("/{id}")
	public UserResponse getById(@PathVariable Long id) { return userService.getById(id); }

	@GetMapping
	public Page<UserResponse> search(
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate dateOfBirth,
			@RequestParam(required = false) String phone, @RequestParam(required = false) String name,
			@RequestParam(required = false) String email, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		return userService.search(dateOfBirth, phone, name, email, page, size);
	}

	@PostMapping("/me/emails")
	public UserResponse addEmail(Authentication auth, @Valid @RequestBody EmailRequest request) {
		return userService.addEmail(userId(auth), request);
	}

	@PutMapping("/me/emails/{id}")
	public UserResponse updateEmail(Authentication auth, @PathVariable Long id, @Valid @RequestBody EmailRequest request) {
		return userService.updateEmail(userId(auth), id, request);
	}

	@DeleteMapping("/me/emails/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteEmail(Authentication auth, @PathVariable Long id) { userService.deleteEmail(userId(auth), id); }

	@PostMapping("/me/phones")
	public UserResponse addPhone(Authentication auth, @Valid @RequestBody PhoneRequest request) {
		return userService.addPhone(userId(auth), request);
	}

	@PutMapping("/me/phones/{id}")
	public UserResponse updatePhone(Authentication auth, @PathVariable Long id, @Valid @RequestBody PhoneRequest request) {
		return userService.updatePhone(userId(auth), id, request);
	}

	@DeleteMapping("/me/phones/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePhone(Authentication auth, @PathVariable Long id) { userService.deletePhone(userId(auth), id); }

	private Long userId(Authentication authentication) { return (Long) authentication.getPrincipal(); }
}
