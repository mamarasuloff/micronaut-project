package io.micronaut.project.micronaut_project.config;

import java.util.Collections;

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
	
	PasswordEncryptionAndDecryptionService passwordEncryptionAndDecryptionService = new PasswordEncryptionAndDecryptionService();
	
    UserRoles userRoles;
	
	@Override
	public AuthenticationResponse authenticate(
			@Nullable 
			HttpRequest<T> httpRequest,
			@NonNull 
			AuthenticationRequest<String, String> authenticationRequest
		) {
		String encryptedPassword = passwordEncryptionAndDecryptionService.encrypt(authenticationRequest.getSecret());
		String username = userRepository.findByUsernameAndPassword(authenticationRequest.getIdentity(), encryptedPassword).getUsername();
		String password = userRepository.findByUsernameAndPassword(authenticationRequest.getIdentity(), encryptedPassword).getPassword();
		String userrole = userRepository.findByUsernameAndPassword(authenticationRequest.getIdentity(), encryptedPassword).getUserrole();
		return authenticationRequest.getIdentity().equals(username)
				&& authenticationRequest.getSecret().equals(passwordEncryptionAndDecryptionService.decrypt(password))
						? AuthenticationResponse.success(username, Collections.singletonList(userrole))
						: AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
	}
}