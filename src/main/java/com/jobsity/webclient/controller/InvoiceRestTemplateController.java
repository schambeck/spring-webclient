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

    @GetMapping("/{index}")
    public Invoice findByIndex(@PathVariable("index") int index) {
        return service.findByIndex(index);
    }

    @PostMapping
    public Invoice create(@RequestBody Invoice invoice) {
        return service.create(invoice);
    }

    @PutMapping("/{index}")
    public Invoice update(@PathVariable("index") Integer index, @RequestBody Invoice invoice) {
        return service.update(index, invoice);
    }

    @DeleteMapping("/{index}")
    public Void delete(@PathVariable int index) {
        return service.delete(index);
    }

}
