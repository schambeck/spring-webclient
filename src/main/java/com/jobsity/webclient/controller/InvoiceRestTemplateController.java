package com.jobsity.webclient.controller;

import com.jobsity.webclient.domain.Invoice;
import com.jobsity.webclient.service.InvoiceRestTemplateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoices-rest-template")
public class InvoiceRestTemplateController {

    private final InvoiceRestTemplateService service;

    public InvoiceRestTemplateController(InvoiceRestTemplateService service) {
        this.service = service;
    }

    @GetMapping
    public List<Invoice> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Invoice findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Invoice create(@RequestBody Invoice invoice) {
        return service.create(invoice);
    }

    @PutMapping("/{id}")
    public Invoice update(@PathVariable("id") Long id, @RequestBody Invoice invoice) {
        return service.update(id, invoice);
    }

    @DeleteMapping("/{id}")
    public Void delete(@PathVariable Long id) {
        return service.delete(id);
    }

}
