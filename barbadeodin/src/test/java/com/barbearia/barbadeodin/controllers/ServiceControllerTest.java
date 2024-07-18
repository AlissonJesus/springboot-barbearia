package com.barbearia.barbadeodin.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.barbearia.barbadeodin.dto.ServiceDetailsDto;
import com.barbearia.barbadeodin.models.ServiceDto;
import com.barbearia.barbadeodin.services.ServiceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class ServiceControllerTest {
	
	@MockBean
	private ServiceService service;
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
    private ObjectMapper mapper;
	
	@Autowired
	private JacksonTester<ServiceDto> payloadJson;
	
	private ServiceDto payload = new ServiceDto(
			"Corte", 
			"Corte de Cabelo Profissional a pedido do cliente, finalizando no lavatório com Shampoo especializado e penteado ao final!",
			25, 30);
	
	private ServiceDetailsDto serviceDetalhado = new ServiceDetailsDto(
			1L, 
			payload.name(), 
			payload.Description(), 
			payload.price(), 
			payload.duration());
	
	private String methodArgumentNotValidMessage = "Dados inválidos fornecidos";
	
	@Test
	@DisplayName("Should register successfully and return status 200")
	void shouldRegisterServiceSuccessfully() throws Exception {
		var requestBody = prepareSimulationScenarioForRegistration();
		var response = executeRegistrationSimulation(requestBody);
		verifyExpectedResponse(response, HttpStatus.CREATED);
		verifyMockBehavior();
	}
	
	@Test
	@DisplayName("Should fail to register with invalid request body and return status 400")
	void shouldFailToRegisterWithInvalidRequestBody() throws Exception {
		var response = executeRegistrationSimulation("{}");
		verifyErrorResponse(response);
	}
	
	@Test
	void shouldGetService() throws Exception {
		prepareSimulationScenarioForGetById();
		var response = executeGetByIdScenarioSimulation();
		verifyExpectedResponse(response, HttpStatus.OK);
		verifyMockBehaviorGetById();
	}
	
	
	

	private void prepareSimulationScenarioForGetById() {
		when(service.getById(any(Long.class))).thenReturn(serviceDetalhado);
	}

	private MockHttpServletResponse executeGetByIdScenarioSimulation() throws Exception {
		return mvc.perform(post("/services/{id}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8"))
				.andReturn().getResponse();
	}

	private void verifyErrorResponse(MockHttpServletResponse response) throws Exception {
		verifyStatus(response.getStatus(), HttpStatus.BAD_REQUEST);
		verifyMessage(response);
	}


	private void verifyMessage(MockHttpServletResponse response) throws UnsupportedEncodingException {
		var body = response.getContentAsString(StandardCharsets.UTF_8);
		assertThat(body).isEqualTo(methodArgumentNotValidMessage);
	}

	private void verifyMockBehavior() {
		verify(service, times(1)).register(payload);	
	}
	
	private void verifyMockBehaviorGetById() {
		verify(service, times(1)).getById(any(Long.class));	
	}

	private void verifyExpectedResponse(MockHttpServletResponse response, HttpStatus status) throws Exception {
		verifyStatus(response.getStatus(), status);
		var body = response.getContentAsString(StandardCharsets.UTF_8);
		verifyBody(body);
	}

	private void verifyBody(String body) throws JsonMappingException, JsonProcessingException {
		JsonNode rootNode = mapper.readTree(body);
		
		Long id = rootNode.path("id").asLong();
		String name = rootNode.path("name").asText();
		String description = rootNode.path("description").asText();
        double price = rootNode.path("price").asDouble();
        int duration = rootNode.path("duration").asInt();
        
        assertThat(id).isEqualTo(serviceDetalhado.id());
        assertThat(name).isEqualTo(serviceDetalhado.name());
        assertThat(description).isEqualTo(serviceDetalhado.description());
        assertThat(price).isEqualTo(serviceDetalhado.price());
        assertThat(duration).isEqualTo(serviceDetalhado.duration());
	}

	private void verifyStatus(int status, HttpStatus type) {		
		assertThat(status).isEqualTo(type.value());
		
	}

	private MockHttpServletResponse executeRegistrationSimulation(String requestBody) throws Exception {
		
		return mvc.perform(post("/services")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
				.characterEncoding("UTF-8"))
				.andReturn().getResponse();
	}
	
	private <T> String createExpectedJson(T payload, JacksonTester<T> payloadJson) throws IOException {
		return payloadJson.write(payload).getJson();
	}

	private String prepareSimulationScenarioForRegistration() throws IOException {
		when(service.register(payload)).thenReturn(serviceDetalhado);
		return createExpectedJson(payload, payloadJson);
	}
}
