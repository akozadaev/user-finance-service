package ru.akozadaev.user_finance_service.auth;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import ru.akozadaev.user_finance_service.auth.dto.TokenResponse;

@Component
public class OAuth2Gateway {

	private final RestClient restClient;
	private final String clientId;
	private final String clientSecret;

	public OAuth2Gateway(RestClient.Builder builder, @Value("${finance.oauth2.base-url}") String baseUrl,
			@Value("${finance.oauth2.client-id}") String clientId,
			@Value("${finance.oauth2.client-secret}") String clientSecret) {
		this.restClient = builder.baseUrl(baseUrl).build();
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	public TokenResponse authenticate(String username, String password) {
		try {
			return requestToken(username, password);
		} catch (RestClientResponseException firstFailure) {
			register(username, password);
			return requestToken(username, password);
		}
	}

	private TokenResponse requestToken(String username, String password) {
		var form = new LinkedMultiValueMap<String, String>();
		form.add("grant_type", "password");
		form.add("username", username);
		form.add("password", password);
		form.add("client_id", clientId);
		form.add("client_secret", clientSecret);
		return restClient.post().uri("/token").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(form).retrieve().body(TokenResponse.class);
	}

	private void register(String username, String password) {
		restClient.post().uri("/users").contentType(MediaType.APPLICATION_JSON)
				.body(Map.of("username", username, "password", password)).retrieve().toBodilessEntity();
	}
}
