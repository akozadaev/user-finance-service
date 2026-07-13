package ru.akozadaev.user_finance_service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.akozadaev.user_finance_service.auth.dto.LoginRequest;
import ru.akozadaev.user_finance_service.auth.dto.TokenResponse;
import ru.akozadaev.user_finance_service.user.model.EmailDataEntity;
import ru.akozadaev.user_finance_service.user.model.UserEntity;
import ru.akozadaev.user_finance_service.user.repository.EmailDataRepository;
import ru.akozadaev.user_finance_service.user.repository.OAuthIdentityRepository;
import ru.akozadaev.user_finance_service.user.repository.PhoneDataRepository;

class AuthServiceTest {

	private OAuth2Gateway gateway;
	private OAuth2IntrospectionService introspectionService;
	private OAuthIdentityRepository identityRepository;
	private EmailDataRepository emailRepository;
	private AuthService authService;

	@BeforeEach
	void setUp() {
		gateway = mock(OAuth2Gateway.class);
		introspectionService = mock(OAuth2IntrospectionService.class);
		identityRepository = mock(OAuthIdentityRepository.class);
		emailRepository = mock(EmailDataRepository.class);
		PhoneDataRepository phoneRepository = mock(PhoneDataRepository.class);
		PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
		authService = new AuthService(gateway, introspectionService, identityRepository,
				emailRepository, phoneRepository, passwordEncoder);
		when(passwordEncoder.encode("password")).thenReturn("bcrypt-hash");
	}

	@Test
	void authenticatesByEmailAndBindsOAuthSubject() {
		UserEntity user = mock(UserEntity.class);
		when(user.getId()).thenReturn(1L);
		EmailDataEntity email = mock(EmailDataEntity.class);
		when(email.getUser()).thenReturn(user);
		when(emailRepository.findByEmailIgnoreCase("ivan@example.com")).thenReturn(Optional.of(email));
		TokenResponse token = new TokenResponse("access-token", "Bearer", 3600, "refresh-token", "");
		when(gateway.authenticate("ivan@example.com", "password")).thenReturn(token);
		when(introspectionService.resolveSubject("access-token")).thenReturn("oauth-subject");
		when(identityRepository.findBySubject("oauth-subject")).thenReturn(Optional.empty());

		assertThat(authService.login(new LoginRequest("ivan@example.com", "password"))).isEqualTo(token);
		verify(identityRepository).save(any());
		verify(user).setPassword("bcrypt-hash");
	}

	@Test
	void rejectsUnknownLogin() {
		when(emailRepository.findByEmailIgnoreCase("unknown@example.com")).thenReturn(Optional.empty());
		assertThatThrownBy(() -> authService.login(new LoginRequest("unknown@example.com", "password")))
				.isInstanceOf(BadCredentialsException.class);
	}
}
