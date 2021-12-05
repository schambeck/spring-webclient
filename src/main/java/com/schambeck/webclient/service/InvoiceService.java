package com.schambeck.webclient.service;

import com.schambeck.webclient.domain.Invoice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InvoiceService {

    Flux<Invoice> findAll();

    Mono<Invoice> findById(Long id);

    Mono<Invoice> create(Invoice invoice);

    Mono<Invoice> update(Long id, Invoice invoice);

    Mono<Void> delete(Long id);

}
