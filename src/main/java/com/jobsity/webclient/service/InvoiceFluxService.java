package com.jobsity.webclient.service;

import com.jobsity.webclient.domain.Invoice;
import reactor.core.publisher.Flux;

public interface InvoiceFluxService {

    Flux<Invoice> findAll();

    Flux<Invoice> findById(Long id);

    Flux<Invoice> create(Invoice invoice);

    Flux<Invoice> update(Long id, Invoice invoice);

    Flux<Void> delete(Long id);

}
