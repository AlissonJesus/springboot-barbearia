package com.barbearia.barbadeodin.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ServiceDto(
		
		@NotBlank
		String name, 
		String Description, 
		
		@NotNull
		double price, 
		@NotNull
		Integer duration) {
	
}
