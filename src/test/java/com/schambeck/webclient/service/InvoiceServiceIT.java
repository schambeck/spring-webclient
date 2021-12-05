package com.schambeck.webclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.schambeck.webclient.base.ObjectMapperUtil;
import com.schambeck.webclient.base.exception.ClientErrorException;
import com.schambeck.webclient.domain.Invoice;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
class InvoiceServiceIT {

    private ObjectMapperUtil mapperUtil;
    private MockWebServer mockWebServer;
    private InvoiceService service;

    @BeforeEach
    void setup() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(WRITE_DATES_AS_TIMESTAMPS);
        mapperUtil = new ObjectMapperUtil(mapper);

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        service = new InvoiceServiceImpl(WebClient.create(baseUrl));
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private static Invoice createInvoice(Long id, String issued, double total) {
        return new Invoice(id, LocalDate.parse(issued), BigDecimal.valueOf(total));
    }

    private boolean assertInvoice(Invoice invoice, int id, String issued, double total) {
        return Long.valueOf(id).equals(invoice.getId())
                && invoice.getIssued().equals(LocalDate.parse(issued))
                && invoice.getTotal().equals(BigDecimal.valueOf(total));
    }

    @Test
    void findAll() throws Exception {
        List<Invoice> payload = new ArrayList<>() {{
            add(createInvoice(1L, "2021-02-01", 1000));
            add(createInvoice(2L, "2021-02-02", 2000));
            add(createInvoice(3L, "2021-02-03", 3000));
            add(createInvoice(4L, "2021-02-04", 4000));
        }};
        mockWebServer.enqueue(new MockResponse().setBody(mapperUtil.asJsonString(payload))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(service.findAll())
                .expectNextMatches(invoices -> assertInvoice(invoices, 1, "2021-02-01", 1000))
                .expectNextMatches(invoices -> assertInvoice(invoices, 2, "2021-02-02", 2000))
                .expectNextMatches(invoices -> assertInvoice(invoices, 3, "2021-02-03", 3000))
                .expectNextMatches(invoices -> assertInvoice(invoices, 4, "2021-02-04", 4000))
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

        StepVerifier.create(service.findById(1L))
                .expectNextMatches(invoice -> assertInvoice(invoice, 1, "2021-02-01", 1000))
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/invoices/1", recordedRequest.getPath());
    }

    @Test
    void findByIdNotFound() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(NOT_FOUND.code()));

        StepVerifier.create(service.findById(1L))
                .expectError(ClientErrorException.class)
                .verify();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/invoices/1", recordedRequest.getPath());
    }

    @Test
    void create() throws Exception {
        Invoice payload = createInvoice(1L, "2021-02-01", 1000);
        mockWebServer.enqueue(new MockResponse().setBody(mapperUtil.asJsonString(payload))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(service.create(payload))
                .expectNextMatches(invoice -> assertInvoice(invoice, 1, "2021-02-01", 1000))
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/invoices", recordedRequest.getPath());
    }

    @Test
    void update() throws Exception {
        Invoice payload = createInvoice(1L, "2021-02-01", 1000);
        mockWebServer.enqueue(new MockResponse().setBody(mapperUtil.asJsonString(payload))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(service.update(1L, payload))
                .expectNextMatches(invoice -> assertInvoice(invoice, 1, "2021-02-01", 1000))
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("PUT", recordedRequest.getMethod());
        assertEquals("/invoices/1", recordedRequest.getPath());
    }

    @Test
    void updateNotFound() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(NOT_FOUND.code()));

        Invoice payload = createInvoice(1L, "2021-02-01", 1000);
        StepVerifier.create(service.update(1L, payload))
                .expectError(ClientErrorException.class)
                .verify();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("PUT", recordedRequest.getMethod());
        assertEquals("/invoices/1", recordedRequest.getPath());
    }

    @Test
    void delete() throws Exception {
        String responseMessage = "Invoice Deleted SuccessFully";
        mockWebServer.enqueue(new MockResponse().setBody(mapperUtil.asJsonString(responseMessage))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(service.delete(1L))
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("DELETE", recordedRequest.getMethod());
        assertEquals("/invoices/1", recordedRequest.getPath());
    }

    @Test
    void deleteNotFound() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(NOT_FOUND.code()));

        StepVerifier.create(service.delete(1L))
                .expectError(ClientErrorException.class)
                .verify();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("DELETE", recordedRequest.getMethod());
        assertEquals("/invoices/1", recordedRequest.getPath());
    }

}
