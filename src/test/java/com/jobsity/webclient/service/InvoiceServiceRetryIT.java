package com.jobsity.webclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jobsity.webclient.base.exception.ClientErrorException;
import com.jobsity.webclient.base.exception.ServiceUnavailableException;
import com.jobsity.webclient.domain.Invoice;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class InvoiceServiceRetryIT {

    private static ObjectMapper mapper;
    private static MockWebServer mockWebServer;
    private InvoiceService service;

    @BeforeAll
    static void setUp() throws IOException {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(WRITE_DATES_AS_TIMESTAMPS);
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        service = new InvoiceServiceImpl(WebClient.builder()
                .baseUrl(baseUrl)
                .build());
    }

    private Invoice createInvoice(Long id, String issued, double total) {
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
    void findAllReturnsError_whenGettingData_thenRetryAndReturnResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        List<Invoice> mockInvoices = new ArrayList<Invoice>() {{
            add(createInvoice(1L, "2021-02-01", 1000));
            add(createInvoice(2L, "2021-02-02", 2000));
            add(createInvoice(3L, "2021-02-03", 3000));
            add(createInvoice(4L, "2021-02-04", 4000));
        }};
        mockWebServer.enqueue(new MockResponse().setResponseCode(OK.code()).setBody(mapper.writeValueAsString(mockInvoices))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(service.findAll())
                .expectNextMatches(invoices -> assertInvoice(invoices, 0, 1, "2021-02-01", 1000)
                        && assertInvoice(invoices, 1, 2, "2021-02-02", 2000)
                        && assertInvoice(invoices, 2, 3, "2021-02-03", 3000)
                        && assertInvoice(invoices, 3, 4, "2021-02-04", 4000))
                .verifyComplete();

        verifyNumberOfFindAllGetRequests(4);
    }

    @Test
    void findAllRetryAttemptsExhausted_whenGettingData_thenRetryAndReturnError() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));

        StepVerifier.create(service.findAll())
                .expectError(ServiceUnavailableException.class)
                .verify();

        verifyNumberOfFindAllGetRequests(4);
    }

    @Test
    void findByIdReturnsError_whenGettingData_thenRetryAndReturnResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        Invoice invoice = createInvoice(1L, "2021-02-01", 1000);
        mockWebServer.enqueue(new MockResponse().setBody(mapper.writeValueAsString(invoice))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(service.findById(1L))
                .expectNextMatches(response -> assertInvoice(response, 1, "2021-02-01", 1000))
                .verifyComplete();

        verifyNumberOfFindByIdGetRequests(4);
    }

    @Test
    void findByIdReturnsClientError_whenGettingData_thenNoRetry() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(UNAUTHORIZED.code()));

        StepVerifier.create(service.findById(1L))
                .expectError(ClientErrorException.class)
                .verify();

        verifyNumberOfFindByIdGetRequests(1);
    }

    @Test
    void findByIdRetryAttemptsExhausted_whenGettingData_thenRetryAndReturnError() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));

        StepVerifier.create(service.findById(1L))
                .expectError(ServiceUnavailableException.class)
                .verify();

        verifyNumberOfFindByIdGetRequests(4);
    }

    private void verifyNumberOfFindAllGetRequests(int times) throws Exception {
        for (int i = 0; i < times; i++) {
            RecordedRequest recordedRequest = mockWebServer.takeRequest();
            assertThat(recordedRequest.getMethod()).isEqualTo("GET");
            assertThat(recordedRequest.getPath()).isEqualTo("/invoices");
        }
    }

    private void verifyNumberOfFindByIdGetRequests(int times) throws Exception {
        for (int i = 0; i < times; i++) {
            RecordedRequest recordedRequest = mockWebServer.takeRequest();
            assertThat(recordedRequest.getMethod()).isEqualTo("GET");
            assertThat(recordedRequest.getPath()).isEqualTo("/invoices/1");
        }
    }

}
