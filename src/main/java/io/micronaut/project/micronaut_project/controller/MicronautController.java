package io.micronaut.project.micronaut_project.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.http.server.util.locale.HttpLocaleResolver;
import io.micronaut.project.micronaut_project.entity.Products;
import io.micronaut.project.micronaut_project.repository.ProductRepository;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.Authenticator;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.event.LoginFailedEvent;
import io.micronaut.security.event.LoginSuccessfulEvent;
import io.micronaut.security.handlers.LoginHandler;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.micronaut.http.HttpHeaders.AUTHORIZATION;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;

@Requires(classes = Controller.class)
@Requires(beans = {LoginHandler.class, Authenticator.class})
@Controller("/head")
public class MicronautController {
	
	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
	
	@Inject
	ProductRepository productRepository;	
    @Inject
    Authenticator<HttpRequest<?>> authenticator;
    @Inject
    LoginHandler<HttpRequest<?>, MutableHttpResponse<?>> loginHandler;
    @Inject
    ApplicationEventPublisher<LoginSuccessfulEvent> loginSuccessfulEventPublisher;
    @Inject
    ApplicationEventPublisher<LoginFailedEvent> loginFailedEventPublisher;
    @Inject
    HttpHostResolver httpHostResolver;
    @Inject
    HttpLocaleResolver httpLocaleResolver;
    
    
	@Post("/products")
	@Produces(MediaType.APPLICATION_JSON) 
	public Products createProduct(
			@Body 
			Products products
		) {
		return productRepository.save(products);
	}
	
	@Post("/data")
	@Secured(SecurityRule.IS_AUTHENTICATED)
	@Produces(MediaType.APPLICATION_JSON)
	@Header(AUTHORIZATION)
	public String getData() {
		return "Test";
	}
	
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post("/login")
    @SingleResult
    public Publisher<MutableHttpResponse<?>> login(
    		@Valid 
    		@Body
    		UsernamePasswordCredentials usernamePasswordCredentials, 
    		HttpRequest<?> request
    	) {
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

    private void publishLoginFailedEvent(AuthenticationResponse authenticationResponse,
                                         UsernamePasswordCredentials usernamePasswordCredentials,
                                         HttpRequest<?> request) {
        loginFailedEventPublisher.publishEvent(
                new LoginFailedEvent(
                        authenticationResponse,
                        usernamePasswordCredentials,
                        httpHostResolver.resolve(request),
                        httpLocaleResolver.resolveOrDefault(request)
                )
        );
    }
}
