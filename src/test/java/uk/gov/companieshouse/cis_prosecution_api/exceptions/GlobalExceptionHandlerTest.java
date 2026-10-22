package uk.gov.companieshouse.cis_prosecution_api.exceptions;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.companieshouse.cis_prosecution_api.exceptions.GlobalExceptionHandlerTestFixtures.createHttpClientErrorException;
import static uk.gov.companieshouse.cis_prosecution_api.exceptions.GlobalExceptionHandlerTestFixtures.createHttpMessageNotReadableException;
import static uk.gov.companieshouse.cis_prosecution_api.exceptions.GlobalExceptionHandlerTestFixtures.createHttpServerErrorException;
import static uk.gov.companieshouse.cis_prosecution_api.exceptions.GlobalExceptionHandlerTestFixtures.createMethodArgumentNotValidException;
import static uk.gov.companieshouse.cis_prosecution_api.exceptions.GlobalExceptionHandlerTestFixtures.createMethodArgumentTypeMismatchException;
import static uk.gov.companieshouse.cis_prosecution_api.exceptions.GlobalExceptionHandlerTestFixtures.createRuntimeException;
import static uk.gov.companieshouse.cis_prosecution_api.exceptions.GlobalExceptionHandlerTestFixtures.createUnknownHttpStatusCodeException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@WebMvcTest(controllers = TestController.class)
public class GlobalExceptionHandlerTest {

    private static final String MESSAGE = "Test message";
    private static final String TEST_URL = "/test";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    TestService testService;

    @Test
    void testHandleMethodArgumentTypeMismatchException() throws Exception {
        // Mock the service method
        MethodArgumentTypeMismatchException exception = createMethodArgumentTypeMismatchException(
            "param1",
            "invalidValue",
            "Invalid value for param1"
        );
        when(testService.getString()).thenThrow(exception);

        // Perform the request and assert the response
        mockMvc.perform(get(TEST_URL))
            .andExpectAll(
                status().isBadRequest(),
                jsonPath("$.api_errors[0].field_name").value("param1"),
                jsonPath("$.api_errors[0].rejected_value").value("invalidValue"),
                jsonPath("$.api_errors[0].message").value("Method parameter 'param1': Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'; Invalid value for param1"),
                jsonPath("$.message").doesNotExist()
            );
    }

    @Test
    void testHandleHttpMessageNotReadableException() throws Exception {
        // Mock the service method
        HttpMessageNotReadableException exception = createHttpMessageNotReadableException(MESSAGE);
        when(testService.getString()).thenThrow(exception);

        // Perform the request and assert the response
        mockMvc.perform(get(TEST_URL))
            .andExpectAll(
                status().isBadRequest(),
                jsonPath("$.api_errors[0].field_name").isEmpty(),
                jsonPath("$.api_errors[0].rejected_value").isEmpty(),
                jsonPath("$.api_errors[0].message").value(MESSAGE)
            );
    }

    @Test
    void testHandleMethodArgumentNotValidException() throws Exception {
        // Mock the service method
        MethodArgumentNotValidException exception = createMethodArgumentNotValidException();
        when(testService.getString()).thenThrow(exception);

        // Perform the request and assert the response
        mockMvc.perform(get(TEST_URL))
            .andExpectAll(
                status().isBadRequest(),
                jsonPath("$.api_errors[0].field_name").value("field1"),
                jsonPath("$.api_errors[0].rejected_value").value(nullValue()),
                jsonPath("$.api_errors[0].message").value("field1 cannot be null"),
                jsonPath("$.api_errors[1].field_name").value("field2"),
                jsonPath("$.api_errors[1].rejected_value").value(11),
                jsonPath("$.api_errors[1].message").value("field2 cannot be greater than 10"),
                jsonPath("$.message").doesNotExist()
            );
    }

    @Test
    void testHandleHttpClientErrorException() throws Exception {
        // Mock the service method
        HttpClientErrorException exception = createHttpClientErrorException();
        when(testService.getString()).thenThrow(exception);

        // Perform the request and assert the response
        mockMvc.perform(get(TEST_URL))
            .andExpect(
                status().isBadRequest()
            );
    }

    @Test
    void testHandleHttpServerErrorException() throws Exception {
        // Mock the service method
        HttpServerErrorException exception = createHttpServerErrorException("HTTP server error");
        when(testService.getString()).thenThrow(exception);

        // Perform the request and assert the response
        mockMvc.perform(get(TEST_URL))
            .andExpectAll(
                status().isInternalServerError(),
                jsonPath("$.message").value("500 HTTP server error")
            );
    }

    @Test
    void testHandleUnknownHttpStatusCodeException() throws Exception {
        // Mock the service method
        UnknownHttpStatusCodeException exception = createUnknownHttpStatusCodeException("Unknown HTTP status code");
        when(testService.getString()).thenThrow(exception);

        // Perform the request and assert the response
        mockMvc.perform(get(TEST_URL))
            .andExpectAll(
                status().is(599),
                jsonPath("$.message").value("Unknown status code [599] Unknown HTTP status code")
            );
    }

    @Test
    void testHandleAllExceptions() throws Exception {
        // Mock the service method
        RuntimeException exception = createRuntimeException(MESSAGE);
        when(testService.getString()).thenThrow(exception);

        // Perform the request and assert the response
        mockMvc.perform(get(TEST_URL))
            .andExpectAll(
                status().isInternalServerError(),
                jsonPath("$.message").value(MESSAGE)
            );
    }
}
