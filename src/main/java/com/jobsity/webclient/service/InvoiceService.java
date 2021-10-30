package com.jobsity.webclient.service;

import com.jobsity.webclient.domain.Invoice;
import reactor.core.publisher.Mono;

import java.util.List;

public interface InvoiceService {

    Mono<List<Invoice>> findAll();

    Mono<Invoice> findById(Long id);

    Mono<Invoice> create(Invoice invoice);

    Mono<Invoice> update(Long id, Invoice invoice);

    Mono<Void> delete(Long id);

}
