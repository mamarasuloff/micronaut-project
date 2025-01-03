package io.micronaut.project.micronaut_project.repository;

import java.util.Optional;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.project.micronaut_project.entity.*;

@Repository
public interface ProductRepository extends CrudRepository<Products, Integer>{
	Optional<Products> findById(int id);
}
