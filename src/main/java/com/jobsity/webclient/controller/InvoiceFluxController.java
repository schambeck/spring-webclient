package com.jobsity.webclient.controller;

import com.jobsity.webclient.domain.Invoice;
import com.jobsity.webclient.service.InvoiceFluxService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/invoices-flux")
public class InvoiceFluxController {

    private final InvoiceFluxService service;

    public InvoiceFluxController(InvoiceFluxService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<Invoice> getInvoicesNonBlockingFlux() {
        return service.findAll();
    }

}
