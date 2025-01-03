package io.micronaut.project.micronaut_project;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.project.micronaut_project.entity.Products;
import io.micronaut.runtime.Micronaut;

public class MicronautProject {
    public static void main(String[] args) {
    	Micronaut.run(MicronautProject.class);
    }
}
