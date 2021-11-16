package com.jobsity.webclient.controller;

import com.jobsity.webclient.domain.Invoice;
import com.jobsity.webclient.service.InvoiceFluxService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/invoices-flux")
@RequiredArgsConstructor
class InvoiceFluxController {

    private final InvoiceFluxService service;

    @GetMapping
    Flux<Invoice> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    Flux<Invoice> findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    Flux<Invoice> create(@RequestBody Invoice invoice) {
        return service.create(invoice);
    }

    @PutMapping("/{id}")
    Flux<Invoice> update(@PathVariable("id") Long id, @RequestBody Invoice invoice) {
        return service.update(id, invoice);
    }

    @DeleteMapping("/{id}")
    Flux<Void> delete(@PathVariable Long id) {
        return service.delete(id);
    }

}
