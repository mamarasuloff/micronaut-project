package io.micronaut.project.micronaut_project.controller;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.*;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.http.server.util.locale.HttpLocaleResolver;
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
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//@Requires(property = LoginControllerConfigurationProperties.PREFIX + ".enabled", notEquals = StringUtils.FALSE, defaultValue = StringUtils.TRUE)
@Requires(classes = Controller.class)
@Requires(beans = {LoginHandler.class, Authenticator.class})
//@Controller("${" + LoginControllerConfigurationProperties.PREFIX + ".path:/login}")
@Controller("/loginController")
@Secured(SecurityRule.IS_ANONYMOUS)
public class LoginController<B> {
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
    
    @Inject
    Authenticator<HttpRequest<B>> authenticator;
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

    /**
     * @param authenticator                 {@link Authenticator} collaborator
     * @param loginHandler                  A collaborator which helps to build HTTP response depending on success or failure.
     * @param loginSuccessfulEventPublisher Application event publisher for {@link LoginSuccessfulEvent}.
     * @param loginFailedEventPublisher     Application event publisher for {@link LoginFailedEvent}.
     * @param httpHostResolver              The http host resolver
     * @param httpLocaleResolver            The http locale resolver
     * @since 4.7.0
     */

    
    /**
     * @param usernamePasswordCredentials An instance of {@link UsernamePasswordCredentials} in the body payload
     * @param request                     The {@link HttpRequest} being executed
     * @return An AccessRefreshToken encapsulated in the HttpResponse or a failure indicated by the HTTP status
     */
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
    @Post
    @SingleResult
    public Publisher<MutableHttpResponse<?>> login(
    		@Valid 
    		@Body
    		UsernamePasswordCredentials usernamePasswordCredentials, 
    		HttpRequest<B> request
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
