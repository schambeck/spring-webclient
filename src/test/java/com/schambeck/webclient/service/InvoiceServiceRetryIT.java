package com.schambeck.webclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.schambeck.webclient.json.ObjectMapperUtil;
import com.schambeck.webclient.exception.ClientErrorException;
import com.schambeck.webclient.exception.ServiceUnavailableException;
import com.schambeck.webclient.domain.Invoice;
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

    private Invoice createInvoice(Long id, String issued, double total) {
        return new Invoice(id, LocalDate.parse(issued), BigDecimal.valueOf(total));
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
        List<Invoice> mockInvoices = new ArrayList<>() {{
            add(createInvoice(1L, "2021-02-01", 1000));
            add(createInvoice(2L, "2021-02-02", 2000));
            add(createInvoice(3L, "2021-02-03", 3000));
            add(createInvoice(4L, "2021-02-04", 4000));
        }};
        mockWebServer.enqueue(new MockResponse().setResponseCode(OK.code()).setBody(mapperUtil.asJsonString(mockInvoices))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(service.findAll())
                .expectNextMatches(invoices -> assertInvoice(invoices, 1, "2021-02-01", 1000))
                .expectNextMatches(invoices -> assertInvoice(invoices, 2, "2021-02-02", 2000))
                .expectNextMatches(invoices -> assertInvoice(invoices, 3, "2021-02-03", 3000))
                .expectNextMatches(invoices -> assertInvoice(invoices, 4, "2021-02-04", 4000))
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
        mockWebServer.enqueue(new MockResponse().setBody(mapperUtil.asJsonString(invoice))
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
