package io.micronaut.project.micronaut_project.controller;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.cli.exceptions.ParseException;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.client.HttpClient;
import io.micronaut.project.micronaut_project.dto.UserDTO;
import io.micronaut.project.micronaut_project.entity.Products;
import io.micronaut.project.micronaut_project.repository.ProductRepository;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.token.generator.RefreshTokenGenerator;
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator;
import io.micronaut.security.token.render.BearerAccessRefreshToken;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import static io.micronaut.http.HttpStatus.OK;
import static io.micronaut.http.HttpHeaders.AUTHORIZATION;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/head")
public class MicronautController {
	
	HttpClient client;
	
	@Inject
	ProductRepository productRepository;

	@Post("/products")
	@Produces(MediaType.APPLICATION_JSON) 
	public Products createProduct(
			@Body 
			Products products
		) {
		return productRepository.save(products);
	}
	
	@Post("/data")
	@Produces(MediaType.APPLICATION_JSON)
	@Header(AUTHORIZATION)
	public String getData() {
		return "Test";
	}
	
	@Post("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public String login() {
		return "Test";
	}
}
