package io.micronaut.project.micronaut_project.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.project.micronaut_project.entity.*;

@Repository
public interface ProductRepository extends JpaRepository<Products, Integer>{
}
