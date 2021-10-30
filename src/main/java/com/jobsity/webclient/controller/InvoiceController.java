package com.jobsity.webclient.controller;

import com.jobsity.webclient.domain.Invoice;
import com.jobsity.webclient.service.InvoiceService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService service;

    public InvoiceController(InvoiceService service) {
        this.service = service;
    }

    @GetMapping
    public Mono<List<Invoice>> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Invoice> findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Mono<Invoice> create(@RequestBody Invoice invoice) {
        return service.create(invoice);
    }

    @PutMapping("/{id}")
    public Mono<Invoice> update(@PathVariable("id") Long id, @RequestBody Invoice invoice) {
        return service.update(id, invoice);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return service.delete(id);
    }

}
