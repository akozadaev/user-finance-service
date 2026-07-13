package ru.akozadaev.user_finance_service.user.service;

import java.time.LocalDate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akozadaev.user_finance_service.common.exception.ConflictException;
import ru.akozadaev.user_finance_service.common.exception.ResourceNotFoundException;
import ru.akozadaev.user_finance_service.user.dto.EmailRequest;
import ru.akozadaev.user_finance_service.user.dto.PhoneRequest;
import ru.akozadaev.user_finance_service.user.dto.UserResponse;
import ru.akozadaev.user_finance_service.user.model.EmailDataEntity;
import ru.akozadaev.user_finance_service.user.model.PhoneDataEntity;
import ru.akozadaev.user_finance_service.user.model.UserEntity;
import ru.akozadaev.user_finance_service.user.repository.EmailDataRepository;
import ru.akozadaev.user_finance_service.user.repository.PhoneDataRepository;
import ru.akozadaev.user_finance_service.user.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final EmailDataRepository emailRepository;
	private final PhoneDataRepository phoneRepository;

	public UserService(UserRepository userRepository, EmailDataRepository emailRepository,
			PhoneDataRepository phoneRepository) {
		this.userRepository = userRepository;
		this.emailRepository = emailRepository;
		this.phoneRepository = phoneRepository;
	}

	@Cacheable(value = "users", key = "#id")
	@Transactional(readOnly = true)
	public UserResponse getById(Long id) { return UserResponse.from(requireUser(id)); }

	@Transactional(readOnly = true)
	public Page<UserResponse> search(LocalDate dateOfBirth, String phone, String name, String email, int page, int size) {
		if (page < 0 || size < 1 || size > 100) throw new IllegalArgumentException("page >= 0, size должен быть от 1 до 100");
		return userRepository.findAll(UserSpecifications.filtered(dateOfBirth, phone, name, email),
				PageRequest.of(page, size, Sort.by("id"))).map(UserResponse::from);
	}

	@CacheEvict(value = "users", key = "#userId")
	@Transactional
	public UserResponse addEmail(Long userId, EmailRequest request) {
		String email = request.email().trim().toLowerCase();
		if (emailRepository.existsByEmailIgnoreCase(email)) throw new ConflictException("Email уже занят");
		UserEntity user = requireUser(userId);
		emailRepository.save(new EmailDataEntity(user, email));
		return UserResponse.from(requireUser(userId));
	}

	@CacheEvict(value = "users", key = "#userId")
	@Transactional
	public void deleteEmail(Long userId, Long contactId) {
		requireUserForUpdate(userId);
		EmailDataEntity item = emailRepository.findById(contactId).orElseThrow(() -> new ResourceNotFoundException("Email не найден"));
		checkOwner(userId, item.getUser().getId());
		if (emailRepository.countByUserId(userId) <= 1) throw new ConflictException("У пользователя должен остаться хотя бы один email");
		emailRepository.delete(item);
	}

	@CacheEvict(value = "users", key = "#userId")
	@Transactional
	public UserResponse updateEmail(Long userId, Long contactId, EmailRequest request) {
		EmailDataEntity item = emailRepository.findById(contactId).orElseThrow(() -> new ResourceNotFoundException("Email не найден"));
		checkOwner(userId, item.getUser().getId());
		String email = request.email().trim().toLowerCase();
		emailRepository.findByEmailIgnoreCase(email).filter(other -> !other.getId().equals(contactId))
				.ifPresent(other -> { throw new ConflictException("Email уже занят"); });
		item.setEmail(email);
		return UserResponse.from(requireUser(userId));
	}

	@CacheEvict(value = "users", key = "#userId")
	@Transactional
	public UserResponse addPhone(Long userId, PhoneRequest request) {
		if (phoneRepository.existsByPhone(request.phone())) throw new ConflictException("Телефон уже занят");
		UserEntity user = requireUser(userId);
		phoneRepository.save(new PhoneDataEntity(user, request.phone()));
		return UserResponse.from(requireUser(userId));
	}

	@CacheEvict(value = "users", key = "#userId")
	@Transactional
	public void deletePhone(Long userId, Long contactId) {
		requireUserForUpdate(userId);
		PhoneDataEntity item = phoneRepository.findById(contactId).orElseThrow(() -> new ResourceNotFoundException("Телефон не найден"));
		checkOwner(userId, item.getUser().getId());
		if (phoneRepository.countByUserId(userId) <= 1) throw new ConflictException("У пользователя должен остаться хотя бы один телефон");
		phoneRepository.delete(item);
	}

	@CacheEvict(value = "users", key = "#userId")
	@Transactional
	public UserResponse updatePhone(Long userId, Long contactId, PhoneRequest request) {
		PhoneDataEntity item = phoneRepository.findById(contactId).orElseThrow(() -> new ResourceNotFoundException("Телефон не найден"));
		checkOwner(userId, item.getUser().getId());
		phoneRepository.findByPhone(request.phone()).filter(other -> !other.getId().equals(contactId))
				.ifPresent(other -> { throw new ConflictException("Телефон уже занят"); });
		item.setPhone(request.phone());
		return UserResponse.from(requireUser(userId));
	}

	private UserEntity requireUser(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + id));
	}

	private UserEntity requireUserForUpdate(Long id) {
		return userRepository.findByIdForUpdate(id)
				.orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + id));
	}

	private void checkOwner(Long expected, Long actual) {
		if (!expected.equals(actual)) throw new ResourceNotFoundException("Контакт не найден");
	}
}
