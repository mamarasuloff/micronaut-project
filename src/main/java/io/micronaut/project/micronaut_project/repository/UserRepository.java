package io.micronaut.project.micronaut_project.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.project.micronaut_project.entity.UserRoles;

@Repository
public interface UserRepository extends JpaRepository<UserRoles, Integer> {
	UserRoles findByUsernameAndPassword(String username, String password);
}
