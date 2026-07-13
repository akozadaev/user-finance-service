package ru.akozadaev.user_finance_service.auth;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientResponseException;
import ru.akozadaev.user_finance_service.auth.dto.LoginRequest;
import ru.akozadaev.user_finance_service.auth.dto.TokenResponse;
import ru.akozadaev.user_finance_service.common.exception.ConflictException;
import ru.akozadaev.user_finance_service.user.model.OAuthIdentityEntity;
import ru.akozadaev.user_finance_service.user.model.UserEntity;
import ru.akozadaev.user_finance_service.user.repository.EmailDataRepository;
import ru.akozadaev.user_finance_service.user.repository.OAuthIdentityRepository;
import ru.akozadaev.user_finance_service.user.repository.PhoneDataRepository;

@Service
public class AuthService {

	private final OAuth2Gateway gateway;
	private final OAuth2IntrospectionService introspectionService;
	private final OAuthIdentityRepository identityRepository;
	private final EmailDataRepository emailRepository;
	private final PhoneDataRepository phoneRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthService(OAuth2Gateway gateway, OAuth2IntrospectionService introspectionService,
			OAuthIdentityRepository identityRepository, EmailDataRepository emailRepository,
			PhoneDataRepository phoneRepository, PasswordEncoder passwordEncoder) {
		this.gateway = gateway;
		this.introspectionService = introspectionService;
		this.identityRepository = identityRepository;
		this.emailRepository = emailRepository;
		this.phoneRepository = phoneRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public TokenResponse login(LoginRequest request) {
		String login = request.login().trim().toLowerCase();
		UserEntity user = findUser(login);
		try {
			TokenResponse token = gateway.authenticate(login, request.password());
			if (token == null || token.accessToken() == null) throw new BadCredentialsException("OAuth2-сервер не выдал токен");
			String subject = introspectionService.resolveSubject(token.accessToken());
			if (subject == null) throw new BadCredentialsException("OAuth2-сервер отклонил токен");
			bindIdentity(user, subject);
			user.setPassword(passwordEncoder.encode(request.password()));
			return token;
		} catch (RestClientResponseException exception) {
			throw new BadCredentialsException("Неверный логин или пароль");
		}
	}

	private UserEntity findUser(String login) {
		if (login.contains("@")) {
			return emailRepository.findByEmailIgnoreCase(login).map(item -> item.getUser())
					.orElseThrow(() -> new BadCredentialsException("Неверный логин или пароль"));
		}
		return phoneRepository.findByPhone(login).map(item -> item.getUser())
				.orElseThrow(() -> new BadCredentialsException("Неверный логин или пароль"));
	}

	private void bindIdentity(UserEntity user, String subject) {
		identityRepository.findBySubject(subject).ifPresentOrElse(identity -> {
			if (!identity.getUser().getId().equals(user.getId())) throw new ConflictException("OAuth2 identity уже привязан");
		}, () -> identityRepository.save(new OAuthIdentityEntity(user, subject)));
	}
}
