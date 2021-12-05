package com.schambeck.webclient.controller;

import com.schambeck.webclient.base.exception.ClientErrorException;
import com.schambeck.webclient.domain.Invoice;
import com.schambeck.webclient.service.InvoiceService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_NDJSON;

@Tag("unit")
@WebFluxTest(InvoiceController.class)
class InvoiceControllerIT {

    @MockBean
    private InvoiceService service;

    @Autowired
    private WebTestClient webClient;

    private Invoice createInvoice(String issued, double total) {
        return createInvoice(null, issued, total);
    }

    private Invoice createInvoice(Long id, String issued, double total) {
        return new Invoice(id, LocalDate.parse(issued), BigDecimal.valueOf(total));
    }

    private void assertInvoice(List<Invoice> invoices, int index, int id, String issued, double total) {
        Invoice invoice = invoices.get(index);
        assertInvoice(invoice, id, issued, total);
    }

    private void assertInvoice(Invoice invoice, int id, String issued, double total) {
        assertEquals(Long.valueOf(id), invoice.getId());
        assertEquals(LocalDate.parse(issued), invoice.getIssued());
        assertEquals(BigDecimal.valueOf(total), invoice.getTotal());
    }

    @Test
    void findAll() {
        Invoice[] mockInvoices = {
            createInvoice(1L, "2021-02-01", 1000),
            createInvoice(2L, "2021-02-02", 2000),
            createInvoice(3L, "2021-02-03", 3000),
            createInvoice(4L, "2021-02-04", 4000)
        };
        when(service.findAll()).thenReturn(Flux.just(mockInvoices));
        webClient.get()
                .uri("/invoices")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_NDJSON)
                .expectBodyList(Invoice.class)
                .value(invoices -> assertInvoice(invoices, 0, 1, "2021-02-01", 1000D))
                .value(invoices -> assertInvoice(invoices, 1, 2, "2021-02-02", 2000D))
                .value(invoices -> assertInvoice(invoices, 2, 3, "2021-02-03", 3000D))
                .value(invoices -> assertInvoice(invoices, 3, 4, "2021-02-04", 4000D));
    }

    @Test
    void findById() {
        Invoice payload = createInvoice(1L, "2021-02-01", 1000);
        when(service.findById(1L)).thenReturn(Mono.just(payload));
        webClient.get()
                .uri("/invoices/{id}", 1)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Invoice.class)
                .value(invoice -> assertInvoice(invoice, 1, "2021-02-01", 1000D));
    }

    @Test
    void findByIdNotFound() {
        when(service.findById(6L)).thenThrow(new ClientErrorException("Entity 6 not found", NOT_FOUND.value()));
        webClient.get()
                .uri("/invoices/{id}", 6)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void create() {
        Invoice payload = createInvoice(5L, "2021-02-05", 5000);
        when(service.create(payload)).thenReturn(Mono.just(payload));
        webClient.post()
                .uri("/invoices")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(Mono.just(payload), Invoice.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody(Invoice.class)
                .isEqualTo(createInvoice(5L, "2021-02-05", 5000));
    }

    @Test
    void update() {
        Invoice payload = createInvoice("2021-02-03", 3000);
        Mono<Invoice> result = Mono.just(createInvoice(1L, "2021-02-03", 3000));
        when(service.update(1L, payload)).thenReturn(result);

        webClient.put()
                .uri("/invoices/{id}", 1)
                .body(Mono.just(payload), Invoice.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Invoice.class)
                .value(invoice -> assertInvoice(invoice, 1, "2021-02-03", 3000D));
    }

    @Test
    void updateNotFound() {
        Invoice payload = createInvoice("2021-02-06", 6000);
        when(service.update(6L, payload)).thenThrow(new ClientErrorException("Entity 6 not found", NOT_FOUND.value()));

        webClient.put()
                .uri("/invoices/{id}", 6)
                .body(Mono.just(payload), Invoice.class)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void delete() {
        when(service.delete(1L)).thenReturn(Mono.empty());

        webClient.delete()
                .uri("/invoices/{id}", 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteNotFound() {
        when(service.delete(7L)).thenThrow(new ClientErrorException("Entity 7 not found", NOT_FOUND.value()));

        webClient.delete()
                .uri("/invoices/{id}", 7)
                .exchange()
                .expectStatus().is4xxClientError();
    }

}
