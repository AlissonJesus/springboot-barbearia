package com.barbearia.barbadeodin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barbearia.barbadeodin.dto.ServiceDetailsDto;
import com.barbearia.barbadeodin.models.ServiceDto;
import com.barbearia.barbadeodin.services.ServiceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("services")
public class ServiceController {
	
	@Autowired
	private ServiceService service;

	@PostMapping
	public ResponseEntity<ServiceDetailsDto> register(@RequestBody @Valid ServiceDto payload) {
		var serviceDetalhado = service.register(payload);
		return ResponseEntity.created(null).body(serviceDetalhado);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ServiceDetailsDto> getById(@PathVariable Long id) {
		var serviceRegistered = service.getById(id);
		return ResponseEntity.ok(serviceRegistered);
	}
	
}
