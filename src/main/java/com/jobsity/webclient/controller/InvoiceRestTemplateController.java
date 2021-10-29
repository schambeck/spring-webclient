package com.jobsity.webclient.controller;

import com.jobsity.webclient.domain.Invoice;
import com.jobsity.webclient.service.InvoiceRestTemplateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/invoices-rest-template")
public class InvoiceRestTemplateController {

    private final InvoiceRestTemplateService restTemplateService;

    public InvoiceRestTemplateController(InvoiceRestTemplateService restTemplateService) {
        this.restTemplateService = restTemplateService;
    }

    @GetMapping
    public List<Invoice> findAll() {
        return restTemplateService.findAll();
    }

}
