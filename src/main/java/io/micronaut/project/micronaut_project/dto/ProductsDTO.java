package io.micronaut.project.micronaut_project.dto;

import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
public class ProductsDTO {
	private String name;
	private double price;
}
