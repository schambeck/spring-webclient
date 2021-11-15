package com.jobsity.webclient.service;

import com.jobsity.webclient.conf.exception.ClientErrorException;
import com.jobsity.webclient.conf.exception.ServerErrorException;
import com.jobsity.webclient.conf.exception.ServiceUnavailableException;
import com.jobsity.webclient.domain.Invoice;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final WebClient client;

    public InvoiceServiceImpl(WebClient client) {
        this.client = client;
    }

    @Override
    public Mono<List<Invoice>> findAll() {
        return client.get()
                .uri("/invoices")
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new ClientErrorException("Client error", response.rawStatusCode())))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.just(new ServerErrorException("Server error", response.rawStatusCode())))
                .bodyToMono(new ParameterizedTypeReference<List<Invoice>>() {})
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof ServerErrorException)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            throw new ServiceUnavailableException("External Service failed to process after max retries", SERVICE_UNAVAILABLE.value());
                        }));
    }

    @Override
    public Mono<Invoice> findById(Long id) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/invoices/{id}").build(id))
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new ClientErrorException("Client error", response.rawStatusCode())))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.just(new ServerErrorException("Server error", response.rawStatusCode())))
                .bodyToMono(Invoice.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                    .filter(throwable -> throwable instanceof ServerErrorException)
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                        throw new ServiceUnavailableException("External Service failed to process after max retries", SERVICE_UNAVAILABLE.value());
                    }));
    }

    @Override
    public Mono<Invoice> create(Invoice invoice) {
        return client.post()
                .uri("/invoices")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(invoice))
                .retrieve()
                .bodyToMono(Invoice.class);
    }

    @Override
    public Mono<Invoice> update(Long id, Invoice invoice) {
        return client.put()
                .uri(uriBuilder -> uriBuilder.path("/invoices/{id}").build(id))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(invoice))
                .retrieve()
                .bodyToMono(Invoice.class);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return client.delete()
                .uri(uriBuilder -> uriBuilder.path("/invoices/{id}").build(id))
                .retrieve()
                .bodyToMono(Void.class);
    }

}
