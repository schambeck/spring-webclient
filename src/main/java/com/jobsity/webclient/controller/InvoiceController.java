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

    @GetMapping("/{index}")
    public Mono<Invoice> findByIndex(@PathVariable("index") int index) {
        return service.findByIndex(index);
    }

    @PostMapping
    public Mono<Invoice> create(@RequestBody Invoice invoice) {
        return service.create(invoice);
    }

    @PutMapping("/{index}")
    public Mono<Invoice> update(@PathVariable("index") Integer index, @RequestBody Invoice invoice) {
        return service.update(index, invoice);
    }

    @DeleteMapping("/{index}")
    public Mono<Void> delete(@PathVariable int index) {
        return service.delete(index);
    }

}
