package io.micronaut.project.micronaut_project.config;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.http.server.util.locale.HttpLocaleResolver;
import io.micronaut.project.micronaut_project.controller.LoginController;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.Authenticator;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.event.LoginFailedEvent;
import io.micronaut.security.event.LoginSuccessfulEvent;
import io.micronaut.security.handlers.LoginHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class JwtAuthentication<B> {
	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

	protected final Authenticator<HttpRequest<B>> authenticator;
	protected final LoginHandler<HttpRequest<?>, MutableHttpResponse<?>> loginHandler;
	protected final ApplicationEventPublisher<LoginSuccessfulEvent> loginSuccessfulEventPublisher;
	protected final ApplicationEventPublisher<LoginFailedEvent> loginFailedEventPublisher;
	protected final HttpHostResolver httpHostResolver;
	protected final HttpLocaleResolver httpLocaleResolver;

	public JwtAuthentication(Authenticator<HttpRequest<B>> authenticator,
			LoginHandler<HttpRequest<?>, MutableHttpResponse<?>> loginHandler,
			ApplicationEventPublisher<LoginSuccessfulEvent> loginSuccessfulEventPublisher,
			ApplicationEventPublisher<LoginFailedEvent> loginFailedEventPublisher, HttpHostResolver httpHostResolver,
			HttpLocaleResolver httpLocaleResolver
		) {

		this.authenticator = authenticator;
		this.loginHandler = loginHandler;
		this.loginSuccessfulEventPublisher = loginSuccessfulEventPublisher;
		this.loginFailedEventPublisher = loginFailedEventPublisher;
		this.httpHostResolver = httpHostResolver;
		this.httpLocaleResolver = httpLocaleResolver;
	}

	public Publisher<MutableHttpResponse<?>> Authenticate(UsernamePasswordCredentials usernamePasswordCredentials, HttpRequest<B> request) {
		return Flux.from(authenticator.authenticate(request, usernamePasswordCredentials))
	            .map(authenticationResponse -> {
	                if (authenticationResponse.isAuthenticated() && authenticationResponse.getAuthentication().isPresent()) {
	                    Authentication authentication = authenticationResponse.getAuthentication().get();
	                    publishLoginSuccessfulEvent(authentication, request);
	                    return loginHandler.loginSuccess(authentication, request);
	                } else {
	                    if (LOG.isTraceEnabled()) {
	                        LOG.trace("login failed for username: {}", usernamePasswordCredentials.getUsername());
	                    }
	                    publishLoginFailedEvent(authenticationResponse, usernamePasswordCredentials, request);
	                    return loginHandler.loginFailed(authenticationResponse, request);
	                }
	            }).switchIfEmpty(Mono.defer(() -> Mono.just(HttpResponse.status(HttpStatus.UNAUTHORIZED))));
	}
	
    private void publishLoginSuccessfulEvent(Authentication authentication, HttpRequest<?> request) {
        loginSuccessfulEventPublisher.publishEvent(
            new LoginSuccessfulEvent(
                authentication,
                httpHostResolver.resolve(request),
                httpLocaleResolver.resolveOrDefault(request)
            )
        );
    }

	private void publishLoginFailedEvent(
			AuthenticationResponse authenticationResponse,
			UsernamePasswordCredentials usernamePasswordCredentials, HttpRequest<?> request
		) {
		loginFailedEventPublisher.publishEvent(new LoginFailedEvent(authenticationResponse, usernamePasswordCredentials,
		httpHostResolver.resolve(request), httpLocaleResolver.resolveOrDefault(request)));
	}
}
