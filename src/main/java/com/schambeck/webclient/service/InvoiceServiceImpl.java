package com.schambeck.webclient.service;

import com.schambeck.webclient.base.exception.ClientErrorException;
import com.schambeck.webclient.base.exception.ServerErrorException;
import com.schambeck.webclient.base.exception.ServiceUnavailableException;
import com.schambeck.webclient.domain.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_NDJSON;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final WebClient client;

    @Override
    public Flux<Invoice> findAll() {
        return client.get()
                .uri("/invoices")
                .accept(APPLICATION_NDJSON, APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new ClientErrorException("Client error", response.rawStatusCode())))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.just(new ServerErrorException("Server error", response.rawStatusCode())))
                .bodyToFlux(Invoice.class)
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
                .body(Mono.just(invoice), Invoice.class)
                .retrieve()
                .bodyToMono(Invoice.class);
    }

    @Override
    public Mono<Invoice> update(Long id, Invoice invoice) {
        return client.put()
                .uri(uriBuilder -> uriBuilder.path("/invoices/{id}").build(id))
                .body(Mono.just(invoice), Invoice.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new ClientErrorException("Client error", response.rawStatusCode())))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new ServerErrorException("Server error", response.rawStatusCode())))
                .bodyToMono(Invoice.class);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return client.delete()
                .uri(uriBuilder -> uriBuilder.path("/invoices/{id}").build(id))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new ClientErrorException("Client error", response.rawStatusCode())))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new ServerErrorException("Server error", response.rawStatusCode())))
                .bodyToMono(Void.class);
    }

}
