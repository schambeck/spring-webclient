package com.jobsity.webclient.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateConfig {

	@Value("${spring-rest.base-url}")
	private String baseUrl;

	@Bean
	public RestTemplate createRestTemplate() {
		RestTemplate restTemplate = new RestTemplateBuilder().build();
		restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));
		return restTemplate;
	}

}