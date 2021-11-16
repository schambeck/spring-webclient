package com.schambeck.webclient.service;

import com.schambeck.webclient.domain.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Service
@RequiredArgsConstructor
class InvoiceRestTemplateServiceImpl implements InvoiceRestTemplateService {

    private final RestTemplate restTemplate;

    @Override
    public List<Invoice> findAll() {
        ParameterizedTypeReference<List<Invoice>> type = new ParameterizedTypeReference<List<Invoice>>() {};
        return restTemplate.exchange("/invoices", GET,null, type).getBody();
    }

    @Override
    public Invoice findById(Long id) {
        return restTemplate.getForObject("/invoices/{id}", Invoice.class, id);
    }

    @Override
    public Invoice create(Invoice invoice) {
        return restTemplate.postForObject("/invoices", invoice, Invoice.class);
    }

    @Override
    public Invoice update(Long id, Invoice invoice) {
        return restTemplate.exchange("/invoices/{id}", PUT, new HttpEntity<>(invoice), Invoice.class, id).getBody();
    }

    @Override
    public Void delete(Long id) {
        return restTemplate.exchange("/invoices/{id}", DELETE, null, Void.class, id).getBody();
    }

}
