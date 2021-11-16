package com.schambeck.webclient.service;

import com.schambeck.webclient.base.exception.ClientErrorException;
import com.schambeck.webclient.domain.Invoice;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Tag("unit")
@SpringBootTest(webEnvironment = RANDOM_PORT)
class InvoiceServiceTest {

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
        assertEquals(id, invoice.getId());
        assertEquals(LocalDate.parse(issued), invoice.getIssued());
        assertEquals(total, invoice.getTotal().doubleValue());
    }

    @Test
    void findAll() {
        List<Invoice> allInvoices = new ArrayList<Invoice>() {{
            add(createInvoice(1L, "2021-02-01", 1000));
            add(createInvoice(2L, "2021-02-02", 2000));
            add(createInvoice(3L, "2021-02-03", 3000));
            add(createInvoice(4L, "2021-02-04", 4000));
        }};
        when(service.findAll()).thenReturn(Mono.just(allInvoices));

        webClient.get().uri("/invoices")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Invoice.class)
                .value(invoices -> assertInvoice(invoices, 0, 1, "2021-02-01", 1000D))
                .value(invoices -> assertInvoice(invoices, 1, 2, "2021-02-02", 2000D))
                .value(invoices -> assertInvoice(invoices, 2, 3, "2021-02-03", 3000D))
                .value(invoices -> assertInvoice(invoices, 3, 4, "2021-02-04", 4000D));

        verify(service).findAll();
    }

    @Test
    void findById() {
        Invoice payload = createInvoice(1L, "2021-02-01", 1000);
        when(service.findById(1L)).thenReturn(Mono.just(payload));

        webClient.get().uri("/invoices/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Invoice.class)
                .value(invoice -> assertInvoice(invoice, 1, "2021-02-01", 1000D));

        verify(service).findById(1L);
    }

    @Test
    void findByIdNotFound() {
        when(service.findById(6L)).thenThrow(new ClientErrorException("Entity 6 not found", NOT_FOUND.value()));
        webClient.get().uri("/invoices/{id}", 6)
                .exchange()
                .expectStatus().is4xxClientError();
        verify(service).findById(6L);
    }

    @Test
    void create() {
        Invoice payload = createInvoice(5L, "2021-02-05", 5000);
        when(service.create(payload)).thenReturn(Mono.just(payload));
        webClient.post()
                .uri("/invoices")
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Invoice.class)
                .value(invoice -> assertInvoice(invoice, 5, "2021-02-05", 5000D));
        verify(service).create(payload);
    }

    @Test
    void update() {
        Invoice payload = createInvoice( "2021-02-03", 3000);
        Invoice updated = createInvoice(1L,"2021-02-03", 3000);
        when(service.update(1L, payload)).thenReturn(Mono.just(updated));

        webClient.put().uri("/invoices/{id}", 1)
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Invoice.class)
                .value(invoice -> assertInvoice(invoice, 1, "2021-02-03", 3000D));

        verify(service).update(1L, payload);
    }

    @Test
    void updateNotFound() {
        Invoice payload = createInvoice("2021-02-06", 6000);
        when(service.update(6L, payload)).thenThrow(new ClientErrorException("Entity 6 not found", NOT_FOUND.value()));

        webClient.put().uri("/invoices/{id}", 6)
                .contentType(APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().is4xxClientError();

        verify(service).update(6L, payload);
    }

    @Test
    void delete() {
        when(service.delete(1L)).thenReturn(Mono.empty());

        webClient.delete().uri("/invoices/{id}", 1)
                .exchange()
                .expectStatus().isOk();

        verify(service).delete(1L);
    }

    @Test
    void deleteNotFound() {
        when(service.delete(7L)).thenThrow(new ClientErrorException("Entity 7 not found", NOT_FOUND.value()));

        webClient.delete().uri("/invoices/{id}", 7)
                .exchange()
                .expectStatus().is4xxClientError();

        verify(service).delete(7L);
    }

}
