package com.schambeck.webclient.service;

import com.schambeck.webclient.base.exception.ServerErrorException;
import com.schambeck.webclient.domain.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
class InvoiceFluxServiceImpl implements InvoiceFluxService {

    private final WebClient client;

    @Override
    public Flux<Invoice> findAll() {
        return client.get()
                .uri("/invoices")
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new ServerErrorException("Server error", response.rawStatusCode())))
                .bodyToFlux(Invoice.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof ServerErrorException)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            throw new ServerErrorException("External Service failed to process after max retries", SERVICE_UNAVAILABLE.value());
                        }));
    }

    @Override
    public Flux<Invoice> findById(Long id) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/invoices/{id}").build(id))
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new ServerErrorException("Server error", response.rawStatusCode())))
                .bodyToFlux(Invoice.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof ServerErrorException)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            throw new ServerErrorException("External Service failed to process after max retries", SERVICE_UNAVAILABLE.value());
                        }));
    }

    @Override
    public Flux<Invoice> create(Invoice invoice) {
        return client.post()
                .uri("/invoices")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(invoice))
                .retrieve()
                .bodyToFlux(Invoice.class);
    }

    @Override
    public Flux<Invoice> update(Long id, Invoice invoice) {
        return client.put()
                .uri(uriBuilder -> uriBuilder.path("/invoices/{id}").build(id))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(invoice))
                .retrieve()
                .bodyToFlux(Invoice.class);
    }

    @Override
    public Flux<Void> delete(Long id) {
        return client.delete()
                .uri(uriBuilder -> uriBuilder.path("/invoices/{id}").build(id))
                .retrieve()
                .bodyToFlux(Void.class);
    }

}
