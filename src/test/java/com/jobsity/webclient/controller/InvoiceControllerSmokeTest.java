package com.jobsity.webclient.controller;

import com.jobsity.webclient.service.InvoiceServiceImpl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@Import(InvoiceServiceImpl.class)
@WebMvcTest(InvoiceController.class)
class InvoiceControllerSmokeTest {

	@Autowired
	private InvoiceController controller;

	@Test
	void contextLoads() {
		assertThat(controller).isNotNull();
	}

	@TestConfiguration
	static class TestConfig {
		@Bean
		public WebClient createWebClient() {
			return WebClient.builder().build();
		}
	}

}
