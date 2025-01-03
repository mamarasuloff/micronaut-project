package io.micronaut.project.micronaut_project.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.project.micronaut_project.dto.ProductsDTO;
import io.micronaut.project.micronaut_project.dto.UserDTO;
import io.micronaut.project.micronaut_project.entity.Products;
import io.micronaut.project.micronaut_project.repository.ProductRepository;
import io.micronaut.project.micronaut_project.service.ProductService;
//import io.micronaut.security.annotation.Secured;
//import io.micronaut.security.authentication.UsernamePasswordCredentials;
//import io.micronaut.security.rules.SecurityRule;
//import io.micronaut.security.token.render.BearerAccessRefreshToken;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import static io.micronaut.http.HttpStatus.OK;
//import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller("/head")
public class MicronautController {
	
	@Inject
	ProductService productService;

	@Post("/products")
	@Produces(MediaType.APPLICATION_JSON) 
	public Products createProduct(
			@Body 
			Products products
		) {
		return productService.saveProducts(products);
	}
	
//	@Get
//	public List<Products> getProducts() {
//		Iterable<Products> products = productRepository.findAll();
//		List<Products> result = new ArrayList<Products>();
//		products.forEach(result::add);
//		return result;
//	}
	
}
