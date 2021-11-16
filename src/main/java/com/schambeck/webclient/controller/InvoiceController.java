package com.schambeck.webclient.controller;

import com.schambeck.webclient.domain.Invoice;
import com.schambeck.webclient.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
class InvoiceController {

    private final InvoiceService service;

    @GetMapping
    Mono<List<Invoice>> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    Mono<Invoice> findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    Mono<Invoice> create(@RequestBody Invoice invoice) {
        return service.create(invoice);
    }

    @PutMapping("/{id}")
    Mono<Invoice> update(@PathVariable("id") Long id, @RequestBody Invoice invoice) {
        return service.update(id, invoice);
    }

    @DeleteMapping("/{id}")
    Mono<Void> delete(@PathVariable Long id) {
        return service.delete(id);
    }

}
