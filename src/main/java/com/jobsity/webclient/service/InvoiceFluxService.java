package com.jobsity.webclient.service;

import com.jobsity.webclient.domain.Invoice;
import reactor.core.publisher.Flux;

public interface InvoiceFluxService {

    Flux<Invoice> findAll();

}
