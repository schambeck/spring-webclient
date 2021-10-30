package com.jobsity.webclient.service;

import com.jobsity.webclient.domain.Invoice;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.HttpMethod.*;

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

    public Invoice findById(Long id) {
        return restTemplate.getForObject("/invoices/{id}", Invoice.class, id);
    }

    public Invoice create(Invoice invoice) {
        return restTemplate.postForObject("/invoices", invoice, Invoice.class);
    }

    public Invoice update(Long id, Invoice invoice) {
        return restTemplate.exchange("/invoices/{id}", PUT, new HttpEntity<>(invoice), Invoice.class, id).getBody();
    }

    public Void delete(Long id) {
        return restTemplate.exchange("/invoices/{id}", DELETE, null, Void.class, id).getBody();
    }

}
