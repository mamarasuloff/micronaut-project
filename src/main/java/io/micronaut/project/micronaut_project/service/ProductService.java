package io.micronaut.project.micronaut_project.service;

import io.micronaut.project.micronaut_project.entity.Products;
import io.micronaut.project.micronaut_project.repository.ProductRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ProductService {
	
	@Inject
	ProductRepository productRepository;
	
	public Products saveProducts(Products products) {
		return productRepository.save(products);
	}
}
