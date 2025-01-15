package io.micronaut.project.micronaut_project.dto;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class UserDTO {
		
	public UserDTO(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	private String username;
	private String password;
	
}
