package com.jobsity.webclient.controller;

import com.jobsity.webclient.domain.Invoice;
import com.jobsity.webclient.service.InvoiceFluxService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/invoices-flux")
public class InvoiceFluxController {

    private final InvoiceFluxService service;

    public InvoiceFluxController(InvoiceFluxService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<Invoice> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Flux<Invoice> findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Flux<Invoice> create(@RequestBody Invoice invoice) {
        return service.create(invoice);
    }

    @PutMapping("/{id}")
    public Flux<Invoice> update(@PathVariable("id") Long id, @RequestBody Invoice invoice) {
        return service.update(id, invoice);
    }

    @DeleteMapping("/{id}")
    public Flux<Void> delete(@PathVariable Long id) {
        return service.delete(id);
    }

}
