package com.schambeck.webclient.controller;

import com.schambeck.webclient.exception.ClientErrorException;
import com.schambeck.webclient.domain.Invoice;
import com.schambeck.webclient.service.InvoiceService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Tag("unit")
@WebFluxTest(InvoiceController.class)
class InvoiceControllerTest {

    @Autowired
    private InvoiceController controller;

    @MockBean
    private InvoiceService service;

    private Invoice createInvoice(String issued, double total) {
        return createInvoice(null, issued, total);
    }

    private Invoice createInvoice(Long id, String issued, double total) {
        return new Invoice(id, LocalDate.parse(issued), BigDecimal.valueOf(total));
    }

    private boolean assertInvoice(Invoice invoice, int id, String issued, double total) {
        return invoice.getId().equals((long) id)
                && invoice.getIssued().equals(LocalDate.parse(issued))
                && invoice.getTotal().equals(BigDecimal.valueOf(total));
    }

    @Test
    void findAll() {
        Invoice[] allInvoices = {
            createInvoice(1L, "2021-02-01", 1000),
            createInvoice(2L, "2021-02-02", 2000),
            createInvoice(3L, "2021-02-03", 3000),
            createInvoice(4L, "2021-02-04", 4000)
        };
        when(service.findAll()).thenReturn(Flux.just(allInvoices));
        Flux<Invoice> found = controller.findAll();
        StepVerifier.create(found)
                .expectNextMatches(invoices -> assertInvoice(invoices, 1, "2021-02-01", 1000))
                .expectNextMatches(invoices -> assertInvoice(invoices, 2, "2021-02-02", 2000))
                .expectNextMatches(invoices -> assertInvoice(invoices, 3, "2021-02-03", 3000))
                .expectNextMatches(invoices -> assertInvoice(invoices, 4, "2021-02-04", 4000))
                .verifyComplete();
    }

    @Test
    void findById() {
        Invoice payload = createInvoice(1L, "2021-02-01", 1000);
        when(service.findById(1L)).thenReturn(Mono.just(payload));
        Mono<Invoice> found = controller.findById(1L);
        StepVerifier.create(found)
                .expectNextMatches(invoice -> assertInvoice(invoice, 1, "2021-02-01", 1000))
                .verifyComplete();
    }

    @Test
    void findByIdNotFound() {
        when(service.findById(6L)).thenThrow(new ClientErrorException("Entity 6 not found", NOT_FOUND.value()));
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> controller.findById(6L));
        String expected = "Entity 6 not found";
        String actual = exception.getMessage();
        assertEquals(actual, expected);
    }

    @Test
    void create() {
        Invoice payload = createInvoice(5L, "2021-02-05", 5000);
        when(service.create(payload)).thenReturn(Mono.just(payload));
        Mono<Invoice> found = controller.create(payload);
        StepVerifier.create(found)
                .expectNextMatches(invoice -> assertInvoice(invoice, 5, "2021-02-05", 5000))
                .verifyComplete();
    }

    @Test
    void update() {
        Invoice updated = createInvoice("2021-02-03", 3000);
        Mono<Invoice> result = Mono.just(createInvoice(1L, "2021-02-03", 3000));
        when(service.update(1L, updated)).thenReturn(result);

        StepVerifier.create(controller.update(1L, updated))
                .expectNextMatches(invoice -> assertInvoice(invoice, 1, "2021-02-03", 3000))
                .verifyComplete();
    }

    @Test
    void updateNotFound() {
        Invoice invoice = createInvoice("2021-02-06", 6000);
        when(service.update(6L, invoice)).thenThrow(new ClientErrorException("Entity 6 not found", NOT_FOUND.value()));

        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> controller.update(6L, invoice));
        String expected = "Entity 6 not found";
        String actual = exception.getMessage();
        assertEquals(actual, expected);
    }

    @Test
    void delete() {
        when(service.delete(1L)).thenReturn(Mono.empty());

        Mono<Void> deleted = controller.delete(1L);

        StepVerifier.create(deleted)
                .verifyComplete();
    }

    @Test
    void deleteNotFound() {
        when(service.delete(7L)).thenThrow(new ClientErrorException("Entity 7 not found", NOT_FOUND.value()));

        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> controller.delete(7L));
        String expected = "Entity 7 not found";
        String actual = exception.getMessage();
        assertEquals(actual, expected);
    }

}
