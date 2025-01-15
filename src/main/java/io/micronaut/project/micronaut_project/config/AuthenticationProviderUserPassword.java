package io.micronaut.project.micronaut_project.config;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.project.micronaut_project.entity.UserRoles;
import io.micronaut.project.micronaut_project.repository.UserRepository;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
class AuthenticationProviderUserPassword<T> implements HttpRequestAuthenticationProvider<T> {

	@Inject
	UserRepository userRepository;
	
    UserRoles userRoles;
	
	@Override
	public AuthenticationResponse authenticate(
			@Nullable 
			HttpRequest<T> httpRequest,
			@NonNull 
			AuthenticationRequest<String, String> authenticationRequest
		) {
		String username = userRepository.findByUsernameAndPassword(authenticationRequest.getIdentity(), authenticationRequest.getSecret()).getUsername();
		String password = userRepository.findByUsernameAndPassword(authenticationRequest.getIdentity(), authenticationRequest.getSecret()).getPassword();
		return authenticationRequest.getIdentity().equals(username)
				&& authenticationRequest.getSecret().equals(password)
						? AuthenticationResponse.success(username)
						: AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
	}
}