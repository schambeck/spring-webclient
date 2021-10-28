package com.jobsity.webclient.service;

import com.jobsity.webclient.domain.Invoice;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
public class InvoiceService {

    private final WebClient client;

    public InvoiceService(WebClient client) {
        this.client = client;
    }

    public Mono<List<Invoice>> findAll() {
        return client.get()
                .uri("/invoices")
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Invoice>>() {});
    }

    public Mono<Invoice> findByIndex(int index) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/invoices/{index}")
                        .build(index))
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Invoice.class);
    }

    public Mono<Invoice> create(Invoice invoice) {
        return client.post()
                .uri("/invoices")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(invoice))
                .retrieve()
                .bodyToMono(Invoice.class);
    }

    public Mono<Invoice> update(int index, Invoice invoice) {
        return client.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/invoices/{index}")
                        .build(index))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(invoice))
                .retrieve()
                .bodyToMono(Invoice.class);
    }

    public Mono<Void> delete(int index) {
        return client.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/invoices/{index}")
                        .build(index))
                .retrieve()
                .bodyToMono(Void.class);
    }

}
