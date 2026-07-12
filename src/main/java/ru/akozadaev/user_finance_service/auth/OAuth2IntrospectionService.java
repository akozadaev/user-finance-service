package ru.akozadaev.user_finance_service.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.akozadaev.user_finance_service.user.repository.OAuthIdentityRepository;

@Service
public class OAuth2IntrospectionService {

	private final RestClient restClient;
	private final OAuthIdentityRepository identityRepository;

	public OAuth2IntrospectionService(RestClient.Builder builder,
			@Value("${finance.oauth2.introspection-uri}") String introspectionUri,
			OAuthIdentityRepository identityRepository) {
		this.restClient = builder.baseUrl(introspectionUri).build();
		this.identityRepository = identityRepository;
	}

	@Cacheable(value = "token-introspection", key = "#token")
	public Long resolveUserId(String token) {
		IntrospectionResponse response = restClient.post().body(new IntrospectionRequest(token, "access_token"))
				.retrieve().body(IntrospectionResponse.class);
		if (response == null || !response.active() || response.userId() == null) return null;
		return identityRepository.findBySubject(response.userId()).map(identity -> identity.getUser().getId()).orElse(null);
	}

	private record IntrospectionRequest(String token, @JsonProperty("token_type_hint") String tokenTypeHint) { }
	private record IntrospectionResponse(boolean active, @JsonProperty("user_id") String userId,
			@JsonProperty("client_id") String clientId, List<String> roles, long exp) { }
}
