package com.jobsity.webclient.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient createWebClient() {
		return WebClient.builder()
				.baseUrl("http://localhost:8080")
				.defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
				.build();
	}

}