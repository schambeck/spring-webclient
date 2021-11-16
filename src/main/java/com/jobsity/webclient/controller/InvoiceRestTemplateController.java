package com.jobsity.webclient.controller;

import com.jobsity.webclient.domain.Invoice;
import com.jobsity.webclient.service.InvoiceRestTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoices-rest-template")
@RequiredArgsConstructor
class InvoiceRestTemplateController {

    private final InvoiceRestTemplateService service;

    @GetMapping
    List<Invoice> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    Invoice findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    Invoice create(@RequestBody Invoice invoice) {
        return service.create(invoice);
    }

    @PutMapping("/{id}")
    Invoice update(@PathVariable("id") Long id, @RequestBody Invoice invoice) {
        return service.update(id, invoice);
    }

    @DeleteMapping("/{id}")
    Void delete(@PathVariable Long id) {
        return service.delete(id);
    }

}
