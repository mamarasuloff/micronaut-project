package io.micronaut.project.micronaut_project.config;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import jakarta.inject.Singleton;

@Singleton
class AuthenticationProviderUserPassword<T> implements HttpRequestAuthenticationProvider<T> {

	@Override
	public AuthenticationResponse authenticate(
			@Nullable 
			HttpRequest<T> httpRequest,
			@NonNull 
			AuthenticationRequest<String, String> authenticationRequest
		) {
		return authenticationRequest.getIdentity().equals("sherlock")
				&& authenticationRequest.getSecret().equals("password")
						? AuthenticationResponse.success(authenticationRequest.getIdentity())
						: AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
	}
}