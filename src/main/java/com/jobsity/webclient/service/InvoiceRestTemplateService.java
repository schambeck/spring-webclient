package com.jobsity.webclient.service;

import com.jobsity.webclient.domain.Invoice;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Service
public class InvoiceRestTemplateService {

    private final RestTemplate restTemplate;

    public InvoiceRestTemplateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Invoice> findAll() {
        ParameterizedTypeReference<List<Invoice>> type = new ParameterizedTypeReference<List<Invoice>>() {};
        return restTemplate.exchange("/invoices", GET,null, type).getBody();
    }

}
