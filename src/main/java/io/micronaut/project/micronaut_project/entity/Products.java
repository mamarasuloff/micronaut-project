package io.micronaut.project.micronaut_project.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "product_spec")
public class Products implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
	@NotNull
    @Column(name = "name", nullable = false)
	@Size(max = 100)
    private String name;
    
	@NotNull
    @Column(name = "price", nullable = false)
    private double price;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
}