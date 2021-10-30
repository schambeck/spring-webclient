package com.jobsity.webclient.service;

import com.jobsity.webclient.domain.Invoice;

import java.util.List;

public interface InvoiceRestTemplateService {

    List<Invoice> findAll();

    Invoice findById(Long id);

    Invoice create(Invoice invoice);

    Invoice update(Long id, Invoice invoice);

    Void delete(Long id);

}
