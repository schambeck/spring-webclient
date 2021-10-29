package com.jobsity.webclient.conf;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateConfig {

	@Bean
	public RestTemplate createRestTemplate() {
		RestTemplate restTemplate = new RestTemplateBuilder().build();
		restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:8080"));
		return restTemplate;
	}

}