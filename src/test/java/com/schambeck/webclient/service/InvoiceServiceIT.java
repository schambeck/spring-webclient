package com.schambeck.webclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.schambeck.webclient.base.ObjectMapperUtil;
import com.schambeck.webclient.domain.Invoice;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.time.Month.FEBRUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
class InvoiceServiceIT {

    private static MockWebServer mockWebServer;

    private static InvoiceService service;

    private static ObjectMapperUtil mapperUtil;

    @BeforeAll
    static void init() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(WRITE_DATES_AS_TIMESTAMPS);
        mapperUtil = new ObjectMapperUtil(mapper);
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        service = new InvoiceServiceImpl(WebClient.create(baseUrl));
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private Invoice createInvoice(String issued, double total) {
        return createInvoice(null, issued, total);
    }

    private static Invoice createInvoice(Long id, String issued, double total) {
        return new Invoice(id, LocalDate.parse(issued), BigDecimal.valueOf(total));
    }

    private boolean assertInvoice(List<Invoice> invoices, int index, int id, String issued, double total) {
        Invoice invoice = invoices.get(index);
        return assertInvoice(invoice, id, issued, total);
    }

    private boolean assertInvoice(Invoice invoice, int id, String issued, double total) {
        return invoice.getId().equals((long) id)
                && invoice.getIssued().equals(LocalDate.parse(issued))
                && invoice.getTotal().equals(BigDecimal.valueOf(total));
    }

    @Test
    void findAll() throws Exception {
        List<Invoice> payload = new ArrayList<Invoice>() {{
            add(createInvoice(1L, "2021-02-01", 1000));
            add(createInvoice(2L, "2021-02-02", 2000));
            add(createInvoice(3L, "2021-02-03", 3000));
            add(createInvoice(4L, "2021-02-04", 4000));
        }};
        mockWebServer.enqueue(new MockResponse().setBody(mapperUtil.asJsonString(payload))
                .addHeader("Content-Type", "application/json"));

        Mono<List<Invoice>> found = service.findAll();

        StepVerifier.create(found)
                .expectNextMatches(invoices -> assertInvoice(invoices, 0, 1, "2021-02-01", 1000)
                        && assertInvoice(invoices, 1, 2, "2021-02-02", 2000)
                        && assertInvoice(invoices, 2, 3, "2021-02-03", 3000)
                        && assertInvoice(invoices, 3, 4, "2021-02-04", 4000))
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/invoices", recordedRequest.getPath());
    }

    @Test
    void findById() throws Exception {
        Invoice payload = createInvoice(1L, "2021-02-01", 1000);
        mockWebServer.enqueue(new MockResponse().setBody(mapperUtil.asJsonString(payload))
                .addHeader("Content-Type", "application/json"));

        Mono<Invoice> found = service.findById(1L);

        StepVerifier.create(found)
                .expectNextMatches(invoice -> assertInvoice(invoice, 1, "2021-02-01", 1000))
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/invoices/1", recordedRequest.getPath());
    }

    @Test
    void create() throws Exception {
        Invoice payload = createInvoice(1L, "2021-02-01", 1000);
        mockWebServer.enqueue(new MockResponse().setBody(mapperUtil.asJsonString(payload))
                .addHeader("Content-Type", "application/json"));

        Mono<Invoice> created = service.create(payload);

        StepVerifier.create(created)
                .expectNextMatches(invoice -> assertInvoice(invoice, 1, "2021-02-01", 1000))
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/invoices", recordedRequest.getPath());
    }

    @Test
    void update() throws Exception {
        LocalDate newIssued = LocalDate.of(2021, FEBRUARY, 1);
        BigDecimal newTotal = BigDecimal.valueOf(1000);
        Invoice response = createInvoice(1L, newIssued.toString(), newTotal.doubleValue());
        mockWebServer.enqueue(new MockResponse().setBody(mapperUtil.asJsonString(response))
                .addHeader("Content-Type", "application/json"));

        Invoice payload = createInvoice(newIssued.toString(), newTotal.doubleValue());
        Mono<Invoice> updated = service.update(1L, payload);

        StepVerifier.create(updated)
                .expectNextMatches(invoice -> assertInvoice(invoice, 1, newIssued.toString(), newTotal.doubleValue()))
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("PUT", recordedRequest.getMethod());
        assertEquals("/invoices/1", recordedRequest.getPath());
    }

    @Test
    void delete() throws Exception {
        String responseMessage = "Invoice Deleted SuccessFully";
        Long invoiceId = 1L;
        mockWebServer.enqueue(new MockResponse().setBody(mapperUtil.asJsonString(responseMessage))
                .addHeader("Content-Type", "application/json"));

        Mono<Void> deleted = service.delete(invoiceId);

        StepVerifier.create(deleted)
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("DELETE", recordedRequest.getMethod());
        assertEquals("/invoices/1", recordedRequest.getPath());
    }

}
