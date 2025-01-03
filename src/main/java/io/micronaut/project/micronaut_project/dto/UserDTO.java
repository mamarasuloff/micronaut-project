package io.micronaut.project.micronaut_project.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
public class UserDTO {
	public String login;
	public String password;
}
